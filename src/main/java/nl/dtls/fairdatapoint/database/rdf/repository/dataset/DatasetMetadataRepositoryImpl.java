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
package nl.dtls.fairdatapoint.database.rdf.repository.dataset;

import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtls.fairdatapoint.database.rdf.repository.common.AbstractMetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepositoryException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import java.util.List;

import static nl.dtls.fairdatapoint.config.CacheConfig.DATASET_THEMES_OF_CATALOG_CACHE;

@Service
public class DatasetMetadataRepositoryImpl extends AbstractMetadataRepository<DatasetMetadata> implements DatasetMetadataRepository {

    @Autowired
    private ConcurrentMapCacheManager cacheManager;

    @Override
    public void storeStatements(List<Statement> statements, IRI... cntx) throws MetadataRepositoryException {
        super.storeStatements(statements, cntx);
        for (IRI uri : cntx) {
            cache().evict(uri.toString());
        }
    }

    @Override
    public void removeStatement(Resource rsrc, IRI pred, Value value) throws MetadataRepositoryException {
        super.removeStatement(rsrc, pred, value);
        if (pred != null) {
            cache().evict(pred.toString());
        }
    }

    @Override
    public void removeResource(IRI uri) throws MetadataRepositoryException {
        super.removeResource(uri);
        cache().evict(uri.toString());
    }

    private Cache cache() {
        return cacheManager.getCache(DATASET_THEMES_OF_CATALOG_CACHE);
    }

}
