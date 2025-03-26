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
package nl.dtls.fairdatapoint.database.rdf.repository.common;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.entity.search.SearchFilterValue;
import nl.dtls.fairdatapoint.entity.search.SearchResult;
import nl.dtls.fairdatapoint.entity.search.SearchResultRelation;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Slf4j
public abstract class AbstractMetadataRepository {

    private static final String FIND_ENTITY_BY_LITERAL = "findEntityByLiteral.sparql";
    private static final String FIND_CHILD_TITLES = "findChildTitles.sparql";
    private static final String FIND_OBJECT_FOR_PREDICATE = "findObjectsForPredicate.sparql";

    private static final String MSG_ERROR_RESOURCE = "Error retrieving resource: ";
    private static final String MSG_ERROR_URI = "Error retrieving repository URI: ";
    private static final String MSG_ERROR_REMOVE = "Error removing statement";
    private static final String MSG_ERROR_REMOVE_ALL = "Error remove all: ";
    private static final String MSG_ERROR_EXISTS = "Error check statement existence: ";
    private static final String MSG_ERROR_SAVE = "Error storing statements: ";
    private static final String MSG_ERROR_SPARQL_LOAD = "Error reading %s.sparql file (error: %s)";

    private static final String FIELD_VALUE = "value";
    private static final String FIELD_LABEL = "label";
    private static final String FIELD_CHILD = "child";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_ENTITY = "entity";
    private static final String FIELD_TYPE = "rdfType";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_REL_PRED = "relationPredicate";
    private static final String FIELD_REL_OBJ = "relationObject";

    @Autowired
    private Repository repository;

    protected Repository getRepository() {
        return repository;
    }

    public List<Resource> findResources() throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {

            return Iterations.asList(
                    conn.getContextIDs()
            );
        }
        catch (RepositoryException exception) {
            throw new MetadataRepositoryException(MSG_ERROR_RESOURCE + exception.getMessage());
        }
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

    public List<SearchResult> findByLiteral(Literal query) throws MetadataRepositoryException {
        return runSparqlQuery(
                FIND_ENTITY_BY_LITERAL,
                AbstractMetadataRepository.class,
                Map.of("query", query)
        )
                .stream()
                .map(item -> toSearchResult(item, true))
                .toList();
    }

    public List<SearchResult> findBySparqlQuery(String query) throws MetadataRepositoryException {
        return runSparqlQuery(query)
                .stream()
                .map(item -> toSearchResult(item, false))
                .toList();
    }

    private SearchResult toSearchResult(BindingSet item, boolean withRelation) {
        SearchResultRelation relation = null;
        if (withRelation) {
            relation = new SearchResultRelation(
                    item.getValue(FIELD_REL_PRED).stringValue(),
                    item.getValue(FIELD_REL_OBJ).stringValue()
            );
        }
        return new SearchResult(
                item.getValue(FIELD_ENTITY).stringValue(),
                item.getValue(FIELD_TYPE).stringValue(),
                item.getValue(FIELD_TITLE).stringValue(),
                ofNullable(item.getValue(FIELD_DESCRIPTION)).map(Value::stringValue).orElse(""),
                relation
        );
    }

    public List<SearchFilterValue> findByFilterPredicate(IRI predicateUri)
            throws MetadataRepositoryException {
        final Map<String, String> values = new HashMap<>();
        runSparqlQuery(
                FIND_OBJECT_FOR_PREDICATE,
                AbstractMetadataRepository.class,
                Map.of("predicate", predicateUri)
        ).forEach(entry -> {
            values.put(
                    entry.getValue(FIELD_VALUE).stringValue(),
                    Optional.ofNullable(entry.getValue(FIELD_LABEL))
                            .map(Value::stringValue)
                            .orElse(null)
            );
        });
        return values
                .entrySet()
                .stream()
                .map(entry -> new SearchFilterValue(entry.getKey(), entry.getValue()))
                .toList();
    }

    public Map<String, String> findChildTitles(IRI parent, IRI relation)
            throws MetadataRepositoryException {
        final Map<String, String> titles = new HashMap<>();

        final List<BindingSet> results = runSparqlQuery(
                FIND_CHILD_TITLES,
                AbstractMetadataRepository.class,
                Map.of(
                        "parent", parent,
                        "relation", relation
                ));

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

    public List<BindingSet> runSparqlQuery(String queryName, Class repositoryType,
                                           Map<String, Value> bindings)
            throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            final String queryString = loadSparqlQuery(queryName, repositoryType);
            final TupleQuery query = conn.prepareTupleQuery(queryString);
            bindings.forEach(query::setBinding);
            return QueryResults.asList(query.evaluate());
        }
        catch (RepositoryException exception) {
            throw new MetadataRepositoryException(MSG_ERROR_URI + exception.getMessage());
        }
        catch (IOException exception) {
            throw new MetadataRepositoryException(format(MSG_ERROR_SPARQL_LOAD, queryName,
                    exception.getMessage()));
        }
    }

    public List<BindingSet> runSparqlQuery(String queryString) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            final TupleQuery query = conn.prepareTupleQuery(queryString);
            return query.evaluate().stream().toList();
        }
        catch (RepositoryException exception) {
            throw new MetadataRepositoryException(MSG_ERROR_URI + exception.getMessage());
        }
    }

    protected String loadSparqlQuery(String queryName, Class repositoryType) throws IOException {
        final URL fileURL = repositoryType.getResource(queryName);
        return Resources.toString(fileURL, Charsets.UTF_8);
    }
}
