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
package org.fairdatapoint.service.boostrap;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatapoint.service.boostrap.components.*;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class BootstrapService {
    private final UserBootstrapper userBootstrapper;
    private final SettingsBootstrapper settingsBootstrapper;
    private final MembershipBootstrapper membershipBootstrapper;
    private final MetadataRecordsBootstrapper metadataRecordsBootstrapper;
    private final MetadataSchemaBootstrapper metadataSchemaBootstrapper;
    private final MetadataSchemaVersionsBootstrapper metadataSchemaVersionsBootstrapper;
    private final ResourceDefinitionBootstrapper resourceDefinitionBootstrapper;
    private final ResourceDefinitionChildrenBootstrapper resourceDefinitionChildrenBootstrapper;

    @Transactional
    public void bootstrapFromDir(String dataPath) {
        final Path basePath = Path.of(dataPath);
        final BootstrapContext context = new BootstrapContext();
        log.info("Bootstrap process started");

        if (!basePath.toFile().exists() || !basePath.toFile().isDirectory()) {
            log.warn("Bootstrap directory {} does not exist or is not a directory, skipping bootstrapping", dataPath);
            return;
        }

        // Settings
        if (settingsBootstrapper.shouldBootstrap()) {
            settingsBootstrapper.bootstrapFromJson(basePath.resolve("settings"), context);
        }
        else {
            log.info("Settings already exist, skipping settings bootstrapping");
        }

        // User (and related entities)
        if (userBootstrapper.shouldBootstrap()) {
            userBootstrapper.bootstrapAllFromDir(basePath.resolve("users"), context);
        }
        else {
            log.info("Users already exist, skipping user bootstrapping");
        }

        // Metadata Schemas
        if (metadataSchemaBootstrapper.shouldBootstrap()) {
            final Path dir = basePath.resolve("metadata-schemas");
            metadataSchemaBootstrapper.bootstrapAllFromDir(dir, context);
            metadataSchemaVersionsBootstrapper.bootstrapAllFromDir(dir, context);
        }
        else {
            log.info("Metadata Schemas already exist, skipping metadata schema bootstrapping");
        }

        // Resource Definitions
        if (resourceDefinitionBootstrapper.shouldBootstrap()) {
            final Path dir = basePath.resolve("resource-definitions");
            resourceDefinitionBootstrapper.bootstrapAllFromDir(dir, context);
            resourceDefinitionChildrenBootstrapper.bootstrapAllFromDir(dir, context);
        }
        else {
            log.info("Resource Definitions already exist, skipping resource definition bootstrapping");
        }

        // Memberships
        if (membershipBootstrapper.shouldBootstrap()) {
            membershipBootstrapper.bootstrapAllFromDir(basePath.resolve("memberships"), context);
        }
        else {
            log.info("Memberships already exist, skipping membership bootstrapping");
        }

        // RDF Records
        if (metadataRecordsBootstrapper.shouldBootstrap()) {
            metadataRecordsBootstrapper.bootstrapAllFromDir(basePath.resolve("records"), context);
        }
        else {
            log.info("Metadata Records already exist, skipping metadata records bootstrapping");
        }

        log.info("Bootstrap process finished");
    }
}
