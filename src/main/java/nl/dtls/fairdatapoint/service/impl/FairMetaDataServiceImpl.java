/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import java.util.List;
import nl.dtls.fairdatapoint.domain.StoreManager;
import nl.dtls.fairdatapoint.service.CatalogMetadata;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.service.impl.utils.RDFUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Autowired
    @Qualifier("baseUri")
    private String baseURI;

    @Override
    public String retrieveFDPMetaData(RDFFormat format) 
            throws FairMetadataServiceException {
        
        if(format == null) {
            String errorMsg = "The RDFFormat can't be NULL";
            LOGGER.error(errorMsg);
            throw(new IllegalArgumentException(errorMsg));
        }
        String fdpURI = this.baseURI.concat("fdp");
        String fdpMetadata = null;
        try {
            List<Statement> statements = 
                    storeManager.retrieveResource(fdpURI);
            if(!statements.isEmpty()) {
                fdpMetadata = RDFUtils.writeToString(statements, format);
            }
        } catch (Exception ex) {
            LOGGER.error("Error retrieving fdp's metadata");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
        return fdpMetadata;
    }

    @Override
    public String retrieveCatalogMetaData(String catalogID, RDFFormat format) 
            throws FairMetadataServiceException {
        
        if(format == null) {
            String errorMsg = "The RDFFormat can't be NULL";
            LOGGER.error(errorMsg);
            throw(new IllegalArgumentException(errorMsg));
        }
        if(catalogID == null || catalogID.isEmpty()) {
            String errorMsg = "The catalogID can't be NULL or empty";
            LOGGER.error(errorMsg);
            throw(new IllegalArgumentException(errorMsg));
        }
        String catalogURI = this.baseURI.concat("fdp").concat("/").
                concat(catalogID);
        String catalogMetadata = null;
        try {
            List<Statement> statements = 
                    storeManager.retrieveResource(catalogURI);
            if(!statements.isEmpty()) {
                catalogMetadata = RDFUtils.writeToString(statements, format);
            }
        } catch (Exception ex) {
            LOGGER.error("Error retrieving catalog metadata of <" + 
                    catalogURI + ">");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
        return catalogMetadata;
    }
    
    @Override
    public void storeCatalogMetaData(CatalogMetadata catalogMetadata) 
            throws FairMetadataServiceException {
        
        if(catalogMetadata == null) {
            String errorMsg = "The CatalogMetadata can't be NULL";
            LOGGER.error(errorMsg);
            throw(new IllegalArgumentException(errorMsg));
        }
        try {
            storeManager.storeRDF(catalogMetadata.getCatalogMetadataModel());
            
        } catch (Exception ex) {
            LOGGER.error("Error storing catalog metadata of");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
    }

    @Override
    public String retrieveDatasetMetaData(String catalogID, 
            String datasetID, RDFFormat format) 
            throws FairMetadataServiceException {
        
        if(format == null) {
            String errorMsg = "The RDFFormat can't be NULL";
            LOGGER.error(errorMsg);
            throw(new IllegalArgumentException(errorMsg));
        }
        if(catalogID == null || catalogID.isEmpty()) {
            String errorMsg = "The catalogID can't be NULL or empty";
            LOGGER.error(errorMsg);
            throw(new IllegalArgumentException(errorMsg));
        }
        if(datasetID == null || datasetID.isEmpty()) {
            String errorMsg = "The datasetID can't be NULL or empty";
            LOGGER.error(errorMsg);
            throw(new IllegalArgumentException(errorMsg));
        }
        String datasetURI = this.baseURI.concat("fdp").concat("/").
                concat(catalogID).concat("/").concat(datasetID);
        String datasetMetadata = null;
        try {
            List<Statement> statements = 
                    storeManager.retrieveResource(datasetURI);
            if(!statements.isEmpty()) {
                datasetMetadata = RDFUtils.writeToString(statements, format);
            }
        } catch (Exception ex) {
            LOGGER.error("Error retrieving dataset metadata of <" + 
                    datasetURI + ">");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
        return datasetMetadata;        
    }     	
}
