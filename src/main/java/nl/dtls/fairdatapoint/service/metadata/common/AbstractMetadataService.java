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
package nl.dtls.fairdatapoint.service.metadata.common;

import com.google.common.base.Preconditions;
import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.model.AccessRights;
import nl.dtl.fairmetadata4j.model.Agent;
import nl.dtl.fairmetadata4j.model.Identifier;
import nl.dtl.fairmetadata4j.model.Metadata;
import nl.dtl.fairmetadata4j.utils.MetadataUtils;
import nl.dtl.fairmetadata4j.utils.RDFUtils;
import nl.dtl.fairmetadata4j.utils.vocabulary.DATACITE;
import nl.dtls.fairdatapoint.database.rdf.repository.MetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.MetadataRepositoryException;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.service.member.MemberService;
import nl.dtls.fairdatapoint.service.metadatametrics.FairMetadataMetricsService;
import nl.dtls.fairdatapoint.service.pid.PIDSystem;
import nl.dtls.fairdatapoint.service.search.FairSearchClient;
import nl.dtls.fairdatapoint.service.user.CurrentUserService;
import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import javax.xml.datatype.DatatypeConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static nl.dtls.fairdatapoint.util.ThrowingFunction.suppress;

/**
 * This is an abstract class for working with Metadata. Child classes are
 * defined with concrete metadata type and few template methods implemented.
 *
 * @param <T> Metadata Type
 */
public abstract class AbstractMetadataService<T extends Metadata> implements MetadataService<T> {
    protected final static ValueFactory VALUE_FACTORY = SimpleValueFactory.getInstance();

    /**
     * Specs URI, defined in child class.
     */
    protected String specs;

    /**
     * Parent Type URI, defined in child class.
     */
    protected IRI parentType;

    @Value("${metadataProperties.accessRightsDescription:This resource has no access restriction}")
    private String accessRightsDescription;

    @Value("${instance.url}")
    private String instanceUrl;

    @Autowired
    @Qualifier("language")
    private IRI language;

    @Autowired
    @Qualifier("license")
    private IRI license;

    @Autowired
    private Agent publisher;

    @Autowired
    private FairMetadataMetricsService fmMetricsService;

    @Autowired
    private FairSearchClient fseService;

    @Autowired
    protected MetadataUpdateService metadataUpdateService;

    @Autowired
    private PIDSystem pidSystem;

    @Autowired
    protected MetadataRepository storeManager;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CurrentUserService currentUserService;


    /**
     * Each child class defines its own logger.
     *
     * @return Logger
     */
    protected abstract Logger getLogger();

    /**
     * Parse a list of statements into metadata object. Each child class uses
     * a parser according to metadata it works with.
     *
     * @param statements List of statements
     * @param uri        Metadata URI
     * @return Parsed metadata object
     */
    protected abstract T parse(@Nonnull List<Statement> statements, @Nonnull IRI uri);


    /**
     * Parent should be updated after metadata is saved. This is implemented in
     * child class using appropriate method.
     *
     * @param metadata Metadata whose parent should be updated
     */
    protected abstract void updateParent(T metadata);

    @Override
    public T retrieve(@Nonnull IRI uri) throws MetadataServiceException {
        List<Statement> statements = retrieveStatements(uri);
        return parse(statements, uri);
    }

    @Override
    public List<T> retrieve(List<IRI> uris) {
        return uris
                .stream()
                .map(suppress(this::retrieve))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void store(@Nonnull T metadata) throws MetadataServiceException {
        try {
            getLogger().info("Storing {} - {}, parent {}", metadata.getTitle(), metadata.getUri(),
                    metadata.getParentURI());

            checkPreconditions(metadata);
            addDefaultValues(metadata);
            setSpecification(metadata);
            setTimestamps(metadata);

            storeManager.storeStatements(MetadataUtils.getStatements(metadata), metadata.getUri());

            updateParent(metadata);
            addPermissions(metadata);
            fseService.submitFdpUri(VALUE_FACTORY.createIRI(instanceUrl));

            getLogger().info("Stored {} - {}", metadata.getTitle(), metadata.getUri());
        } catch (DatatypeConfigurationException | MetadataException | MetadataRepositoryException e) {
            getLogger().error("Error storing distribution metadata");
            throw new MetadataServiceException(e.getMessage());
        }
    }

    @Override
    public void update(IRI uri, T metadataUpdate) throws MetadataServiceException {
        try {
            T metadata = retrieve(uri);
            updateProperties(metadata, metadataUpdate);
            storeManager.removeResource(uri);
            store(metadata);
        } catch (MetadataRepositoryException e) {
            getLogger().error("Error updating metadata");
            throw (new MetadataServiceException(e.getMessage()));
        }
    }

    private List<Statement> retrieveStatements(@Nonnull IRI uri)
            throws MetadataServiceException, ResourceNotFoundException {

        try {
            Preconditions.checkNotNull(uri, "Resource uri not be null.");
            List<Statement> statements = storeManager.retrieveResource(uri);
            if (statements.isEmpty()) {
                String msg = ("No metadata found for the uri : " + uri);
                throw new ResourceNotFoundException(msg);
            }
            return statements;
        } catch (MetadataRepositoryException ex) {
            getLogger().error("Error retrieving the metadata");
            throw new MetadataServiceException(ex.getMessage());
        }
    }

    protected void checkPreconditions(@Nonnull T metadata) throws MetadataServiceException {
        Preconditions.checkState(metadata.getParentURI() != null, "No parent uri");
        Preconditions.checkState(parentExists(metadata.getParentURI()), "Parent is not of correct type");
    }

    protected void addDefaultValues(@Nonnull T metadata) {
        // Add PID
        Identifier id = new Identifier();
        IRI pidIri = pidSystem.getURI(metadata);
        id.setUri(pidIri);
        id.setIdentifier(VALUE_FACTORY.createLiteral(pidSystem.getId(pidIri), XMLSchema.STRING));
        id.setType(DATACITE.RESOURCEIDENTIFIER);
        metadata.setIdentifier(id);

        // Add default publisher
        if (metadata.getPublisher() == null && publisher != null) {
            metadata.setPublisher(publisher);
        }

        // Add default language
        if (metadata.getLanguage() == null && language != null) {
            metadata.setLanguage(language);
        }

        // Add default license
        if (metadata.getLicense() == null && license != null) {
            metadata.setLicense(license);
        }

        // Add access rights
        if (metadata.getAccessRights() == null) {
            getLogger().info("Metadata ID is null or empty, adding default value for access rights");
            AccessRights accessRights = new AccessRights();
            accessRights.setUri(VALUE_FACTORY.createIRI(metadata.getUri().stringValue() + "#accessRights"));
            Literal description = VALUE_FACTORY.createLiteral(accessRightsDescription, XMLSchema.STRING);
            accessRights.setDescription(description);
            metadata.setAccessRights(accessRights);
        }

        // Add FAIR metrics
        metadata.setMetrics(fmMetricsService.getMetrics(metadata.getUri()));
    }

    private void setSpecification(@Nonnull T metadata) {
        if (!specs.isEmpty()) {
            metadata.setSpecification(VALUE_FACTORY.createIRI(specs));
        }
    }

    private void setTimestamps(T metadata) throws DatatypeConfigurationException {
        metadata.setIssued(RDFUtils.getCurrentTime());
        metadata.setModified(RDFUtils.getCurrentTime());
    }

    private boolean parentExists(IRI uri) throws MetadataServiceException {
        try {
            if (parentType != null) {
                return storeManager.isStatementExist(uri, RDF.TYPE, parentType);
            }
            return false;
        } catch (MetadataRepositoryException e) {
            getLogger().error("Error checking existence of subject URI");
            throw new MetadataServiceException(e.getMessage());
        }
    }

    private void updateProperties(T original, T updated) {
        try {
            PropertyUtils
                    .describe(updated)
                    .entrySet()
                    .stream()
                    .filter(e -> e.getValue() != null && !e.getKey().equals("class"))
                    .forEach(e -> {
                        try {
                            PropertyUtils.setProperty(original, e.getKey(), e.getValue());
                        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                            getLogger().error(ex.getMessage());
                        }
                    });
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            getLogger().error(ex.getMessage());
        }
    }

    private void addPermissions(T metadata) {
        Optional<User> oUser = currentUserService.getCurrentUser();
        if (oUser.isEmpty()) {
            return;
        }
        User user = oUser.get();
        String entityId = metadata.getIdentifier().getIdentifier().getLabel();
        memberService.createOwner(entityId, metadata.getClass(), user.getUuid());
    }
}
