/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.reset;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fairdatateam.fairdatapoint.rdf.metadata.Metadata;
import org.fairdatateam.fairdatapoint.rdf.metadata.MetadataState;
import org.fairdatateam.fairdatapoint.rdf.metadata.MetadataStateRepository;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionRepository;
import org.fairdatateam.fairdatapoint.rdf.metadata.MetadataServiceException;
import org.fairdatateam.fairdatapoint.rdf.metadata.GenericMetadataService;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionCache;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionTargetClassesCache;
import org.fairdatateam.fairdatapoint.rdf.schema.MetadataSchemaRepository;
import org.fairdatateam.fairdatapoint.security.acl.AclEntryJdbcRepository;
import org.fairdatateam.fairdatapoint.security.apikey.ApiKeyRepository;
import org.fairdatateam.fairdatapoint.security.membership.MemberService;
import org.fairdatateam.fairdatapoint.security.membership.MembershipRepository;
import org.fairdatateam.fairdatapoint.settings.SettingsService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.fairdatateam.fairdatapoint.user.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.model.AclCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResetService {

    @Value("${metadataProperties.accessRightsDescription:This resource has no access restriction}")
    private String accessRightsDescription;

    private final AclCache aclCache;

    private final AclEntryJdbcRepository aclEntryJdbcRepository;

    private final ApiKeyRepository apiKeyRepository;

    private final GenericMetadataService genericMetadataService;

    private final IRI language;

    private final IRI license;

    private final MemberService memberService;

    private final MembershipRepository membershipRepository;

    private final MetadataStateRepository metadataStateRepository;

    private final MetadataSchemaRepository metadataSchemaRepository;

    @Qualifier("persistentUrl")
    private final String persistentUrl;

    private final Repository repository;

    private final ResourceDefinitionCache resourceDefinitionCache;

    private final ResourceDefinitionRepository resourceDefinitionRepository;

    private final ResourceDefinitionTargetClassesCache resourceDefinitionTargetClassesCache;

    private final SettingsService settingsService;

    private final UserRepository userRepository;

    // the transaction covers the relational part of the reset, i.e. the ACL tables; the triple
    // store and MongoDB are written outside of it, which is accepted during the transition
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void resetToFactoryDefaults(ResetDTO reqDto) throws Exception {
        log.info("Resetting to factory defaults");
        if (reqDto.isSettings()) {
            settingsService.resetSettings();
        }
        if (reqDto.isUsers() || reqDto.isMetadata()) {
            clearMemberships();
            restoreDefaultMemberships();
        }
        if (reqDto.isUsers()) {
            clearApiKeys();
            clearUsers();
            restoreDefaultUsers();
        }
        if (reqDto.isMetadata()) {
            clearMetadata();
            restoreDefaultMetadata();
        }
        if (reqDto.isResourceDefinitions()) {
            clearResourceDefinitions();
            clearMetadataSchemas();
            restoreDefaultMetadataSchemas();
            restoreDefaultResourceDefinitions();
        }
        resourceDefinitionCache.computeCache();
        resourceDefinitionTargetClassesCache.computeCache();
    }

    private void clearApiKeys() {
        log.debug("Clearing API keys");
        apiKeyRepository.deleteAll();
    }

    private void clearMemberships() {
        log.debug("Clearing memberships");
        // the baseline schema declares membership_permission and membership_allowed_entity with
        // ON DELETE CASCADE, so the batch delete below removes the child rows too; it runs
        // immediately, ahead of the factory memberships being re-inserted in this same transaction
        membershipRepository.deleteAllInBatch();
        log.debug("Clearing access control lists");
        aclEntryJdbcRepository.deleteAll();
        aclCache.clearCache();
    }

    private void clearUsers() {
        log.debug("Clearing users");
        userRepository.deleteAll();
    }

    private void clearMetadataSchemas() {
        log.debug("Clearing metadata schemas");
        metadataSchemaRepository.deleteAll();
    }

    private void clearResourceDefinitions() {
        log.debug("Clearing resource definitions");
        resourceDefinitionRepository.deleteAll();
    }

    private void clearMetadata() throws MetadataServiceException {
        log.debug("Clearing metadata");
        final Optional<ResourceDefinition> resourceDefinition =
                resourceDefinitionRepository.findByUrlPrefix("");
        if (resourceDefinition.isPresent()) {
            genericMetadataService.delete(i(persistentUrl), resourceDefinition.get());
            metadataStateRepository.deleteAll();
        }
    }

    private void restoreDefaultUsers() {
        log.debug("Creating default users");
        userRepository.save(FactoryDefaults.USER_ALBERT);
        userRepository.save(FactoryDefaults.USER_NIKOLA);
    }

    private void restoreDefaultMemberships() {
        log.debug("Creating default memberships");
        membershipRepository.save(FactoryDefaults.membershipOwner());
        membershipRepository.save(FactoryDefaults.membershipDataProvider());

        log.debug("Creating the access control list of the repository record");
        memberService.createOwner(
                persistentUrl, Metadata.class, FactoryDefaults.USER_ALBERT.getUuid().toString()
        );
    }

    private void restoreDefaultMetadata() {
        log.debug("Creating default metadata");
        try (RepositoryConnection conn = repository.getConnection()) {
            final List<Statement> statements = FactoryDefaults.fdpStatements(
                    persistentUrl,
                    license,
                    language,
                    accessRightsDescription
            );
            conn.add(statements);
            metadataStateRepository.save(i(persistentUrl), MetadataState.PUBLISHED);
        }
        catch (RepositoryException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private void restoreDefaultMetadataSchemas() throws Exception {
        log.debug("Creating default metadata schemas");
        metadataSchemaRepository.save(FactoryDefaults.schemaResource());
        metadataSchemaRepository.save(FactoryDefaults.schemaDataService());
        metadataSchemaRepository.save(FactoryDefaults.schemaMetadataService());
        metadataSchemaRepository.save(FactoryDefaults.schemaFDP());
        metadataSchemaRepository.save(FactoryDefaults.schemaCatalog());
        metadataSchemaRepository.save(FactoryDefaults.schemaDataset());
        metadataSchemaRepository.save(FactoryDefaults.schemaDistribution());
    }

    private void restoreDefaultResourceDefinitions() {
        log.debug("Creating default resource definitions");
        resourceDefinitionRepository.save(FactoryDefaults.RESOURCE_DEFINITION_FDP);
        resourceDefinitionRepository.save(FactoryDefaults.RESOURCE_DEFINITION_CATALOG);
        resourceDefinitionRepository.save(FactoryDefaults.RESOURCE_DEFINITION_DATASET);
        resourceDefinitionRepository.save(FactoryDefaults.RESOURCE_DEFINITION_DISTRIBUTION);
    }
}
