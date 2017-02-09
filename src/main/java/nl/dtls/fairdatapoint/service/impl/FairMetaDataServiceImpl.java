/**
 * The MIT License
 * Copyright © 2016 DTL
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
import javax.annotation.Nonnull;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtl.fairmetadata.io.CatalogMetadataParser;
import nl.dtl.fairmetadata.io.DatasetMetadataParser;
import nl.dtl.fairmetadata.io.DistributionMetadataParser;
import nl.dtl.fairmetadata.io.FDPMetadataParser;
import nl.dtl.fairmetadata.io.MetadataException;
import nl.dtl.fairmetadata.model.CatalogMetadata;
import nl.dtl.fairmetadata.model.DatasetMetadata;
import nl.dtl.fairmetadata.model.DistributionMetadata;
import nl.dtl.fairmetadata.model.FDPMetadata;
import nl.dtl.fairmetadata.model.Metadata;
import nl.dtl.fairmetadata.utils.MetadataParserUtils;
import nl.dtl.fairmetadata.utils.MetadataUtils;
import nl.dtl.fairmetadata.utils.RDFUtils;
import nl.dtl.fairmetadata.utils.vocabulary.DCAT;
import nl.dtl.fairmetadata.utils.vocabulary.FDP;
import nl.dtl.fairmetadata.utils.vocabulary.R3D;
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
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2015-12-17
 * @version 0.2
 */
@Service("fairMetaDataServiceImpl")
public class FairMetaDataServiceImpl implements FairMetaDataService {

    private final static Logger LOGGER
            = LogManager.getLogger(FairMetaDataServiceImpl.class);
    @Autowired
    private StoreManager storeManager;

    @Override
    public FDPMetadata retrieveFDPMetaData(@Nonnull IRI uri) throws
            FairMetadataServiceException {
        List<Statement> statements = retrieveStatements(uri);
        if (statements.isEmpty()) {
            return null;
        }
        FDPMetadataParser parser = MetadataParserUtils.getFdpParser();
        FDPMetadata metadata = parser.parse(statements, uri);
        return metadata;
    }

    @Override
    public CatalogMetadata retrieveCatalogMetaData(@Nonnull IRI uri)
            throws FairMetadataServiceException {
        List<Statement> statements = retrieveStatements(uri);
        if (statements.isEmpty()) {
            return null;
        }
        CatalogMetadataParser parser = MetadataParserUtils.
                getCatalogParser();
        CatalogMetadata metadata = parser.parse(statements, uri);
        return metadata;
    }

    @Override
    public DatasetMetadata retrieveDatasetMetaData(@Nonnull IRI uri)
            throws FairMetadataServiceException {
        List<Statement> statements = retrieveStatements(uri);
        if (statements.isEmpty()) {
            return null;
        }
        DatasetMetadataParser parser = MetadataParserUtils.
                getDatasetParser();
        DatasetMetadata metadata = parser.parse(statements, uri);
        return metadata;
    }

    @Override
    public DistributionMetadata retrieveDistributionMetaData(@Nonnull IRI uri)
            throws FairMetadataServiceException {
        List<Statement> statements = retrieveStatements(uri);
        if (statements.isEmpty()) {
            return null;
        }
        DistributionMetadataParser parser = MetadataParserUtils.
                getDistributionParser();
        DistributionMetadata metadata = parser.parse(statements, uri);
        return metadata;
    }

    @Override
    public void storeFDPMetaData(@Nonnull FDPMetadata metadata)
            throws FairMetadataServiceException, MetadataException {
        Preconditions.checkNotNull(metadata, "FDPMetadata must not be null.");
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
        storeMetadata(metadata);
    }

    @Override
    public void storeDatasetMetaData(@Nonnull DatasetMetadata metadata)
            throws FairMetadataServiceException, MetadataException {
        Preconditions.checkState(metadata.getParentURI() != null,
                "No catalog URI is provied. Include dcterms:isPartOf statement "
                + "in the post body rdf");
        Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()),
                "The catalogy URI doesn't exist in the repository. "
                + "Please try with valid catalogy URI");
        storeMetadata(metadata);
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
        storeMetadata(metadata);
    }

    private <T extends Metadata> void storeMetadata(@Nonnull T metadata)
            throws FairMetadataServiceException, MetadataException {
        Preconditions.checkNotNull(metadata,
                "Metadata must not be null.");
        Preconditions.checkState(!isSubjectURIExist(metadata.getUri()),
                "The metadata URI already exist in the repository. "
                + "Please try with different ID");
        try {
            if (metadata instanceof FDPMetadata) {
                if (metadata.getIssued() == null) {
                    metadata.setIssued(RDFUtils.getCurrentTime());
                }
            } else {
                metadata.setIssued(RDFUtils.getCurrentTime());
            }
            metadata.setModified(RDFUtils.getCurrentTime());
            storeManager.storeStatements(MetadataUtils.getStatements(metadata));
            updateParentResource(metadata);
        } catch (StoreManagerException | DatatypeConfigurationException ex) {
            LOGGER.error("Error storing distribution metadata");
            throw (new FairMetadataServiceException(ex.getMessage()));
        }
    }

    /**
     * Update properties of parent class. (E.g) dcat:Modified
     *
     * @param <T>
     * @param metadata Subtype of Metadata object
     */
    private <T extends Metadata> void updateParentResource(@Nonnull T metadata) 
    {
        Preconditions.checkNotNull(metadata,
                "Metadata object must not be null.");
        try {
            ValueFactory f = SimpleValueFactory.getInstance();
            List<Statement> stmts = new ArrayList<>();
            if (metadata instanceof FDPMetadata) {
                return;
            } else if (metadata instanceof CatalogMetadata) {
                stmts.add(f.createStatement(metadata.getParentURI(),
                        R3D.DATA_CATALOG, metadata.getUri()));
            } else if (metadata instanceof DatasetMetadata) {
                stmts.add(f.createStatement(metadata.getParentURI(),
                        DCAT.DATASET, metadata.getUri()));
            } else if (metadata instanceof DistributionMetadata) {
                stmts.add(f.createStatement(metadata.getParentURI(),
                        DCAT.DISTRIBUTION, metadata.getUri()));
            }
            storeManager.removeStatement(metadata.getParentURI(),
                    DCTERMS.MODIFIED, null);
            stmts.add(f.createStatement(metadata.getParentURI(),
                    DCTERMS.MODIFIED, RDFUtils.getCurrentTime()));
            storeManager.storeStatements(stmts);
        } catch (StoreManagerException | DatatypeConfigurationException ex) {
            LOGGER.error("Error updating parent resource :" + ex.getMessage());
        }
    }

    private List<Statement> retrieveStatements(@Nonnull IRI uri) throws
            FairMetadataServiceException {
        try {
            Preconditions.checkNotNull(uri, "Resource uri not be null.");
            List<Statement> statements = storeManager.retrieveResource(uri);
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
            if (predicate.equals(FDP.METADATA_IDENTIFIER)) {
                otherResources.addAll(storeManager.retrieveResource(
                        (IRI) object));
            } else if (predicate.equals(R3D.INSTITUTION)) {
                otherResources.addAll(storeManager.retrieveResource(
                        (IRI) object));
            } else if (predicate.equals(DCTERMS.PUBLISHER)) {
                otherResources.addAll(storeManager.retrieveResource(
                        (IRI) object));
            } else if (predicate.equals(R3D.REPO_IDENTIFIER)) {
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
     * @param <T> datatype of the getter return value.
     */
    @FunctionalInterface
    private interface Getter<T> {
        T get();
    }
    
    /**
     * Convenience interface to facilitate referring to a setter method as a
     * function pointer.
     * @param <T> datatype of the setter parameter.
     */
    @FunctionalInterface
    private interface Setter<T> {
        void set(T value);
    }
}
