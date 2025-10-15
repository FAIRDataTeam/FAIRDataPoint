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
package org.fairdatapoint.service.resource;

import org.fairdatapoint.api.dto.resource.*;
import org.fairdatapoint.entity.resource.*;
import org.fairdatapoint.entity.schema.MetadataSchema;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ResourceDefinitionMapper {

    public ResourceDefinition fromChangeDTO(ResourceDefinitionChangeDTO dto) {
        return ResourceDefinition.builder()
                .uuid(null)
                .name(dto.getName())
                .urlPrefix(dto.getUrlPrefix())
                .build();
    }

    public ResourceDefinition fromChangeDTO(ResourceDefinitionChangeDTO dto, ResourceDefinition resourceDefinition) {
        return resourceDefinition.toBuilder()
                .name(dto.getName())
                .urlPrefix(dto.getUrlPrefix())
                .updatedAt(Instant.now())
                .build();
    }

    public ResourceDefinitionChangeDTO toChangeDTO(
            ResourceDefinition resourceDefinition
    ) {
        return ResourceDefinitionChangeDTO.builder()
                .name(resourceDefinition.getName())
                .urlPrefix(resourceDefinition.getUrlPrefix())
                .metadataSchemaUuids(
                        resourceDefinition
                                .getMetadataSchemaUsages()
                                .stream()
                                .map(usage -> usage.getUsedMetadataSchema().getUuid())
                                .toList()
                )
                .children(resourceDefinition.getChildren().stream().map(this::toChildDTO).toList())
                .externalLinks(resourceDefinition.getExternalLinks().stream().map(this::toLinkDTO).toList())
                .build();
    }

    public ResourceDefinitionDTO toDTO(
            ResourceDefinition resourceDefinition,
            List<String> targetClassUris
    ) {
        return ResourceDefinitionDTO.builder()
                .uuid(resourceDefinition.getUuid())
                .name(resourceDefinition.getName())
                .urlPrefix(resourceDefinition.getUrlPrefix())
                .targetClassUris(targetClassUris)
                .metadataSchemaUuids(
                        resourceDefinition
                                .getMetadataSchemaUsages()
                                .stream()
                                .map(usage -> usage.getUsedMetadataSchema().getUuid())
                                .toList()
                )
                .children(resourceDefinition.getChildren().stream().map(this::toChildDTO).toList())
                .externalLinks(resourceDefinition.getExternalLinks().stream().map(this::toLinkDTO).toList())
                .build();
    }

    private ResourceDefinitionLinkDTO toLinkDTO(ResourceDefinitionLink link) {
        return ResourceDefinitionLinkDTO.builder()
                .title(link.getTitle())
                .propertyUri(link.getPropertyUri())
                .build();
    }

    private ResourceDefinitionChildDTO toChildDTO(ResourceDefinitionChild child) {
        return ResourceDefinitionChildDTO.builder()
                .resourceDefinitionUuid(child.getTarget().getUuid())
                .relationUri(child.getRelationUri())
                .listView(
                        ResourceDefinitionChildListViewDTO.builder()
                                .title(child.getTitle())
                                .tagsUri(child.getTagsUri())
                                .metadata(child.getMetadata().stream().map(this::toChildMetadataDTO).toList())
                                .build()
                )
                .build();
    }

    private ResourceDefinitionChildListViewMetadataDTO toChildMetadataDTO(ResourceDefinitionChildMetadata metadata) {
        return ResourceDefinitionChildListViewMetadataDTO.builder()
                .title(metadata.getTitle())
                .propertyUri(metadata.getPropertyUri())
                .build();
    }

    public MetadataSchemaUsage toUsage(MetadataSchema schema, ResourceDefinition definition, int orderPriority) {
        return MetadataSchemaUsage.builder()
                .uuid(null)
                .usedMetadataSchema(schema)
                .resourceDefinition(definition)
                .orderPriority(orderPriority)
                .build();
    }

    public ResourceDefinitionLink toLink(
            ResourceDefinitionLinkDTO dto, ResourceDefinition definition, int orderPriority
    ) {
        return ResourceDefinitionLink.builder()
                .uuid(null)
                .title(dto.getTitle())
                .propertyUri(dto.getPropertyUri())
                .resourceDefinition(definition)
                .orderPriority(orderPriority)
                .createdAt(definition.getCreatedAt())
                .updatedAt(definition.getUpdatedAt())
                .build();
    }

    public ResourceDefinitionChild toChild(
            ResourceDefinitionChildDTO dto, ResourceDefinition source, ResourceDefinition target, int orderPriority
    ) {
        return ResourceDefinitionChild.builder()
                .uuid(null)
                .relationUri(dto.getRelationUri())
                .title(dto.getListView().getTitle())
                .tagsUri(dto.getListView().getTagsUri())
                .source(source)
                .target(target)
                .orderPriority(orderPriority)
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .build();
    }

    public ResourceDefinitionChildMetadata toChildMetadata(
            ResourceDefinitionChildListViewMetadataDTO dto, ResourceDefinitionChild child, int orderPriority
    ) {
        return ResourceDefinitionChildMetadata.builder()
                .uuid(null)
                .title(dto.getTitle())
                .propertyUri(dto.getPropertyUri())
                .child(child)
                .orderPriority(orderPriority)
                .createdAt(child.getCreatedAt())
                .updatedAt(child.getUpdatedAt())
                .build();
    }
}
