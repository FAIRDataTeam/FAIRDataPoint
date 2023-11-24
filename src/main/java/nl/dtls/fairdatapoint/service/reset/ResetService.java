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
package nl.dtls.fairdatapoint.service.reset;

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.reset.ResetDTO;
import nl.dtls.fairdatapoint.database.db.repository.*;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.generic.GenericMetadataService;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionTargetClassesCache;
import nl.dtls.fairdatapoint.service.settings.SettingsService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.model.AclCache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Slf4j
@Service
public class ResetService {

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Value("${metadataProperties.accessRightsDescription:This resource has no access restriction}")
    private String accessRightsDescription;

    @Autowired
    private IRI license;

    @Autowired
    private IRI language;

    @Autowired
    private Repository mainRepository;

    @Autowired
    private Repository draftsRepository;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private AclCache aclCache;

    @Autowired
    private UserAccountRepository userRepository;

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    @Autowired
    private ResourceDefinitionTargetClassesCache resourceDefinitionTargetClassesCache;

    @Autowired
    private GenericMetadataService genericMetadataService;

    @Autowired
    private SettingsService settingsService;

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
        membershipRepository.deleteAll();
        log.debug("Clearing ACL cache");
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
        }
    }

    private void restoreDefaultUsers() {
        log.debug("Creating default users");
        // TODO: data seed from specs
    }

    private void restoreDefaultMemberships() {
        log.debug("Creating default memberships");
        // TODO: data seed from specs
    }

    private void restoreDefaultMetadata() {
        log.debug("Creating default metadata");
        try (RepositoryConnection conn = mainRepository.getConnection()) {
            final List<Statement> statements = FactoryDefaults.fdpStatements(
                    persistentUrl,
                    license,
                    language,
                    accessRightsDescription
            );
            conn.add(statements);
        }
        catch (RepositoryException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private void restoreDefaultMetadataSchemas() throws Exception {
        log.debug("Creating default metadata schemas");
        // TODO: data seed from specs
    }

    private void restoreDefaultResourceDefinitions() {
        log.debug("Creating default resource definitions");
        // TODO: data seed from specs
    }
}
