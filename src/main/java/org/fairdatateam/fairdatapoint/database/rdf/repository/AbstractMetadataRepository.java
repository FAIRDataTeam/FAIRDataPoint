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
package org.fairdatateam.fairdatapoint.database.rdf.repository;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;

import java.util.*;

import static org.fairdatateam.fairdatapoint.util.ResourceReader.loadResource;

@Slf4j
public abstract class AbstractMetadataRepository implements MetadataRepository {

    private static final String FIND_CHILD_TITLES = "/sparql/findChildTitles.sparql";

    private static final String MSG_ERROR_RESOURCE = "Error retrieving resource: ";
    private static final String MSG_ERROR_URI = "Error retrieving repository URI: ";
    private static final String MSG_ERROR_REMOVE = "Error removing statement";
    private static final String MSG_ERROR_REMOVE_ALL = "Error remove all: ";
    private static final String MSG_ERROR_EXISTS = "Error check statement existence: ";
    private static final String MSG_ERROR_SAVE = "Error storing statements: ";
    private static final String MSG_ERROR_SPARQL_LOAD = "Error reading %s.sparql file (error: %s)";

    private static final String FIELD_CHILD = "child";
    private static final String FIELD_TITLE = "title";

    private final Repository repository;

    /**
     * Constructor (autowired)
     */
    public AbstractMetadataRepository(Repository repository) {
        this.repository = repository;
    }

    public List<Statement> find(IRI context) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            return Iterations.asList(
                    conn.getStatements(null, null, null, context)
            );
        }
        catch (RepositoryException exception) {
            throw new MetadataRepositoryException(MSG_ERROR_RESOURCE + exception.getMessage());
        }
    }

    public Map<String, String> findChildTitles(IRI parent, IRI relation) throws MetadataRepositoryException {
        final Map<String, String> titles = new HashMap<>();
        final List<BindingSet> results = runSparqlQueryFromFile(
                FIND_CHILD_TITLES, Map.of("parent", parent, "relation", relation)
        );
        for (var result : results) {
            final String childUri = result.getValue(FIELD_CHILD).stringValue();
            final String title = result.getValue(FIELD_TITLE).stringValue();
            titles.put(childUri, title);
        }
        return titles;
    }

    public boolean checkExistence(Resource subject, IRI predicate, Value object)
            throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            return conn.hasStatement(subject, predicate, object, false);
        }
        catch (RepositoryException exception) {
            throw new MetadataRepositoryException(MSG_ERROR_EXISTS + exception.getMessage());
        }
    }

    public void save(List<Statement> statements, IRI context) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.add(statements, context);
        }
        catch (RepositoryException exception) {
            throw new MetadataRepositoryException(MSG_ERROR_SAVE + exception.getMessage());
        }
    }

    public void removeAll() throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.clear();
        }
        catch (RepositoryException exception) {
            throw new MetadataRepositoryException(MSG_ERROR_REMOVE_ALL + exception.getMessage());
        }
    }

    public void remove(IRI uri) throws MetadataRepositoryException {
        removeStatement(null, null, null, uri);
    }

    public void removeStatement(Resource subject, IRI predicate, Value object, IRI context)
            throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.remove(subject, predicate, object, context);
        }
        catch (RepositoryException exception) {
            throw new MetadataRepositoryException(MSG_ERROR_REMOVE);
        }
    }

    /**
     * Evaluate a full SPARQL query string. The <code>bindings</code> argument may be used to assign (bind) values
     * to any of the SPARQL query variables.
     * @param queryString string containing a SPARQL query
     * @param bindings map of values to be assigned to SPARQL query variables (or null if there are none)
     * @return list of query results
     */
    public List<BindingSet> runSparqlQuery(
            String queryString, @Nullable Map<String, Value> bindings
    ) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            final TupleQuery query = conn.prepareTupleQuery(queryString);
            if (bindings != null) {
                // assign values to variables defined in the sparql query
                bindings.forEach(query::setBinding);
            }
            return QueryResults.asList(query.evaluate());
        }
        catch (RepositoryException exception) {
            throw new MetadataRepositoryException(MSG_ERROR_URI + exception.getMessage());
        }
    }

    /**
     * Evaluate SPARQL query stored in a file
     * @param queryFilePath path to SPARQL query file
     * @param bindings map of values to be assigned to SPARQL query variables (or null if there are none)
     * @return list of query results
     */
    public List<BindingSet> runSparqlQueryFromFile(
            String queryFilePath, @Nullable Map<String, Value> bindings
    ) throws MetadataRepositoryException {
        final String queryString = loadResource(queryFilePath);
        return runSparqlQuery(queryString, bindings);
    }
}
