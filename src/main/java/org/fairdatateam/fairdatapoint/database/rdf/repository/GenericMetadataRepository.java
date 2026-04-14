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

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.fairdatateam.fairdatapoint.config.CacheConfig.CATALOG_THEMES_CACHE;
import static org.fairdatateam.fairdatapoint.entity.metadata.MetadataGetter.getParent;

@Service
public class GenericMetadataRepository extends AbstractMetadataRepository {

    @Autowired
    private ConcurrentMapCacheManager cacheManager;

    @Override
    public void save(List<Statement> statements, IRI context) throws MetadataRepositoryException {
        super.save(statements, context);
        clearCatalogCache(context);
    }

    @Override
    public void remove(IRI uri) throws MetadataRepositoryException {
        clearCatalogCache(uri);
        super.remove(uri);
    }

    @Override
    public void removeStatement(Resource subject, IRI predicate, Value object, IRI context)
            throws MetadataRepositoryException {
        clearCatalogCache(context);
        super.removeStatement(subject, predicate, object, context);
    }

    private void clearCatalogCache(IRI uri) throws MetadataRepositoryException {
        final Model metadata = new LinkedHashModel();
        metadata.addAll(find(uri));
        final IRI parent = getParent(metadata);
        if (parent != null) {
            cacheManager.getCache(CATALOG_THEMES_CACHE).evict(parent.stringValue());
        }
    }

}
