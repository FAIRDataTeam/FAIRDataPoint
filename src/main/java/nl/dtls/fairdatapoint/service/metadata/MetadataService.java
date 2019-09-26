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
package nl.dtls.fairdatapoint.service.metadata;

import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DataRecordMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import org.eclipse.rdf4j.model.IRI;

public interface MetadataService {

    /**
     * Get metadata of given fdp URI
     *
     * @param uri fdp URI
     * @return FDPMetadata object
     * @throws MetadataServiceException
     */
    FDPMetadata retrieveFDPMetadata(IRI uri) throws MetadataServiceException;

    /**
     * Get metadata of given catalog URI
     *
     * @param uri catalog URI
     * @return CatalogMetadata object
     * @throws MetadataServiceException
     */
    CatalogMetadata retrieveCatalogMetadata(IRI uri)
            throws MetadataServiceException;

    /**
     * Get metadata of given dataset URI
     *
     * @param uri dataset URI
     * @return DatasetMetadata object
     * @throws MetadataServiceException
     */
    DatasetMetadata retrieveDatasetMetadata(IRI uri) throws MetadataServiceException;

    /**
     * Get metadata of given dataset URI
     *
     * @param uri dataset URI
     * @return DataRecordMetadata object
     * @throws MetadataServiceException
     */
    DataRecordMetadata retrieveDataRecordMetadata(IRI uri) throws MetadataServiceException;

    /**
     * Get metadata of given distribution URI
     *
     * @param uri distribution URI
     * @return DistributionMetadata object
     * @throws MetadataServiceException
     */
    DistributionMetadata retrieveDistributionMetadata(IRI uri) throws MetadataServiceException;

    /**
     * Store catalog metadata
     *
     * @param catalogMetadata
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    void storeCatalogMetadata(CatalogMetadata catalogMetadata)
            throws MetadataServiceException, MetadataException;

    /**
     * Store dataset metadata
     *
     * @param datasetMetadata
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    void storeDatasetMetadata(DatasetMetadata datasetMetadata)
            throws MetadataServiceException, MetadataException;

    /**
     * Store fdp metadata
     *
     * @param fdpMetaData
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    void storeFDPMetadata(FDPMetadata fdpMetaData)
            throws MetadataServiceException, MetadataException;

    /**
     * Store distribution metadata
     *
     * @param distributionMetadata
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    void storeDistributionMetadata(DistributionMetadata distributionMetadata)
            throws MetadataServiceException, MetadataException;

    /**
     * Store dataRecordMetadata metadata
     *
     * @param metadata
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    void storeDataRecordMetadata(DataRecordMetadata metadata)
            throws MetadataServiceException, MetadataException;

    /**
     * Update fdp metadata
     *
     * @param uri
     * @param fdpMetaData
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    void updateFDPMetadata(IRI uri, FDPMetadata fdpMetaData)
            throws MetadataServiceException, MetadataException;

    /**
     * Get fdp URI for given URI
     *
     * @param uri any metadata URI
     * @return IRI object
     * @throws MetadataServiceException
     */
    IRI getFDPIri(IRI uri) throws MetadataServiceException;

}
