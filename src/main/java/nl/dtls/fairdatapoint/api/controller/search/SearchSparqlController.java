/**
 * The MIT License
 * Copyright © 2017 DTL
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

package nl.dtls.fairdatapoint.api.controller.search;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.rdf4j.http.server.readonly.sparql.EvaluateResult;
import org.eclipse.rdf4j.http.server.readonly.sparql.SparqlQueryEvaluator;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.repository.Repository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;

@Tag(name = "Search")
@RestController
public class SearchSparqlController {

    private static final String[] ALL_GRAPHS = {};

    private final Repository rdf4jRepository;

    private final SparqlQueryEvaluator sparqlQueryEvaluator;

    /**
     * Constructor
     */
    public SearchSparqlController(Repository rdf4jRepository, SparqlQueryEvaluator sparqlQueryEvaluator) {
        this.rdf4jRepository = rdf4jRepository;
        this.sparqlQueryEvaluator = sparqlQueryEvaluator;
    }

    /**
     * Allows authenticated users to POST a full SPARQL query.
     * Method body copied from org.eclipse.rdf4j.http.server.readonly.QueryResponder.
     * The "Accept" header is required, and allowable values depend on the type of query,
     * as defined in <code>org.eclipse.rdf4j.http.server.readonly.sparql.QueryTypes.formats</code>.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping(path = "/search/sparql", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sparqlPost(
        @Schema(example = MediaType.APPLICATION_JSON_VALUE)
            @RequestHeader(value = HttpHeaders.ACCEPT) String acceptHeader,
        @RequestBody SparqlQuery sparqlQuery,
        HttpServletResponse response
    ) throws IOException {
        // enforce default accept header for wildcard
        final String accept = ("*/*".equals(acceptHeader)) ? MediaType.APPLICATION_JSON_VALUE : acceptHeader;
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
