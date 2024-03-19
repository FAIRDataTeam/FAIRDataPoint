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

import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaChangeDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaVersionDTO;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaExtensionRepository;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaVersionRepository;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaExtension;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaState;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaVersion;
import nl.dtls.fairdatapoint.util.RdfIOUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class MetadataSchemaValidator {

    private final MetadataSchemaVersionRepository versionRepository;

    private final MetadataSchemaExtensionRepository extensionRepository;

    private void validateShacl(String shaclDefinition) {
        try {
            RdfIOUtil.read(shaclDefinition, "");
        }
        catch (ValidationException exception) {
            throw new ValidationException("Unable to read SHACL definition");
        }
    }

    public void validateNotUsed(MetadataSchema schema) {
        if (!schema.getUsages().isEmpty()) {
            throw new ValidationException(
                    format("Schema is used in %d resource definitions",
                            schema.getUsages().size())
            );
        }
        final List<MetadataSchemaExtension> extensions =
                extensionRepository.findByExtendedMetadataSchema(schema);
        if (!extensions.isEmpty()) {
            throw new ValidationException(
                    format("Schema is used in %d other schemas", extensions.size())
            );
        }
    }

    public void validateNoExtendsCycle(UUID uuid, List<UUID> extendSchemaUuids) {
        if (extendSchemaUuids.contains(uuid)) {
            throw new ValidationException("Extends-cycle detected for the metadata schema");
        }
        extendSchemaUuids.forEach(schemaUuid -> {
            final Optional<MetadataSchemaVersion> oSchema =
                    versionRepository.getLatestBySchemaUuid(schemaUuid);
            oSchema.ifPresent(schema -> {
                validateNoExtendsCycle(uuid,
                         schema.getExtensions()
                                 .stream()
                                 .map(extension -> extension.getExtendedMetadataSchema().getUuid())
                                 .toList()
                );
            });
        });
    }

    public void validate(MetadataSchemaVersion newVersion, MetadataSchemaVersion previousVersion) {
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

    public void validate(MetadataSchemaVersion schemaVersion) {
        validate(schemaVersion, null);
    }

    public void validate(MetadataSchemaVersionDTO reqDto) {
        // Check SHACL definition
        validateShacl(reqDto.getDefinition());
    }

    public void validate(MetadataSchemaChangeDTO reqDto) {
        // Check SHACL definition
        validateShacl(reqDto.getDefinition());
    }

    private List<UUID> getMissingSchemaUuids(List<UUID> schemasUuids) {
        final Set<UUID> existingUuids = versionRepository
                .findAllByState(MetadataSchemaState.LATEST)
                .stream()
                .map(ver -> ver.getSchema().getUuid())
                .collect(Collectors.toSet());
        return schemasUuids
                .stream()
                .filter(schemaUuid -> !existingUuids.contains(schemaUuid))
                .toList();
    }

    public void validateAllExist(List<UUID> schemasUuids) {
        final List<UUID> missing = getMissingSchemaUuids(schemasUuids);
        if (!missing.isEmpty()) {
            throw new ValidationException(format("Metadata schemas not found: %s", missing));
        }
    }
}
