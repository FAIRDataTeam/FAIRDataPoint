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

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.repository.Repository;
import org.fairdatateam.fairdatapoint.rdf.RdfRepositoryException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.fairdatateam.fairdatapoint.common.config.CacheConfig.CATALOG_THEMES_CACHE;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.fairdatateam.fairdatapoint.rdf.metadata.MetadataGetter.getParent;

@Service
public class MetadataRdfRepository extends AbstractMetadataRdfRepository {

    private static final String GET_DATASET_THEMES_FOR_CATALOG = "/sparql/getDatasetThemesForCatalog.sparql";

    private final ConcurrentMapCacheManager cacheManager;

    /**
     * Constructor (autowired)
     */
    public MetadataRdfRepository(ConcurrentMapCacheManager cacheManager, Repository repository) {
        super(repository);
        this.cacheManager = cacheManager;
    }

    @Override
    public void save(List<Statement> statements, IRI context) throws RdfRepositoryException {
        super.save(statements, context);
        clearCatalogThemesCache(context);
    }

    @Override
    public void remove(IRI uri) throws RdfRepositoryException {
        clearCatalogThemesCache(uri);
        super.remove(uri);
    }

    @Override
    public void removeStatement(Resource subject, IRI predicate, Value object, IRI context)
            throws RdfRepositoryException {
        clearCatalogThemesCache(context);
        super.removeStatement(subject, predicate, object, context);
    }

    /**
     * Returns a list of dcat:theme URIs representing the aggregated themes from all datasets in the catalog
     */
    @Cacheable(cacheNames = CATALOG_THEMES_CACHE)
    public List<IRI> getDatasetThemesForCatalog(IRI uri) throws RdfRepositoryException {
        // todo: this assumes the uri represents a dcat:Catalog, but we should probably check that
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

}
