package nl.dtls.fairdatapoint.service.reset;

import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.reset.ResetDTO;
import nl.dtls.fairdatapoint.database.mongo.repository.*;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import nl.dtls.fairdatapoint.service.metadata.generic.GenericMetadataService;
import nl.dtls.fairdatapoint.service.resource.ResourceDefinitionCache;
import nl.dtls.fairdatapoint.vocabulary.DATACITE;
import nl.dtls.fairdatapoint.vocabulary.FDP;
import nl.dtls.fairdatapoint.vocabulary.R3D;
import nl.dtls.fairdatapoint.vocabulary.Sio;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @Qualifier("metadataMetrics")
    private Map<String, String> metadataMetrics;

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
    private ShapeRepository shapeRepository;

    @Autowired
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Autowired
    private ResourceDefinitionCache resourceDefinitionCache;

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private GenericMetadataService genericMetadataService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PreAuthorize("hasRole('ADMIN')")
    public void resetToFactoryDefaults(ResetDTO reqDto) throws Exception {
        log.info("Resetting to factory defaults");
        if (reqDto.isUser() || reqDto.isMetadata()) {
            clearMemberships();
            restoreDefaultMemberships();
        }
        if (reqDto.isUser()) {
            clearApiKeys();
            clearUsers();
            restoreDefaultUsers();
        }
        if (reqDto.isMetadata()) {
            clearMetadata();
            restoreDefaultMetadata();
        }
        if (reqDto.isResourceDefinition()) {
            clearShapes();
            clearResourceDefinitions();
            restoreDefaultShapes();
            restoreDefaultResourceDefinitions();
        }
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

    private void clearShapes() {
        log.debug("Clearing SHACL shapes");
        shapeRepository.deleteAll();
    }

    private void clearResourceDefinitions() {
        log.debug("Clearing resource definitions");
        resourceDefinitionRepository.deleteAll();
        resourceDefinitionCache.computeCache();
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
            List<Statement> s = FactoryDefaults.repositoryStatements(
                    persistentUrl,
                    metadataMetrics,
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

    private void restoreDefaultShapes() throws Exception {
        log.debug("Creating default shapes");
        shapeRepository.save(FactoryDefaults.shapeResource());
        shapeRepository.save(FactoryDefaults.shapeRepository());
        shapeRepository.save(FactoryDefaults.shapeCatalog());
        shapeRepository.save(FactoryDefaults.shapeDataset());
        shapeRepository.save(FactoryDefaults.shapeDistribution());
    }

    private void restoreDefaultResourceDefinitions() {
        log.debug("Creating default resource definitions");
        resourceDefinitionRepository.save(FactoryDefaults.RESOURCE_DEFINITION_REPOSITORY);
        resourceDefinitionRepository.save(FactoryDefaults.RESOURCE_DEFINITION_CATALOG);
        resourceDefinitionRepository.save(FactoryDefaults.RESOURCE_DEFINITION_DATASET);
        resourceDefinitionRepository.save(FactoryDefaults.RESOURCE_DEFINITION_DISTRIBUTION);
    }
}
