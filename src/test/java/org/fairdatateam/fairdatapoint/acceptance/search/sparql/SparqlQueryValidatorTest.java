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


import org.fairdatateam.fairdatapoint.api.controller.search.SparqlQueryValidator;
import org.fairdatateam.fairdatapoint.entity.exception.ValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class SparqlQueryValidatorTest {

    // common prefixes (part of "prologue" in sparql grammar)
    final static String PROLOGUE = """
            PREFIX dc: <http://purl.org/dc/terms/>
            PREFIX ex: <http://example.org/>
            PREFIX foaf: <http://xmlns.com/foaf/0.1/>
            PREFIX vcard: <http://www.w3.org/2001/vcard-rdf/3.0#>
            """;

    /**
     * Verify that <a href="https://www.w3.org/TR/sparql11-query/">SPARQL query</a> operations *are* allowed
     */
    @ParameterizedTest
    @ValueSource(strings = {
            // some examples from https://www.w3.org/TR/sparql11-query
            "ASK { ?x foaf:name \"Alice\" }",
            "CONSTRUCT { <http://example.org/person#Alice> vcard:FN ?name } WHERE { ?x foaf:name ?name }",
            "DESCRIBE ?x WHERE { ?x foaf:mbox <mailto:alice@org> }",
            "SELECT * WHERE {?s ?p ?o}"
    })
    public void queryOperationsAreValid(String query) {
        // validation should succeed
        assertDoesNotThrow(() -> SparqlQueryValidator.validate(PROLOGUE + query));
    }

    /**
     * Verify that <a href="https://www.w3.org/TR/sparql11-update/">SPARQL Update</a> operations are *not* allowed
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
    public void updateOperationsAreNotValid(String update) {
        // common prefixes (part of prologue in sparql grammar)
        final String prologue = """
                PREFIX dc: <http://purl.org/dc/terms/>
                PREFIX ex: <http://example.org/>
                """;

        // validation should fail because these are SPARQL update operations instead of SPARQL query operations
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> SparqlQueryValidator.validate(PROLOGUE + update)
        );
        assertEquals(SparqlQueryValidator.MESSAGE_INVALID_QUERY, exception.getMessage());
    }

}
