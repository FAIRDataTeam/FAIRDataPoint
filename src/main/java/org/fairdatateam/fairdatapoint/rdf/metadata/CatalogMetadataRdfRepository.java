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

import jakarta.annotation.PostConstruct;
import org.eclipse.rdf4j.repository.Repository;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.fairdatateam.fairdatapoint.common.config.CacheConfig.CATALOG_THEMES_CACHE;

@Service
public class CatalogMetadataRdfRepository extends AbstractMetadataRdfRepository {

    private final ConcurrentMapCacheManager cacheManager;

    /**
     * Constructor (autowired)
     */
    public CatalogMetadataRdfRepository(ConcurrentMapCacheManager cacheManager, Repository repository) {
        super(repository);
        this.cacheManager = cacheManager;
    }

    @PostConstruct
    public void init() {
        cacheManager.setCacheNames(List.of(CATALOG_THEMES_CACHE));
    }



    private Cache cache() {
        return cacheManager.getCache(CATALOG_THEMES_CACHE);
    }

}
