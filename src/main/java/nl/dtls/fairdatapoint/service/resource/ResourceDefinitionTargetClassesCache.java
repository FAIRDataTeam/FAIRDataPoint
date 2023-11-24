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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.database.db.repository.*;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaExtension;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaState;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaVersion;
import org.apache.solr.client.solrj.io.Tuple;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static nl.dtls.fairdatapoint.config.CacheConfig.RESOURCE_DEFINITION_TARGET_CLASSES_CACHE;

@Service
@RequiredArgsConstructor
public class ResourceDefinitionTargetClassesCache {

    private final ConcurrentMapCacheManager cacheManager;

    private final ResourceDefinitionRepository resourceDefinitionRepository;

    private final MetadataSchemaVersionRepository metadataSchemaRepository;

    private final MetadataSchemaUsageRepository usageRepository;

    private final MetadataSchemaExtensionRepository extensionRepository;

    @PostConstruct
    public void computeCache() {
        // Get cache
        final Cache cache = cache();

        // Clear cache
        cache.clear();

        // Add to cache
        final List<ResourceDefinition> rds = resourceDefinitionRepository.findAll();
        final Map<UUID, UUID> latestMap = new HashMap<>();
        final Map<UUID, MetadataSchemaVersion> metadataSchemaMap = new HashMap<>();
        metadataSchemaRepository.findAllByState(MetadataSchemaState.LATEST).forEach(schema -> {
            final boolean isNewer = Optional.ofNullable(metadataSchemaMap.get(schema.getSchema().getUuid()))
                    .map(otherSchema -> otherSchema.getVersion().compareTo(schema.getVersion()) < 0)
                    .orElse(false);
            if (!metadataSchemaMap.containsKey(schema.getSchema().getUuid()) || isNewer) {
                metadataSchemaMap.put(schema.getSchema().getUuid(), schema);
                latestMap.put(schema.getUuid(), schema.getSchema().getUuid());
            }
        });

        final Map<UUID, List<UUID>> extensionsMap = new HashMap<>();
        metadataSchemaMap.keySet().forEach(schemaUuid -> extensionsMap.put(schemaUuid, extensionRepository.getExtendedSchemaUuids(schemaUuid)));

        rds.forEach(resourceDefinition -> {
            final Set<String> targetClassUris = new HashSet<>();
            usageRepository.findAllByResourceDefinition(resourceDefinition).forEach(usage -> {
                final MetadataSchema schema = usage.getUsedMetadataSchema();
                if (metadataSchemaMap.containsKey(schema.getUuid())) {
                    final MetadataSchemaVersion latestVersion = metadataSchemaMap.get(schema.getUuid());
                    targetClassUris.addAll(latestVersion.getTargetClasses());

                    final Queue<UUID> parentUuids = new LinkedList<>(extensionsMap.get(latestVersion.getSchema().getUuid()));
                    final Set<UUID> visitedParents = new HashSet<>();
                    UUID parentUuid = null;
                    while (!parentUuids.isEmpty()) {
                        parentUuid = parentUuids.poll();
                        if (!visitedParents.contains(parentUuid)) {
                            visitedParents.add(parentUuid);
                            targetClassUris.addAll(
                                    metadataSchemaMap.get(parentUuid).getTargetClasses()
                            );
                            parentUuids.addAll(extensionsMap.get(parentUuid));
                        }
                    }

                }
            });
            cache.put(resourceDefinition.getUuid(), targetClassUris.stream().toList());
        });
    }

    public List<String> getByUuid(String uuid) {
        return cache().get(uuid, List.class);
    }

    private Cache cache() {
        return cacheManager.getCache(RESOURCE_DEFINITION_TARGET_CLASSES_CACHE);
    }

}
