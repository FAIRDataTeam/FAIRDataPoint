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

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.database.db.repository.ResourceDefinitionChildRepository;
import nl.dtls.fairdatapoint.database.db.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinitionLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import java.util.*;

import static nl.dtls.fairdatapoint.config.CacheConfig.RESOURCE_DEFINITION_CACHE;
import static nl.dtls.fairdatapoint.config.CacheConfig.RESOURCE_DEFINITION_PARENT_CACHE;

@Service
@RequiredArgsConstructor
public class ResourceDefinitionCache {

    private final ConcurrentMapCacheManager cacheManager;

    private final ResourceDefinitionRepository resourceDefinitionRepository;

    private final ResourceDefinitionChildRepository resourceDefinitionChildRepository;

    @PostConstruct
    public void computeCache() {
        // Get cache
        final Cache cache = cache();
        final Cache parentCache = parentCache();

        // Clear cache
        cache.clear();
        parentCache.clear();

        // Add to cache
        final List<ResourceDefinition> rds = resourceDefinitionRepository.findAll();
        rds.forEach(resourceDefinition -> {
            parentCache.put(resourceDefinition.getUuid(), new ResourceDefinitionParents());
        });
        rds.forEach(resourceDefinition -> {
            cache.put(resourceDefinition.getUuid(), resourceDefinition);
            resourceDefinitionChildRepository.findAllBySourceUuid(resourceDefinition.getUuid())
                    .forEach(child -> {
                        parentCache.get(
                                child.getSource().getUuid(),
                                ResourceDefinitionParents.class
                        ).add(resourceDefinition);
                    });
        });
    }

    public ResourceDefinition getByUuid(UUID uuid) {
        return cache().get(uuid, ResourceDefinition.class);
    }

    public Set<ResourceDefinition> getParentsByUuid(UUID uuid) {
        var parents = parentCache().get(uuid, ResourceDefinitionParents.class);
        if (parents == null) {
            computeCache();
            parents = parentCache().get(uuid, ResourceDefinitionParents.class);
            if (parents == null) {
                return Collections.emptySet();
            }
        }
        return parents.getParents();
    }

    private Cache cache() {
        return cacheManager.getCache(RESOURCE_DEFINITION_CACHE);
    }

    private Cache parentCache() {
        return cacheManager.getCache(RESOURCE_DEFINITION_PARENT_CACHE);
    }

    @Getter
    private static final class ResourceDefinitionParents {
        private final Set<ResourceDefinition> parents = new HashSet<>();

        public void add(ResourceDefinition rdParent) {
            parents.add(rdParent);
        }
    }
}
