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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fairdatateam.fairdatapoint.api.controller.search.SearchSparqlController.*;
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

    final static String MALICIOUS_SPARQL_UPDATE = "CLEAR GRAPH ex:";

    final static String TEST_SPARQL_ENDPOINT_URL = "https://triple.store.example.org/sparql";

    // mock ResTemplate to prevent autoconfig issues with MockRestServiceServer (the controller uses RestClient)
    @MockitoBean
    RestTemplate restTemplate;

    private final MockMvcTester mockMvc;

    private final MockRestServiceServer mockBackendSparqlServer;

    private final SearchSparqlController searchSparqlController;

    private final String defaultGraphUri = "http://default.graph.uri";

    private final String namedGraphUri = "http://named.graph.uri";

    private final String path = "/search/sparql";

    // mock json response body (https://www.w3.org/TR/sparql11-results-json/)
    private final String mockJsonBody = "{\"head\": {\"vars\": []}, \"results\": {\"bindings\": []}}";

    /**
     * Constructor
     */
    @Autowired
    public SearchSparqlControllerTest(
            MockMvcTester mockMvc,
            MockRestServiceServer mockBackendSparqlServer,
            SearchSparqlController searchSparqlController
    ) {
        this.mockMvc = mockMvc;
        this.mockBackendSparqlServer = mockBackendSparqlServer;
        this.searchSparqlController = searchSparqlController;
    }

    @BeforeEach
    public void setup() {
        // override the repository query url
        ReflectionTestUtils.setField(searchSparqlController, "sparqlEndpointUrl", TEST_SPARQL_ENDPOINT_URL);
    }

    @Test
    @WithAnonymousUser
    public void unauthenticatedRequestsAreDeniedWithoutContactingRemoteSparqlServer() {
        // specify request with url query, but without authentication
        URI uriWithQuery = UriComponentsBuilder
                .fromPath(path)
                .queryParam("query", EXAMPLE_QUERY)
                .build()
                .toUri();

        MvcTestResult testResult = mockMvc.get().uri(uriWithQuery).accept(MediaType.APPLICATION_JSON).exchange();

        assertThat(testResult).hasStatus(HttpStatus.FORBIDDEN);
    }


    /**
     * The <a href="https://www.w3.org/TR/sparql11-protocol/#update-operation">SPARQL protocol</a> requires that update
     * operations are done either via POST with content-type "application/x-www-form-urlencoded" and an "update" field,
     * or via POST with content-type "application/sparql-update" and a raw SPARQL update command string.
     */
    @Test
    public void formPostWithUpdateFieldIsIgnored() {
        // mock form data with "update" field, no "query" field (which is required)
        final MultiValueMap<String, String> formData = MultiValueMap.fromSingleValue(Map.of(
                PARAM_UPDATE, MALICIOUS_SPARQL_UPDATE
        ));

        // execute request
        MvcTestResult testResult = mockMvc.post().uri(URI.create(path)).formFields(formData).exchange();

        // check response headers
        assertThat(testResult).hasStatus(HttpStatus.BAD_REQUEST);

        // verify that proxy returns json response body
        assertThat(testResult).hasErrorMessage("Required parameter 'query' is not present.");
    }

    @Test
    public void formPostWithUpdateAndQueryFieldIsDenied() {
        // mock form data with "update" field and a valid "query" field
        final MultiValueMap<String, String> formData = MultiValueMap.fromSingleValue(Map.of(
                PARAM_UPDATE, MALICIOUS_SPARQL_UPDATE,
                PARAM_QUERY, EXAMPLE_QUERY
        ));

        // execute request
        MvcTestResult testResult = mockMvc.post().uri(URI.create(path)).formFields(formData).exchange();

        // check response headers
        assertThat(testResult).hasStatus(HttpStatus.BAD_REQUEST);

        // verify that proxy returns json response body
        assertThat(testResult).hasErrorMessage(MESSAGE_UPDATE_DENIED);
    }

    @Test
    public void formPostWithSparqlUpdateContentTypeIsDenied() {
        // execute request sparql-update content type
        MvcTestResult testResult = mockMvc.post()
                .uri(URI.create(path))
                .contentType(MEDIA_TYPE_SPARQL_UPDATE)
                .content(MALICIOUS_SPARQL_UPDATE)
                .exchange();

        // the request is denied because the content type is not supported (i.e. not in @PostMapping.consumes)
        assertThat(testResult).hasStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
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

        // configure mock server for remote SPARQL endpoint
        this.mockBackendSparqlServer
                // startsWith is required, otherwise it will expect a url without (query) parameters
                .expect(requestTo(startsWith(TEST_SPARQL_ENDPOINT_URL)))
                .andExpect(method(HttpMethod.GET))
                // forwarded header should have been added to request
                .andExpect(header("X-Forwarded-For", matchesPattern(".+")))
                // authorization headers should have been removed from request
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
                .andRespond(withSuccess().headers(mockBackendResponseHeaders).body(mockJsonBody));

        // specify request with url query and normal user (non-admin)
        URI uriWithQuery = UriComponentsBuilder
                .fromPath(path)
                .queryParam(PARAM_QUERY, EXAMPLE_QUERY)
                .build()
                .toUri();

        // execute request
        MvcTestResult testResult = mockMvc.get()
                .uri(uriWithQuery)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "dummy")
                .exchange();

        // check that mock server has received expected requests
        // (for some reason this hides exceptions from the RestClient, so comment out to debug)
        mockBackendSparqlServer.verify();

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

    @Test
    public void proxyForwardingWorksForFormPostRequests() {
        // mock form data
        final MultiValueMap<String, String> formData = MultiValueMap.fromSingleValue(Map.of(
                PARAM_QUERY, EXAMPLE_QUERY,
                PARAM_DEFAULT_GRAPH_URI, defaultGraphUri,
                PARAM_NAMED_GRAPH_URI, namedGraphUri
        ));

        // configure mock server for remote SPARQL endpoint
        this.mockBackendSparqlServer
                .expect(requestTo(TEST_SPARQL_ENDPOINT_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED))
                // form data from original request must arrive at server unaltered (except for empty lists)
                .andExpect(content().formData(formData))
                .andRespond(withSuccess().body(mockJsonBody));

        // execute request
        MvcTestResult testResult = mockMvc.post()
                .uri(URI.create(path))
                .accept(MediaType.APPLICATION_JSON)
                // note that empty list values in formData are not included in the actual form data
                .formFields(formData)
                .exchange();

        // check that mock server has received expected requests
        // (for some reason this hides exceptions from the RestClient, so comment out to debug)
        mockBackendSparqlServer.verify();

        // check response headers
        assertThat(testResult).hasStatusOk();

        // verify that proxy returns json response body
        assertThat(testResult).bodyJson().hasPath("head.vars").hasPath("results.bindings");
    }

    @Test
    public void proxyForwardingWorksForRawPostRequests() {
        // configure mock server for remote SPARQL endpoint
        this.mockBackendSparqlServer
                .expect(requestTo(startsWith(TEST_SPARQL_ENDPOINT_URL)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentTypeCompatibleWith(MEDIA_TYPE_SPARQL_QUERY))
                .andExpect(queryParam(PARAM_DEFAULT_GRAPH_URI, defaultGraphUri))
                .andExpect(queryParam(PARAM_NAMED_GRAPH_URI, namedGraphUri))
                .andExpect(content().string(EXAMPLE_QUERY))
                .andRespond(withSuccess().body(mockJsonBody));

        // specify request with url query and normal user (non-admin)
        URI uriWithQuery = UriComponentsBuilder
                .fromPath(path)
                .queryParam(PARAM_DEFAULT_GRAPH_URI, defaultGraphUri)
                .queryParam(PARAM_NAMED_GRAPH_URI, namedGraphUri)
                .build()
                .toUri();

        // execute request
        MvcTestResult testResult = mockMvc.post()
                .uri(uriWithQuery)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MEDIA_TYPE_SPARQL_QUERY)
                .content(EXAMPLE_QUERY)
                .exchange();

        // check that mock server has received expected requests
        // (for some reason this hides exceptions from the RestClient, so comment out to debug)
        mockBackendSparqlServer.verify();

        // check response headers
        assertThat(testResult).hasStatusOk();

        // verify that proxy returns json response body
        assertThat(testResult).bodyJson().hasPath("head.vars").hasPath("results.bindings");

    }
}
