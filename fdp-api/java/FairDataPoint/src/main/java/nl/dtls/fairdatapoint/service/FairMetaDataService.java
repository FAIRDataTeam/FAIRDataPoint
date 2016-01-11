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
 * @since 2015-11-23
 * @version 0.2
 */
public interface FairMetaDataService {   
    /**
     * Get FDP server's metadata
     * @return  String object  
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException  
     */
    public String retrieveFDPMetaData(RDFFormat format) 
            throws FairMetadataServiceException;  

    /**
     *  Get catalog's metadata
     * @param catalogID Unique catalog ID
     * @return  String object
     * @throws FairMetadataServiceException
     */
    public String retrieveCatalogMetaData
        (String catalogID, RDFFormat format) throws FairMetadataServiceException;

    /**
     *  Get dataset's metadata
     * @param catalogID
     * @param datasetID Unique dataset ID
     * @return  String object
     * @throws FairMetadataServiceException
     */
    public String retrieveDatasetMetaData
        (String catalogID, String datasetID, RDFFormat format) 
                throws FairMetadataServiceException;
        
}
