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
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.NoneNestedConditions;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Slf4j
@Tag(name = "Search")
@Conditional(SearchSparqlController.OnExternalTripleStore.class)
@RestController
public class SearchSparqlController {

    @Value("${openapi.title} ${openapi.version}")
    private String serverHeader;

    private final Repository rdf4jRepository;

    private final RestClient restClient;

    /**
     * Constructor
     */
    public SearchSparqlController(Repository rdf4jRepository, RestClient restClient) {
        this.rdf4jRepository = rdf4jRepository;
        this.restClient = restClient;
    }

    /**
     * Returns the url of the backend triple store's SPARQL endpoint, obtained from the RDF4J repository.
     */
    private String determineSparqlEndpointUrl() {
        final String sparqlEndpointUrl;
        if (rdf4jRepository instanceof HTTPRepository httpRepository) {
            sparqlEndpointUrl = httpRepository.getRepositoryURL();
        }
        else if (rdf4jRepository instanceof SPARQLRepository sparqlRepository) {
            // toString returns the repository's queryEndpointUrl
            sparqlEndpointUrl = sparqlRepository.toString();
        }
        else {
            throw new UnsupportedOperationException(
                    "The SPARQL proxy endpoint is only available for external triple stores");
        }
        return sparqlEndpointUrl;
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
                .fromUriString(determineSparqlEndpointUrl())
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

    /**
     * Custom condition returns true only when an external triple store is configured.
     * That is, the repository type is not "in-memory" and not "native" (file system).
     */
    static class OnExternalTripleStore extends NoneNestedConditions {

        OnExternalTripleStore() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnProperty(prefix = "repository", name = "type", havingValue = "1")
        static class OnInMemoryType {
        }

        @ConditionalOnProperty(prefix = "repository", name = "type", havingValue = "2")
        static class OnNativeType {
        }

    }
}
