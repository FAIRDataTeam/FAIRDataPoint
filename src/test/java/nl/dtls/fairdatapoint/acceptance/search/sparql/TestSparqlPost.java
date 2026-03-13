package nl.dtls.fairdatapoint.acceptance.search.sparql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.dtls.fairdatapoint.WebIntegrationTest;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("POST /search/sparql")
public class TestSparqlPost extends WebIntegrationTest {

    private final URI url = URI.create("/search/sparql");

    private final ObjectMapper jsonMapper = new ObjectMapper();

    private final String querySelectAll = "SELECT * WHERE { ?s ?p ?o }";

    @Test
    public void sparqlPostUnauthenticated() throws JsonProcessingException {
        // prepare request
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("query", querySelectAll);
        RequestEntity<?> request = RequestEntity
                .post(url)
                .accept(MediaType.APPLICATION_JSON)
                .body(requestBody);

        // perform
        ResponseEntity<String> response = client.exchange(request, String.class);

        // evaluate
        // TODO: this should actually be HttpStatus.UNAUTHORIZED, but FDP returns the wrong status code (see #704)
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        HashMap<String, Object> responseBodyMap = jsonMapper.readValue(response.getBody(), new TypeReference<>() {
        });
        assertTrue(responseBodyMap.containsKey("error"));
    }

    @Test
    public void sparqlPostSelectAll() throws JsonProcessingException {
        // prepare request
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("query", querySelectAll);
        RequestEntity<?> request = RequestEntity
                .post(url)
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(requestBody);

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
    public void sparqlPostAskAny() throws JsonProcessingException {
        // prepare request
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("query", "ASK { ?s ?p ?o }");
        RequestEntity<?> request = RequestEntity
                .post(url)
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(requestBody);

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
            "CONSTRUCT WHERE { ?s a <https://w3id.org/fdp/fdp-o#MetadataService> } ",
            "DESCRIBE ?s WHERE { ?s a <https://w3id.org/fdp/fdp-o#MetadataService> } "
    })
    public void sparqlPostConstructOrDescribe(String query) throws JsonProcessingException {
        // prepare request
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("query", query);
        RequestEntity<?> request = RequestEntity
                .post(url)
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                // QueryTypes.CONSTRUCT_OR_DESCRIBE does not support simple JSON, only application/ld+json (or ttl, n3)
                .accept(MediaType.valueOf(RDFFormat.JSONLD.getDefaultMIMEType()))
                .body(requestBody);

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
     * Verify that <a href="https://www.w3.org/TR/sparql11-update/">SPARQL Update</a> operations are disallowed
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
    public void sparqlPostUpdateDenied(String update) throws JsonProcessingException {
        // common prefixes (part of prologue in sparql grammar)
        final String prologue = """
                PREFIX dc: <http://purl.org/dc/terms/>
                PREFIX ex: <http://example.org/>
                """;

        // prepare request
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("query", prologue + update);
        RequestEntity<?> request = RequestEntity
                .post(url)
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(requestBody);

        // perform
        ResponseEntity<String> response = client.exchange(request, String.class);

        // SPARQL Update operations should always be denied
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

}
