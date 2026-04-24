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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fairdatateam.fairdatapoint.WebIntegrationTest;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.fairdatateam.fairdatapoint.entity.search.SparqlQueryFull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("POST /search/sparql")
public class TestSearchSparqlController extends WebIntegrationTest {

    private final URI url = URI.create("/search/sparql");

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private final String querySelectAll = "SELECT * WHERE { ?s ?p ?o }";

    @Test
    public void postSparqlUnauthenticated() throws JsonProcessingException {
        // prepare request
        SparqlQueryFull sparqlQueryFull = new SparqlQueryFull(querySelectAll, null, null);
        RequestEntity<?> request = RequestEntity
                .post(url)
                .accept(MediaType.APPLICATION_JSON)
                .body(sparqlQueryFull);

        // perform
        ResponseEntity<String> response = client.exchange(request, String.class);

        // evaluate
        // TODO: this should actually be HttpStatus.UNAUTHORIZED, but FDP returns the wrong status code (see #704)
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        HashMap<String, Object> responseBodyMap = jsonMapper.readValue(response.getBody(), new TypeReference<>() {
        });
        assertTrue(responseBodyMap.containsKey("error"));
    }

    @ParameterizedTest
    @ValueSource(strings = { MediaType.APPLICATION_JSON_VALUE, "*/*" })
    public void postSparqlSelectAll(String acceptHeader) throws JsonProcessingException {
        // prepare request
        SparqlQueryFull sparqlQueryFull = new SparqlQueryFull(querySelectAll, null, null);
        RequestEntity<SparqlQueryFull> request = RequestEntity
                .post(url)
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .accept(MediaType.valueOf(acceptHeader))
                .contentType(MediaType.APPLICATION_JSON)
                .body(sparqlQueryFull);

        // perform
        ResponseEntity<String> response = client.exchange(request, String.class);

        // evaluate
        assertEquals(HttpStatus.OK, response.getStatusCode());
        HashMap<String, Object> responseBodyMap = jsonMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        // expected SPARQL SELECT result structure: https://www.w3.org/TR/sparql11-results-json/
        assertEquals(Set.of("head", "results"), responseBodyMap.keySet());
        if (responseBodyMap.get("results") instanceof HashMap<?, ?> results) {
            assertEquals(Set.of("bindings"), results.keySet());
        }
    }

    @Test
    public void postSparqlAskAny() throws JsonProcessingException {
        // prepare request
        SparqlQueryFull sparqlQueryFull = new SparqlQueryFull("ASK { ?s ?p ?o }", null, null);
        RequestEntity<?> request = RequestEntity
                .post(url)
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(sparqlQueryFull);

        // perform
        ResponseEntity<String> response = client.exchange(request, String.class);

        // evaluate
        assertEquals(HttpStatus.OK, response.getStatusCode());
        HashMap<String, Object> responseBodyMap = jsonMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        // expected SPARQL ASK result structure: https://www.w3.org/TR/sparql11-results-json/
        assertEquals(Set.of("head", "boolean"), responseBodyMap.keySet());
        if (responseBodyMap.get("boolean") instanceof Boolean bool) {
            assertTrue(bool);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "CONSTRUCT WHERE { ?s a <https://w3id.org/fdp/fdp-o#MetadataService> }",
            "DESCRIBE ?s WHERE { ?s a <https://w3id.org/fdp/fdp-o#MetadataService> }"
    })
    public void postSparqlConstructOrDescribe(String query) throws JsonProcessingException {
        // prepare request
        SparqlQueryFull sparqlQueryFull = new SparqlQueryFull(query, null, null);
        RequestEntity<?> request = RequestEntity
                .post(url)
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                // QueryTypes.CONSTRUCT_OR_DESCRIBE does not support simple JSON, only application/ld+json (or ttl, n3)
                .accept(MediaType.valueOf(RDFFormat.JSONLD.getDefaultMIMEType()))
                .body(sparqlQueryFull);

        // perform
        ResponseEntity<String> response = client.exchange(request, String.class);

        // evaluate
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<HashMap<String, Object>> responseBodyList = jsonMapper.readValue(
                response.getBody(), new TypeReference<>() {}
        );
        assertFalse(responseBodyList.isEmpty());
    }


    /**
     * Verify that <a href="https://www.w3.org/TR/sparql11-update/">SPARQL Update</a> operations are disallowed.
     * The <code>SparqlQueryEvaluator</code> implements a whitelist in the <code>QueryTypes</code> enum.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            // https://www.w3.org/TR/sparql11-update/#graphUpdate
            "INSERT DATA { ex:test1 dc:title \"test\" }",
            "INSERT { ?s dc:title ?ol } WHERE { ?s dc:title ?o . BIND( STRLANG(STR(?o), \"en\") AS ?ol ) . }",
            "DELETE DATA { ?s dc:title ?o } WHERE { ?s dc:title ?o }",
            "DELETE WHERE { ?s dc:title ?o }",
            "LOAD dc:",
            "CLEAR GRAPH ex:",
            // https://www.w3.org/TR/sparql11-update/#graphManagement
            "CREATE GRAPH ex:",
            "DROP GRAPH ex:",
            "COPY DEFAULT TO GRAPH ex:",
            "MOVE DEFAULT TO GRAPH ex:",
            "ADD DEFAULT TO GRAPH ex:"
    })
    public void postSparqlUpdateDenied(String update) throws JsonProcessingException {
        // common prefixes (part of prologue in sparql grammar)
        final String prologue = """
                PREFIX dc: <http://purl.org/dc/terms/>
                PREFIX ex: <http://example.org/>
                """;

        // prepare request
        SparqlQueryFull sparqlQueryFull = new SparqlQueryFull(prologue + update, null, null);
        RequestEntity<?> request = RequestEntity
                .post(url)
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(sparqlQueryFull);

        // perform
        ResponseEntity<String> response = client.exchange(request, String.class);

        // SPARQL Update operations should always be denied
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
