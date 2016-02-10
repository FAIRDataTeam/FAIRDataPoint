/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import nl.dtls.fairdatapoint.domain.StoreManager;
import nl.dtls.fairdatapoint.service.DataAccessorService;
import nl.dtls.fairdatapoint.service.DataAccessorServiceException;
import nl.dtls.fairdatapoint.service.impl.utils.RDFUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-07
 * @version 0.1
 */
@Service
public class DataAccessorServiceImpl implements DataAccessorService {
    
    private final static Logger LOGGER 
            = LogManager.getLogger(DataAccessorServiceImpl.class);    
    
    private final StoreManager storeManager;
    private final String baseURI;        
    
    @Autowired
    public DataAccessorServiceImpl(StoreManager storeManager, String baseURI) {
        this.storeManager = storeManager;
        this.baseURI = baseURI;
        if(this.storeManager == null) {
            String errorMsg = "The STORE_MANAGER can't be NULL";
            LOGGER.error(errorMsg);
            throw(new IllegalArgumentException(errorMsg));
        }
        if(baseURI == null || baseURI.isEmpty()) {
            String errorMsg = "The base URI is can't be NULL (or) empty string";
            LOGGER.error(errorMsg);
            throw(new IllegalArgumentException(errorMsg));
        }
    }
    
    @Override
    public String retrieveDatasetDistribution(String catalogID, 
            String datasetID, String distributionID, RDFFormat format) 
            throws DataAccessorServiceException {  
        
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
        if(distributionID == null || distributionID.isEmpty()) {
            String errorMsg = "The distributionID can't be NULL or empty";
            LOGGER.error(errorMsg);
            throw(new IllegalArgumentException(errorMsg));
        }
        String datasetDistributionURI = this.baseURI.concat("fdp").concat("/").
                concat(catalogID).concat("/").concat(datasetID).
                concat("/").concat(distributionID);
        String datasetDistribution = null;
        try {
            RepositoryResult<Statement> statements = 
                    this.storeManager.retrieveResource(
                            datasetDistributionURI);
            if(statements != null) {
                datasetDistribution = 
                        RDFUtils.writeToString(statements, format);
            }
            this.storeManager.closeRepositoryConnection();
        } catch (Exception ex) {
            LOGGER.error("Error retrieving dataset metadata of <" + 
                    datasetDistributionURI + ">");
            throw(new DataAccessorServiceException(ex.getMessage()));
        }
        return datasetDistribution;        
    } 
    
}
