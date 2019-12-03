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

import nl.dtls.fairdatapoint.database.rdf.repository.common.MetadataRepositoryException;
import nl.dtls.fairdatapoint.utils.MetadataFixtureFilesHelper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Collections;

import static nl.dtls.fairdatapoint.config.CacheConfig.DATASET_THEMES_OF_CATALOG_CACHE;
import static org.mockito.Mockito.*;

public class DatasetMetadataRepositoryTest {

    private static final ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

    private IRI catalogUri = VALUE_FACTORY.createIRI(MetadataFixtureFilesHelper.CATALOG_URI);

    @Spy
    private Cache cache;

    @Mock
    private Repository repository;

    @Mock
    private RepositoryConnection repositoryConnection;

    @Mock
    private Resource resource;

    @Mock
    private Value value;

    @Mock
    private ConcurrentMapCacheManager cacheManager;

    @InjectMocks
    @Spy
    private DatasetMetadataRepositoryImpl catalogMetadataRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("'storeStatements' should evict cache")
    public void storeStatementsEvictsCache() throws MetadataRepositoryException {
        // GIVEN:
        when(cacheManager.getCache(DATASET_THEMES_OF_CATALOG_CACHE)).thenReturn(cache);
        when(repository.getConnection()).thenReturn(repositoryConnection);

        // WHEN:
        catalogMetadataRepository.storeStatements(Collections.emptyList(), catalogUri);

        // THEN:
        verify(cache, times(1)).evict(catalogUri.toString());
    }

    @Test
    @DisplayName("'removeStatement' should evict cache")
    public void removeStatementEvictsCache() throws MetadataRepositoryException {
        // GIVEN:
        when(cacheManager.getCache(DATASET_THEMES_OF_CATALOG_CACHE)).thenReturn(cache);
        when(repository.getConnection()).thenReturn(repositoryConnection);

        // WHEN:
        catalogMetadataRepository.removeStatement(resource, catalogUri, value);

        // THEN:
        verify(cache, times(1)).evict(catalogUri.toString());
    }

    @Test
    @DisplayName("'removeResource' should evict cache")
    public void removeResourceEvictsCache() throws MetadataRepositoryException {
        // GIVEN:
        when(cacheManager.getCache(DATASET_THEMES_OF_CATALOG_CACHE)).thenReturn(cache);
        when(repository.getConnection()).thenReturn(repositoryConnection);

        // WHEN:
        catalogMetadataRepository.removeResource(catalogUri);

        // THEN:
        verify(cache, times(1)).evict(catalogUri.toString());
    }

}
