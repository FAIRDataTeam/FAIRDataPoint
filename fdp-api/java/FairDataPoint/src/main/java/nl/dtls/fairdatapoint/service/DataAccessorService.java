/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service;

import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-07
 * @version 0.1
 */
public interface DataAccessorService {
    
    /**
     * Get dataset distribution
     * 
     * @param catalogID
     * @param datasetID
     * @param distributionID
     * @param format
     * @return  String object  
     * @throws nl.dtls.fairdatapoint.service.DataAccessorServiceException  
     */
    public String retrieveDatasetDistribution(String catalogID, 
            String datasetID, String distributionID, RDFFormat format) 
            throws DataAccessorServiceException;  
    
}
