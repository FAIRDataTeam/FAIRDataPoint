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
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaDraft;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaType;
import nl.dtls.fairdatapoint.entity.schema.SemVer;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Service
public class MetadataSchemaMapper {

    public MetadataSchemaDraft fromChangeDTO(MetadataSchemaChangeDTO dto, String uuid) {
        final Instant now = Instant.now();
        return MetadataSchemaDraft.builder()
                .uuid(uuid)
                .name(dto.getName())
                .description(dto.getDescription())
                .abstractSchema(dto.isAbstractSchema())
                .definition(dto.getDefinition())
                .targetClasses(MetadataSchemaShaclUtils.extractTargetClasses(dto.getDefinition()))
                .extendSchemas(dto.getExtendsSchemaUuids())
                .suggestedResourceName(dto.getSuggestedResourceName())
                .suggestedUrlPrefix(dto.getSuggestedUrlPrefix())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public MetadataSchemaDraft fromChangeDTO(
            MetadataSchemaChangeDTO dto, MetadataSchemaDraft draft
    ) {
        return
                draft
                        .toBuilder()
                        .name(dto.getName())
                        .abstractSchema(dto.isAbstractSchema())
                        .description(dto.getDescription())
                        .definition(dto.getDefinition())
                        .extendSchemas(dto.getExtendsSchemaUuids())
                        .targetClasses(
                                MetadataSchemaShaclUtils
                                        .extractTargetClasses(dto.getDefinition())
                        )
                        .suggestedResourceName(dto.getSuggestedResourceName())
                        .suggestedUrlPrefix(dto.getSuggestedUrlPrefix())
                        .build();
    }

    public MetadataSchemaDraftDTO toDraftDTO(
            MetadataSchemaDraft draft, MetadataSchema lastVersion
    ) {
        return MetadataSchemaDraftDTO.builder()
                .uuid(draft.getUuid())
                .name(draft.getName())
                .description(draft.getDescription())
                .abstractSchema(draft.isAbstractSchema())
                .definition(draft.getDefinition())
                .extendsSchemaUuids(draft.getExtendSchemas())
                .suggestedResourceName(draft.getSuggestedResourceName())
                .suggestedUrlPrefix(draft.getSuggestedUrlPrefix())
                .lastVersion(lastVersion == null ? null : lastVersion.getVersionString())
                .build();
    }

    public MetadataSchemaDraftDTO toDraftDTO(MetadataSchema schema) {
        return MetadataSchemaDraftDTO.builder()
                .uuid(schema.getUuid())
                .name(schema.getName())
                .description(schema.getDescription())
                .abstractSchema(schema.isAbstractSchema())
                .definition(schema.getDefinition())
                .extendsSchemaUuids(schema.getExtendSchemas())
                .suggestedResourceName(schema.getSuggestedResourceName())
                .suggestedUrlPrefix(schema.getSuggestedUrlPrefix())
                .lastVersion(schema.getVersionString())
                .build();
    }

    public MetadataSchemaVersionDTO toVersionDTO(MetadataSchema schema) {
        return MetadataSchemaVersionDTO.builder()
                .uuid(schema.getUuid())
                .versionUuid(schema.getVersionUuid())
                .version(schema.getVersion().toString())
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
                .extendsSchemaUuids(schema.getExtendSchemas())
                .suggestedResourceName(schema.getSuggestedResourceName())
                .suggestedUrlPrefix(schema.getSuggestedUrlPrefix())
                .previousVersionUuid(schema.getPreviousVersionUuid())
                .build();
    }

    public MetadataSchema fromReleaseDTO(
            MetadataSchemaReleaseDTO reqDto, MetadataSchemaDraft draft, String versionUuid
    ) {
        return MetadataSchema.builder()
                .uuid(draft.getUuid())
                .versionUuid(versionUuid)
                .version(new SemVer(reqDto.getVersion()))
                .versionString(reqDto.getVersion())
                .type(MetadataSchemaType.CUSTOM)
                .origin(null)
                .name(draft.getName())
                .description(reqDto.getDescription())
                .definition(draft.getDefinition())
                .targetClasses(draft.getTargetClasses())
                .extendSchemas(draft.getExtendSchemas())
                .abstractSchema(draft.isAbstractSchema())
                .published(reqDto.isPublished())
                .latest(true)
                .previousVersionUuid(null)
                .suggestedResourceName(draft.getSuggestedResourceName())
                .suggestedUrlPrefix(draft.getSuggestedUrlPrefix())
                .createdAt(Instant.now())
                .build();
    }

    public MetadataSchemaDTO toDTO(
            MetadataSchema latest,
            MetadataSchemaDraft draft,
            List<MetadataSchema> schemaVersions,
            List<MetadataSchema> childSchemas
    ) {
        if (latest != null) {
            return MetadataSchemaDTO.builder()
                    .uuid(latest.getUuid())
                    .name(latest.getName())
                    .latest(toVersionDTO(latest))
                    .draft(draft == null ? null : toDraftDTO(draft, latest))
                    .versions(schemaVersions
                            .stream()
                            .map(MetadataSchema::getVersion)
                            .sorted()
                            .map(SemVer::toString)
                            .toList()
                    )
                    .extendSchemaUuids(latest.getExtendSchemas())
                    .childSchemaUuids(childSchemas
                            .stream()
                            .map(MetadataSchema::getUuid)
                            .distinct()
                            .toList()
                    )
                    .build();
        }
        if (draft != null) {
            return MetadataSchemaDTO.builder()
                    .uuid(draft.getUuid())
                    .name(draft.getName())
                    .latest(null)
                    .draft(toDraftDTO(draft, latest))
                    .versions(schemaVersions
                                    .stream()
                                    .map(MetadataSchema::getVersion)
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

    public MetadataSchema fromUpdateDTO(MetadataSchema schema, MetadataSchemaUpdateDTO reqDto) {
        return
                schema
                        .toBuilder()
                        .name(reqDto.getName())
                        .description(reqDto.getDescription())
                        .published(reqDto.isPublished())
                        .build();
    }

    public MetadataSchemaDraft toDraft(MetadataSchema schema) {
        return MetadataSchemaDraft.builder()
                .uuid(schema.getUuid())
                .name(schema.getName())
                .name(schema.getName())
                .description(schema.getDescription())
                .abstractSchema(schema.isAbstractSchema())
                .definition(schema.getDefinition())
                .extendSchemas(schema.getExtendSchemas())
                .suggestedResourceName(schema.getSuggestedResourceName())
                .suggestedUrlPrefix(schema.getSuggestedUrlPrefix())
                .build();
    }

    public MetadataSchemaVersionDTO toPublishedVersionDTO(
            MetadataSchema schema, String persistentUrl
    ) {
        final MetadataSchemaVersionDTO dto = toVersionDTO(schema);
        if (dto.getOrigin() == null) {
            dto.setOrigin(persistentUrl);
        }
        dto.setImportedFrom(persistentUrl);
        return dto;
    }

    public MetadataSchema fromRemoteVersion(MetadataSchemaVersionDTO remoteVersion) {
        return MetadataSchema.builder()
                .uuid(remoteVersion.getUuid())
                .versionUuid(remoteVersion.getVersionUuid())
                .name(remoteVersion.getName())
                .description(remoteVersion.getDescription())
                .definition(remoteVersion.getDefinition())
                .versionString(remoteVersion.getVersion())
                .origin(remoteVersion.getOrigin())
                .importedFrom(remoteVersion.getImportedFrom())
                .extendSchemas(remoteVersion.getExtendsSchemaUuids())
                .targetClasses(remoteVersion.getTargetClasses())
                .abstractSchema(remoteVersion.isAbstractSchema())
                .published(false)
                .suggestedResourceName(remoteVersion.getSuggestedResourceName())
                .suggestedUrlPrefix(remoteVersion.getSuggestedUrlPrefix())
                .type(MetadataSchemaType.REFERENCE)
                .previousVersionUuid(remoteVersion.getPreviousVersionUuid())
                .createdAt(Instant.now())
                .build();
    }

    public MetadataSchema fromRemoteVersion(
            MetadataSchemaVersionDTO remoteVersion, MetadataSchema schema
    ) {
        return schema.toBuilder()
                .name(remoteVersion.getName())
                .description(remoteVersion.getDescription())
                .definition(remoteVersion.getDefinition())
                .extendSchemas(remoteVersion.getExtendsSchemaUuids())
                .type(MetadataSchemaType.REFERENCE)
                .targetClasses(remoteVersion.getTargetClasses())
                .abstractSchema(remoteVersion.isAbstractSchema())
                .published(schema.isPublished())
                .origin(remoteVersion.getOrigin())
                .importedFrom(remoteVersion.getImportedFrom())
                .suggestedUrlPrefix(remoteVersion.getSuggestedUrlPrefix())
                .suggestedResourceName(remoteVersion.getSuggestedResourceName())
                .versionString(remoteVersion.getVersion())
                .createdAt(Instant.now())
                .build();
    }
}
