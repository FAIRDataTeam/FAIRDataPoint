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
package org.fairdatateam.fairdatapoint.rdf.metadata;

import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Regression tests for the triple store outage scenario: a query that fails while it is being
 * evaluated (e.g. the store becomes unreachable mid-request) must be reported as a repository
 * failure, not left as an unchecked exception that gets misclassified further up the stack.
 */
@ExtendWith(MockitoExtension.class)
public class GenericMetadataRdfRepositoryFailureTest {

    @Mock
    private Repository repository;

    @Mock
    private RepositoryConnection connection;

    @Mock
    private TupleQuery query;

    private GenericMetadataRdfRepository metadataRepository;

    @BeforeEach
    public void setUp() {
        metadataRepository = new GenericMetadataRdfRepository(new ConcurrentMapCacheManager(), repository);
    }

    @Test
    public void runSparqlQueryWrapsQueryEvaluationExceptionAsRepositoryException() {
        // GIVEN: the triple store fails while the query is being evaluated
        when(repository.getConnection()).thenReturn(connection);
        when(connection.prepareTupleQuery(anyString())).thenReturn(query);
        when(query.evaluate()).thenThrow(new QueryEvaluationException("Connection refused"));

        // WHEN/THEN:
        assertThrows(
                MetadataRdfRepositoryException.class,
                () -> metadataRepository.runSparqlQuery("SELECT * WHERE { ?s ?p ?o }", null)
        );
    }
}
