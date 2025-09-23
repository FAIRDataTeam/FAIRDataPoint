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
package org.fairdatapoint.service.bootstrap.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.fairdatapoint.api.dto.resource.ResourceDefinitionChildDTO;
import org.fairdatapoint.database.db.repository.*;
import org.fairdatapoint.entity.resource.ResourceDefinition;
import org.fairdatapoint.entity.resource.ResourceDefinitionChild;
import org.fairdatapoint.service.bootstrap.BootstrapContext;
import org.fairdatapoint.service.bootstrap.fixtures.ResourceDefinitionFixture;
import org.fairdatapoint.service.resource.ResourceDefinitionMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.stream.IntStream;

@Slf4j
@Component
public class ResourceDefinitionChildrenBootstrapper extends AbstractBootstrapper {
    private final ResourceDefinitionChildRepository childRepository;
    private final ResourceDefinitionChildMetadataRepository childMetadataRepository;
    private final ResourceDefinitionMapper resourceDefinitionMapper;

    public ResourceDefinitionChildrenBootstrapper(ObjectMapper objectMapper,
                                                  ResourceDefinitionChildRepository childRepository,
                                                  ResourceDefinitionChildMetadataRepository childMetadataRepository,
                                                  ResourceDefinitionMapper resourceDefinitionMapper) {
        super(objectMapper);
        this.childRepository = childRepository;
        this.childMetadataRepository = childMetadataRepository;
        this.resourceDefinitionMapper = resourceDefinitionMapper;
    }

    @Override
    protected JpaRepository getRepository() {
        return childRepository;
    }

    @Override
    public void bootstrapFromJson(Path resourcePath, BootstrapContext context) {
        try {
            final ResourceDefinitionFixture resourceDefinitionFixture =
                    getObjectMapper().readValue(resourcePath.toString(), ResourceDefinitionFixture.class);
            final ResourceDefinition resourceDefinition =
                    context.getResourceDefinitions().get(resourceDefinitionFixture.getUuid());
            // Children
            IntStream.range(0, resourceDefinitionFixture.getChildren().size())
                    .mapToObj(index -> {
                        final ResourceDefinitionChildDTO childDTO =
                                resourceDefinitionFixture.getChildren().get(index);
                        return resourceDefinitionMapper.toChild(
                                resourceDefinitionFixture.getChildren().get(index),
                                resourceDefinition,
                                context.getResourceDefinitions().get(childDTO.getResourceDefinitionUuid()),
                                index);
                    })
                    .forEach(child -> {
                        final ResourceDefinitionChild savedChild = childRepository.saveAndFlush(child);
                        final ResourceDefinitionChildDTO childDTO =
                                resourceDefinitionFixture.getChildren().get(child.getOrderPriority());
                        // Child metadata
                        childMetadataRepository.saveAllAndFlush(
                                IntStream.range(0, childDTO.getListView().getMetadata().size())
                                        .mapToObj(metaIndex -> {
                                            return resourceDefinitionMapper.toChildMetadata(
                                                    childDTO.getListView().getMetadata().get(metaIndex),
                                                    savedChild,
                                                    metaIndex
                                            );
                                        })
                                        .toList()
                        );
                    });
        }
        catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
