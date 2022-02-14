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

import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaChangeDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaRemoteDTO;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaType;
import org.springframework.stereotype.Service;

@Service
public class MetadataSchemaMapper {

    public MetadataSchemaDTO toDTO(MetadataSchema metadataSchema) {
        return
                new MetadataSchemaDTO(
                        metadataSchema.getUuid(),
                        metadataSchema.getName(),
                        metadataSchema.isPublished(),
                        metadataSchema.getType(),
                        metadataSchema.getDefinition(),
                        metadataSchema.getTargetClasses().stream().sorted().toList()
                );
    }

    public MetadataSchema fromChangeDTO(MetadataSchemaChangeDTO dto, String uuid) {
        return
                new MetadataSchema(
                        null,
                        uuid,
                        dto.getName(),
                        dto.isPublished(),
                        MetadataSchemaType.CUSTOM,
                        dto.getDefinition(),
                        MetadataSchemaShaclUtils.extractTargetClasses(dto.getDefinition())
                );

    }

    public MetadataSchema fromChangeDTO(MetadataSchemaChangeDTO dto, MetadataSchema schema) {
        return
                schema
                        .toBuilder()
                        .name(dto.getName())
                        .published(dto.isPublished())
                        .definition(dto.getDefinition())
                        .targetClasses(MetadataSchemaShaclUtils.extractTargetClasses(dto.getDefinition()))
                        .build();
    }

    public MetadataSchemaRemoteDTO toRemoteDTO(String fdpUrl, MetadataSchemaDTO schema) {
        return
                new MetadataSchemaRemoteDTO(
                        fdpUrl,
                        schema.getUuid(),
                        schema.getName(),
                        schema.getDefinition()
                );
    }

    public MetadataSchemaChangeDTO fromRemoteDTO(MetadataSchemaRemoteDTO schema) {
        return
                new MetadataSchemaChangeDTO(
                        schema.getName(),
                        false,
                        schema.getDefinition()
                );
    }
}
