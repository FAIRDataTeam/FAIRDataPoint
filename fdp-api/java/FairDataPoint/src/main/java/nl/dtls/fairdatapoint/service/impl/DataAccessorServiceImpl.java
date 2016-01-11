/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import nl.dtls.fairdatapoint.domain.StoreManager;
import nl.dtls.fairdatapoint.domain.StoreManagerException;
import nl.dtls.fairdatapoint.service.DataAccessorService;
import nl.dtls.fairdatapoint.service.DataAccessorServiceException;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.service.impl.utils.RDFUtils;
import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-07
 * @version 0.1
 */
public class DataAccessorServiceImpl implements DataAccessorService {
    
    private final static Logger LOGGER 
            = Logger.getLogger(DataAccessorServiceImpl.class);
    
    private final StoreManager storeManager;
    private final String BASE_URI;
    
    public DataAccessorServiceImpl(StoreManager storeManager, String baseURI) {
        this.storeManager = storeManager;
        this.BASE_URI = baseURI;
        if(this.storeManager == null) {
            LOGGER.debug("The storeManager is NULL");
        }
        if(baseURI !=null && !baseURI.isEmpty()) {
            LOGGER.debug("The base URI is NULL (or) empty string");
        }
    }

    @Override
    public String retrieveDatasetDistribution(String catalogID, 
            String datasetID, String distributionID, RDFFormat format) 
            throws DataAccessorServiceException {        
        String datasetDistributionURI = this.BASE_URI.concat("fdp").concat("/").
                concat(catalogID).concat("/").concat(datasetID).
                concat("/").concat(distributionID);
        String datasetDistribution = null;
        try {
            RepositoryResult<Statement> statements = 
                    storeManager.retrieveResource(datasetDistributionURI);
            if(statements != null) {
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
