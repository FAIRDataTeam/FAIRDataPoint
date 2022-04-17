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

import java.time.Instant;
import java.util.List;

@Service
public class MetadataSchemaMapper {

    public MetadataSchemaDraft fromChangeDTO(MetadataSchemaChangeDTO dto, String uuid) {
        Instant now = Instant.now();
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

    public MetadataSchemaDraft fromChangeDTO(MetadataSchemaChangeDTO dto, MetadataSchemaDraft draft) {
        return
                draft
                        .toBuilder()
                        .name(dto.getName())
                        .abstractSchema(dto.isAbstractSchema())
                        .description(dto.getDescription())
                        .definition(dto.getDefinition())
                        .extendSchemas(dto.getExtendsSchemaUuids())
                        .targetClasses(MetadataSchemaShaclUtils.extractTargetClasses(dto.getDefinition()))
                        .build();
    }

    public MetadataSchemaDraftDTO toDraftDTO(MetadataSchemaDraft draft) {
        return MetadataSchemaDraftDTO.builder()
                .uuid(draft.getUuid())
                .name(draft.getName())
                .description(draft.getDescription())
                .abstractSchema(draft.isAbstractSchema())
                .definition(draft.getDefinition())
                .extendsSchemaUuids(draft.getExtendSchemas())
                .suggestedResourceName(draft.getSuggestedResourceName())
                .suggestedUrlPrefix(draft.getSuggestedUrlPrefix())
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
                .build();
    }

    public MetadataSchemaVersionDTO toVersionDTO(MetadataSchema schema) {
        return MetadataSchemaVersionDTO.builder()
                .uuid(schema.getUuid())
                .version(schema.getVersion().toString())
                .name(schema.getName())
                .description(schema.getDescription())
                .published(schema.isPublished())
                .abstractSchema(schema.isAbstractSchema())
                .latest(schema.isLatest())
                .type(schema.getType())
                .origin(schema.getOrigin())
                .definition(schema.getDefinition())
                .targetClasses(schema.getTargetClasses())
                .extendsSchemaUuids(schema.getExtendSchemas())
                .suggestedResourceName(schema.getSuggestedResourceName())
                .suggestedUrlPrefix(schema.getSuggestedUrlPrefix())
                .build();
    }

    public MetadataSchema fromReleaseDTO(MetadataSchemaReleaseDTO reqDto, MetadataSchemaDraft draft) {
        return MetadataSchema.builder()
                .uuid(draft.getUuid())
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
                .previousVersion(null)
                .suggestedResourceName(draft.getSuggestedResourceName())
                .suggestedUrlPrefix(draft.getSuggestedUrlPrefix())
                .createdAt(Instant.now())
                .build();
    }

    public MetadataSchemaDTO toDTO(MetadataSchema latest, MetadataSchemaDraft draft, List<MetadataSchema> schemaVersions) {
        if (latest != null) {
            return
                    new MetadataSchemaDTO(
                            latest.getUuid(),
                            latest.getName(),
                            toVersionDTO(latest),
                            draft == null ? null : toDraftDTO(draft),
                            schemaVersions.stream().map(MetadataSchema::getVersion).sorted().map(SemVer::toString).toList()
                    );
        }
        if (draft != null) {
            return
                    new MetadataSchemaDTO(
                            draft.getUuid(),
                            draft.getName(),
                            null,
                            toDraftDTO(draft),
                            schemaVersions.stream().map(MetadataSchema::getVersion).sorted().map(SemVer::toString).toList()
                    );
        }
        return null;
    }

    public MetadataSchemaRemoteDTO toRemoteDTO(MetadataSchema schema, String persistentUrl) {
        return MetadataSchemaRemoteDTO.builder()
                .origin(
                        new MetadataSchemaOrigin(
                            persistentUrl + "/metadata-schemas/" + schema.getUuid(),
                            persistentUrl,
                            schema.getUuid()
                        )
                )
                .version(schema.getVersionString())
                .name(schema.getName())
                .description(schema.getDescription())
                .definition(schema.getDefinition())
                .abstractSchema(schema.isAbstractSchema())
                .extendsSchemaUuids(schema.getExtendSchemas())
                .suggestedResourceName(schema.getSuggestedResourceName())
                .suggestedUrlPrefix(schema.getSuggestedUrlPrefix())
                .build();
    }

    public MetadataSchema fromRemoteDTO(MetadataSchemaRemoteDTO remoteDto, String uuid) {
        return
                MetadataSchema
                        .builder()
                        .uuid(uuid)
                        .name(remoteDto.getName())
                        .versionString(remoteDto.getVersion())
                        .version(new SemVer(remoteDto.getVersion()))
                        .description(remoteDto.getDescription())
                        .definition(remoteDto.getDefinition())
                        .abstractSchema(remoteDto.isAbstractSchema())
                        .published(false)
                        .type(MetadataSchemaType.CUSTOM)
                        .origin(remoteDto.getOrigin())
                        .extendSchemas(remoteDto.getExtendsSchemaUuids()) // TODO!
                        .previousVersion(null) // TODO!
                        .build();
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
}
