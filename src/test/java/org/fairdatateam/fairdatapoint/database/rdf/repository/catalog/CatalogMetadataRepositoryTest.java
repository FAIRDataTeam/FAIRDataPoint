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

import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Collections;
import java.util.List;

import static nl.dtls.fairdatapoint.config.CacheConfig.CATALOG_THEMES_CACHE;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CatalogMetadataRepositoryTest {

    private final IRI catalogUri = i("http://localhost/textmining");

    @Mock
    private Cache cache;

    @Mock
    private Repository repository;

    @Mock
    private RepositoryConnection repositoryConnection;

    @Mock
    private TupleQuery tupleQuery;

    @Mock
    private TupleQueryResult tupleQueryResult;

    @Mock
    private ConcurrentMapCacheManager cacheManager;

    @Spy
    @InjectMocks
    private CatalogMetadataRepositoryImpl catalogMetadataRepository;

    @Test
    @DisplayName("Themes for catalog are in cache (no query to triple store)")
    public void themesInCache() throws MetadataRepositoryException {
        // GIVEN:
        when(cacheManager.getCache(CATALOG_THEMES_CACHE)).thenReturn(cache);
        when(cache.get(catalogUri.toString(), List.class)).thenReturn(Collections.emptyList());

        // WHEN:
        catalogMetadataRepository.getDatasetThemesForCatalog(catalogUri);

        // THEN:
        verify(catalogMetadataRepository, never()).runSparqlQuery(any(), any(), any());
    }

    @Test
    @DisplayName("Themes for catalog are not in cache (we have to query to triple store)")
    public void themesNotInCache() throws MetadataRepositoryException {
        // GIVEN:
        when(cacheManager.getCache(CATALOG_THEMES_CACHE)).thenReturn(cache);
        when(repository.getConnection()).thenReturn(repositoryConnection);
        when(repositoryConnection.prepareTupleQuery(any())).thenReturn(tupleQuery);
        when(tupleQuery.evaluate()).thenReturn(tupleQueryResult);

        // WHEN:
        catalogMetadataRepository.getDatasetThemesForCatalog(catalogUri);

        // THEN:
        verify(catalogMetadataRepository, times(1)).runSparqlQuery(any(), any(), any());
    }

}
