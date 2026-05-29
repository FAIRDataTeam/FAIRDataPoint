/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fairdatateam.fairdatapoint.acceptance.search.sparql;

import org.fairdatateam.fairdatapoint.Profiles;
import org.fairdatateam.fairdatapoint.api.controller.search.SearchSparqlController;
import org.fairdatateam.fairdatapoint.config.properties.RepositoryProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles(Profiles.TESTING)
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@SpringBootTest(properties = { "instance.sparqlProxyEnabled=true" })
@WithMockUser
public class SearchSparqlControllerTest {

    // mock ResTemplate to prevent autoconfig issues with MockRestServiceServer (the controller uses RestClient)
    @MockitoBean
    RestTemplate restTemplate;

    private final MockMvcTester mockMvc;

    private final MockRestServiceServer mockBackendSparqlServer;

    private final String path = "/search/sparql";

    private final String querySelectAll = "SELECT * WHERE { ?s ?p ?o }";

    /**
     * Constructor
     */
    @Autowired
    public SearchSparqlControllerTest(MockMvcTester mockMvc, MockRestServiceServer mockBackendSparqlServer) {
        this.mockMvc = mockMvc;
        this.mockBackendSparqlServer = mockBackendSparqlServer;
    }

    @Test
    @WithAnonymousUser
    public void unauthenticatedRequestsAreDeniedWithoutContactingRemoteSparqlServer() {
        // specify request with url query, but without authentication
        URI uriWithQuery = UriComponentsBuilder
                .fromPath(path)
                .queryParam("query", querySelectAll)
                .build()
                .toUri();

        MvcTestResult testResult = mockMvc.get().uri(uriWithQuery).accept(MediaType.APPLICATION_JSON).exchange();

        assertThat(testResult).hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    public void proxyForwardingWorksForGetRequests() {
        // mock headers for response from mock backend sparql server
        final HttpHeaders mockBackendResponseHeaders = new HttpHeaders();
        // add a custom header
        final String customHeaderName = "my-custom-header";
        mockBackendResponseHeaders.add(customHeaderName, "dummy");
        // add standard hop-by-hop headers
        final List<String> hopByHopHeaders = SearchSparqlController.getHopByHopHeaders();
        hopByHopHeaders.forEach(header -> mockBackendResponseHeaders.add(header, "dummy"));
        // connection list is incomplete on purpose (does not contain all hop-by-hop headers)
        mockBackendResponseHeaders.put(HttpHeaders.CONNECTION, List.of(customHeaderName, hopByHopHeaders.get(0)));
        // add server header
        final String backendServerHeader = "mock backend sparql server 1.0";
        mockBackendResponseHeaders.add(HttpHeaders.SERVER, backendServerHeader);
        // mock json response body (https://www.w3.org/TR/sparql11-results-json/)
        final String mockJsonBody = "{\"head\": {\"vars\": []}, \"results\": {\"bindings\": []}}";
        // configure mock server for remote SPARQL endpoint
        this.mockBackendSparqlServer
                // startsWith is required, otherwise it will expect a url without (query) parameters
                .expect(requestTo(startsWith(TestConfig.TEST_SPARQL_ENDPOINT_URL)))
                // forwarded header should have been added to request
                .andExpect(header("X-Forwarded-For", matchesPattern(".+")))
                // authorization headers should have been removed from request
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
                .andRespond(withSuccess().headers(mockBackendResponseHeaders).body(mockJsonBody));

        // specify request with url query and normal user (non-admin)
        URI uriWithQuery = UriComponentsBuilder
                .fromPath(path)
                .queryParam("query", querySelectAll)
                .build()
                .toUri();

        // execute request
        MvcTestResult testResult = mockMvc.get()
                .uri(uriWithQuery)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "dummy")
                .exchange();

        // check response headers
        assertThat(testResult).hasStatusOk();
        assertThat(testResult).doesNotContainHeader(customHeaderName);
        assertThat(testResult).doesNotContainHeader(HttpHeaders.CONNECTION);
        hopByHopHeaders.forEach(header -> assertThat(testResult).doesNotContainHeader(header));
        assertThat(testResult).containsHeader(HttpHeaders.SERVER);
        assertThat(testResult).headers().doesNotContainEntry(HttpHeaders.SERVER, List.of(backendServerHeader));
        // verify that proxy returns json response body
        assertThat(testResult).bodyJson().hasPath("head.vars").hasPath("results.bindings");

    }

    // note that @Configuration overrides the primary config, whereas @TestConfiguration extends it
    @TestConfiguration
    static class TestConfig {

        public static final String TEST_SPARQL_ENDPOINT_URL = "https://triple.store.example.org/sparql";

        /**
         * Overrides the default repositoryProperties bean to return a dummy url from getUrl().
         */
        @Bean
        @Primary
        public RepositoryProperties repositoryProperties() {
            return new RepositoryProperties() {
                @Override
                public String getUrl() {
                    return TEST_SPARQL_ENDPOINT_URL;
                }
            };
        }

    }

}
