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

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaChangeDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaVersionDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.service.rdf.ShaclValidator;
import nl.dtls.fairdatapoint.util.RdfIOUtil;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Component
public class MetadataSchemaValidator {

    private static final String SHACL_SHACL_FILENAME = "shacl-shacl.ttl";

    private static final Model SHACL_SHACL_DEF = loadShaclShaclDefinition();

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private ShaclValidator shaclValidator;

    @Autowired
    private String persistentUrl;

    private void validateShacl(String shaclDefinition) {
        final Model data;
        try {
            data = RdfIOUtil.read(shaclDefinition, "");
        }
        catch (ValidationException exception) {
            throw new ValidationException("Unable to read SHACL definition");
        }

        shaclValidator.validate(SHACL_SHACL_DEF, data, persistentUrl);
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

    private static Model loadShaclShaclDefinition() {
        try {
            final URL fileURL = MetadataSchemaValidator.class.getResource(SHACL_SHACL_FILENAME);
            return RdfIOUtil.read(
                    Resources.toString(fileURL, Charsets.UTF_8),
                    "http://www.w3.org/ns/shacl-shacl#"
            );
        }
        catch (IOException exception) {
            throw new RuntimeException(
                    format("Cannot load SHACL-SHACL definition: %s",
                            exception.getMessage())
            );
        }
    }
}
