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

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@Slf4j
public abstract class AbstractMetadataRepository {

    private static final String FIND_ENTITY_BY_LITERAL = "findEntityByLiteral.sparql";
    private static final String FIND_CHILD_TITLES = "findChildTitles.sparql";

    @Autowired
    protected Repository repository;

    public List<Resource> findResources() throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {

            return Iterations.asList(
                    conn.getContextIDs()
            );
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Error retrieve resource :" + e.getMessage());
        }
    }

    public List<Statement> find(IRI context) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            return Iterations.asList(
                    conn.getStatements(null, null, null, context)
            );
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Error retrieve resource :" + e.getMessage());
        }
    }

    public List<SearchResult> findByLiteral(Literal query) throws MetadataRepositoryException {
        return runSparqlQuery(FIND_ENTITY_BY_LITERAL, AbstractMetadataRepository.class, Map.of(
                "query", query))
                .stream()
                .map(s -> new SearchResult(
                                s.getValue("entity").stringValue(),
                                s.getValue("rdfType").stringValue(),
                                s.getValue("title").stringValue(),
                                ofNullable(s.getValue("description")).map(Value::stringValue).orElse(""),
                                new SearchResultRelation(
                                        s.getValue("relationPredicate").stringValue(),
                                        s.getValue("relationObject").stringValue())
                        )
                )
                .collect(toList());
    }

    public Map<String, String> findChildTitles(IRI parent, IRI relation) throws MetadataRepositoryException {
        Map<String, String> titles = new HashMap<>();

        var results = runSparqlQuery(FIND_CHILD_TITLES, AbstractMetadataRepository.class, Map.of(
            "parent", parent,
            "relation", relation
        ));

        for (var result : results) {
            var childUri = result.getValue("child").stringValue();
            var title = result.getValue("title").stringValue();
            titles.put(childUri, title);
        }

        return titles;
    }

    public boolean checkExistence(Resource subject, IRI predicate, Value object) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            return conn.hasStatement(subject, predicate, object, false);
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Error check statement existence :" + e.getMessage());
        }
    }

    public void save(List<Statement> statements, IRI context) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.add(statements, context);
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Error storing statements :" + e.getMessage());
        }
    }

    public void removeAll() throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.clear();
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Error remove all :" + e.getMessage());
        }
    }

    public void remove(IRI uri) throws MetadataRepositoryException {
        removeStatement(null, null, null, uri);
    }

    public void removeStatement(Resource subject, IRI predicate, Value object, IRI context) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.remove(subject, predicate, object, context);
        } catch (RepositoryException e) {
            throw (new MetadataRepositoryException("Error removing statement"));
        }
    }

    public List<BindingSet> runSparqlQuery(String queryName, Class repositoryType, Map<String, Value> bindings) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            String queryString = loadSparqlQuery(queryName, repositoryType);
            TupleQuery query = conn.prepareTupleQuery(queryString);
            bindings.forEach(query::setBinding);
            return QueryResults.asList(query.evaluate());
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Error retrieve repository uri :" + e.getMessage());
        } catch (IOException e) {
            throw new MetadataRepositoryException(format("Error reading %s.sparql file (error: %s)", queryName,
                    e.getMessage()));
        }
    }

    protected String loadSparqlQuery(String queryName, Class repositoryType) throws IOException {
        URL fileURL = repositoryType.getResource(queryName);
        return Resources.toString(fileURL, Charsets.UTF_8);
    }
}
