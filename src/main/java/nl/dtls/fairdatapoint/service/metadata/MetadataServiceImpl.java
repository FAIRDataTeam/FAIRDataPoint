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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.metadata;

import com.google.common.base.Preconditions;
import nl.dtl.fairmetadata4j.io.*;
import nl.dtl.fairmetadata4j.model.*;
import nl.dtl.fairmetadata4j.utils.MetadataParserUtils;
import nl.dtl.fairmetadata4j.utils.MetadataUtils;
import nl.dtl.fairmetadata4j.utils.RDFUtils;
import nl.dtl.fairmetadata4j.utils.vocabulary.DATACITE;
import nl.dtl.fairmetadata4j.utils.vocabulary.FDP;
import nl.dtl.fairmetadata4j.utils.vocabulary.R3D;
import nl.dtls.fairdatapoint.repository.metadata.MetadataRepository;
import nl.dtls.fairdatapoint.repository.metadata.MetadataRepositoryException;
import nl.dtls.fairdatapoint.service.metadatametrics.FairMetadataMetricsService;
import nl.dtls.fairdatapoint.service.pid.PIDSystem;
import nl.dtls.fairdatapoint.service.search.FairSearchClient;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MetadataServiceImpl implements MetadataService {

    private final static Logger LOGGER = LoggerFactory.getLogger(MetadataServiceImpl.class);

    private static final ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();

    private IRI fdpUri;
    @Autowired
    private MetadataRepository storeManager;
    @Autowired
    private Agent publisher;
    @Autowired
    @Qualifier("language")
    private IRI language;
    @Autowired
    @Qualifier("license")
    private IRI license;
    @Autowired
    private FairSearchClient fseService;
    @Autowired
    private FairMetadataMetricsService fmMetricsService;
    @Autowired
    private PIDSystem pidSystem;

    @Value("${metadataProperties.rootSpecs:}")
    private String fdpSpecs;
    @Value("${metadataProperties.catalogSpecs:}")
    private String catalogSpecs;
    @Value("${metadataProperties.datasetSpecs:}")
    private String datasetSpecs;
    @Value("${metadataProperties.datarecordSpecs:}")
    private String datarecordSpecs;
    @Value("${metadataProperties.distributionSpecs:}")
    private String distributionSpecs;
    @Value("${metadataProperties.accessRightsDescription:This resource has no access restriction}")
    private String accessRightsDescription;

    @Override
    public FDPMetadata retrieveFDPMetadata(@Nonnull IRI uri)
            throws MetadataServiceException, ResourceNotFoundException {

        List<Statement> statements = retrieveStatements(uri);
        FDPMetadataParser parser = MetadataParserUtils.getFdpParser();
        return parser.parse(statements, uri);
    }

    @Override
    public CatalogMetadata retrieveCatalogMetadata(@Nonnull IRI uri)
            throws MetadataServiceException, ResourceNotFoundException {

        List<Statement> statements = retrieveStatements(uri);
        CatalogMetadataParser parser = MetadataParserUtils.getCatalogParser();
        return parser.parse(statements, uri);
    }

    @Override
    public DatasetMetadata retrieveDatasetMetadata(@Nonnull IRI uri)
            throws MetadataServiceException, ResourceNotFoundException {

        List<Statement> statements = retrieveStatements(uri);
        DatasetMetadataParser parser = MetadataParserUtils.getDatasetParser();
        return parser.parse(statements, uri);
    }

    //TODO finish -- check red labels
    @Override
    public DataRecordMetadata retrieveDataRecordMetadata(IRI uri)
            throws MetadataServiceException {

        List<Statement> statements = retrieveStatements(uri);
        DataRecordMetadataParser parser = MetadataParserUtils.getDataRecordParser();
        return parser.parse(statements, uri);
    }

    @Override
    public DistributionMetadata retrieveDistributionMetadata(@Nonnull IRI uri)
            throws MetadataServiceException, ResourceNotFoundException {

        List<Statement> statements = retrieveStatements(uri);
        DistributionMetadataParser parser = MetadataParserUtils.getDistributionParser();
        return parser.parse(statements, uri);
    }

    @Override
    public void storeFDPMetadata(@Nonnull FDPMetadata metadata)
            throws MetadataServiceException, MetadataException {

        Preconditions.checkNotNull(metadata, "FDPMetadata must not be null.");
        if (!fdpSpecs.isEmpty()) {
            metadata.setSpecification(VALUEFACTORY.createIRI(fdpSpecs));
        }
        storeMetadata(metadata);
        /*
        This method is called for the very first time the FDP is accessed. So it is better to assign
        fdpUri static variable here
         */
        this.fdpUri = metadata.getUri();
        fseService.submitFdpUri(fdpUri);
    }

    @Override
    public void storeCatalogMetadata(@Nonnull CatalogMetadata metadata)
            throws MetadataServiceException, MetadataException {

        Preconditions.checkState(metadata.getParentURI() != null,
                "No fdp URI is provied. Include dcterms:isPartOf statement "
                        + "in the post body rdf");
//        Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
//                "The fdp URI doesn't exist in the repository. "
//                + "Please try with valid fdp URI");        
        if (!catalogSpecs.isEmpty()) {
            metadata.setSpecification(VALUEFACTORY.createIRI(catalogSpecs));
        }
        if (doesParentResourceExists(metadata)) {
            storeMetadata(metadata);
        } else {
            String msg = "The fdp URI provided is not of type re3:Repository "
                    + "Please try with valid fdp URI";
            throw new IllegalStateException(msg);
        }
        fseService.submitFdpUri(fdpUri);
    }

    @Override
    public void storeDatasetMetadata(@Nonnull DatasetMetadata metadata)
            throws MetadataServiceException, MetadataException {

        Preconditions.checkState(metadata.getParentURI() != null,
                "No catalog URI is provied. Include dcterms:isPartOf statement "
                        + "in the post body rdf");
        Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
                "The catalog URI doesn't exist in the repository. "
                        + "Please try with valid catalog URI");

        if (!datasetSpecs.isEmpty()) {
            metadata.setSpecification(VALUEFACTORY.createIRI(datasetSpecs));
        }
        if (doesParentResourceExists(metadata)) {
            storeMetadata(metadata);
        } else {
            String msg = "The catalog URI provided is not of type dcat:Catalog "
                    + "Please try with valid catalog URI";
            throw new IllegalStateException(msg);
        }
        fseService.submitFdpUri(fdpUri);
    }

    @Override
    public void storeDistributionMetadata(@Nonnull DistributionMetadata metadata)
            throws MetadataServiceException, MetadataException {

        Preconditions.checkState(metadata.getParentURI() != null,
                "No dataset URI is provied. Include dcterms:isPartOf statement "
                        + "in the post body rdf");
        Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
                "The dataset URI doesn't exist in the repository. "
                        + "Please try with valid dataset URI");

        if (!distributionSpecs.isEmpty()) {
            metadata.setSpecification(VALUEFACTORY.createIRI(distributionSpecs));
        }
        if (doesParentResourceExists(metadata)) {
            storeMetadata(metadata);
        } else {
            String msg = "The dataset URI provided is not of type dcat:Dataset "
                    + "Please try with valid dataset URI";
            throw new IllegalStateException(msg);
        }
        fseService.submitFdpUri(fdpUri);
    }

    @Override
    public void storeDataRecordMetadata(DataRecordMetadata metadata)
            throws MetadataServiceException, MetadataException {

        Preconditions.checkState(metadata.getParentURI() != null,
                "No dataset URI is provied. Include dcterms:isPartOf statement "
                        + "in the post body rdf");
        Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
                "The dataset URI doesn't exist in the repository. "
                        + "Please try with valid dataset URI");

        if (!datarecordSpecs.isEmpty()) {
            metadata.setSpecification(VALUEFACTORY.createIRI(datarecordSpecs));
        }
        if (doesParentResourceExists(metadata)) {
            storeMetadata(metadata);
        } else {
            String msg = "The dataset URI provided is not of type dcat:Dataset "
                    + "Please try with valid dataset URI";
            throw new IllegalStateException(msg);
        }
        fseService.submitFdpUri(fdpUri);

    }

    private <T extends Metadata> void storeMetadata(@Nonnull T metadata)
            throws MetadataServiceException, MetadataException {

        Preconditions.checkNotNull(metadata,
                "Metadata must not be null.");
        Preconditions.checkState(!isSubjectURIExist(metadata.getUri()),
                "The metadata URI already exist in the repository. "
                        + "Please try with different ID");

        addDefaultValues(metadata);
        try {
            if (metadata instanceof FDPMetadata) {
                if (metadata.getIssued() == null) {
                    metadata.setIssued(RDFUtils.getCurrentTime());
                    if (((FDPMetadata) metadata).getRepostoryIdentifier()
                            == null) {
                        LOGGER.info("Repository ID is null or empty, this feild"
                                + " value will be generated automatically");
                        Identifier id = new Identifier();
                        id.setUri(VALUEFACTORY.createIRI(metadata.getUri()
                                .stringValue() + "#repositoryID"));
                        UUID uid = UUID.randomUUID();
                        id.setIdentifier(VALUEFACTORY
                                .createLiteral(uid.toString(), XMLSchema.STRING));
                        id.setType(DATACITE.IDENTIFIER);
                        ((FDPMetadata) metadata).setRepostoryIdentifier(id);
                    }
                }
            } else {
                metadata.setIssued(RDFUtils.getCurrentTime());
            }
            metadata.setModified(RDFUtils.getCurrentTime());
            storeManager.storeStatements(MetadataUtils.getStatements(metadata), metadata.getUri());
            updateParentResource(metadata);
        } catch (MetadataRepositoryException | DatatypeConfigurationException ex) {
            LOGGER.error("Error storing distribution metadata");
            throw (new MetadataServiceException(ex.getMessage()));
        }
    }

    /**
     * Add default values for the mandatory metadata properties
     */
    private <T extends Metadata> void addDefaultValues(@Nonnull T metadata) {

        // Add PID
        Identifier id = new Identifier();
        IRI pidIri = pidSystem.getURI(metadata);
        id.setUri(pidIri);
        id.setIdentifier(VALUEFACTORY.createLiteral(pidSystem.getId(pidIri), XMLSchema.STRING));
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
        //Add FAIR metrics
        metadata.setMetrics(fmMetricsService.getMetrics(metadata.getUri()));

        // Add access rights
        if (metadata.getAccessRights() == null) {
            LOGGER.info("Metadata ID is null or empty, adding default value for access rights");
            AccessRights accessRights = new AccessRights();
            accessRights.setUri(VALUEFACTORY.createIRI(metadata.getUri().stringValue()
                    + "#accessRights"));
            Literal description = VALUEFACTORY.createLiteral(accessRightsDescription,
                    XMLSchema.STRING);
            accessRights.setDescription(description);
            metadata.setAccessRights(accessRights);
        }
    }

    /**
     * Check if the parent resources exists
     *
     * @param <T>
     * @param metadata Subtype of Metadata object
     */
    private <T extends Metadata> boolean doesParentResourceExists(@Nonnull T metadata)
            throws MetadataServiceException {

        boolean doesParentResourceExists = false;
        try {
            if (metadata instanceof CatalogMetadata) {
                doesParentResourceExists = storeManager.isStatementExist(
                        metadata.getParentURI(), RDF.TYPE, R3D.REPOSITORY);
            } else if (metadata instanceof DatasetMetadata) {
                doesParentResourceExists = storeManager.isStatementExist(
                        metadata.getParentURI(), RDF.TYPE, DCAT.CATALOG);
            } else if (metadata instanceof DataRecordMetadata) {
                doesParentResourceExists = storeManager.isStatementExist(
                        metadata.getParentURI(), RDF.TYPE, DCAT.DATASET);
            } else if (metadata instanceof DistributionMetadata) {
                doesParentResourceExists = storeManager.isStatementExist(
                        metadata.getParentURI(), RDF.TYPE, DCAT.DATASET);
            }
        } catch (MetadataRepositoryException ex) {
            LOGGER.error("Error checking existence of subject URI");
            throw (new MetadataServiceException(ex.getMessage()));
        }
        return doesParentResourceExists;
    }

    /**
     * Update properties of parent class. (E.g) dcat:Modified
     *
     * @param <T>
     * @param metadata Subtype of Metadata object
     */
    private <T extends Metadata> void updateParentResource(@Nonnull T metadata) {

        Preconditions.checkNotNull(metadata, "Metadata object must not be null.");
        try {
            ValueFactory f = SimpleValueFactory.getInstance();
            List<Statement> stmts = new ArrayList<>();

            IRI parent = metadata.getParentURI();
            final Metadata parentMetadata;

            if (metadata instanceof FDPMetadata) {
                return;
            } else if (metadata instanceof CatalogMetadata) {
                stmts.add(f.createStatement(parent, R3D.DATACATALOG, metadata.getUri()));
                parentMetadata = retrieveFDPMetadata(parent);
            } else if (metadata instanceof DatasetMetadata) {
                stmts.add(f.createStatement(parent, DCAT.HAS_DATASET, metadata.getUri()));
                parentMetadata = retrieveCatalogMetadata(parent);
            } else if (metadata instanceof DistributionMetadata) {
                stmts.add(f.createStatement(parent, DCAT.HAS_DISTRIBUTION, metadata.getUri()));
                parentMetadata = retrieveDatasetMetadata(parent);
            } else if (metadata instanceof DataRecordMetadata) {
                // TODO add link to parent
                parentMetadata = retrieveDatasetMetadata(parent);
            } else {
                throw new IllegalStateException("Unknown type of metadata passed");
            }

            storeManager.removeStatement(parent, FDP.METADATAMODIFIED, null);
            stmts.add(f.createStatement(parent, FDP.METADATAMODIFIED, RDFUtils.getCurrentTime()));
            storeManager.storeStatements(stmts, parent);

            // Propagate the update upward the parent hierarchy. Effectively, this will update the
            // timestamp properties of the parents.
            updateParentResource(parentMetadata);
        } catch (MetadataRepositoryException | DatatypeConfigurationException
                | MetadataServiceException ex) {
            LOGGER.error("Error updating parent resource {}", ex.getMessage());
        }
    }

    private List<Statement> retrieveStatements(@Nonnull IRI uri)
            throws MetadataServiceException, ResourceNotFoundException {

        try {
            Preconditions.checkNotNull(uri, "Resource uri not be null.");
            List<Statement> statements = storeManager.retrieveResource(uri);
            if (statements.isEmpty()) {
                String msg = ("No metadata found for the uri : " + uri);
                throw (new ResourceNotFoundException(msg));
            }
            return statements;
        } catch (MetadataRepositoryException ex) {
            LOGGER.error("Error retrieving fdp metadata from the metadata");
            throw (new MetadataServiceException(ex.getMessage()));
        }
    }

    /**
     * Check if URI exist in a repository as a subject
     *
     * @param uri
     * @return
     * @throws MetadataServiceException
     */
    private boolean isSubjectURIExist(@Nonnull IRI uri)
            throws MetadataServiceException {

        boolean isURIExist = false;

        try {
            isURIExist = storeManager.isStatementExist(uri, null, null);
        } catch (MetadataRepositoryException ex) {
            LOGGER.error("Error checking existence of subject URI");
            throw (new MetadataServiceException(ex.getMessage()));
        }
        return isURIExist;
    }

    @Override
    public void updateFDPMetadata(IRI uri, FDPMetadata metaDataUpdate)
            throws MetadataServiceException, MetadataException {

        FDPMetadata metadata = retrieveFDPMetadata(uri);

        // This is an unconventional way of copying values from a source to a
        // target object, and the original developers are aware of that fact.
        // The original approach used a numer of repeated if-blocks to check for
        // null values before settings, like the following example:
        // if (x.getY() != null) {
        //    z.setY(x.getY());
        // }
        // This resulted in an NPath complexity of over 16000. In order to
        // work around the repeated if-blocks, the null check and getter/setter
        // logic is extracted into the setMetadataProperty method.
        setMetadataProperty(metaDataUpdate::getDescription, metadata::setDescription);
        setMetadataProperty(metaDataUpdate::getIdentifier, metadata::setIdentifier);
        setMetadataProperty(metaDataUpdate::getInstitution, metadata::setInstitution);
        setMetadataProperty(metaDataUpdate::getInstitutionCountry,
                metadata::setInstitutionCountry);
        setMetadataProperty(metaDataUpdate::getLanguage, metadata::setLanguage);
        setMetadataProperty(metaDataUpdate::getLicense, metadata::setLicense);
        setMetadataProperty(metaDataUpdate::getPublisher, metadata::setPublisher);
        setMetadataProperty(metaDataUpdate::getRepostoryIdentifier,
                metadata::setRepostoryIdentifier);
        setMetadataProperty(metaDataUpdate::getRights, metadata::setRights);
        setMetadataProperty(metaDataUpdate::getStartDate, metadata::setStartDate);
        setMetadataProperty(metaDataUpdate::getSwaggerDoc, metadata::setSwaggerDoc);
        setMetadataProperty(metaDataUpdate::getTitle, metadata::setTitle);
        setMetadataProperty(metaDataUpdate::getVersion, metadata::setVersion);

        try {
            storeManager.removeResource(uri);
            storeFDPMetadata(metadata);
        } catch (MetadataRepositoryException ex) {
            LOGGER.error("Error deleting existence fdp resource");
            throw (new MetadataServiceException(ex.getMessage()));
        }
    }

    /**
     * Convenience method to reduce the NPath complexity measure of the {@link
     * #updateFDPMetadata(IRI, FDPMetadata)} method.
     *
     * @param getter the getter method of the source object.
     * @param setter the setter method of the target object.
     */
    private <T> void setMetadataProperty(Getter<T> getter, Setter<T> setter) {

        if (getter.get() != null) {
            setter.set(getter.get());
        }
    }

    @Override
    public IRI getFDPIri(IRI uri) throws MetadataServiceException {
        Preconditions.checkNotNull(uri, "URI must not be null.");
        LOGGER.info("Get fdp uri for the given uri {}", uri.toString());
        try {
            return storeManager.getFDPIri(uri);
        } catch (MetadataRepositoryException ex) {
            LOGGER.error("Error getting fdp uri from the metadata");
            throw new MetadataServiceException(ex.getMessage());
        }
    }

    /**
     * Convenience interface to facilitate referring to a getter method as a function pointer.
     *
     * @param <T> datatype of the getter return value.
     */
    @FunctionalInterface
    private interface Getter<T> {

        T get();
    }

    /**
     * Convenience interface to facilitate referring to a setter method as a function pointer.
     *
     * @param <T> datatype of the setter parameter.
     */
    @FunctionalInterface
    private interface Setter<T> {

        void set(T value);
    }
}
