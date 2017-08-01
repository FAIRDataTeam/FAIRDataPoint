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
package nl.dtls.fairdatapoint.service.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtl.fairmetadata4j.io.CatalogMetadataParser;
import nl.dtl.fairmetadata4j.io.DataRecordMetadataParser;
import nl.dtl.fairmetadata4j.io.DatasetMetadataParser;
import nl.dtl.fairmetadata4j.io.DistributionMetadataParser;
import nl.dtl.fairmetadata4j.io.FDPMetadataParser;
import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.model.Agent;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DataRecordMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtl.fairmetadata4j.model.Identifier;
import nl.dtl.fairmetadata4j.model.Metadata;
import nl.dtl.fairmetadata4j.utils.MetadataParserUtils;
import nl.dtl.fairmetadata4j.utils.MetadataUtils;
import nl.dtl.fairmetadata4j.utils.RDFUtils;
import nl.dtl.fairmetadata4j.utils.vocabulary.DATACITE;
import nl.dtl.fairmetadata4j.utils.vocabulary.FDP;
import nl.dtl.fairmetadata4j.utils.vocabulary.R3D;
import nl.dtls.fairdatapoint.repository.StoreManager;
import nl.dtls.fairdatapoint.repository.StoreManagerException;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service layer for manipulating fair metadata
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2015-12-17
 * @version 0.2
 */
@Service("fairMetaDataServiceImpl")
public class FairMetaDataServiceImpl implements FairMetaDataService {

    private final static Logger LOGGER
            = LogManager.getLogger(FairMetaDataServiceImpl.class);
    @Autowired
    private StoreManager storeManager;
    @Autowired
    private Agent publisher;
    @Autowired
    @Qualifier("language")
    private IRI language;
    @Autowired
    @Qualifier("license")
    private IRI license;
    

    @org.springframework.beans.factory.annotation.Value("${metadataProperties.rootSpecs:nil}")
    private String fdpSpecs;
    @org.springframework.beans.factory.annotation.Value("${metadataProperties.catalogSpecs:nil}")
    private String catalogSpecs;
    @org.springframework.beans.factory.annotation.Value("${metadataProperties.datasetSpecs:nil}")
    private String datasetSpecs;
    @org.springframework.beans.factory.annotation.Value("${metadataProperties.datarecordSpecs:nil}")
    private String datarecordSpecs;
    @org.springframework.beans.factory.annotation.Value("${metadataProperties.distributionSpecs:nil}")
    private String distributionSpecs;
    private final ValueFactory valueFactory = SimpleValueFactory.getInstance();

    @Override
    public FDPMetadata retrieveFDPMetaData(@Nonnull IRI uri) throws
            FairMetadataServiceException, ResourceNotFoundException {
        List<Statement> statements = retrieveStatements(uri);
        FDPMetadataParser parser = MetadataParserUtils.getFdpParser();
        FDPMetadata metadata = parser.parse(statements, uri);
        return metadata;
    }

    @Override
    public CatalogMetadata retrieveCatalogMetaData(@Nonnull IRI uri)
            throws FairMetadataServiceException, ResourceNotFoundException {
        List<Statement> statements = retrieveStatements(uri);
        CatalogMetadataParser parser = MetadataParserUtils.
                getCatalogParser();
        CatalogMetadata metadata = parser.parse(statements, uri);
        return metadata;
    }

    @Override
    public DatasetMetadata retrieveDatasetMetaData(@Nonnull IRI uri)
            throws FairMetadataServiceException, ResourceNotFoundException {
        List<Statement> statements = retrieveStatements(uri);
        DatasetMetadataParser parser = MetadataParserUtils.
                getDatasetParser();
        DatasetMetadata metadata = parser.parse(statements, uri);
        return metadata;
    }   
    
    //TODO finish -- check red labels
    @Override
    public DataRecordMetadata retrieveDataRecordMetadata(IRI uri) throws 
            FairMetadataServiceException {
        List<Statement> statements = retrieveStatements(uri);
    	DataRecordMetadataParser parser = MetadataParserUtils.
        		getDataRecordParser();
    	DataRecordMetadata metadata = parser.parse(statements, uri);
        return metadata;
    }

    @Override
    public DistributionMetadata retrieveDistributionMetaData(@Nonnull IRI uri)
            throws FairMetadataServiceException, ResourceNotFoundException {
        List<Statement> statements = retrieveStatements(uri);
        DistributionMetadataParser parser = MetadataParserUtils.
                getDistributionParser();
        DistributionMetadata metadata = parser.parse(statements, uri);
        return metadata;
    }

    @Override
    public void storeFDPMetaData(@Nonnull FDPMetadata metadata)
            throws FairMetadataServiceException, MetadataException {
        Preconditions.checkNotNull(metadata, "FDPMetadata must not be null.");
        if (!fdpSpecs.isEmpty() && !fdpSpecs.contains("nil")) {
            metadata.setSpecification(valueFactory.createIRI(fdpSpecs));
        }
        storeMetadata(metadata);
    }

    @Override
    public void storeCatalogMetaData(@Nonnull CatalogMetadata metadata)
            throws FairMetadataServiceException, MetadataException {
        Preconditions.checkState(metadata.getParentURI() != null,
                "No fdp URI is provied. Include dcterms:isPartOf statement "
                + "in the post body rdf");
//        Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
//                "The fdp URI doesn't exist in the repository. "
//                + "Please try with valid fdp URI");        
        if (!catalogSpecs.isEmpty()
                && !catalogSpecs.contains("nil")) {
            metadata.setSpecification(valueFactory.createIRI(catalogSpecs));
        }
        if (doesParentResourceExists(metadata)) {
            storeMetadata(metadata);
        } else {
            String msg = "The fdp URI provided is not of type re3:Repository "
                + "Please try with valid fdp URI";
            throw new IllegalStateException(msg);
        } 
    }

    @Override
    public void storeDatasetMetaData(@Nonnull DatasetMetadata metadata)
            throws FairMetadataServiceException, MetadataException {
        Preconditions.checkState(metadata.getParentURI() != null,
                "No catalog URI is provied. Include dcterms:isPartOf statement "
                + "in the post body rdf");
        Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
                "The catalog URI doesn't exist in the repository. "
                + "Please try with valid catalog URI");
        if (!datasetSpecs.isEmpty()
                && !datasetSpecs.contains("nil")) {
            metadata.setSpecification(valueFactory.createIRI(datasetSpecs));
        }
        if (doesParentResourceExists(metadata)) {
            storeMetadata(metadata);
        } else {
            String msg = "The catalog URI provided is not of type dcat:Catalog "
                + "Please try with valid catalog URI";
            throw new IllegalStateException(msg);
        } 
    }

    @Override
    public void storeDistributionMetaData(@Nonnull DistributionMetadata metadata)
            throws FairMetadataServiceException, MetadataException {
        Preconditions.checkState(metadata.getParentURI() != null,
                "No dataset URI is provied. Include dcterms:isPartOf statement "
                + "in the post body rdf");
        Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
                "The dataset URI doesn't exist in the repository. "
                + "Please try with valid dataset URI");
        if (!distributionSpecs.isEmpty()
                && !distributionSpecs.contains("nil")) {
            metadata.setSpecification(valueFactory.createIRI(
                    distributionSpecs));
        }
        if (doesParentResourceExists(metadata)) {
            storeMetadata(metadata);
        } else {
            String msg = "The dataset URI provided is not of type dcat:Dataset "
                + "Please try with valid dataset URI";
            throw new IllegalStateException(msg);
        }        
    }
    
    @Override
    public void storeDataRecordMetaData(DataRecordMetadata metadata) 
            throws FairMetadataServiceException, MetadataException {
        Preconditions.checkState(metadata.getParentURI() != null,
                "No dataset URI is provied. Include dcterms:isPartOf statement "
                + "in the post body rdf");
        Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
                "The dataset URI doesn't exist in the repository. "
                + "Please try with valid dataset URI");
        if (!datarecordSpecs.isEmpty()
                && !datarecordSpecs.contains("nil")) {
            metadata.setSpecification(valueFactory.createIRI(
                    datarecordSpecs));
        }
        if (doesParentResourceExists(metadata)) {
            storeMetadata(metadata);
        } else {
            String msg = "The dataset URI provided is not of type dcat:Dataset "
                + "Please try with valid dataset URI";
            throw new IllegalStateException(msg);
        }
        
    }

    private <T extends Metadata> void storeMetadata(@Nonnull T metadata)
            throws FairMetadataServiceException, MetadataException {
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
                    if (((FDPMetadata) metadata).getRepostoryIdentifier() == 
                            null) {
                        LOGGER.info("Repository ID is null or empty, this feild"
                                + " value will be generated automatically");
                        Identifier id = new Identifier();
                        id.setUri(valueFactory.createIRI(metadata.getUri().
                                stringValue() + "#repositoryID"));
                        UUID uid = UUID.randomUUID();
                        id.setIdentifier(valueFactory.createLiteral(
                                uid.toString(), XMLSchema.STRING));
                        id.setType(DATACITE.IDENTIFIER);
                        ((FDPMetadata) metadata).setRepostoryIdentifier(id);
                    }
                }
            } else {
                metadata.setIssued(RDFUtils.getCurrentTime());
            }
            metadata.setModified(RDFUtils.getCurrentTime());
            storeManager.storeStatements(MetadataUtils.getStatements(metadata),
                    metadata.getUri());
            updateParentResource(metadata);
        } catch (StoreManagerException | DatatypeConfigurationException ex) {
            LOGGER.error("Error storing distribution metadata");
            throw (new FairMetadataServiceException(ex.getMessage()));
        }
    }
    
    /** Add default values for the mandatory metadata properties
     * 
     */ 
    private <T extends Metadata> void addDefaultValues(@Nonnull T metadata) 
    {
        if (metadata.getIdentifier() == null) {
            LOGGER.info("Metadata ID is null or empty, this feild value will "
                    + "be generated automatically");
            Identifier id = new Identifier();
            id.setUri(valueFactory.createIRI(metadata.getUri().stringValue()
                    + "#metadataID"));
            UUID uid = UUID.randomUUID();
            id.setIdentifier(valueFactory.createLiteral(uid.toString(),
                    XMLSchema.STRING));
            id.setType(DATACITE.RESOURCEIDENTIFIER);
            metadata.setIdentifier(id);
        }
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
    }
    
    /**
     * Check if the parent resources exists
     * 
     * @param <T>
     * @param metadata Subtype of Metadata object
     */
    private <T extends Metadata> boolean doesParentResourceExists(
            @Nonnull T metadata) throws FairMetadataServiceException {
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
        } catch (StoreManagerException ex) {
            LOGGER.error("Error checking existence of subject URI");
            throw (new FairMetadataServiceException(ex.getMessage()));
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
        Preconditions.checkNotNull(metadata,
                "Metadata object must not be null.");
        try {
            ValueFactory f = SimpleValueFactory.getInstance();
            List<Statement> stmts = new ArrayList<>();
            if (metadata instanceof FDPMetadata) {
                return;
            } else if (metadata instanceof CatalogMetadata) {
                stmts.add(f.createStatement(metadata.getParentURI(),
                        R3D.DATACATALOG, metadata.getUri()));
            } else if (metadata instanceof DatasetMetadata) {
                stmts.add(f.createStatement(metadata.getParentURI(),
                        DCAT.HAS_DATASET, metadata.getUri()));
            } else if (metadata instanceof DistributionMetadata) {
                stmts.add(f.createStatement(metadata.getParentURI(),
                        DCAT.HAS_DISTRIBUTION, metadata.getUri()));
            }
            storeManager.removeStatement(metadata.getParentURI(),
                    DCTERMS.MODIFIED, null);
            stmts.add(f.createStatement(metadata.getParentURI(),
                    DCTERMS.MODIFIED, RDFUtils.getCurrentTime()));
            storeManager.storeStatements(stmts, metadata.getParentURI());
        } catch (StoreManagerException | DatatypeConfigurationException ex) {
            LOGGER.error("Error updating parent resource :" + ex.getMessage());
        }
    }

    private List<Statement> retrieveStatements(@Nonnull IRI uri) throws
            FairMetadataServiceException, ResourceNotFoundException {
        try {
            Preconditions.checkNotNull(uri, "Resource uri not be null.");
            List<Statement> statements = storeManager.retrieveResource(uri);
            if (statements.isEmpty()) {
                String msg = ("No metadata found for the uri : " + uri);
                throw (new ResourceNotFoundException(msg));
            }
            addAddtionalResource(statements);
            return statements;
        } catch (StoreManagerException ex) {
            LOGGER.error("Error retrieving fdp metadata from the store");
            throw (new FairMetadataServiceException(ex.getMessage()));
        }
    }

    /**
     * Check if URI exist in a repository as a subject
     *
     * @param uri
     * @return
     * @throws FairMetadataServiceException
     */
    private boolean isSubjectURIExist(@Nonnull IRI uri) throws
            FairMetadataServiceException {
        boolean isURIExist = false;

        try {
            isURIExist = storeManager.isStatementExist(uri, null, null);
        } catch (StoreManagerException ex) {
            LOGGER.error("Error checking existence of subject URI");
            throw (new FairMetadataServiceException(ex.getMessage()));
        }
        return isURIExist;
    }

    private void addAddtionalResource(List<Statement> statements) throws
            StoreManagerException {
        List<Statement> otherResources = new ArrayList<>();
        for (Statement st : statements) {
            IRI predicate = st.getPredicate();
            Value object = st.getObject();
            if (predicate.equals(FDP.METADATAIDENTIFIER)) {
                otherResources.addAll(storeManager.retrieveResource(
                        (IRI) object));
            } else if (predicate.equals(R3D.INSTITUTION)) {
                otherResources.addAll(storeManager.retrieveResource(
                        (IRI) object));
            } else if (predicate.equals(DCTERMS.PUBLISHER)) {
                otherResources.addAll(storeManager.retrieveResource(
                        (IRI) object));
            } else if (predicate.equals(R3D.REPOSITORYIDENTIFIER)) {
                otherResources.addAll(storeManager.retrieveResource(
                        (IRI) object));
            }
        }
        statements.addAll(otherResources);
    }

    @Override
    public void updateFDPMetaData(IRI uri, FDPMetadata metaDataUpdate)
            throws FairMetadataServiceException, MetadataException {
        FDPMetadata metadata = retrieveFDPMetaData(uri);

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
            storeFDPMetaData(metadata);
        } catch (StoreManagerException ex) {
            LOGGER.error("Error deleting existence fdp resource");
            throw (new FairMetadataServiceException(ex.getMessage()));
        }
    }

    /**
     * Convenience method to reduce the NPath complexity measure of the {@link
     * #updateFDPMetaData(IRI, FDPMetadata)} method.
     *
     * @param getter the getter method of the source object.
     * @param setter the setter method of the target object.
     */
    private <T> void setMetadataProperty(Getter<T> getter, Setter<T> setter) {
        if (getter.get() != null) {
            setter.set(getter.get());
        }
    }

    /**
     * Convenience interface to facilitate referring to a getter method as a
     * function pointer.
     *
     * @param <T> datatype of the getter return value.
     */
    @FunctionalInterface
    private interface Getter<T> {

        T get();
    }

    /**
     * Convenience interface to facilitate referring to a setter method as a
     * function pointer.
     *
     * @param <T> datatype of the setter parameter.
     */
    @FunctionalInterface
    private interface Setter<T> {

        void set(T value);
    }
}
