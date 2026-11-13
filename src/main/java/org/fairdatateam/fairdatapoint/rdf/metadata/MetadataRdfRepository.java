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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.fairdatateam.fairdatapoint.rdf.RdfRepository;
import org.fairdatateam.fairdatapoint.rdf.RdfRepositoryException;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.fairdatateam.fairdatapoint.common.config.CacheConfig.CATALOG_THEMES_CACHE;
import static org.fairdatateam.fairdatapoint.common.util.SpringResourceReader.loadResource;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getParent;

@Service
@Slf4j
@RequiredArgsConstructor
public class MetadataRdfRepository implements RdfRepository {

    private static final String GET_DATASET_THEMES_FOR_CATALOG = "/sparql/getDatasetThemesForCatalog.sparql";

    private static final String FIND_CHILD_TITLES = "/sparql/findChildTitles.sparql";

    private static final String MSG_ERROR_RESOURCE = "Error retrieving resource: ";
    private static final String MSG_ERROR_URI = "Error retrieving repository URI: ";
    private static final String MSG_ERROR_REMOVE = "Error removing statement";
    private static final String MSG_ERROR_REMOVE_ALL = "Error remove all: ";
    private static final String MSG_ERROR_EXISTS = "Error check statement existence: ";
    private static final String MSG_ERROR_SAVE = "Error storing statements: ";
    private static final String MSG_ERROR_QUERY = "Error evaluating SPARQL query: ";

    private static final String FIELD_CHILD = "child";
    private static final String FIELD_TITLE = "title";

    private final ConcurrentMapCacheManager cacheManager;

    private final Repository repository;

    public List<Statement> find(IRI context) throws RdfRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            return Iterations.asList(
                    conn.getStatements(null, null, null, context)
            );
        }
        catch (RepositoryException exception) {
            throw new RdfRepositoryException(MSG_ERROR_RESOURCE + exception.getMessage());
        }
    }

    public Map<String, String> findChildTitles(IRI parent, IRI relation) throws RdfRepositoryException {
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
            throws RdfRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            return conn.hasStatement(subject, predicate, object, false);
        }
        catch (RepositoryException exception) {
            throw new RdfRepositoryException(MSG_ERROR_EXISTS + exception.getMessage());
        }
    }

    public void save(List<Statement> statements, IRI context) throws RdfRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.add(statements, context);
        }
        catch (RepositoryException exception) {
            throw new RdfRepositoryException(MSG_ERROR_SAVE + exception.getMessage());
        }
        clearCatalogThemesCache(context);
    }

    public void removeAll() throws RdfRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.clear();
        }
        catch (RepositoryException exception) {
            throw new RdfRepositoryException(MSG_ERROR_REMOVE_ALL + exception.getMessage());
        }
    }

    public void remove(IRI uri) throws RdfRepositoryException {
        clearCatalogThemesCache(uri);
        removeStatement(null, null, null, uri);
    }

    public void removeStatement(
            Resource subject, IRI predicate, Value object, IRI context
    ) throws RdfRepositoryException {
        clearCatalogThemesCache(context);
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.remove(subject, predicate, object, context);
        }
        catch (RepositoryException exception) {
            throw new RdfRepositoryException(MSG_ERROR_REMOVE);
        }
    }

    /**
     * Returns a list of <code>dcat:theme</code> URIs representing the aggregated themes from all datasets in the
     * specified catalog. If called with a <code>uri</code> that does not represent a <code>dcat:Catalog</code>,
     * this will simply return an empty list, because the themes are obtained via a SPARQL query that
     * looks for <code>dcat:dataset</code>. Of course this does assume that the domain <code>dcat:Catalog</code> is
     * strictly observed.
     */
    @Cacheable(cacheNames = CATALOG_THEMES_CACHE)
    public List<IRI> getDatasetThemesForCatalog(IRI uri) throws RdfRepositoryException {
        return runSparqlQueryFromFile(GET_DATASET_THEMES_FOR_CATALOG, Map.of("catalog", uri))
                .stream()
                .map(item -> i(item.getValue("theme").stringValue()))
                .toList();
    }

    /**
     * Clears the catalog themes cache for the parent of the resource represented by the specified <code>uri</code>.
     * This is necessary whenever a resource is removed or changes in such a way that the parent's cache becomes
     * outdated. Typically, this would only happen if the themes change, but we don't check that explicitly.
     */
    private void clearCatalogThemesCache(IRI uri) throws RdfRepositoryException {
        final Model metadata = new LinkedHashModel();
        metadata.addAll(find(uri));
        final IRI parentUri = getParent(metadata);
        if (parentUri != null) {
            // No need to check if the parent is actually a catalog, because non-existent cache keys are simply ignored
            cacheManager.getCache(CATALOG_THEMES_CACHE).evict(parentUri);
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
    ) throws RdfRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            final TupleQuery query = conn.prepareTupleQuery(queryString);
            if (bindings != null) {
                // assign values to variables defined in the sparql query
                bindings.forEach(query::setBinding);
            }
            return QueryResults.asList(query.evaluate());
        }
        catch (RepositoryException exception) {
            throw new RdfRepositoryException(MSG_ERROR_URI + exception.getMessage());
        }
        catch (QueryEvaluationException exception) {
            // Thrown when the query fails to execute against the triple store (e.g. it is
            // unreachable mid-query), not when the query text itself is malformed - treat it
            // as a repository/infrastructure failure rather than a client error.
            throw new RdfRepositoryException(MSG_ERROR_QUERY + exception.getMessage());
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
    ) throws RdfRepositoryException {
        final String queryString = loadResource(queryFilePath);
        return runSparqlQuery(queryString, bindings);
    }

}
