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
package nl.dtls.fairdatapoint.service.resource;

import nl.dtls.fairdatapoint.database.mongo.migration.development.resource.data.ResourceDefinitionFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.List;

import static nl.dtls.fairdatapoint.config.CacheConfig.RESOURCE_DEFINITION_CACHE;
import static nl.dtls.fairdatapoint.config.CacheConfig.RESOURCE_DEFINITION_PARENT_CACHE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceDefinitionCacheTest {

    @Mock
    private ConcurrentMapCacheManager concurrentMapCacheManager;

    @Mock
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @InjectMocks
    private ResourceDefinitionCache resourceDefinitionCache;

    @InjectMocks
    private ResourceDefinitionFixtures resourceDefinitionFixtures;

    @Spy
    private Cache cache;

    @Spy
    private Cache parentCache;

    @Test
    public void computeCacheWorks() {
        // GIVEN: Prepare cache
        when(concurrentMapCacheManager.getCache(RESOURCE_DEFINITION_CACHE)).thenReturn(cache);
        when(concurrentMapCacheManager.getCache(RESOURCE_DEFINITION_PARENT_CACHE)).thenReturn(parentCache);

        // AND: Prepare resource definitions
        ResourceDefinition rdRepository = resourceDefinitionFixtures.repositoryDefinition();
        ResourceDefinition rdCatalog = resourceDefinitionFixtures.catalogDefinition();
        ResourceDefinition rdDataset = resourceDefinitionFixtures.datasetDefinition();
        ResourceDefinition rdDistribution = resourceDefinitionFixtures.distributionDefinition();
        when(resourceDefinitionRepository.findAll())
                .thenReturn(List.of(rdRepository, rdCatalog, rdDataset, rdDistribution));

        // WHEN:
        resourceDefinitionCache.computeCache();

        // THEN:
        verify(cache, times(1)).put(rdRepository.getUuid(), rdRepository);
        verify(cache, times(1)).put(rdCatalog.getUuid(), rdCatalog);
        verify(cache, times(1)).put(rdDataset.getUuid(), rdDataset);
        verify(cache, times(1)).put(rdDistribution.getUuid(), rdDistribution);

        verify(parentCache, times(1)).put(rdCatalog.getUuid(), rdRepository);
        verify(parentCache, times(1)).put(rdDataset.getUuid(), rdCatalog);
        verify(parentCache, times(1)).put(rdDistribution.getUuid(), rdDataset);
    }

}
