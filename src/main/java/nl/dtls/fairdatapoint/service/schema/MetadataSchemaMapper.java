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
package nl.dtls.fairdatapoint.service.schema;

import nl.dtls.fairdatapoint.api.dto.schema.*;
import nl.dtls.fairdatapoint.entity.schema.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class MetadataSchemaMapper {

    public MetadataSchemaVersion fromChangeDTO(
            MetadataSchemaChangeDTO dto, MetadataSchema schema
    ) {
        return MetadataSchemaVersion.builder()
                .uuid(UUID.randomUUID())
                .name(dto.getName())
                .description(dto.getDescription())
                .abstractSchema(dto.isAbstractSchema())
                .type(MetadataSchemaType.CUSTOM)
                .state(MetadataSchemaState.DRAFT)
                .version("0.0.0")
                .definition(dto.getDefinition())
                .targetClasses(MetadataSchemaShaclUtils.extractTargetClasses(dto.getDefinition()).stream().toList())
                .suggestedResourceName(dto.getSuggestedResourceName())
                .suggestedUrlPrefix(dto.getSuggestedUrlPrefix())
                .schema(schema)
                .build();
    }

    public MetadataSchemaVersion fromChangeDTO(
            MetadataSchemaChangeDTO dto, MetadataSchemaVersion draft
    ) {
        return
                draft
                        .toBuilder()
                        .name(dto.getName())
                        .abstractSchema(dto.isAbstractSchema())
                        .description(dto.getDescription())
                        .definition(dto.getDefinition())
                        .targetClasses(
                                MetadataSchemaShaclUtils
                                        .extractTargetClasses(dto.getDefinition())
                                        .stream().toList()
                        )
                        .suggestedResourceName(dto.getSuggestedResourceName())
                        .suggestedUrlPrefix(dto.getSuggestedUrlPrefix())
                        .updatedAt(Instant.now())
                        .build();
    }

    public MetadataSchemaDraftDTO toDraftDTO(
            MetadataSchemaVersion draft
    ) {
        return MetadataSchemaDraftDTO.builder()
                .uuid(draft.getUuid())
                .name(draft.getName())
                .description(draft.getDescription())
                .abstractSchema(draft.isAbstractSchema())
                .definition(draft.getDefinition())
                .extendsSchemaUuids(draft.getExtensions().stream().map(extension -> extension.getExtendedMetadataSchema().getUuid()).toList())
                .suggestedResourceName(draft.getSuggestedResourceName())
                .suggestedUrlPrefix(draft.getSuggestedUrlPrefix())
                .lastVersion(draft.getPreviousVersion() == null ? null : draft.getPreviousVersion().getVersion())
                .build();
    }

    public MetadataSchemaDraftDTO toNewDraftDTO(MetadataSchemaVersion schema) {
        return MetadataSchemaDraftDTO.builder()
                .uuid(schema.getSchema().getUuid())
                .name(schema.getName())
                .description(schema.getDescription())
                .abstractSchema(schema.isAbstractSchema())
                .definition(schema.getDefinition())
                .extendsSchemaUuids(schema.getExtensions().stream().map(extension -> extension.getExtendedMetadataSchema().getUuid()).toList())
                .suggestedResourceName(schema.getSuggestedResourceName())
                .suggestedUrlPrefix(schema.getSuggestedUrlPrefix())
                .lastVersion(schema.getVersion())
                .build();
    }

    public MetadataSchemaVersionDTO toVersionDTO(MetadataSchemaVersion schema) {
        return MetadataSchemaVersionDTO.builder()
                .uuid(schema.getSchema().getUuid())
                .versionUuid(schema.getUuid())
                .version(schema.getVersion())
                .name(schema.getName())
                .description(schema.getDescription())
                .published(schema.isPublished())
                .abstractSchema(schema.isAbstractSchema())
                .latest(schema.isLatest())
                .type(schema.getType())
                .origin(schema.getOrigin())
                .importedFrom(schema.getImportedFrom())
                .definition(schema.getDefinition())
                .targetClasses(schema.getTargetClasses())
                .extendsSchemaUuids(schema.getExtensions().stream().map(extension -> extension.getExtendedMetadataSchema().getUuid()).toList())
                .suggestedResourceName(schema.getSuggestedResourceName())
                .suggestedUrlPrefix(schema.getSuggestedUrlPrefix())
                .previousVersionUuid(schema.getPreviousVersion() == null ? null : schema.getPreviousVersion().getUuid())
                .build();
    }

    public MetadataSchemaVersion fromReleaseDTO(
            MetadataSchemaReleaseDTO reqDto, MetadataSchemaVersion draft
    ) {
        return draft.toBuilder()
                .version(reqDto.getVersion())
                .state(MetadataSchemaState.LATEST)
                .type(MetadataSchemaType.CUSTOM)
                .origin(null)
                .description(reqDto.getDescription())
                .published(reqDto.isPublished())
                .updatedAt(Instant.now())
                .build();
    }

    public MetadataSchemaDTO toDTO(
            MetadataSchemaVersion latest,
            MetadataSchemaVersion draft,
            List<MetadataSchemaVersion> schemaVersions,
            List<MetadataSchemaVersion> childSchemas
    ) {
        if (latest != null) {
            return MetadataSchemaDTO.builder()
                    .uuid(latest.getSchema().getUuid())
                    .name(latest.getName())
                    .latest(toVersionDTO(latest))
                    .draft(draft == null ? null : toDraftDTO(draft))
                    .versions(schemaVersions
                            .stream()
                            .map(MetadataSchemaVersion::getSemVer)
                            .sorted()
                            .map(SemVer::toString)
                            .toList()
                    )
                    .extendSchemaUuids(latest
                            .getExtensions()
                            .stream()
                            .map(extension -> extension.getExtendedMetadataSchema().getUuid())
                            .toList()
                    )
                    .childSchemaUuids(childSchemas
                            .stream()
                            .map(child -> child.getSchema().getUuid())
                            .distinct()
                            .toList()
                    )
                    .build();
        }
        if (draft != null) {
            return MetadataSchemaDTO.builder()
                    .uuid(draft.getSchema().getUuid())
                    .name(draft.getName())
                    .latest(null)
                    .draft(toDraftDTO(draft))
                    .versions(schemaVersions
                            .stream()
                            .map(MetadataSchemaVersion::getSemVer)
                            .sorted()
                            .map(SemVer::toString)
                            .toList()
                    )
                    .extendSchemaUuids(Collections.emptyList())
                    .childSchemaUuids(Collections.emptyList())
                    .build();
        }
        return null;
    }

    public MetadataSchemaVersion fromUpdateDTO(MetadataSchemaVersion schema, MetadataSchemaUpdateDTO reqDto) {
        return
                schema
                        .toBuilder()
                        .name(reqDto.getName())
                        .description(reqDto.getDescription())
                        .published(reqDto.isPublished())
                        .build();
    }

    public MetadataSchemaVersion toDraft(MetadataSchemaVersion schema) {
        return MetadataSchemaVersion.builder()
                .uuid(schema.getUuid())
                .name(schema.getName())
                .description(schema.getDescription())
                .abstractSchema(schema.isAbstractSchema())
                .definition(schema.getDefinition())
                .suggestedResourceName(schema.getSuggestedResourceName())
                .suggestedUrlPrefix(schema.getSuggestedUrlPrefix())
                .build();
    }

    public MetadataSchemaVersionDTO toPublishedVersionDTO(
            MetadataSchemaVersion schema, String persistentUrl
    ) {
        final MetadataSchemaVersionDTO dto = toVersionDTO(schema);
        if (dto.getOrigin() == null) {
            dto.setOrigin(persistentUrl);
        }
        dto.setImportedFrom(persistentUrl);
        return dto;
    }

    public MetadataSchemaVersion fromRemoteVersion(MetadataSchemaVersionDTO remoteVersion) {
        return MetadataSchemaVersion.builder()
                .uuid(remoteVersion.getVersionUuid())
                .name(remoteVersion.getName())
                .description(remoteVersion.getDescription())
                .definition(remoteVersion.getDefinition())
                .version(remoteVersion.getVersion())
                .origin(remoteVersion.getOrigin())
                .importedFrom(remoteVersion.getImportedFrom())
                .targetClasses(remoteVersion.getTargetClasses().stream().toList())
                .abstractSchema(remoteVersion.isAbstractSchema())
                .published(false)
                .suggestedResourceName(remoteVersion.getSuggestedResourceName())
                .suggestedUrlPrefix(remoteVersion.getSuggestedUrlPrefix())
                .type(MetadataSchemaType.REFERENCE)
                .build();
    }

    public MetadataSchemaVersion fromRemoteVersion(
            MetadataSchemaVersionDTO remoteVersion, MetadataSchemaVersion schema
    ) {
        return schema.toBuilder()
                .name(remoteVersion.getName())
                .description(remoteVersion.getDescription())
                .definition(remoteVersion.getDefinition())
                .type(MetadataSchemaType.REFERENCE)
                .targetClasses(remoteVersion.getTargetClasses().stream().toList())
                .abstractSchema(remoteVersion.isAbstractSchema())
                .published(schema.isPublished())
                .origin(remoteVersion.getOrigin())
                .importedFrom(remoteVersion.getImportedFrom())
                .suggestedUrlPrefix(remoteVersion.getSuggestedUrlPrefix())
                .suggestedResourceName(remoteVersion.getSuggestedResourceName())
                .version(remoteVersion.getVersion())
                .updatedAt(Instant.now())
                .build();
    }

    public MetadataSchema newSchema(UUID uuid) {
        return MetadataSchema.builder()
                .uuid(uuid)
                .build();
    }

    public MetadataSchema newSchema() {
        return newSchema(UUID.randomUUID());
    }

    public MetadataSchemaExtension newExtension(MetadataSchemaVersion draft, MetadataSchema metadataSchema, int orderPriority) {
        return MetadataSchemaExtension.builder()
                .uuid(UUID.randomUUID())
                .extendedMetadataSchema(metadataSchema)
                .metadataSchemaVersion(draft)
                .orderPriority(orderPriority)
                .build();
    }
}
