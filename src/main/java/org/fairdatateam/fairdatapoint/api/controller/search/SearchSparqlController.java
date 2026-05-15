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
// This code is mostly copied from rdf4j spring-boot-sparql-web, with some customizations:
//
// https://github.com/eclipse-rdf4j/rdf4j/blob/main/spring-components/spring-boot-sparql-web
//
// Copyright (c) 2021 Eclipse RDF4J contributors.
//
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Distribution License v1.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/org/documents/edl-v10.php.
//
// SPDX-License-Identifier: BSD-3-Clause

package org.fairdatateam.fairdatapoint.api.controller.search;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.http.server.readonly.sparql.EvaluateResult;
import org.eclipse.rdf4j.http.server.readonly.sparql.SparqlQueryEvaluator;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Tag(name = "Search")
@RestController
public class SearchSparqlController {

    @Value("${openapi.title} ${openapi.version}")
    private String serverHeader;

    private static final String[] ALL_GRAPHS = {};

    private static final String JSON_MEDIA_TYPES = "application/json, application/ld+json";

    private final Repository rdf4jRepository;

    private final SparqlQueryEvaluator sparqlQueryEvaluator;

    private final RestClient restClient;

    /**
     * Constructor
     */
    public SearchSparqlController(
            Repository rdf4jRepository, RestClient restClient, SparqlQueryEvaluator sparqlQueryEvaluator
    ) {
        this.rdf4jRepository = rdf4jRepository;
        this.restClient = restClient;
        this.sparqlQueryEvaluator = sparqlQueryEvaluator;
    }

    /**
     * Returns the url of the backend triple store's SPARQL endpoint, obtained from the RDF4J repository.
     */
    private String determineSparqlEndpointUrl() {
        final String sparqlEndpointUrl;
        if (rdf4jRepository instanceof HTTPRepository httpRepository) {
            sparqlEndpointUrl = httpRepository.getRepositoryURL();
        } else if (rdf4jRepository instanceof SPARQLRepository sparqlRepository) {
            // toString returns the repository's queryEndpointUrl
            sparqlEndpointUrl = sparqlRepository.toString();
        } else {
            throw new UnsupportedOperationException(
                    "The SPARQL proxy endpoint is only available for external triple stores");
        }
        log.info("Backend SPARQL endpoint URL: {}", sparqlEndpointUrl);
        return sparqlEndpointUrl;
    }

    /**
     * Extracts headers from response and removes the ones that should not be forwarded,
     * such as hop-by-hop headers. These must be listed in the Connection header (see rfc9110 7.6.1).
     */
    private HttpHeaders cleanResponseHeaders(RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse response) {
        // copy all headers from the response
        HttpHeaders headers = new HttpHeaders();
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
     * Makes an unauthenticated request to the triple store SPARQL endpoint, and returns the response unchanged,
     * except for headers that should not be forwarded.
     */
    @GetMapping("/sparql")
    public ResponseEntity<byte[]> proxySparqlEndpoint() throws Exception {
        // todo: pass on the request headers and content
        final String endpointUrl = determineSparqlEndpointUrl();
        return restClient.get()
                .uri(endpointUrl)
                .exchange((request, response) -> ResponseEntity
                        .status(response.getStatusCode())
                        .headers(cleanResponseHeaders(response))
                        .body(response.getBody().readAllBytes())
                );
    }

    /**
     * Allows authenticated users to POST a full SPARQL query.
     * Method body copied from org.eclipse.rdf4j.http.server.readonly.QueryResponder.
     * The "Accept" header is required, and allowable media types depend on the type of query,
     * as defined in <code>org.eclipse.rdf4j.http.server.readonly.sparql.QueryTypes.formats</code>.
     * However, to simplify things, we restrict the allowable media types to JSON and/or JSON-LD.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping(
            path = "/search/sparql",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = { MediaType.APPLICATION_JSON_VALUE, "application/ld+json" }
    )
    public void sparqlPost(
        @RequestHeader(value = HttpHeaders.ACCEPT, defaultValue = JSON_MEDIA_TYPES) String acceptHeader,
        @RequestBody SparqlQuery sparqlQuery,
        HttpServletResponse response
    ) throws IOException {
        // enforce default accept header for wildcard
        final String accept = ("*/*".equals(acceptHeader)) ? JSON_MEDIA_TYPES : acceptHeader;
        try {
            final EvaluateResultHttpResponse result = new EvaluateResultHttpResponse(response);
            sparqlQueryEvaluator.evaluate(
                    result,
                    rdf4jRepository,
                    sparqlQuery.query,
                    accept,
                    toArray(sparqlQuery.defaultGraphUri),
                    toArray(sparqlQuery.namedGraphUri)
            );
        }
        catch (MalformedQueryException | IllegalStateException | IOException exception) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String[] toArray(String graphUri) {
        if (graphUri != null && !graphUri.isEmpty()) {
            return new String[]{graphUri};
        }
        return ALL_GRAPHS;
    }

    /**
     * Encapsulates the {@link HttpServletResponse}.
     * Copied from org.eclipse.rdf4j.http.server.readonly.EvaluateResultHttpResponse.
     */
    protected static class EvaluateResultHttpResponse implements EvaluateResult {

        private final HttpServletResponse response;

        public EvaluateResultHttpResponse(HttpServletResponse response) {
            this.response = response;
        }

        @Override
        public void setContentType(String contentType) {
            response.setContentType(contentType);
        }

        @Override
        public String getContentType() {
            return response.getContentType();
        }

        @Override
        public OutputStream getOutputstream() throws IOException {
            return response.getOutputStream();
        }
    }

    /**
     * Defines the content of the query request body, for JSON deserialization.
     * @param query
     * @param defaultGraphUri
     * @param namedGraphUri
     */
    public record SparqlQuery(String query, String defaultGraphUri, String namedGraphUri) {
    }
}
