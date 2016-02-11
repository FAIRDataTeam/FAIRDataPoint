/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import java.util.List;
import nl.dtls.fairdatapoint.domain.StoreManager;
import nl.dtls.fairdatapoint.service.DataAccessorService;
import nl.dtls.fairdatapoint.service.DataAccessorServiceException;
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
 * @since 2016-01-07
 * @version 0.1
 */
@Service("dataAccessorServiceImpl")
public class DataAccessorServiceImpl implements DataAccessorService {
    
    private final static Logger LOGGER 
            = LogManager.getLogger(DataAccessorServiceImpl.class);    
    @Autowired
    private StoreManager storeManager;
    @Autowired
    @Qualifier("baseURI")
    private String baseURI;      
    
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
            List<Statement> statements = 
                    this.storeManager.retrieveResource(
                            datasetDistributionURI);
            if(!statements.isEmpty()) {
                datasetDistribution = 
                        RDFUtils.writeToString(statements, format);
            }
        } catch (Exception ex) {
            LOGGER.error("Error retrieving dataset metadata of <" + 
                    datasetDistributionURI + ">");
            throw(new DataAccessorServiceException(ex.getMessage()));
        }
        return datasetDistribution;        
    } 
    
}
