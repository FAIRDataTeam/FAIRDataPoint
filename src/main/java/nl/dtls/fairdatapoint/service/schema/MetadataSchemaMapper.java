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
        return
                new MetadataSchemaDraft(
                        null,
                        uuid,
                        dto.getName(),
                        dto.getDescription(),
                        dto.isAbstractSchema(),
                        dto.getDefinition(),
                        MetadataSchemaShaclUtils.extractTargetClasses(dto.getDefinition()),
                        dto.getExtendsSchemaUuids(),
                        now,
                        now
                );

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
        return
                new MetadataSchemaDraftDTO(
                        draft.getUuid(),
                        draft.getName(),
                        draft.getDescription(),
                        draft.isAbstractSchema(),
                        draft.getDefinition(),
                        draft.getExtendSchemas()
                );
    }

    public MetadataSchemaDraftDTO toDraftDTO(MetadataSchema draft) {
        return
                new MetadataSchemaDraftDTO(
                        draft.getUuid(),
                        draft.getName(),
                        draft.getDescription(),
                        draft.isAbstractSchema(),
                        draft.getDefinition(),
                        draft.getExtendSchemas()
                );
    }

    public MetadataSchemaVersionDTO toVersionDTO(MetadataSchema schema) {
        return
                new MetadataSchemaVersionDTO(
                        schema.getUuid(),
                        schema.getVersion().toString(),
                        schema.getName(),
                        schema.isPublished(),
                        schema.isAbstractSchema(),
                        schema.isLatest(),
                        schema.getType(),
                        schema.getOrigin(),
                        schema.getDefinition(),
                        schema.getDescription(),
                        schema.getTargetClasses(),
                        schema.getExtendSchemas()
                );
    }

    public MetadataSchema fromPublishDTO(MetadataSchemaPublishDTO reqDto, MetadataSchemaDraft draft) {
        return
                new MetadataSchema(
                        null,
                        draft.getUuid(),
                        reqDto.getVersion(),
                        new SemVer(reqDto.getVersion()),
                        draft.getName(),
                        reqDto.getDescription(),
                        draft.getDefinition(),
                        draft.getTargetClasses(),
                        draft.getExtendSchemas(),
                        MetadataSchemaType.CUSTOM,
                        null,
                        true,
                        reqDto.isPublished(),
                        draft.isAbstractSchema(),
                        Instant.now(),
                        null
                );
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
        return
                new MetadataSchemaRemoteDTO(
                        new MetadataSchemaOrigin(
                                persistentUrl + "/metadata-schemas/" + schema.getUuid(),
                                persistentUrl,
                                schema.getUuid()
                        ),
                        schema.getVersionString(),
                        schema.getName(),
                        schema.getDefinition(),
                        schema.getDescription(),
                        schema.isAbstractSchema(),
                        schema.getExtendSchemas()
                );
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
}
