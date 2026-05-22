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
import org.fairdatateam.fairdatapoint.Profiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles(Profiles.TESTING)
@WebMvcTest
public class SearchSparqlControllerTest {

    public static final String NIKOLA_TOKEN = "Bearer eyJhbGciOiJIUzUxMiJ9" +
            ".eyJzdWIiOiJiNWI5MmM2OS01ZWQ5LTQwNTQtOTU0ZC0wMTIxYzI5YjY4MDAiLCJpYXQiOjE2MjA4Mzg3MDgsImV4cCI6MjUzMzcwNzY4NDYxfQ" +
            ".U3mPUE0fREeVlresvl6uHR-aTj3ATFYn7CsAJ0cyOhqvaICTvURewF8QPfw2WVZ4GGc8Ej46BqHI9rpwKqRxpQ";



    private RestClient restClient;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setup() {
        // see examples at main/spring-test/src/test/java/org/springframework/test/web/client/samples/SampleTests.java
        // https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-client.html
        RestClient.Builder clientBuilder = RestClient.builder();
        this.mockServer = MockRestServiceServer.bindTo(clientBuilder).ignoreExpectOrder(true).build();
        this.restClient = clientBuilder.build();
    }

    private final String path = "/search/sparql";

    private final String querySelectAll = "SELECT * WHERE { ?s ?p ?o }";

    /**
     * Constructor
     */
    public SearchSparqlControllerTest() {
    }



    /**
     * Unauthenticated requests are denied
     */
    @Test
    public void getSparqlUnauthenticated() {
        // specify request with url query, but without authentication
        URI uriWithQuery = UriComponentsBuilder
                .fromPath(path)
                .queryParam("query", querySelectAll)
                .build()
                .toUri();

        // get response
        ResponseEntity<JsonNode> response = restClient.get()
                .uri(uriWithQuery)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(JsonNode.class);

        // evaluate results
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    /**
     * Performs a basic SELECT query via GET request to external triple store, if available.
     */
    @Test
    public void getSparqlAuthenticated() {
        // specify request with url query and normal user (non-admin)
        URI uriWithQuery = UriComponentsBuilder
                .fromPath(path)
                .queryParam("query", querySelectAll)
                .build()
                .toUri();

        // get response
        ResponseEntity<JsonNode> response = restClient.get()
                .uri(uriWithQuery)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .retrieve()
                .toEntity(JsonNode.class);

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
