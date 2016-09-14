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
import nl.dtl.fairmetadata.utils.vocabulary.DCAT;
import nl.dtl.fairmetadata.utils.vocabulary.LDP;
import nl.dtls.fairdatapoint.api.repository.StoreManager;
import nl.dtls.fairdatapoint.api.repository.StoreManagerException;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.utils.RDFUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.rio.RDFFormat;
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
    public FDPMetadata retrieveFDPMetaData(String uri) throws 
            FairMetadataServiceException {
        try {
            List<Statement> statements = 
                    storeManager.retrieveResource(uri);
            if(statements.isEmpty()) {
                return null;
            }
            FDPMetadataParser parser = 
                    MetadataParserUtils.getFdpParser();
            FDPMetadata metadata = parser.parse(statements, 
                    new URIImpl(uri));
            return metadata;
        } catch (StoreManagerException  ex) {
            LOGGER.error("Error retrieving fdp metadata from the store");
            throw(new FairMetadataServiceException(ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error("Error pharsing fdp metadata");
            throw(new FairMetadataServiceException(ex.getMessage()));
        } 
        
        
    }

    @Override
    public CatalogMetadata retrieveCatalogMetaData(String uri) 
            throws FairMetadataServiceException {
        try {
            List<Statement> statements = 
                    storeManager.retrieveResource(uri);
            if(statements.isEmpty()) {
                return null;
            }
            CatalogMetadataParser parser = 
                    MetadataParserUtils.getCatalogParser();
            CatalogMetadata metadata = parser.parse(statements, 
                    new URIImpl(uri));
            return metadata;
        } catch (StoreManagerException  ex) {
            LOGGER.error("Error retrieving catalog metadata from the store");
            throw(new FairMetadataServiceException(ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error("Error pharsing catalog metadata");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
        
        
    }
    
    @Override
    public DatasetMetadata retrieveDatasetMetaData(String uri) 
            throws FairMetadataServiceException {
        try {
            List<Statement> statements = 
                    storeManager.retrieveResource(uri);
            if(statements.isEmpty()) {
                return null;
            }
            DatasetMetadataParser parser = 
                    MetadataParserUtils.getDatasetParser();
            DatasetMetadata metadata = parser.parse(statements, 
                    new URIImpl(uri));
            return metadata;
        } catch (StoreManagerException  ex) {
            LOGGER.error("Error retrieving dataset metadata from the store");
            throw(new FairMetadataServiceException(ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error("Error pharsing dataset metadata");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }        
        
    }

    @Override
    public DistributionMetadata retrieveDistributionMetaData(String uri) 
            throws FairMetadataServiceException {
        try {
            List<Statement> statements = 
                    storeManager.retrieveResource(uri);
            if(statements.isEmpty()) {
                return null;
            }            
            DistributionMetadataParser parser = 
                    MetadataParserUtils.getDistributionParser();
            DistributionMetadata metadata = parser.parse(statements, 
                    new URIImpl(uri));
            return metadata;
        } catch (StoreManagerException  ex) {
            LOGGER.error(
                    "Error retrieving distribution metadata from the store");
            throw(new FairMetadataServiceException(ex.getMessage()));
        } catch (Exception ex) {
            LOGGER.error("Error pharsing distribution metadata");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }        
        
    }
    
    @Override
    public void storeFDPMetaData(@Nonnull FDPMetadata metadata) 
            throws FairMetadataServiceException {        
        Preconditions.checkNotNull(metadata, "FDPMetadata must not be null.");
        try {
            storeManager.storeRDF(MetadataUtils.getStatements(metadata));
            
        } catch (MetadataException | StoreManagerException ex) {
            LOGGER.error("Error storing fdp metadata");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
    }      
    
    @Override
    public void storeCatalogMetaData(CatalogMetadata metadata) 
            throws FairMetadataServiceException {
        Preconditions.checkNotNull(metadata, 
                "Catalog metadata must not be null.");
        Preconditions.checkState(!isSubjectURIExist(metadata.getUri()), 
                "The catalog URI already exist in the repository. "
                        + "Please try with different dataset ID");
        try {           
            storeManager.storeRDF(MetadataUtils.getStatements(metadata));
            updateParentResource(metadata);
        } catch (MetadataException | StoreManagerException ex) {
            LOGGER.error("Error storing catalog metadata");
            throw(new FairMetadataServiceException(ex.getMessage()));        
        } 
    }
    
    @Override
    public void storeDatasetMetaData(DatasetMetadata metadata) 
            throws FairMetadataServiceException {
        Preconditions.checkNotNull(metadata, 
                "Dataset metadata must not be null.");
        Preconditions.checkState(!isSubjectURIExist(metadata.getUri()), 
                "The dataset URI already exist in the repository. "
                        + "Please try with different dataset ID");
        Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()), 
                "The catalogy URI doesn't exist in the repository. "
                        + "Please try with valid catalogy ID");
        try {           
            storeManager.storeRDF(MetadataUtils.getStatements(metadata));  
            updateParentResource(metadata);            
        } catch (StoreManagerException | MetadataException ex) {
            LOGGER.error("Error storing dataset metadata");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
    }

    @Override
    public void storeDistributionMetaData(DistributionMetadata 
            metadata) throws FairMetadataServiceException {
        Preconditions.checkNotNull(metadata, 
                "Distribution metadata must not be null.");
        Preconditions.checkState(!isSubjectURIExist(metadata.getUri()), 
                "The distribution URI already exist in the repository. "
                        + "Please try with different distribution ID");
        Preconditions.checkState(isSubjectURIExist(metadata.getParentURI()), 
                "The dataset URI doesn't exist in the repository. "
                        + "Please try with valid dataset ID");
        try {         
            storeManager.storeRDF(MetadataUtils.getStatements(metadata)); 
            updateParentResource(metadata);
        } catch (StoreManagerException | MetadataException ex) {
            LOGGER.error("Error storing distribution metadata");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
    }    
    
    
    public String retrieveMetaData(String uri, RDFFormat format) 
            throws FairMetadataServiceException {
        
        if(format == null) {
            String errorMsg = "The RDFFormat can't be NULL";
            LOGGER.error(errorMsg);
            throw(new IllegalArgumentException(errorMsg));
        }
        String fdpMetadata = null;
        try {
            List<Statement> statements = 
                    storeManager.retrieveResource(uri);
            if(!statements.isEmpty()) {
                fdpMetadata = RDFUtils.writeToString(statements, format);
            }
        } catch (Exception ex) {
            LOGGER.error("Error retrieving fdp's metadata");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
        return fdpMetadata;
    }
    
    private <T extends Metadata> void updateParentResource(@Nonnull 
            T metadata) {
        Preconditions.checkNotNull(metadata, 
                "Metadata object must not be null.");
        
        try {
            List<Statement> stmts = new ArrayList();
            if (metadata instanceof CatalogMetadata) {            
                stmts.add(new StatementImpl(metadata.getParentURI(),                   
                        LDP.CONTAINS, metadata.getUri()));                
            } else if (metadata instanceof DatasetMetadata) {              
            stmts.add(new StatementImpl(metadata.getParentURI(), 
                    DCAT.DATASET, metadata.getUri()));  
            } else if (metadata instanceof DistributionMetadata) {
                stmts.add(new StatementImpl(metadata.getParentURI(), 
                    DCAT.DISTRIBUTION, metadata.getUri()));                
            }            
            storeManager.removeStatement(metadata.getParentURI(),            
                    DCTERMS.MODIFIED, null);             
            stmts.add(new StatementImpl(metadata.getParentURI(), 
                    DCTERMS.MODIFIED, RDFUtils.getCurrentTime()));
            storeManager.storeRDF(stmts);
        } catch (StoreManagerException | DatatypeConfigurationException ex) {
            LOGGER.error("Error updating parent resource :" + ex.getMessage());
        }
    }
    
     /**
     * Check if URI exist in a repository 
     * 
     * @param uri
     * @return
     * @throws FairMetadataServiceException 
     */
    private boolean isSubjectURIExist(URI uri) throws 
            FairMetadataServiceException {
        boolean isURIExist = false;
        
        try {
            isURIExist = storeManager.isStatementExist(uri, null, null);
        } catch (StoreManagerException ex) {
            LOGGER.error("Error checking existence of subject URI");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }        
        return isURIExist;
    }
    
}
