/**
 * The MIT License
 * Copyright © 2017 DTL
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
package nl.dtls.fairdatapoint.service;

import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import org.eclipse.rdf4j.model.IRI;


/**
 * Fair metadata service interface
 * 
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2015-11-23
 * @version 0.2
 */
public interface FairMetaDataService {   
        
    /**
     * Get metadata of given fdp URI 
     * 
     * @param uri fdp URI
     * @return FDPMetadata object
     * @throws FairMetadataServiceException 
     */
    FDPMetadata retrieveFDPMetaData(IRI uri) 
            throws FairMetadataServiceException; 
    
    
    /**
     * Get metadata of given catalog URI 
     * 
     * @param uri catalog URI
     * @return CatalogMetadata object
     * @throws FairMetadataServiceException 
     */
    CatalogMetadata retrieveCatalogMetaData(IRI uri) 
            throws FairMetadataServiceException; 
    
    /**
     * Get metadata of given dataset URI 
     * 
     * @param uri dataset URI
     * @return DatasetMetadata object
     * @throws FairMetadataServiceException 
     */
    DatasetMetadata retrieveDatasetMetaData(IRI uri) 
            throws FairMetadataServiceException;  
    
    /**
     * Get metadata of given distribution URI 
     * 
     * @param uri distribution URI
     * @return DistributionMetadata object
     * @throws FairMetadataServiceException 
     */
    DistributionMetadata retrieveDistributionMetaData(IRI uri) 
            throws FairMetadataServiceException;  
    /**
     * Store catalog metadata
     * 
     * @param catalogMetadata
     * @throws FairMetadataServiceException 
     * @throws nl.dtl.fairmetadata4j.io.MetadataException 
     */
    void storeCatalogMetaData(CatalogMetadata catalogMetadata) 
            throws FairMetadataServiceException, MetadataException;
    /**
     * Store dataset metadata
     * 
     * @param datasetMetadata
     * @throws FairMetadataServiceException 
     * @throws nl.dtl.fairmetadata4j.io.MetadataException 
     */
    void storeDatasetMetaData(DatasetMetadata datasetMetadata) 
            throws FairMetadataServiceException, MetadataException;
    
    /**
     * Store fdp metadata
     * 
     * @param fdpMetaData
     * @throws FairMetadataServiceException 
     * @throws nl.dtl.fairmetadata4j.io.MetadataException 
     */
    void storeFDPMetaData(FDPMetadata fdpMetaData) 
            throws FairMetadataServiceException, MetadataException; 
    
    /**
     * Store distribution metadata
     * 
     * @param distributionMetadata
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    void storeDistributionMetaData(DistributionMetadata distributionMetadata)
            throws FairMetadataServiceException, MetadataException;
    
    /**
     * Update fdp metadata
     * 
     * @param uri
     * @param fdpMetaData
     * @throws FairMetadataServiceException 
     * @throws nl.dtl.fairmetadata4j.io.MetadataException 
     */
    void updateFDPMetaData(IRI uri, FDPMetadata fdpMetaData) 
            throws FairMetadataServiceException, MetadataException; 
        
}
