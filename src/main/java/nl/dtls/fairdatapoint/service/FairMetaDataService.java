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
     * Get metadata of given URI
     * 
     * @param uri
     * @param format RDFFormat serialization formats
     * @return  String object  
     * @throws FairMetadataServiceException  
     */
    String retrieveMetaData(String uri, RDFFormat format) 
            throws FairMetadataServiceException;  
    /**
     * Store catalog metadata
     * 
     * @param catalogMetadata
     * @throws FairMetadataServiceException 
     */
    void storeCatalogMetaData(CatalogMetadata catalogMetadata) 
            throws FairMetadataServiceException;
    /**
     * Store dataset metadata
     * 
     * @param datasetMetadata
     * @throws FairMetadataServiceException 
     */
    void storeDatasetMetaData(DatasetMetadata datasetMetadata) 
            throws FairMetadataServiceException;
    
    /**
     * Store fdp metadata
     * 
     * @param fdpMetaData
     * @throws FairMetadataServiceException 
     */
    void storeFDPMetaData(FDPMetadata fdpMetaData) 
            throws FairMetadataServiceException; 
        
}
