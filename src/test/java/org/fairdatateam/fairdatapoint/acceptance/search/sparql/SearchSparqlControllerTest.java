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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles(Profiles.TESTING)
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@SpringBootTest
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
    public void simpleSelectQueryWorksViaGet() {
        // configure mock server for remote SPARQL endpoint
        this.mockBackendSparqlServer
                // startsWith is required, otherwise it will expect a url without (query) parameters
                .expect(requestTo(startsWith(TestConfig.TEST_SPARQL_ENDPOINT_URL)))
                // forwarded header should have been added to request
                .andExpect(header("X-Forwarded-For", matchesPattern(".+")))
                // authorization headers should have been removed from request
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
                .andRespond(withSuccess());

        // specify request with url query and normal user (non-admin)
        URI uriWithQuery = UriComponentsBuilder
                .fromPath(path)
                .queryParam("query", querySelectAll)
                .build()
                .toUri();

        // execute request
        MvcTestResult testResult = mockMvc.get().uri(uriWithQuery).accept(MediaType.APPLICATION_JSON).exchange();

        assertThat(testResult).hasStatusOk();

//        // get response
//        ResponseEntity<JsonNode> response = restClient.get()
//                .uri(uriWithQuery)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve()
//                .toEntity(JsonNode.class);
//
//        // evaluate results
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        final MediaType contentType = response.getHeaders().getContentType();
//        assertNotNull(contentType);
//        final MediaType expectedType = MediaType.parseMediaType("application/sparql-results+json");
//        assertTrue(contentType.equalsTypeAndSubtype(expectedType));
//        final JsonNode body = response.getBody();
//        assertNotNull(body);
//        // https://www.w3.org/TR/sparql11-results-json/
//        assertTrue(body.has("head"));
//        assertTrue(body.has("results"));
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
