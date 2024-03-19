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
import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.database.db.repository.*;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import java.util.*;

import static nl.dtls.fairdatapoint.config.CacheConfig.RESOURCE_DEFINITION_TARGET_CLASSES_CACHE;

@Service
@RequiredArgsConstructor
public class ResourceDefinitionTargetClassesCache {

    private final ConcurrentMapCacheManager cacheManager;

    private final ResourceDefinitionRepository resourceDefinitionRepository;

    private final MetadataSchemaRepository metadataSchemaRepository;

    private final MetadataSchemaUsageRepository usageRepository;

    private final MetadataSchemaExtensionRepository extensionRepository;

    @PostConstruct
    public void computeCache() {
        // Get cache
        final Cache cache = cache();

        // Clear cache
        cache.clear();

        // Add to cache
        // - Get all latest metadata schemas and organize them by schema UUID
        final Map<UUID, MetadataSchemaRepository.MetadataSchemaBasic> metadataSchemas = new HashMap<>();
        metadataSchemaRepository.getBasicLatestMetadataSchemas().forEach(schema -> {
            metadataSchemas.put(schema.getUuid(), schema);
        });

        // - Get all extensions
        final Map<UUID, Set<UUID>> extensions = new HashMap<>();
        metadataSchemas.forEach((schemaUuid, schema) -> {
            extensions.put(schemaUuid, new HashSet<>());
        });
        extensionRepository.getBasicExtensionsForLatest().forEach(extension -> {
            extensions.get(extension.getSchemaUuid()).add(extension.getExtendedMetadataSchemaUuid());
        });

        // - Resolve target classes
        final Map<UUID, Set<String>> targetClasses = new HashMap<>();
        metadataSchemas.forEach((schemaUuid, schema) -> {
            targetClasses.put(schemaUuid, new HashSet<>(schema.getTargetClasses()));
        });
        metadataSchemas.forEach((schemaUuid, schema) -> {
            final Queue<UUID> parentUuids = new LinkedList<>(extensions.get(schemaUuid));
            final Set<UUID> visitedParents = new HashSet<>();
            UUID parentUuid = null;
            while (!parentUuids.isEmpty()) {
                parentUuid = parentUuids.poll();
                if (!visitedParents.contains(parentUuid)) {
                    visitedParents.add(parentUuid);
                    targetClasses.get(schemaUuid).addAll(metadataSchemas.get(parentUuid).getTargetClasses());
                    parentUuids.addAll(extensions.get(parentUuid));
                }
            }
        });

        // - Resolve for resource definitions
        final Map<UUID, Set<String>> resourceDefinitionTargetClasses = new HashMap<>();
        resourceDefinitionRepository.findAll().forEach(resourceDefinition -> {
            resourceDefinitionTargetClasses.put(resourceDefinition.getUuid(), new HashSet<>());
        });
        usageRepository.getBasicUsages().forEach(usage -> {
            resourceDefinitionTargetClasses
                    .get(usage.getResourceDefinitionUuid())
                    .addAll(targetClasses.get(usage.getSchemaUuid()));
        });

        // - Add to cache
        resourceDefinitionTargetClasses.forEach((resourceDefinitionUuid, classes) -> {
            cache.put(resourceDefinitionUuid.toString(), classes);
        });
    }

    public Set<String> getByUuid(String uuid) {
        return cache().get(uuid, Set.class);
    }

    private Cache cache() {
        return cacheManager.getCache(RESOURCE_DEFINITION_TARGET_CLASSES_CACHE);
    }

}
