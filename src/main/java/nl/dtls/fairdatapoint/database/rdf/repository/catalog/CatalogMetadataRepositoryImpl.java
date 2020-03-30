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
package nl.dtls.fairdatapoint.database.rdf.repository.catalog;

import nl.dtls.fairdatapoint.database.rdf.repository.common.GenericMetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepositoryException;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nl.dtls.fairdatapoint.config.CacheConfig.DATASET_THEMES_OF_CATALOG_CACHE;

@Service
public class CatalogMetadataRepositoryImpl extends GenericMetadataRepository implements CatalogMetadataRepository {

    private static final String GET_DATASET_THEMES_FOR_CATALOG = "getDatasetThemesForCatalog.sparql";

    @Autowired
    private ConcurrentMapCacheManager cacheManager;

    @PostConstruct
    public void init() {
        cacheManager.setCacheNames(List.of(DATASET_THEMES_OF_CATALOG_CACHE));
    }

    public List<IRI> getDatasetThemesForCatalog(IRI uri) throws MetadataRepositoryException {
        List<IRI> result = cache().get(uri.toString(), List.class);
        if (result != null) {
            return result;
        }
        result = runSparqlQuery(GET_DATASET_THEMES_FOR_CATALOG, CatalogMetadataRepository.class, Map.of(
                "catalog", uri))
                .stream()
                .map(s -> VALUEFACTORY.createIRI(s.getValue("theme").stringValue()))
                .collect(Collectors.toList());
        cache().put(uri.toString(), result);
        return result;
    }

    private Cache cache() {
        return cacheManager.getCache(DATASET_THEMES_OF_CATALOG_CACHE);
    }

}
