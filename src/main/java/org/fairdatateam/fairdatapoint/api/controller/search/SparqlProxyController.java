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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.fairdatateam.fairdatapoint.entity.exception.ResourceNotFoundException;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

/**
 * Acts as a proxy that forwards read-only query requests to a SPARQL endpoint provided by an external triple store.
 * The triple store SPARQL endpoint must comply with the
 * <a href="https://www.w3.org/TR/sparql11-protocol/">SPARQL protocol</a>.
 * The proxy controller performs basic input validation and removes authentication credentials before forwarding
 * requests to the triple store SPARQL endpoint. It also cleans up the headers before returning the response.
 * Returns status <code>404 NOT FOUND</code> when using an in-memory or native (file system) triple store,
 * because those do not provide a SPARQL endpoint.
 * This controller is disabled by default, and can be enabled by setting <code>instance.sparqlProxyEnabled=true</code>.
 */
@ConditionalOnProperty(value = "instance.sparqlProxyEnabled", havingValue = "true")
@Slf4j
@Tag(name = "Search")
@RestController
public class SparqlProxyController {

    public static final String EXAMPLE_QUERY = "SELECT * WHERE { ?s ?p ?o }";

    public static final String MESSAGE_UPDATE_DENIED = "SPARQL update not allowed";

    // request parameters and media types defined in https://www.w3.org/TR/sparql11-protocol/
    public static final String MEDIA_TYPE_SPARQL_QUERY = "application/sparql-query";
    public static final String MEDIA_TYPE_SPARQL_UPDATE = "application/sparql-update";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_DEFAULT_GRAPH_URI = "default-graph-uri";
    public static final String PARAM_NAMED_GRAPH_URI = "named-graph-uri";
    public static final String PARAM_UPDATE = "update";

    private static final String DESCRIPTION_EXPERIMENTAL = """
            [EXPERIMENTAL]
            SPARQL endpoint - Supports SPARQL query operations following the SPARQL protocol.
            Update operations are not allowed.
            This endpoint is not part of the stable API and may change in future releases.
            """;

    private final SparqlProxyCleaningService cleaningService;

    private final RestClient restClient;

    private String sparqlEndpointUrl;

    /**
     * Constructor
     */
    public SparqlProxyController(
            Repository repository, SparqlProxyCleaningService cleaningService, RestClient restClient
    ) {
        this.cleaningService = cleaningService;
        this.restClient = restClient;
        // todo: simplify as part of #824
        if (repository instanceof SPARQLRepository sparqlRepository) {
            this.sparqlEndpointUrl = sparqlRepository.toString();
        }
        else if (repository instanceof HTTPRepository httpRepository) {
            this.sparqlEndpointUrl = httpRepository.getRepositoryURL();
        }
    }

    /**
     * Abort with "resource not found" if there is no upstream SPARQL endpoint
     */
    private void abortIfSparqlEndpointUnavailable() {
        if (sparqlEndpointUrl == null || sparqlEndpointUrl.isEmpty()) {
            throw new ResourceNotFoundException("SPARQL endpoint unavailable");
        }
    }

    /**
     * Handles GET requests with queries in URL parameters.
     */
    @Operation(description = DESCRIPTION_EXPERIMENTAL)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search/sparql")
    public ResponseEntity<byte[]> proxySparqlEndpointGet(
            HttpServletRequest request,
            @RequestHeader HttpHeaders requestHeaders,
            @RequestParam(name = PARAM_QUERY) @Parameter(example = EXAMPLE_QUERY) String query,
            @RequestParam(name = PARAM_DEFAULT_GRAPH_URI, required = false) List<@URL String> defaultGraphUri,
            @RequestParam(name = PARAM_NAMED_GRAPH_URI, required = false) List<@URL String> namedGraphUri
    ) {
        abortIfSparqlEndpointUnavailable();
        SparqlQueryValidator.validate(query);
        // add query parameters
        final URI uriWithQuery = UriComponentsBuilder
                .fromUriString(sparqlEndpointUrl)
                // the query is automatically encoded because it contains illegal characters, but the uris are not
                .queryParam(PARAM_QUERY, query)
                .queryParamIfPresent(PARAM_DEFAULT_GRAPH_URI, Optional.ofNullable(defaultGraphUri))
                .queryParamIfPresent(PARAM_NAMED_GRAPH_URI, Optional.ofNullable(namedGraphUri))
                .build()
                // convert to URI (instead of String) to prevent RestClient from trying to do template expansion
                // on the sparql query, e.g. {?s ?p ?o}
                .toUri();
        log.info("SPARQL URI query: {}", uriWithQuery);
        // send get request to backend sparql endpoint
        return restClient.get()
                .uri(uriWithQuery)
                .headers(cleaningService.cleanRequestHeadersFactory(request, requestHeaders))
                .exchange(cleaningService::cleanResponse);
    }

    /**
     * Handles POST requests using form data (<code>"application/x-www-form-urlencoded"</code>).
     */
    @Operation(description = DESCRIPTION_EXPERIMENTAL)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/search/sparql", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<byte[]> proxySparqlEndpointPostForm(
            HttpServletRequest request,
            @RequestHeader HttpHeaders requestHeaders,
            // bind form data from request body
            @RequestParam(name = PARAM_QUERY) @Parameter(schema = @Schema(example = EXAMPLE_QUERY)) String query,
            @RequestParam(name = PARAM_DEFAULT_GRAPH_URI, required = false) List<@URL String> defaultGraphUri,
            @RequestParam(name = PARAM_NAMED_GRAPH_URI, required = false) List<@URL String> namedGraphUri
    ) {
        abortIfSparqlEndpointUnavailable();
        SparqlQueryValidator.validate(query);
        // abort if request contains a SPARQL update attempt
        if (request.getParameter(PARAM_UPDATE) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MESSAGE_UPDATE_DENIED);
        }
        // @RequestParam is used in the method signature for convenient validation and generation of api docs.
        // However, this means we need to reconstruct the form data before we can forward it to the backend server.
        // (an alternative would be to use `@RequestBody MultiValueMap<String, String> sparqlForm`, combined with
        // custom validation, instead of the individual @RequestParam entries)
        final Map<String, List<String>> sparqlForm = new HashMap<>(3);
        sparqlForm.put(PARAM_QUERY, List.of(query));
        if (defaultGraphUri != null && !defaultGraphUri.isEmpty()) {
            sparqlForm.put(PARAM_DEFAULT_GRAPH_URI, defaultGraphUri);
        }
        if (namedGraphUri != null && !namedGraphUri.isEmpty()) {
            sparqlForm.put(PARAM_NAMED_GRAPH_URI, namedGraphUri);
        }
        // post to backend sparql endpoint
        final URI uri = URI.create(sparqlEndpointUrl);
        return restClient.post()
                .uri(uri)
                .headers(cleaningService.cleanRequestHeadersFactory(request, requestHeaders))
                .body(MultiValueMap.fromMultiValue(sparqlForm))
                .exchange(cleaningService::cleanResponse);
    }

    /**
     * Handles POST requests with raw sparql queries (<code>"application/sparql-query"</code>)
     */
    @Operation(description = DESCRIPTION_EXPERIMENTAL)
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/search/sparql", consumes = MEDIA_TYPE_SPARQL_QUERY)
    public ResponseEntity<byte[]> proxySparqlEndpointPostRaw(
            HttpServletRequest request,
            @RequestHeader HttpHeaders requestHeaders,
            // raw query in body
            @RequestBody @Parameter(schema = @Schema(example = EXAMPLE_QUERY)) String query,
            // graph uris in url parameters
            @RequestParam(name = PARAM_DEFAULT_GRAPH_URI, required = false) List<@URL String> defaultGraphUri,
            @RequestParam(name = PARAM_NAMED_GRAPH_URI, required = false) List<@URL String> namedGraphUri
    ) {
        abortIfSparqlEndpointUnavailable();
        SparqlQueryValidator.validate(query);
        // add query parameters if present
        final URI uriWithQuery = UriComponentsBuilder
                .fromUriString(sparqlEndpointUrl)
                .queryParamIfPresent(PARAM_DEFAULT_GRAPH_URI, Optional.ofNullable(defaultGraphUri))
                .queryParamIfPresent(PARAM_NAMED_GRAPH_URI, Optional.ofNullable(namedGraphUri))
                .build().toUri();
        // post to backend sparql endpoint
        return restClient.post()
                .uri(uriWithQuery)
                .headers(cleaningService.cleanRequestHeadersFactory(request, requestHeaders))
                .body(query)
                .exchange(cleaningService::cleanResponse);
    }
}
