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
import org.fairdatateam.fairdatapoint.api.controller.search.SparqlProxyController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.AutoConfigureMockRestServiceServer;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.fairdatateam.fairdatapoint.api.controller.search.SparqlProxyController.*;
import static org.fairdatateam.fairdatapoint.api.controller.search.SparqlQueryValidator.MESSAGE_INVALID_QUERY;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests proxy functionality.
 * Note: If you encounter "AssertionError: No further requests expected", that means a request is *unexpectedly* being
 * forwarded to the mock upstream sparql server. This may happen, for example, if the proxy controller is supposed to
 * deny the request but does not do so.
 */
@ActiveProfiles(Profiles.TESTING)
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
@SpringBootTest(properties = { "instance.sparqlProxyEnabled=true" })
@WithMockUser
public class SparqlProxyControllerTest {

    // note we're now using @URL instead of the custom (RDF4J-based) @ValidIri because the latter ignores spaces
    final static String INVALID_URI = "https://in valid.example.org";

    final static String MALICIOUS_SPARQL_UPDATE = "CLEAR GRAPH ex:";

    final static String TEST_SPARQL_ENDPOINT_URL = "https://triple.store.example.org/sparql";

    // mock ResTemplate to prevent autoconfig issues with MockRestServiceServer (the controller uses RestClient)
    @MockitoBean
    RestTemplate restTemplate;

    private final MockMvcTester mockMvc;

    private final MockRestServiceServer mockBackendSparqlServer;

    private final SparqlProxyController sparqlProxyController;

    private final String defaultGraphUri = "http://default.graph.uri";

    private final String namedGraphUri = "http://named.graph.uri";

    private final String path = "/search/sparql";

    // mock json response body (https://www.w3.org/TR/sparql11-results-json/)
    private final String mockJsonBody = "{\"head\": {\"vars\": []}, \"results\": {\"bindings\": []}}";

    /**
     * Constructor
     */
    @Autowired
    public SparqlProxyControllerTest(
            MockMvcTester mockMvc,
            MockRestServiceServer mockBackendSparqlServer,
            SparqlProxyController sparqlProxyController
    ) {
        this.mockMvc = mockMvc;
        this.mockBackendSparqlServer = mockBackendSparqlServer;
        this.sparqlProxyController = sparqlProxyController;
    }

    @BeforeEach
    public void setup() {
        // override the repository query url
        ReflectionTestUtils.setField(sparqlProxyController, "sparqlEndpointUrl", TEST_SPARQL_ENDPOINT_URL);
    }

    @Test
    @WithAnonymousUser
    public void unauthenticatedRequestsAreDeniedWithoutContactingRemoteSparqlServer() {
        // perform request with url query but without authentication (@WithAnonymousUser)
        MvcTestResult testResult = mockMvc.get()
                .uri(path)
                .queryParam(PARAM_QUERY, EXAMPLE_QUERY)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // should be denied
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

        // request should fail
        assertThat(testResult).hasStatus(HttpStatus.BAD_REQUEST);
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

        // request should fail
        assertThat(testResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(testResult).hasErrorMessage(MESSAGE_UPDATE_DENIED);
    }

    @Test
    public void formPostWithSparqlUpdateContentTypeIsDenied() {
        // execute request with sparql-update content type
        MvcTestResult testResult = mockMvc.post()
                .uri(URI.create(path))
                .contentType(MEDIA_TYPE_SPARQL_UPDATE)
                .content(MALICIOUS_SPARQL_UPDATE)
                .exchange();

        // the request is denied because the content type is not supported (i.e. not in @PostMapping.consumes)
        assertThat(testResult).hasStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    public void getRequestWithMaliciousUrlQueryIsDenied() {
        // execute request with malicious url query (trying to update)
        MvcTestResult testResult = mockMvc.get()
                .uri(path)
                .queryParam(PARAM_QUERY, MALICIOUS_SPARQL_UPDATE)
                .exchange();

        // request should fail
        assertThat(testResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(testResult).hasBodyTextEqualTo(MESSAGE_INVALID_QUERY);
    }

    @Test
    public void formPostWithMaliciousQueryIsDenied() {
        // execute request with malicious form data
        MvcTestResult testResult = mockMvc.post()
                .uri(path)
                .formField(PARAM_QUERY, MALICIOUS_SPARQL_UPDATE)
                .exchange();

        // request should fail
        assertThat(testResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(testResult).hasBodyTextEqualTo(MESSAGE_INVALID_QUERY);
    }

    @Test
    public void rawPostWithMaliciousQueryIsDenied() {
        // execute request with malicious raw query
        MvcTestResult testResult = mockMvc.post()
                .uri(path)
                .contentType(MEDIA_TYPE_SPARQL_QUERY)
                .content(MALICIOUS_SPARQL_UPDATE)
                .exchange();

        // request should fail
        assertThat(testResult).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(testResult).hasBodyTextEqualTo(MESSAGE_INVALID_QUERY);
    }

    @Test
    public void getWithInvalidUriIsDenied() {
        // execute get request with invalid graph uris
        MvcTestResult testResult = mockMvc.get()
                .uri(path)
                .queryParam(PARAM_DEFAULT_GRAPH_URI, INVALID_URI)
                .queryParam(PARAM_NAMED_GRAPH_URI, INVALID_URI)
                .queryParam(PARAM_QUERY, EXAMPLE_QUERY)
                .exchange();

        // request should fail
        assertThat(testResult).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void rawPostWithInvalidUriIsDenied() {
        // execute post request with invalid graph uris
        MvcTestResult testResult = mockMvc.post()
                .uri(path)
                .queryParam(PARAM_DEFAULT_GRAPH_URI, INVALID_URI)
                .queryParam(PARAM_NAMED_GRAPH_URI, INVALID_URI)
                .contentType(MEDIA_TYPE_SPARQL_QUERY)
                .content(EXAMPLE_QUERY)
                .exchange();

        // request should be denied
        assertThat(testResult).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void formPostWithInvalidUriIsDenied() {
        // mock form data
        final MultiValueMap<String, String> formData = MultiValueMap.fromSingleValue(Map.of(
                PARAM_QUERY, EXAMPLE_QUERY,
                PARAM_DEFAULT_GRAPH_URI, INVALID_URI,
                PARAM_NAMED_GRAPH_URI, INVALID_URI
        ));

        // execute post request with invalid graph uris
        MvcTestResult testResult = mockMvc.post().uri(path).formFields(formData).exchange();

        // request should be denied
        assertThat(testResult).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void proxyForwardingWorksForGetRequests() {
        // configure mock server for remote SPARQL endpoint
        this.mockBackendSparqlServer
                // startsWith is required, otherwise it will expect a url without (query) parameters
                .expect(requestTo(startsWith(TEST_SPARQL_ENDPOINT_URL)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess().body(mockJsonBody));

        // execute request with url query
        MvcTestResult testResult = mockMvc.get()
                .uri(path)
                .queryParam(PARAM_QUERY, EXAMPLE_QUERY)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();

        // check that mock server has received expected requests
        // (for some reason this hides exceptions from the RestClient, so comment out to debug)
        mockBackendSparqlServer.verify();

        // verify that proxy returns json response body
        assertThat(testResult).hasStatusOk();
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

        // execute request with raw query in body and graph uris in url
        MvcTestResult testResult = mockMvc.post()
                .uri(path)
                .queryParam(PARAM_DEFAULT_GRAPH_URI, defaultGraphUri)
                .queryParam(PARAM_NAMED_GRAPH_URI, namedGraphUri)
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
