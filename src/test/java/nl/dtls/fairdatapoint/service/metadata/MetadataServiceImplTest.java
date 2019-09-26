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
import nl.dtl.fairmetadata4j.model.*;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.annotation.DirtiesContext;

import java.time.ZonedDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@DirtiesContext
public class MetadataServiceImplTest extends BaseIntegrationTest {

    private final static ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();
    private final String TEST_FDP_URI = "http://example.com/fdp";
    private final String TEST_CATALOG_URI = "http://example.com/fdp/catalog";
    private final String TEST_DATASET_URI = "http://example.com/fdp/catalog/dataset";
    private final String TEST_DATARECORD_URI = "http://example.com/fdp/catalog/dataset/datarecord";
    private final String TEST_DISTRIBUTION_URI
            = "http://example.com/fdp/catalog/dataset/distrubtion";
    @Autowired
    private MetadataService fairMetaDataService;
    @Mock
    private MetadataService mockFairMetaDataService;

    @Before
    public void storeExampleMetadata() throws Exception {

        // Store fdp metadata
        fairMetaDataService.storeFDPMetadata(ExampleFilesUtils
                .getFDPMetadata(ExampleFilesUtils.FDP_URI));

        // Store catalog metadata
        fairMetaDataService.storeCatalogMetadata(ExampleFilesUtils
                .getCatalogMetadata(ExampleFilesUtils.CATALOG_URI, ExampleFilesUtils.FDP_URI));

        // Store dataset metadata
        fairMetaDataService.storeDatasetMetadata(ExampleFilesUtils.
                getDatasetMetadata(ExampleFilesUtils.DATASET_URI, ExampleFilesUtils.CATALOG_URI));

        // Store distribution metadata
        fairMetaDataService.storeDistributionMetadata(ExampleFilesUtils
                .getDistributionMetadata(ExampleFilesUtils.DISTRIBUTION_URI,
                        ExampleFilesUtils.DATASET_URI));

        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test to store FDP metadata, this test is excepted to throw error
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = MetadataException.class)
    public void storeFDPMetaDataWithNoTitle() throws Exception {

        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(TEST_FDP_URI);
        metadata.setTitle(null);
        fairMetaDataService.storeFDPMetadata(metadata);
    }

    /**
     * Test to store FDP metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeFDPMetaData() {

        try {
            fairMetaDataService.storeFDPMetadata(ExampleFilesUtils.getFDPMetadata(TEST_FDP_URI));
        } catch (MetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }

    /**
     * Test to store FDP metadata without metadata ID
     */
    @DirtiesContext
    @Test
    public void storeFDPMetaDataWithNoID() {

        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(TEST_FDP_URI);
        metadata.setIdentifier(null);
        try {
            fairMetaDataService.storeFDPMetadata(metadata);
            FDPMetadata mdata = fairMetaDataService
                    .retrieveFDPMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
            assertNotNull(mdata.getIdentifier());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store FDP metadata without repo ID
     */
    @DirtiesContext
    @Test
    public void storeFDPMetaDataWithNoRepoID() {

        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(TEST_FDP_URI);
        metadata.setRepostoryIdentifier(null);
        try {
            fairMetaDataService.storeFDPMetadata(metadata);
            FDPMetadata mdata = fairMetaDataService
                    .retrieveFDPMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
            assertNotNull(mdata.getRepostoryIdentifier());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store FDP metadata without publisher
     */
    @DirtiesContext
    @Test
    public void storeFDPMetaDataWithNoPublisher() {

        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(TEST_FDP_URI);
        metadata.setPublisher(null);
        try {
            fairMetaDataService.storeFDPMetadata(metadata);
            FDPMetadata mdata = fairMetaDataService
                    .retrieveFDPMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
            assertNotNull(mdata.getPublisher());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store FDP metadata without language
     */
    @DirtiesContext
    @Test
    public void storeFDPMetaDataWithNoLanguage() {

        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(TEST_FDP_URI);
        metadata.setLanguage(null);
        try {
            fairMetaDataService.storeFDPMetadata(metadata);
            FDPMetadata mdata = fairMetaDataService
                    .retrieveFDPMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
            assertNotNull(mdata.getLanguage());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store FDP metadata without license
     */
    @DirtiesContext
    @Test
    public void storeFDPMetaDataWithNoLicense() {

        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(TEST_FDP_URI);
        metadata.setLicense(null);
        try {
            fairMetaDataService.storeFDPMetadata(metadata);
            FDPMetadata mdata = fairMetaDataService
                    .retrieveFDPMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
            assertNotNull(mdata.getLicense());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to update FDP metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void updateFDPMetaData() {

        try {
            FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(TEST_FDP_URI);
            fairMetaDataService.storeFDPMetadata(metadata);
            fairMetaDataService.updateFDPMetadata(VALUEFACTORY.createIRI(TEST_FDP_URI), metadata);
        } catch (MetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }

    /**
     * This test is excepted to throw an error
     *
     * @throws MetadataServiceException
     */
    @DirtiesContext
    @Test(expected = NullPointerException.class)
    public void nullFDPURI() throws Exception {

        fairMetaDataService.retrieveFDPMetadata(null);
        fail("No RDF statements excepted for NULL URI");
    }

    /**
     * Test to retrieve FDP metadata, this test is excepted to pass
     *
     * @throws MetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveFDPMetaData() throws MetadataServiceException {

        assertNotNull(fairMetaDataService.retrieveFDPMetadata(VALUEFACTORY
                .createIRI(ExampleFilesUtils.FDP_URI)));
    }

    /**
     * Test MetadataServiceException exception
     *
     * @throws MetadataServiceException
     */
    @DirtiesContext
    @Test(expected = MetadataServiceException.class)
    public void retrieveFDPMetaDataCheckException() throws Exception {

        String uri = "http://example.com/dummy";
        when(mockFairMetaDataService.retrieveFDPMetadata(VALUEFACTORY.createIRI(uri)))
                .thenThrow(new MetadataServiceException(""));
        mockFairMetaDataService.retrieveFDPMetadata(VALUEFACTORY.createIRI(uri));
    }

    /**
     * Test existence of FDP metadata specs link
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test
    public void existenceFDPMetaDataSpecsLink() throws Exception {

        FDPMetadata metadata = fairMetaDataService
                .retrieveFDPMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
        assertNotNull(metadata.getSpecification());
    }

    /**
     * Test existence of mertic
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test
    public void existenceMetric() throws Exception {

        FDPMetadata metadata = fairMetaDataService
                .retrieveFDPMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
        assertFalse(metadata.getMetrics().isEmpty());
    }

    /**
     * Test existence of access rights description
     */
    @DirtiesContext
    @Test
    public void existenceAccessRightsStatement() throws Exception {

        FDPMetadata metadata = fairMetaDataService.retrieveFDPMetadata(VALUEFACTORY
                .createIRI(ExampleFilesUtils.FDP_URI));
        assertNotNull(metadata.getAccessRights().getDescription());
    }

    /**
     * Test to store catalog metadata, this test is excepted to throw error
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeCatalogMetaDataWithNOParentUri() throws Exception {

        CatalogMetadata metadata = ExampleFilesUtils
                .getCatalogMetadata(TEST_CATALOG_URI, ExampleFilesUtils.FDP_URI);
        metadata.setParentURI(null);
        fairMetaDataService.storeCatalogMetadata(metadata);
    }

    /**
     * Test to store catalog metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeCatalogMetaData() {

        try {
            fairMetaDataService.storeCatalogMetadata(ExampleFilesUtils
                    .getCatalogMetadata(TEST_CATALOG_URI, ExampleFilesUtils.FDP_URI));
        } catch (MetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }

    /**
     * Test to store catalog metadata without metadata ID
     */
    @DirtiesContext
    @Test
    public void storeCatalogMetaDataWithNoID() {

        CatalogMetadata metadata = ExampleFilesUtils
                .getCatalogMetadata(TEST_CATALOG_URI, ExampleFilesUtils.FDP_URI);
        metadata.setIdentifier(null);
        try {
            fairMetaDataService.storeCatalogMetadata(metadata);
            CatalogMetadata mdata = fairMetaDataService
                    .retrieveCatalogMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
            assertNotNull(mdata.getIdentifier());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store catalog metadata without publisher
     */
    @DirtiesContext
    @Test
    public void storeCatalogMetaDataWithNoPublisher() {

        CatalogMetadata metadata = ExampleFilesUtils
                .getCatalogMetadata(TEST_CATALOG_URI, ExampleFilesUtils.FDP_URI);
        metadata.setPublisher(null);
        try {
            fairMetaDataService.storeCatalogMetadata(metadata);
            CatalogMetadata mdata = fairMetaDataService
                    .retrieveCatalogMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
            assertNotNull(mdata.getPublisher());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store catalog metadata without language
     */
    @DirtiesContext
    @Test
    public void storeCatalogMetaDataWithNoLanguage() {

        CatalogMetadata metadata = ExampleFilesUtils
                .getCatalogMetadata(TEST_CATALOG_URI, ExampleFilesUtils.FDP_URI);
        metadata.setLanguage(null);
        try {
            fairMetaDataService.storeCatalogMetadata(metadata);
            CatalogMetadata mdata = fairMetaDataService
                    .retrieveCatalogMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
            assertNotNull(mdata.getLanguage());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store catalog metadata without license
     */
    @DirtiesContext
    @Test
    public void storeCatalogMetaDataWithNoLicense() {

        CatalogMetadata metadata = ExampleFilesUtils
                .getCatalogMetadata(TEST_CATALOG_URI, ExampleFilesUtils.FDP_URI);
        metadata.setLicense(null);
        try {
            fairMetaDataService.storeCatalogMetadata(metadata);
            CatalogMetadata mdata = fairMetaDataService
                    .retrieveCatalogMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
            assertNotNull(mdata.getLicense());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to retrieve NonExiting catalog metadata, this test is excepted to throw an exception
     *
     * @throws MetadataServiceException
     */
    @DirtiesContext
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveNonExitingCatalogMetaData() throws Exception {

        String uri = ExampleFilesUtils.FDP_URI + "/dummpID676";
        fairMetaDataService.retrieveCatalogMetadata(VALUEFACTORY.createIRI(uri));
    }

    /**
     * Test to retrieve catalog metadata, this test is excepted to pass
     *
     * @throws MetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveCatalogMetaData() throws Exception {

        assertNotNull(fairMetaDataService
                .retrieveCatalogMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI)));
    }

    /**
     * Test existence of catalog metadata specs link
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test
    public void existenceCatalogMetaDataSpecsLink() throws Exception {

        CatalogMetadata metadata = fairMetaDataService
                .retrieveCatalogMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
        assertNotNull(metadata.getSpecification());
    }

    /**
     * Test to store dataset metadata, this test is excepted to throw error
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDatasetMetaDataWithNoParentURI() throws Exception {

        DatasetMetadata metadata = ExampleFilesUtils
                .getDatasetMetadata(TEST_DATASET_URI, ExampleFilesUtils.CATALOG_URI);
        metadata.setParentURI(null);
        fairMetaDataService.storeDatasetMetadata(metadata);
    }

    /**
     * Test to store dataset metadata with wrong parent uri
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDatasetMetaDataWrongParentUri() throws Exception {

        fairMetaDataService.storeDatasetMetadata(ExampleFilesUtils
                .getDatasetMetadata(TEST_DATASET_URI, ExampleFilesUtils.FDP_URI));
    }

    /**
     * Test to store dataset metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeDatasetMetaData() {

        try {
            fairMetaDataService.storeDatasetMetadata(ExampleFilesUtils
                    .getDatasetMetadata(TEST_DATASET_URI, ExampleFilesUtils.CATALOG_URI));
        } catch (MetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }

    /**
     * Test to store dataset metadata without metadata ID
     */
    @DirtiesContext
    @Test
    public void storeDatsetMetaDataWithNoID() {

        DatasetMetadata metadata = ExampleFilesUtils
                .getDatasetMetadata(TEST_DATASET_URI, ExampleFilesUtils.CATALOG_URI);
        metadata.setIdentifier(null);
        try {
            fairMetaDataService.storeDatasetMetadata(metadata);
            DatasetMetadata mdata = fairMetaDataService
                    .retrieveDatasetMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
            assertNotNull(mdata.getIdentifier());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store dataset metadata without publisher
     */
    @DirtiesContext
    @Test
    public void storeDatsetMetaDataWithNoPublisher() {

        DatasetMetadata metadata = ExampleFilesUtils
                .getDatasetMetadata(TEST_DATASET_URI, ExampleFilesUtils.CATALOG_URI);
        metadata.setPublisher(null);
        try {
            fairMetaDataService.storeDatasetMetadata(metadata);
            DatasetMetadata mdata = fairMetaDataService
                    .retrieveDatasetMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
            assertNotNull(mdata.getPublisher());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store dataset metadata without language
     */
    @DirtiesContext
    @Test
    public void storeDatsetMetaDataWithNoLanguage() {

        DatasetMetadata metadata = ExampleFilesUtils
                .getDatasetMetadata(TEST_DATASET_URI, ExampleFilesUtils.CATALOG_URI);
        metadata.setLanguage(null);
        try {
            fairMetaDataService.storeDatasetMetadata(metadata);
            DatasetMetadata mdata = fairMetaDataService
                    .retrieveDatasetMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
            assertNotNull(mdata.getLanguage());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store dataset metadata without license
     */
    @DirtiesContext
    @Test
    public void storeDatsetMetaDataWithNoLicense() {

        DatasetMetadata metadata = ExampleFilesUtils
                .getDatasetMetadata(TEST_DATASET_URI, ExampleFilesUtils.CATALOG_URI);
        metadata.setLicense(null);
        try {
            fairMetaDataService.storeDatasetMetadata(metadata);
            DatasetMetadata mdata = fairMetaDataService
                    .retrieveDatasetMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
            assertNotNull(mdata.getLicense());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to retrieve NonExiting dataset metadata, this test is excepted to throw an exception
     *
     * @throws MetadataServiceException
     */
    @DirtiesContext
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveNonExitingdDatasetMetaData() throws Exception {

        String uri = ExampleFilesUtils.CATALOG_URI + "/dummpID676";
        fairMetaDataService.retrieveDatasetMetadata(VALUEFACTORY.createIRI(uri));
    }

    /**
     * Test to retrieve dataset metadata, this test is excepted to pass
     *
     * @throws MetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveDatasetMetaData() throws Exception {

        assertNotNull(fairMetaDataService
                .retrieveDatasetMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI)));
    }

    /**
     * Test existence of dataset metadata specs link
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test
    public void existenceDatasetMetaDataSpecsLink() throws Exception {

        DatasetMetadata metadata = fairMetaDataService
                .retrieveDatasetMetadata(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
        assertNotNull(metadata.getSpecification());
    }

    /**
     * Test to store dataset distribution, this test is excepted to throw error
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDistributionMetaDataNoParentURI() throws Exception {

        DistributionMetadata metadata = ExampleFilesUtils
                .getDistributionMetadata(TEST_DISTRIBUTION_URI, ExampleFilesUtils.DATASET_URI);
        metadata.setParentURI(null);
        fairMetaDataService.storeDistributionMetadata(metadata);
    }

    /**
     * Test to store distribution, this test is excepted to pass
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDistributionMetaDataWrongParentURI() throws Exception {

        fairMetaDataService.storeDistributionMetadata(ExampleFilesUtils
                .getDistributionMetadata(TEST_DISTRIBUTION_URI, ExampleFilesUtils.CATALOG_URI));
    }

    /**
     * Test to store distribution metadata with wrong parent uri
     */
    @DirtiesContext
    @Test
    public void storeDistributionMetaData() {

        try {
            fairMetaDataService.storeDistributionMetadata(ExampleFilesUtils
                    .getDistributionMetadata(TEST_DISTRIBUTION_URI, ExampleFilesUtils.DATASET_URI));
        } catch (MetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }

    /**
     * Test to store distribution metadata without metadata ID
     */
    @DirtiesContext
    @Test
    public void storeDistributionMetaDataWithNoID() {

        DistributionMetadata metadata = ExampleFilesUtils
                .getDistributionMetadata(TEST_DISTRIBUTION_URI, ExampleFilesUtils.DATASET_URI);
        metadata.setIdentifier(null);
        try {
            fairMetaDataService.storeDistributionMetadata(metadata);
            DistributionMetadata mdata = fairMetaDataService
                    .retrieveDistributionMetadata(VALUEFACTORY
                            .createIRI(ExampleFilesUtils.DISTRIBUTION_URI));
            assertNotNull(mdata.getIdentifier());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store distribution metadata without publisher
     */
    @DirtiesContext
    @Test
    public void storeDistributionMetaDataWithNoPublisher() {

        DistributionMetadata metadata = ExampleFilesUtils
                .getDistributionMetadata(TEST_DISTRIBUTION_URI, ExampleFilesUtils.DATASET_URI);
        metadata.setPublisher(null);
        try {
            fairMetaDataService.storeDistributionMetadata(metadata);
            DistributionMetadata mdata = fairMetaDataService
                    .retrieveDistributionMetadata(VALUEFACTORY
                            .createIRI(ExampleFilesUtils.DISTRIBUTION_URI));
            assertNotNull(mdata.getPublisher());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store distribution metadata without license
     */
    @DirtiesContext
    @Test
    public void storeDistributionMetaDataWithNoLicense() {

        DistributionMetadata metadata = ExampleFilesUtils
                .getDistributionMetadata(TEST_DISTRIBUTION_URI, ExampleFilesUtils.DATASET_URI);
        metadata.setLicense(null);
        try {
            fairMetaDataService.storeDistributionMetadata(metadata);
            DistributionMetadata mdata = fairMetaDataService
                    .retrieveDistributionMetadata(VALUEFACTORY
                            .createIRI(ExampleFilesUtils.DISTRIBUTION_URI));
            assertNotNull(mdata.getLicense());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store distribution metadata without language
     */
    @DirtiesContext
    @Test
    public void storeDistributionMetaDataWithNoLanguage() {

        DistributionMetadata metadata = ExampleFilesUtils
                .getDistributionMetadata(TEST_DISTRIBUTION_URI, ExampleFilesUtils.DATASET_URI);
        metadata.setLanguage(null);
        try {
            fairMetaDataService.storeDistributionMetadata(metadata);
            DistributionMetadata mdata = fairMetaDataService
                    .retrieveDistributionMetadata(VALUEFACTORY
                            .createIRI(ExampleFilesUtils.DISTRIBUTION_URI));
            assertNotNull(mdata.getLanguage());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store datarecord metadata, this test is excepted to throw error
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDataRecordMetaDataNoParentURI() throws Exception {

        DataRecordMetadata metadata = ExampleFilesUtils
                .getDataRecordMetadata(TEST_DATARECORD_URI, ExampleFilesUtils.DATASET_URI);
        metadata.setParentURI(null);
        fairMetaDataService.storeDataRecordMetadata(metadata);
    }

    /**
     * Test to store datarecord metadata, this test is excepted to pass
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDataRecordMetaDataWrongParentURI() throws Exception {

        DataRecordMetadata metadata = ExampleFilesUtils
                .getDataRecordMetadata(TEST_DATARECORD_URI, ExampleFilesUtils.CATALOG_URI);
        fairMetaDataService.storeDataRecordMetadata(metadata);
    }

    /**
     * Test to store datarecord metadata
     */
    @DirtiesContext
    @Test
    public void storeDataRecordMetaData() {

        try {
            DataRecordMetadata metadata = ExampleFilesUtils
                    .getDataRecordMetadata(TEST_DATARECORD_URI, ExampleFilesUtils.DATASET_URI);
            fairMetaDataService.storeDataRecordMetadata(metadata);
        } catch (MetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }

    /**
     * Test to store datarecord metadata without metadata ID
     */
    @DirtiesContext
    @Test
    public void storeDataRecordMetaDataWithNoID() {

        DataRecordMetadata metadata = ExampleFilesUtils
                .getDataRecordMetadata(TEST_DATARECORD_URI, ExampleFilesUtils.DATASET_URI);
        metadata.setIdentifier(null);
        try {
            fairMetaDataService.storeDataRecordMetadata(metadata);
            DataRecordMetadata mdata = fairMetaDataService.retrieveDataRecordMetadata(
                    VALUEFACTORY.createIRI(TEST_DATARECORD_URI));
            assertNotNull(mdata.getIdentifier());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to store datarecord metadata without publisher
     */
    @DirtiesContext
    @Test
    public void storeDataRecordMetaDataWithNoPublisher() {

        DataRecordMetadata metadata = ExampleFilesUtils
                .getDataRecordMetadata(TEST_DATARECORD_URI, ExampleFilesUtils.DATASET_URI);
        metadata.setPublisher(null);
        try {
            fairMetaDataService.storeDataRecordMetadata(metadata);
            DataRecordMetadata mdata = fairMetaDataService.
                    retrieveDataRecordMetadata(VALUEFACTORY.createIRI(
                            TEST_DATARECORD_URI));
            assertNotNull(mdata.getPublisher());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        }
    }

    /**
     * Test to retrieve non exiting distribution metadata, this test is to throw an exception
     *
     * @throws MetadataServiceException
     */
    @DirtiesContext
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveNonExitingDatasetDistribution() throws Exception {

        String uri = ExampleFilesUtils.DATASET_URI + "/dummpID676";
        fairMetaDataService.retrieveDistributionMetadata(VALUEFACTORY.createIRI(uri));
    }

    /**
     * Test to retrieve distribution metadata, this test is excepted to pass
     *
     * @throws MetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveDatasetDistribution() throws Exception {

        assertNotNull(fairMetaDataService.retrieveDistributionMetadata(
                VALUEFACTORY.createIRI(ExampleFilesUtils.DISTRIBUTION_URI)));
    }

    /**
     * Test existence of distribution metadata specs link
     *
     * @throws MetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test
    public void existenceDistributionMetaDataSpecsLink() throws Exception {

        DistributionMetadata metadata = fairMetaDataService.retrieveDistributionMetadata(
                VALUEFACTORY.createIRI(ExampleFilesUtils.DISTRIBUTION_URI));
        assertNotNull(metadata.getSpecification());
    }

    @DirtiesContext
    @Test
    public void updatePropagationToParents() throws Exception {

        // given
        DistributionMetadata newDistribution = ExampleFilesUtils
                .getDistributionMetadata(TEST_DISTRIBUTION_URI, ExampleFilesUtils.DATASET_URI);
        newDistribution.setByteSize(VALUEFACTORY.createLiteral(1_000L));

        // when
        fairMetaDataService.storeDistributionMetadata(newDistribution);

        // then
        newDistribution = fairMetaDataService.retrieveDistributionMetadata(
                VALUEFACTORY.createIRI(TEST_DISTRIBUTION_URI));
        ZonedDateTime distributionModified = ZonedDateTime.parse(
                newDistribution.getModified().stringValue());

        // compare dataset timestamp with distribution timestamp
        {
            DatasetMetadata updated = fairMetaDataService.retrieveDatasetMetadata(
                    VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
            ZonedDateTime updatedModified = ZonedDateTime.parse(
                    updated.getModified().stringValue());
            assertFalse("Distribution is modified after the dataset is modified",
                    distributionModified.isAfter(updatedModified));
        }
        // compare catalog timestamp with distribution timestamp
        {
            CatalogMetadata updated = fairMetaDataService.retrieveCatalogMetadata(
                    VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
            ZonedDateTime updatedModified = ZonedDateTime.parse(
                    updated.getModified().stringValue());
            assertFalse("Distribution is modified after the catalog is modified",
                    distributionModified.isAfter(updatedModified));
        }
        // compare repository timestamp with distribution timestamp
        {
            FDPMetadata updated = fairMetaDataService.retrieveFDPMetadata(
                    VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
            ZonedDateTime updatedModified = ZonedDateTime.parse(
                    updated.getModified().stringValue());
            assertFalse("Distribution is modified after the repository is modified",
                    distributionModified.isAfter(updatedModified));
        }
    }

    /**
     * Test non exist fdp uri
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = NullPointerException.class)
    public void getFdpUriForNullUri() throws Exception {
        fairMetaDataService.getFDPIri(null);
    }

    /**
     * Test non exist fdp uri
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void getNonExistFdpUri() throws Exception {
        assertNull(fairMetaDataService.getFDPIri(VALUEFACTORY.createIRI(
                ExampleFilesUtils.FDP_URI + "/dummy")));
    }

    /**
     * Test existing fdp uri
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void getExistingFdpUri() throws Exception {
        IRI fdpUri = VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI);

        assertEquals(fdpUri, fairMetaDataService.getFDPIri(
                VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI)));
        assertEquals(fdpUri, fairMetaDataService.getFDPIri(
                VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI)));
        assertEquals(fdpUri, fairMetaDataService.getFDPIri(
                VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI)));
        assertEquals(fdpUri, fairMetaDataService.getFDPIri(
                VALUEFACTORY.createIRI(ExampleFilesUtils.DISTRIBUTION_URI)));
    }
}
