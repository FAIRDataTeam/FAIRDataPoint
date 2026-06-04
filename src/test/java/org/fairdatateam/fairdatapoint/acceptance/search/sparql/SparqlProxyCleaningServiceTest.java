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

import jakarta.servlet.http.HttpServletRequest;
import org.fairdatateam.fairdatapoint.api.controller.search.SparqlProxyCleaningService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig(SparqlProxyCleaningService.class)
@TestPropertySource(properties = {"openapi.title=Test API", "openapi.version=1.0"})
public class SparqlProxyCleaningServiceTest {

    private final SparqlProxyCleaningService cleaningService;

    /**
     * Constructor
     */
    @Autowired
    public SparqlProxyCleaningServiceTest(SparqlProxyCleaningService cleaningService) {
        this.cleaningService = cleaningService;
    }

    @Test
    public void cleansRequestHeaders() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final String remoteIP = "1.2.3.4";
        // headers for the incoming request from a remote client
        HttpHeaders requestHeadersIncoming = new HttpHeaders();
        requestHeadersIncoming.setAccept(List.of(MediaType.APPLICATION_JSON));
        requestHeadersIncoming.setBearerAuth("dummy-token");
        MockHttpServletRequest requestIncoming = new MockHttpServletRequest();
        requestIncoming.setRemoteAddr(remoteIP);

        // headers for the outgoing request to the upstream sparql endpoint
        HttpHeaders requestHeadersOutgoing = new HttpHeaders();

        // get access to package-private method for testing
        Method methodUnderTest = SparqlProxyCleaningService.class.getDeclaredMethod(
                "cleanRequestHeadersFactory", HttpServletRequest.class, HttpHeaders.class
        );
        methodUnderTest.setAccessible(true);

        // create callable using factory
        @SuppressWarnings("unchecked")
        Consumer<HttpHeaders> cleanRequestHeaders = (Consumer<HttpHeaders>) methodUnderTest.invoke(
                cleaningService, requestIncoming, requestHeadersIncoming);

        // perform header clean-up
        cleanRequestHeaders.accept(requestHeadersOutgoing);

        // check result
        assertThat(requestHeadersOutgoing.getAccept()).hasSize(1).contains(MediaType.APPLICATION_JSON);
        assertThat(requestHeadersOutgoing.containsKey(HttpHeaders.AUTHORIZATION)).isFalse();
        assertThat(requestHeadersOutgoing.get(SparqlProxyCleaningService.HEADER_X_FORWARDED_FOR))
                .hasSize(1).contains(remoteIP);
    }

    @Test
    public void cleansResponseHeaders() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // mock headers "received" from the upstream sparql endpoint
        final HttpHeaders mockBackendResponseHeaders = new HttpHeaders();

        // add custom header
        final String customHeaderName = "my-custom-header";
        mockBackendResponseHeaders.add(customHeaderName, "dummy");

        // add standard hop-by-hop headers
        final List<String> hopByHopHeaders = SparqlProxyCleaningService.getHopByHopHeaders();
        hopByHopHeaders.forEach(header -> mockBackendResponseHeaders.add(header, "dummy"));

        // add connection header (list is incomplete on purpose, e.g. it does not contain all hop-by-hop headers)
        mockBackendResponseHeaders.put(HttpHeaders.CONNECTION, List.of(customHeaderName, hopByHopHeaders.get(0)));

        // add server header
        final String backendServerHeader = "mock backend sparql server 1.0";
        mockBackendResponseHeaders.add(HttpHeaders.SERVER, backendServerHeader);

        // get access to package-private method for testing
        Method methodUnderTest = SparqlProxyCleaningService.class.getDeclaredMethod(
                "cleanResponse", HttpRequest.class, ClientHttpResponse.class
        );
        methodUnderTest.setAccessible(true);

        try (final MockClientHttpResponse sparqlServerResponse = new MockClientHttpResponse()) {
            // clean the response
            @SuppressWarnings("unchecked")
            ResponseEntity<byte[]> cleanedResponse = (ResponseEntity<byte[]>) methodUnderTest.invoke(
                    cleaningService, new MockClientHttpRequest(), sparqlServerResponse
            );

            // check result
            final HttpHeaders cleanedResponseHeaders = cleanedResponse.getHeaders();
            System.out.println(cleanedResponseHeaders);
            assertThat(cleanedResponseHeaders).doesNotContainKey(customHeaderName);
            hopByHopHeaders.forEach(header -> assertThat(cleanedResponseHeaders).doesNotContainKey(header));
            assertThat(cleanedResponseHeaders).doesNotContainKey(HttpHeaders.CONNECTION);
            assertThat(cleanedResponseHeaders).doesNotContainEntry(HttpHeaders.SERVER, List.of(backendServerHeader));
            assertThat(cleanedResponseHeaders).containsEntry(HttpHeaders.SERVER, List.of("Test API 1.0"));
        }
    }
}
