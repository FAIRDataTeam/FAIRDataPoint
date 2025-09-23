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
import org.fairdatapoint.database.db.repository.MetadataSchemaExtensionRepository;
import org.fairdatapoint.database.db.repository.MetadataSchemaVersionRepository;
import org.fairdatapoint.entity.schema.MetadataSchema;
import org.fairdatapoint.entity.schema.MetadataSchemaVersion;
import org.fairdatapoint.service.bootstrap.BootstrapContext;
import org.fairdatapoint.service.bootstrap.fixtures.MetadataSchemaFixture;
import org.fairdatapoint.service.bootstrap.fixtures.MetadataSchemaVersionFixture;
import org.fairdatapoint.service.schema.MetadataSchemaMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Component
public class MetadataSchemaVersionsBootstrapper extends AbstractBootstrapper {
    private final MetadataSchemaVersionRepository metadataSchemaVersionRepository;
    private final MetadataSchemaExtensionRepository metadataSchemaExtensionRepository;
    private final MetadataSchemaMapper metadataSchemaMapper;

    public MetadataSchemaVersionsBootstrapper(ObjectMapper objectMapper,
                                              MetadataSchemaVersionRepository metadataSchemaVersionRepository,
                                              MetadataSchemaExtensionRepository metadataSchemaExtensionRepository,
                                              MetadataSchemaMapper metadataSchemaMapper) {
        super(objectMapper);
        this.metadataSchemaVersionRepository = metadataSchemaVersionRepository;
        this.metadataSchemaExtensionRepository = metadataSchemaExtensionRepository;
        this.metadataSchemaMapper = metadataSchemaMapper;
    }

    @Override
    protected JpaRepository getRepository() {
        return metadataSchemaVersionRepository;
    }

    @Override
    public void bootstrapFromJson(Path resourcePath, BootstrapContext context) {
        try {
            final MetadataSchemaFixture metadataSchemaFixture =
                    getObjectMapper().readValue(resourcePath.toFile(), MetadataSchemaFixture.class);
            final MetadataSchema metadataSchema =
                    context.getMetadataSchemas().get(metadataSchemaFixture.getUuid());
            metadataSchemaFixture.getVersions().forEach(version -> {
                final MetadataSchemaVersion metadataSchemaVersion = metadataSchemaVersionRepository.saveAndFlush(
                        fromFixture(resourcePath, version, metadataSchema)
                );

                // Extensions
                metadataSchemaExtensionRepository.saveAllAndFlush(
                        IntStream.range(0, version.getExtendsSchemaUuids().size())
                                .mapToObj(index -> {
                                    final UUID metadataSchemaUuid = version.getExtendsSchemaUuids().get(index);
                                    return metadataSchemaMapper.newExtension(
                                            metadataSchemaVersion,
                                            context.getMetadataSchemas().get(metadataSchemaUuid),
                                            index
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

    private MetadataSchemaVersion fromFixture(Path resourcePath, MetadataSchemaVersionFixture fixture,
                                              MetadataSchema schema) {
        final MetadataSchemaVersion version = metadataSchemaMapper.fromMetadataSchemaVersionFixture(fixture, schema);
        if (fixture.getDefinitionFile() != null) {
            final Path definitionPath = resourcePath.getParent().resolve(fixture.getDefinitionFile());
            try {
                final String definition = Files.readString(definitionPath);
                version.setDefinition(definition);
            }
            catch (IOException exception) {
                log.warn("Failed to read definition file for schema version: {}", definitionPath);
            }
        }
        return version;
    }
}
