/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
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
package org.fairdatapoint.service.boostrap.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.fairdatapoint.database.db.repository.*;
import org.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatapoint.service.boostrap.BootstrapContext;
import org.fairdatapoint.service.boostrap.fixtures.ResourceDefinitionFixture;
import org.fairdatapoint.service.resource.ResourceDefinitionMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.stream.IntStream;

@Slf4j
@Component
public class ResourceDefinitionBootstrapper extends AbstractBootstrapper {
    private final ResourceDefinitionRepository resourceDefinitionRepository;
    private final ResourceDefinitionLinkRepository resourceDefinitionLinkRepository;
    private final MetadataSchemaUsageRepository metadataSchemaUsageRepository;
    private final ResourceDefinitionMapper resourceDefinitionMapper;

    public ResourceDefinitionBootstrapper(ObjectMapper objectMapper,
                                          ResourceDefinitionRepository resourceDefinitionRepository,
                                          ResourceDefinitionLinkRepository resourceDefinitionLinkRepository,
                                          MetadataSchemaUsageRepository metadataSchemaUsageRepository,
                                          ResourceDefinitionMapper resourceDefinitionMapper) {
        super(objectMapper);
        this.resourceDefinitionRepository = resourceDefinitionRepository;
        this.resourceDefinitionLinkRepository = resourceDefinitionLinkRepository;
        this.metadataSchemaUsageRepository = metadataSchemaUsageRepository;
        this.resourceDefinitionMapper = resourceDefinitionMapper;
    }

    @Override
    protected JpaRepository getRepository() {
        return resourceDefinitionRepository;
    }

    @Override
    public void bootstrapFromJson(Path resourcePath, BootstrapContext context) {
        try {
            final ResourceDefinitionFixture resourceDefinitionFixture =
                    getObjectMapper().readValue(resourcePath.toString(), ResourceDefinitionFixture.class);
            final ResourceDefinition resourceDefinition =
                    resourceDefinitionRepository.saveAndFlush(
                    resourceDefinitionMapper.fromResourceDefinitionFixture(resourceDefinitionFixture)
            );
            context.getResourceDefinitions().put(resourceDefinitionFixture.getUuid(), resourceDefinition);
            // External Links
            resourceDefinitionLinkRepository.saveAllAndFlush(
                    IntStream.range(0, resourceDefinitionFixture.getExternalLinks().size())
                            .mapToObj(index -> {
                                return resourceDefinitionMapper.toLink(
                                        resourceDefinitionFixture.getExternalLinks().get(index),
                                        resourceDefinition,
                                        index
                                );
                            })
                            .toList()
            );
            // Metadata Schema Usages
            metadataSchemaUsageRepository.saveAllAndFlush(
                    IntStream.range(0, resourceDefinitionFixture.getMetadataSchemaUuids().size())
                            .mapToObj(index -> {
                                return resourceDefinitionMapper.toUsage(
                                        context.getMetadataSchemas().get(
                                                resourceDefinitionFixture.getMetadataSchemaUuids().get(index)
                                        ),
                                        resourceDefinition,
                                        index
                                );
                            })
                            .toList()
            );
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
