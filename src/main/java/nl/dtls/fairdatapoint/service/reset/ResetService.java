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

import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.reset.ResetDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.*;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.generic.GenericMetadataService;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionTargetClassesCache;
import nl.dtls.fairdatapoint.service.settings.SettingsService;
import org.bson.Document;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.dao.AclRepository;
import org.springframework.security.acls.model.AclCache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.l;

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
    protected Repository repository;

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private AclRepository aclRepository;

    @Autowired
    private AclCache aclCache;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    @Autowired
    private ResourceDefinitionTargetClassesCache resourceDefinitionTargetClassesCache;

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private GenericMetadataService genericMetadataService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private MongoTemplate mongoTemplate;

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
            clearMetadataSchemas();
            clearResourceDefinitions();
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
        aclRepository.deleteAll();
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
        Optional<ResourceDefinition> rd = resourceDefinitionRepository.findByUrlPrefix("");
        if (rd.isPresent()) {
            genericMetadataService.delete(i(persistentUrl), rd.get());
            metadataRepository.deleteAll();
        }
    }

    private void restoreDefaultUsers() {
        log.debug("Creating default users");
        userRepository.save(FactoryDefaults.USER_ALBERT);
        userRepository.save(FactoryDefaults.USER_NIKOLA);
    }

    private void restoreDefaultMemberships() {
        log.debug("Creating default users");
        membershipRepository.save(FactoryDefaults.MEMBERSHIP_OWNER);
        membershipRepository.save(FactoryDefaults.MEMBERSHIP_DATA_PROVIDER);

        MongoCollection<Document> aclCol = mongoTemplate.getCollection("ACL");
        aclCol.insertOne(FactoryDefaults.aclRepository(persistentUrl));
    }

    private void restoreDefaultMetadata() {
        log.debug("Creating default metadata");
        try (RepositoryConnection conn = repository.getConnection()) {
            List<Statement> s = FactoryDefaults.fdpStatements(
                    persistentUrl,
                    license,
                    language,
                    accessRightsDescription
            );
            conn.add(s);
            metadataRepository.save(FactoryDefaults.metadataRepository(persistentUrl));
        } catch (RepositoryException e) {
            log.error(e.getMessage(), e);
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
