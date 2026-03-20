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
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaVersionDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaDraftRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.util.RdfIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class MetadataSchemaValidator {

    @Autowired
    private MetadataSchemaDraftRepository metadataSchemaDraftRepository;

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    private void validateShacl(String shaclDefinition) {
        try {
            RdfIOUtil.read(shaclDefinition, "");
        }
        catch (ValidationException exception) {
            throw new ValidationException("Unable to read SHACL definition");
        }
    }

    public void validateNotUsed(String uuid) {
        final List<ResourceDefinition> resourceDefinitions =
                resourceDefinitionRepository.findByMetadataSchemaUuidsIsContaining(uuid);
        if (!resourceDefinitions.isEmpty()) {
            throw new ValidationException(
                    format("Schema is used in %d resource definitions",
                            resourceDefinitions.size())
            );
        }
        final List<MetadataSchema> children =
                metadataSchemaRepository.findAllByExtendSchemasContains(uuid);
        if (!children.isEmpty()) {
            throw new ValidationException(
                    format("Schema is used in %d other schemas", children.size())
            );
        }
    }

    public void validateNoExtendsCycle(String uuid, List<String> extendSchemaUuids) {
        if (extendSchemaUuids.contains(uuid)) {
            throw new ValidationException("Extends-cycle detected for the metadata schema");
        }
        extendSchemaUuids.forEach(schemaUuid -> {
            final Optional<MetadataSchema> oSchema =
                    metadataSchemaRepository.findByUuidAndLatestIsTrue(schemaUuid);
            oSchema.ifPresent(schema -> validateNoExtendsCycle(uuid, schema.getExtendSchemas()));
        });
    }

    public void validate(MetadataSchema newVersion, MetadataSchema previousVersion) {
        // Check previous
        if (previousVersion != null) {
            if (previousVersion.getVersion().compareTo(newVersion.getVersion()) >= 0) {
                throw new ValidationException("Version is not higher than previous");
            }
            if (previousVersion.isLatest()) {
                throw new ValidationException("Older version is still marked as latest");
            }
        }
        // Check SHACL definition
        validateShacl(newVersion.getDefinition());
    }

    public void validate(MetadataSchema metadataSchema) {
        validate(metadataSchema, null);
    }

    public void validate(MetadataSchemaVersionDTO reqDto) {
        // Check SHACL definition
        validateShacl(reqDto.getDefinition());
    }

    public void validate(MetadataSchemaChangeDTO reqDto) {
        // Check SHACL definition
        validateShacl(reqDto.getDefinition());
    }

    private List<String> getMissingSchemaUuids(List<String> schemasUuids) {
        final Set<String> existingUuids = metadataSchemaRepository
                .findAllByLatestIsTrue()
                .stream()
                .map(MetadataSchema::getUuid)
                .collect(Collectors.toSet());
        return schemasUuids
                .stream()
                .filter(schemaUuid -> !existingUuids.contains(schemaUuid))
                .toList();
    }

    public void validateAllExist(List<String> schemasUuids) {
        final List<String> missing = getMissingSchemaUuids(schemasUuids);
        if (!missing.isEmpty()) {
            throw new ValidationException(format("Metadata schemas not found: %s", missing));
        }
    }
}
