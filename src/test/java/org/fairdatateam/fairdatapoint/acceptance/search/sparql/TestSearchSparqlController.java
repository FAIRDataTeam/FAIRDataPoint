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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fairdatateam.fairdatapoint.WebIntegrationTest;
import org.fairdatateam.fairdatapoint.config.properties.RepositoryProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("POST /search/sparql")
public class TestSearchSparqlController extends WebIntegrationTest {

    private final Environment environment;

    private final URI url = URI.create("/search/sparql");

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private final String querySelectAll = "SELECT * WHERE { ?s ?p ?o }";

    /**
     * Constructor
     */
    @Autowired
    public TestSearchSparqlController(Environment environment) {
        this.environment = environment;
    }

    /**
     * Returns true if an external triple store is configured.
     * For example, set <code>repository.type = 4</code> and <code>repository.graph-db.repository = "fdp-test"</code>.
     */
    boolean externalTripleStoreConfigured() {
        final int repoType = Integer.parseInt(Objects.requireNonNull(environment.getProperty("repository.type")));
        return repoType > RepositoryProperties.TYPE_NATIVE;
    }

    /**
     * Unauthenticated requests are denied
     */
    @Test
    public void getSparqlUnauthenticated() {
        // specify request with url query, but without authentication
        URI uriWithQuery = UriComponentsBuilder
                .fromPath("/search/sparql")
                .queryParam("query", querySelectAll)
                .build()
                .toUri();
        RequestEntity<?> request = RequestEntity.get(uriWithQuery).accept(MediaType.APPLICATION_JSON).build();

        // get response
        ResponseEntity<JsonNode> response = client.exchange(request, JsonNode.class);

        // evaluate results
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    /**
     * Performs a basic SELECT query via GET request to external triple store, if available.
     */
    @Test
//    @EnabledIf("externalTripleStoreConfigured")
    public void getSparqlAuthenticated() {
        // specify request with url query and normal user (non-admin)
        URI uriWithQuery = UriComponentsBuilder
                .fromPath("/search/sparql")
                .queryParam("query", querySelectAll)
                .build()
                .toUri();
        RequestEntity<?> request = RequestEntity
                .get(uriWithQuery)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .build();

        // get response
        ResponseEntity<JsonNode> response = client.exchange(request, JsonNode.class);

        // evaluate results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        final MediaType contentType = response.getHeaders().getContentType();
        assertNotNull(contentType);
        final MediaType expectedType = MediaType.parseMediaType("application/sparql-results+json");
        assertTrue(contentType.equalsTypeAndSubtype(expectedType));
        final JsonNode body = response.getBody();
        assertNotNull(body);
        // https://www.w3.org/TR/sparql11-results-json/
        assertTrue(body.has("head"));
        assertTrue(body.has("results"));
    }
}
