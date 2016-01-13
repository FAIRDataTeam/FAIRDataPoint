/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import nl.dtls.fairdatapoint.domain.StoreManager;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.service.impl.utils.RDFUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2015-12-17
 * @version 0.2
 */
public class FairMetaDataServiceImpl implements FairMetaDataService {
    private final static Logger LOGGER 
            = LogManager.getLogger(FairMetaDataServiceImpl.class);
    
    private final StoreManager storeManager;
    private final String BASE_URI;
    
    public FairMetaDataServiceImpl(StoreManager storeManager, String baseURI) {
        this.storeManager = storeManager;
        this.BASE_URI = baseURI;
        if(this.storeManager == null) {
            LOGGER.debug("The storeManager is NULL");
        }
        if(baseURI == null || baseURI.isEmpty()) {
            LOGGER.debug("The base URI is NULL (or) empty string");
        }
    }

    @Override
    public String retrieveFDPMetaData(RDFFormat format) 
            throws FairMetadataServiceException {
        String fdpURI = this.BASE_URI.concat("fdp");
        String fdpMetadata = null;
        try {
            RepositoryResult<Statement> statements = 
                    storeManager.retrieveResource(fdpURI);
            if(statements != null) {
                fdpMetadata = RDFUtils.writeToString(statements, format);
            }    
            storeManager.closeRepositoryConnection();
        } catch (Exception ex) {
            LOGGER.error("Error retrieving fdp's metadata");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
        return fdpMetadata;
    }

    @Override
    public String retrieveCatalogMetaData(String catalogID, RDFFormat format) 
            throws FairMetadataServiceException {
        String catalogURI = this.BASE_URI.concat("fdp").concat("/").
                concat(catalogID);
        String catalogMetadata = null;
        try {
            RepositoryResult<Statement> statements = 
                    storeManager.retrieveResource(catalogURI);
            if(statements != null) {
                catalogMetadata = RDFUtils.writeToString(statements, format);
            }
            storeManager.closeRepositoryConnection();
        } catch (Exception ex) {
            LOGGER.error("Error retrieving catalog metadata of <" + 
                    catalogURI + ">");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
        return catalogMetadata;
    }

    @Override
    public String retrieveDatasetMetaData(String catalogID, 
            String datasetID, RDFFormat format) 
            throws FairMetadataServiceException {
        String datasetURI = this.BASE_URI.concat("fdp").concat("/").
                concat(catalogID).concat("/").concat(datasetID);
        String datasetMetadata = null;
        try {
            RepositoryResult<Statement> statements = 
                    storeManager.retrieveResource(datasetURI);
            if(statements != null) {
                datasetMetadata = RDFUtils.writeToString(statements, format);
            }
            storeManager.closeRepositoryConnection();
        } catch (Exception ex) {
            LOGGER.error("Error retrieving dataset metadata of <" + 
                    datasetURI + ">");
            throw(new FairMetadataServiceException(ex.getMessage()));
        }
        return datasetMetadata;        
    }     	
}
