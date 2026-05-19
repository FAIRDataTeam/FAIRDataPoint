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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.fairdatateam.fairdatapoint.config.properties.RepositoryProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * This controller depends on a SPARQL endpoint provided by an external triple store.
 * For this reason it is disabled when using an in-memory or native (file system) triple store.
 */
@Slf4j
@Tag(name = "Search")
@ConditionalOnExpression("'${repository.type:}' != '1' and '${repository.type:}' != '2'")
@RestController
public class SearchSparqlController {

    @Value("${openapi.title} ${openapi.version}")
    private String serverHeader;

    private final RestClient restClient;

    private final String sparqlEndpointUrl;

    /**
     * Constructor
     */
    public SearchSparqlController(RepositoryProperties graphRepositoryProperties, RestClient restClient) {
        this.restClient = restClient;
        this.sparqlEndpointUrl = graphRepositoryProperties.getUrl();
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
        // explicitly remove standard hop-by-hop headers mentioned in rfc2616 (although Connection should list them too)
        headers.remove("Keep-Alive");
        headers.remove(HttpHeaders.PROXY_AUTHENTICATE);
        headers.remove(HttpHeaders.PROXY_AUTHORIZATION);
        headers.remove(HttpHeaders.TE);
        headers.remove(HttpHeaders.TRAILER);
        headers.remove(HttpHeaders.TRANSFER_ENCODING);
        headers.remove(HttpHeaders.UPGRADE);
        // rewrite the server header to hide the type of backend triple store
        headers.set(HttpHeaders.SERVER, serverHeader);
        return headers;
    }

    /**
     * Proxy for the triple store SPARQL endpoint.
     * Makes an unauthenticated request to the triple store SPARQL endpoint and returns the response unchanged,
     * except for headers that should not be forwarded.
     * The triple store SPARQL endpoint is expected to comply with the
     * <a href="https://www.w3.org/TR/sparql11-protocol/">SPARQL protocol</a>.
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search/sparql")
    public ResponseEntity<byte[]> proxySparqlEndpoint(
            HttpServletRequest request,
            @RequestHeader HttpHeaders requestHeaders,
            // request parameters defined in SPARQL protocol
            @RequestParam(name = "query") String query,
            @RequestParam(name = "default-graph-uri", required = false) List<String> defaultGraphUri,
            @RequestParam(name = "named-graph-uri", required = false) List<String> namedGraphUri
    ) {
        // add query parameters
        log.info("here's the query: {}", query);
        final URI uriWithQuery = UriComponentsBuilder
                .fromUriString(sparqlEndpointUrl)
                // the query is automatically encoded, because it contains illegal characters, but the uris are not
                .queryParam("query", query)
                .queryParamIfPresent("default-graph-uri", Optional.ofNullable(defaultGraphUri))
                .queryParamIfPresent("named-graph-uri", Optional.ofNullable(namedGraphUri))
                .build()
                // convert to URI (instead of String) to prevent RestClient from trying to do template expansion
                // on the sparql query, e.g. {?s ?p ?o}
                .toUri();
        log.info("SPARQL URI query: {}", uriWithQuery);
        // execute request
        return restClient.get()
                .uri(uriWithQuery)
                .headers(restHeaders -> {
                    // copy all headers and forward the client ip (otherwise the upstream only sees the proxy ip)
                    restHeaders.putAll(requestHeaders);
                    restHeaders.add("X-Forwarded-For", request.getRemoteAddr());
                })
                .exchange((restRequest, restResponse) -> {
                            return ResponseEntity
                                    .status(restResponse.getStatusCode())
                                    .headers(cleanResponseHeaders(restResponse))
                                    .body(restResponse.getBody().readAllBytes());
                        }
                );
    }

}
