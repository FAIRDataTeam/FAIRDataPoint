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
package org.fairdatateam.fairdatapoint.api.controller.search;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Provides methods for cleaning request and response headers in a SPARQL proxy endpoint
 */
@Service
public class SparqlProxyHeaderCleaner {

    public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";

    // standard hop-by-hop headers mentioned in rfc2616
    private static final List<String> HOP_BY_HOP_HEADERS = List.of(
            "Keep-Alive",
            HttpHeaders.PROXY_AUTHENTICATE,
            HttpHeaders.PROXY_AUTHORIZATION,
            HttpHeaders.TE,
            HttpHeaders.TRAILER,
            HttpHeaders.TRANSFER_ENCODING,
            HttpHeaders.UPGRADE
    );

    @Value("${openapi.title} ${openapi.version}")
    private String headerServer;

    /**
     * Getter for hop-by-hop headers which need to be removed by a proxy.
     */
    public static List<String> getHopByHopHeaders() {
        return HOP_BY_HOP_HEADERS;
    }

    /**
     * Returns a function that updates an HttpHeaders object based on the headers from a request.
     * To be used as input for <code>RestClient.*.headers()</code> methods.
     */
    Consumer<HttpHeaders> cleanRequestHeadersFactory(HttpServletRequest request, HttpHeaders requestHeaders) {
        // Design note: It seems redundant to have both request and requestHeaders arguments because the headers are
        // already available in the request. However, HttpServletRequest does not provide a simple way to get an
        // HttpHeaders object, whereas the controller does provide one with @RequestHeader. Still, we do also need the
        // HttpServletRequest to get the remote IP address.
        return restRequestHeaders -> {
            // copy all headers
            restRequestHeaders.putAll(requestHeaders);
            // remove any authorization to prevent privilege escalation attempts
            restRequestHeaders.remove(HttpHeaders.AUTHORIZATION);
            // forward the client ip (otherwise the upstream only sees the proxy ip)
            restRequestHeaders.add(HEADER_X_FORWARDED_FOR, request.getRemoteAddr());
        };
    }

    /**
     * Extracts headers from response and removes the ones that should not be forwarded,
     * such as hop-by-hop headers. These must be listed in the Connection header (see rfc9110 7.6.1).
     */
    private HttpHeaders cleanResponseHeaders(RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response) {
        // copy all headers from the response
        final HttpHeaders headers = new HttpHeaders();
        headers.putAll(response.getHeaders());
        // remove all headers listed in the "Connection" header
        headers.getConnection().forEach(headers::remove);
        headers.remove(HttpHeaders.CONNECTION);
        // explicitly remove hop-by-hop headers (although Connection should list them too)
        HOP_BY_HOP_HEADERS.forEach(headers::remove);
        // rewrite the server header to hide the type of backend triple store
        headers.set(HttpHeaders.SERVER, headerServer);
        return headers;
    }

    /**
     * Modifies the response from RestClient before returning it from the controller.
     * To be used as input for <code>RestClient.*.exchange()</code> calls.
     */
    ResponseEntity<byte[]> cleanResponse(
            HttpRequest restRequest, RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse restResponse
    ) throws IOException {
        return ResponseEntity
                .status(restResponse.getStatusCode())
                .headers(cleanResponseHeaders(restResponse))
                .body(restResponse.getBody().readAllBytes());
    }

}
