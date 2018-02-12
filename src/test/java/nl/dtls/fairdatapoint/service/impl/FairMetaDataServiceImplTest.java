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
package nl.dtls.fairdatapoint.service.impl;

import java.net.MalformedURLException;
import java.time.ZonedDateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtl.fairmetadata4j.model.DataRecordMetadata;
import nl.dtl.fairmetadata4j.model.DatasetMetadata;
import nl.dtl.fairmetadata4j.model.DistributionMetadata;
import nl.dtl.fairmetadata4j.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.repository.StoreManagerException;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * FairMetaDataServiceImpl class unit tests
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2016-02-08
 * @version 0.4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiTestContext.class})
@DirtiesContext
public class FairMetaDataServiceImplTest {

    private final static Logger LOGGER
            = LogManager.getLogger(FairMetaDataServiceImplTest.class.getName());
    
    private final static ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();
    @Autowired
    private FairMetaDataService fairMetaDataService;
    private final String TEST_FDP_URI = "http://example.com/fdp";
    private final String TEST_CATALOG_URI = "http://example.com/fdp/catalog";
    private final String TEST_DATASET_URI
            = "http://example.com/fdp/catalog/dataset";
    private final String TEST_DATARECORD_URI
            = "http://example.com/fdp/catalog/dataset/datarecord";
    private final String TEST_DISTRIBUTION_URI
            = "http://example.com/fdp/catalog/dataset/distrubtion";

    @Before
    public void storeExampleMetadata() throws StoreManagerException,
            MalformedURLException, DatatypeConfigurationException,
            FairMetadataServiceException, MetadataException {
        LOGGER.info("Storing example FDP metadata for service layer tests");
        fairMetaDataService.storeFDPMetaData(
                ExampleFilesUtils.getFDPMetadata(ExampleFilesUtils.FDP_URI));
        LOGGER.info("Storing example catalog metadata for service layer tests");
        fairMetaDataService.storeCatalogMetaData(ExampleFilesUtils.
                getCatalogMetadata(ExampleFilesUtils.CATALOG_URI,
                        ExampleFilesUtils.FDP_URI));
        LOGGER.info("Storing example dataset metadata for service layer tests");
        fairMetaDataService.storeDatasetMetaData(ExampleFilesUtils.
                getDatasetMetadata(ExampleFilesUtils.DATASET_URI,
                        ExampleFilesUtils.CATALOG_URI));
        LOGGER.info("Storing example distribution "
                + "metadata for service layer tests");
        fairMetaDataService.storeDistributionMetaData(
                ExampleFilesUtils.getDistributionMetadata(
                        ExampleFilesUtils.DISTRIBUTION_URI,
                        ExampleFilesUtils.DATASET_URI));
    }

    /**
     * Test to store FDP metadata, this test is excepted to throw error
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = MetadataException.class)
    public void storeFDPMetaDataWithNoTitle() throws 
            FairMetadataServiceException, MetadataException {
        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(
                TEST_FDP_URI);
        metadata.setTitle(null);
        fairMetaDataService.storeFDPMetaData(metadata);
    }

    /**
     * Test to store FDP metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeFDPMetaData() {
        try {
            fairMetaDataService.storeFDPMetaData(
                    ExampleFilesUtils.getFDPMetadata(TEST_FDP_URI));
        } catch (FairMetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }
    
    

    /**
     * Test to store FDP metadata without metadata ID
     */
    @DirtiesContext
    @Test
    public void storeFDPMetaDataWithNoID() {
        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(
                TEST_FDP_URI);
        metadata.setIdentifier(null);
        try {
            fairMetaDataService.storeFDPMetaData(metadata);
            FDPMetadata mdata = fairMetaDataService.retrieveFDPMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
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
        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(
                TEST_FDP_URI);
        metadata.setRepostoryIdentifier(null);
        try {
            fairMetaDataService.storeFDPMetaData(metadata);
            FDPMetadata mdata = fairMetaDataService.retrieveFDPMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
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
        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(
                TEST_FDP_URI);
        metadata.setPublisher(null);
        try {
            fairMetaDataService.storeFDPMetaData(metadata);
            FDPMetadata mdata = fairMetaDataService.retrieveFDPMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
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
        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(
                TEST_FDP_URI);
        metadata.setLanguage(null);
        try {
            fairMetaDataService.storeFDPMetaData(metadata);
            FDPMetadata mdata = fairMetaDataService.retrieveFDPMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
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
        FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(
                TEST_FDP_URI);
        metadata.setLicense(null);
        try {
            fairMetaDataService.storeFDPMetaData(metadata);
            FDPMetadata mdata = fairMetaDataService.retrieveFDPMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
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
            FDPMetadata metadata = ExampleFilesUtils.getFDPMetadata(
                    TEST_FDP_URI);
            fairMetaDataService.storeFDPMetaData(metadata);
            fairMetaDataService.updateFDPMetaData(VALUEFACTORY.
                    createIRI(TEST_FDP_URI), metadata);
        } catch (FairMetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }

    /**
     * This test is excepted to throw an error
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test(expected = NullPointerException.class)
    public void nullFDPURI() throws FairMetadataServiceException {
        fairMetaDataService.retrieveFDPMetaData(null);
        fail("No RDF statements excepted for NULL URI");
    }

    /**
     * Test to retrieve FDP metadata, this test is excepted to pass
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveFDPMetaData() throws FairMetadataServiceException {
        assertNotNull(fairMetaDataService.retrieveFDPMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI)));
    }     
    
    /**
     * Test existence of FDP metadata specs link
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test
    public void existenceFDPMetaDataSpecsLink() throws 
            FairMetadataServiceException, MetadataException {
        FDPMetadata metadata = fairMetaDataService.retrieveFDPMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
        assertNotNull(metadata.getSpecification());
    }
    
    /**
     * Test existence of mertic
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test
    public void existenceMetric() throws
            FairMetadataServiceException, MetadataException {
        FDPMetadata metadata = fairMetaDataService.retrieveFDPMetaData(VALUEFACTORY.createIRI(
                ExampleFilesUtils.FDP_URI));
        assertFalse(metadata.getMetrics().isEmpty());
    }
    
    /**
     * Test existence of access rights description
     */
    @DirtiesContext
    @Test
    public void existenceAccessRightsStatement() throws Exception {
        FDPMetadata metadata = fairMetaDataService.retrieveFDPMetaData(VALUEFACTORY.createIRI(
                ExampleFilesUtils.FDP_URI));
        assertNotNull(metadata.getAccessRights().getDescription());
    }

    /**
     * Test to store catalog metadata, this test is excepted to throw error
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeCatalogMetaDataWithNOParentUri() throws
            FairMetadataServiceException, MetadataException,
            IllegalStateException {
        CatalogMetadata metadata = ExampleFilesUtils.getCatalogMetadata(
                TEST_CATALOG_URI, ExampleFilesUtils.FDP_URI);
        metadata.setParentURI(null);
        fairMetaDataService.storeCatalogMetaData(metadata);
    }

    /**
     * Test to store catalog metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeCatalogMetaData() {
        try {
            fairMetaDataService.storeCatalogMetaData(ExampleFilesUtils.
                    getCatalogMetadata(TEST_CATALOG_URI,
                            ExampleFilesUtils.FDP_URI));
        } catch (FairMetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }
    
    /**
     * Test to store catalog metadata without metadata ID
     */
    @DirtiesContext
    @Test
    public void storeCatalogMetaDataWithNoID() {
        CatalogMetadata metadata = ExampleFilesUtils.
                    getCatalogMetadata(TEST_CATALOG_URI,
                            ExampleFilesUtils.FDP_URI);
        metadata.setIdentifier(null);
        try {
            fairMetaDataService.storeCatalogMetaData(metadata);
            CatalogMetadata mdata = fairMetaDataService.retrieveCatalogMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
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
        CatalogMetadata metadata = ExampleFilesUtils.
                    getCatalogMetadata(TEST_CATALOG_URI,
                            ExampleFilesUtils.FDP_URI);
        metadata.setPublisher(null);
        try {
            fairMetaDataService.storeCatalogMetaData(metadata);
            CatalogMetadata mdata = fairMetaDataService.retrieveCatalogMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
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
        CatalogMetadata metadata = ExampleFilesUtils.
                    getCatalogMetadata(TEST_CATALOG_URI,
                            ExampleFilesUtils.FDP_URI);
        metadata.setLanguage(null);
        try {
            fairMetaDataService.storeCatalogMetaData(metadata);
            CatalogMetadata mdata = fairMetaDataService.retrieveCatalogMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
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
        CatalogMetadata metadata = ExampleFilesUtils.
                    getCatalogMetadata(TEST_CATALOG_URI,
                            ExampleFilesUtils.FDP_URI);
        metadata.setLicense(null);
        try {
            fairMetaDataService.storeCatalogMetaData(metadata);
            CatalogMetadata mdata = fairMetaDataService.retrieveCatalogMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
            assertNotNull(mdata.getLicense());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        } 
    }

    /**
     * Test to retrieve NonExiting catalog metadata, this test is excepted to
     * throw an exception
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveNonExitingCatalogMetaData() throws
            FairMetadataServiceException {
        String uri = ExampleFilesUtils.FDP_URI + "/dummpID676";
        fairMetaDataService.retrieveCatalogMetaData(VALUEFACTORY.createIRI(uri));
    }

    /**
     * Test to retrieve catalog metadata, this test is excepted to pass
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveCatalogMetaData() throws FairMetadataServiceException {
        assertNotNull(fairMetaDataService.retrieveCatalogMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI)));
    }
    
    /**
     * Test existence of catalog metadata specs link
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test
    public void existenceCatalogMetaDataSpecsLink() throws 
            FairMetadataServiceException, MetadataException {
        CatalogMetadata metadata = fairMetaDataService.retrieveCatalogMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
        assertNotNull(metadata.getSpecification());
    }

    /**
     * Test to store dataset metadata, this test is excepted to throw error
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDatasetMetaDataWithNoParentURI() throws
            FairMetadataServiceException,
            MetadataException, IllegalStateException {

        DatasetMetadata metadata = ExampleFilesUtils.getDatasetMetadata(
                TEST_DATASET_URI, ExampleFilesUtils.CATALOG_URI);
        metadata.setParentURI(null);
        fairMetaDataService.storeDatasetMetaData(metadata);
    }
    
    /**
     * Test to store dataset metadata with wrong parent uri
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDatasetMetaDataWrongParentUri() throws
            FairMetadataServiceException, MetadataException {
        fairMetaDataService.storeDatasetMetaData(ExampleFilesUtils.
                getDatasetMetadata(TEST_DATASET_URI,
                        ExampleFilesUtils.FDP_URI));
    }

    /**
     * Test to store dataset metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeDatasetMetaData() {
        try {
            fairMetaDataService.storeDatasetMetaData(ExampleFilesUtils.
                    getDatasetMetadata(TEST_DATASET_URI,
                            ExampleFilesUtils.CATALOG_URI));
        } catch (FairMetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }
    
    /**
     * Test to store dataset metadata without metadata ID
     */
    @DirtiesContext
    @Test
    public void storeDatsetMetaDataWithNoID() {
        DatasetMetadata metadata = ExampleFilesUtils.
                    getDatasetMetadata(TEST_DATASET_URI,
                            ExampleFilesUtils.CATALOG_URI);
        metadata.setIdentifier(null);
        try {
            fairMetaDataService.storeDatasetMetaData(metadata);
            DatasetMetadata mdata = fairMetaDataService.retrieveDatasetMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
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
        DatasetMetadata metadata = ExampleFilesUtils.
                    getDatasetMetadata(TEST_DATASET_URI,
                            ExampleFilesUtils.CATALOG_URI);
        metadata.setPublisher(null);
        try {
            fairMetaDataService.storeDatasetMetaData(metadata);
            DatasetMetadata mdata = fairMetaDataService.retrieveDatasetMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
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
        DatasetMetadata metadata = ExampleFilesUtils.
                    getDatasetMetadata(TEST_DATASET_URI,
                            ExampleFilesUtils.CATALOG_URI);
        metadata.setLanguage(null);
        try {
            fairMetaDataService.storeDatasetMetaData(metadata);
            DatasetMetadata mdata = fairMetaDataService.retrieveDatasetMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
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
        DatasetMetadata metadata = ExampleFilesUtils.
                    getDatasetMetadata(TEST_DATASET_URI,
                            ExampleFilesUtils.CATALOG_URI);
        metadata.setLicense(null);
        try {
            fairMetaDataService.storeDatasetMetaData(metadata);
            DatasetMetadata mdata = fairMetaDataService.retrieveDatasetMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
            assertNotNull(mdata.getLicense());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        } 
    }

    /**
     * Test to retrieve NonExiting dataset metadata, this test is excepted to
     * throw an exception
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveNonExitingdDatasetMetaData() throws
            FairMetadataServiceException {
        String uri = ExampleFilesUtils.CATALOG_URI + "/dummpID676";
        fairMetaDataService.retrieveDatasetMetaData(VALUEFACTORY.createIRI(uri));
    }

    /**
     * Test to retrieve dataset metadata, this test is excepted to pass
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveDatasetMetaData() throws FairMetadataServiceException {
        assertNotNull(fairMetaDataService.retrieveDatasetMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI)));
    }
    
    /**
     * Test existence of dataset metadata specs link
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test
    public void existenceDatasetMetaDataSpecsLink() throws 
            FairMetadataServiceException, MetadataException {
        DatasetMetadata metadata = fairMetaDataService.retrieveDatasetMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
        assertNotNull(metadata.getSpecification());
    }

    /**
     * Test to store dataset distribution, this test is excepted to throw error
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDistributionMetaDataNoParentURI() throws
            FairMetadataServiceException, MetadataException,
            IllegalStateException {
        DistributionMetadata metadata = ExampleFilesUtils.
                getDistributionMetadata(TEST_DISTRIBUTION_URI,
                        ExampleFilesUtils.DATASET_URI);
        metadata.setParentURI(null);
        fairMetaDataService.storeDistributionMetaData(metadata);
    }
    
    /**
     * Test to store distribution, this test is excepted to pass
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDistributionMetaDataWrongParentURI() 
            throws FairMetadataServiceException, MetadataException {
        fairMetaDataService.storeDistributionMetaData(ExampleFilesUtils.
                getDistributionMetadata(TEST_DISTRIBUTION_URI,
                        ExampleFilesUtils.CATALOG_URI));
    }
    /**
     * Test to store distribution metadata with wrong parent uri
     */
    @DirtiesContext
    @Test
    public void storeDistributionMetaData() {
        try {
            fairMetaDataService.storeDistributionMetaData(ExampleFilesUtils.
                    getDistributionMetadata(TEST_DISTRIBUTION_URI,
                            ExampleFilesUtils.DATASET_URI));
        } catch (FairMetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }
    
    /**
     * Test to store distribution metadata without metadata ID
     */
    @DirtiesContext
    @Test
    public void storeDistributionMetaDataWithNoID() {
        DistributionMetadata metadata = ExampleFilesUtils.
                    getDistributionMetadata(TEST_DISTRIBUTION_URI,
                            ExampleFilesUtils.DATASET_URI);
        metadata.setIdentifier(null);
        try {
            fairMetaDataService.storeDistributionMetaData(metadata);
            DistributionMetadata mdata = fairMetaDataService.
                    retrieveDistributionMetaData(VALUEFACTORY.createIRI(
                            ExampleFilesUtils.DISTRIBUTION_URI));
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
        DistributionMetadata metadata = ExampleFilesUtils.
                    getDistributionMetadata(TEST_DISTRIBUTION_URI,
                            ExampleFilesUtils.DATASET_URI);
        metadata.setPublisher(null);
        try {
            fairMetaDataService.storeDistributionMetaData(metadata);
            DistributionMetadata mdata = fairMetaDataService.
                    retrieveDistributionMetaData(VALUEFACTORY.createIRI(
                            ExampleFilesUtils.DISTRIBUTION_URI));
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
        DistributionMetadata metadata = ExampleFilesUtils.
                    getDistributionMetadata(TEST_DISTRIBUTION_URI,
                            ExampleFilesUtils.DATASET_URI);
        metadata.setLicense(null);
        try {
            fairMetaDataService.storeDistributionMetaData(metadata);
            DistributionMetadata mdata = fairMetaDataService.
                    retrieveDistributionMetaData(VALUEFACTORY.createIRI(
                            ExampleFilesUtils.DISTRIBUTION_URI));
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
        DistributionMetadata metadata = ExampleFilesUtils.
                    getDistributionMetadata(TEST_DISTRIBUTION_URI,
                            ExampleFilesUtils.DATASET_URI);
        metadata.setLanguage(null);
        try {
            fairMetaDataService.storeDistributionMetaData(metadata);
            DistributionMetadata mdata = fairMetaDataService.
                    retrieveDistributionMetaData(VALUEFACTORY.createIRI(
                            ExampleFilesUtils.DISTRIBUTION_URI));
            assertNotNull(mdata.getLanguage());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        } 
    }
    
    /**
     * Test to store datarecord metadata, this test is excepted to throw error
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDataRecordMetaDataNoParentURI() throws
            FairMetadataServiceException, MetadataException,
            IllegalStateException {
        DataRecordMetadata metadata = ExampleFilesUtils.
                getDataRecordMetadata(TEST_DATARECORD_URI,
                        ExampleFilesUtils.DATASET_URI);
        metadata.setParentURI(null);
        fairMetaDataService.storeDataRecordMetaData(metadata);
    }
    
    /**
     * Test to store datarecord metadata, this test is excepted to pass
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDataRecordMetaDataWrongParentURI() 
            throws FairMetadataServiceException, MetadataException {
        DataRecordMetadata metadata = ExampleFilesUtils.
                getDataRecordMetadata(TEST_DATARECORD_URI,
                        ExampleFilesUtils.CATALOG_URI);
        fairMetaDataService.storeDataRecordMetaData(metadata);
    }
    /**
     * Test to store datarecord metadata
     */
    @DirtiesContext
    @Test
    public void storeDataRecordMetaData() {
        try {
            DataRecordMetadata metadata = ExampleFilesUtils.
                getDataRecordMetadata(TEST_DATARECORD_URI,
                        ExampleFilesUtils.DATASET_URI);
            fairMetaDataService.storeDataRecordMetaData(metadata);
        } catch (FairMetadataServiceException | MetadataException ex) {
            fail("This test is not expected to throw an errors");
        }
    }
    
    /**
     * Test to store datarecord metadata without metadata ID
     */
    @DirtiesContext
    @Test
    public void storeDataRecordMetaDataWithNoID() {
        DataRecordMetadata metadata = ExampleFilesUtils.
                getDataRecordMetadata(TEST_DATARECORD_URI,
                        ExampleFilesUtils.DATASET_URI);
        metadata.setIdentifier(null);
        try {
            fairMetaDataService.storeDataRecordMetaData(metadata);
            DataRecordMetadata mdata = fairMetaDataService.
                    retrieveDataRecordMetadata(VALUEFACTORY.createIRI(
                            TEST_DATARECORD_URI));
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
        DataRecordMetadata metadata = ExampleFilesUtils.
                getDataRecordMetadata(TEST_DATARECORD_URI,
                        ExampleFilesUtils.DATASET_URI);
        metadata.setPublisher(null);
        try {
            fairMetaDataService.storeDataRecordMetaData(metadata);
            DataRecordMetadata mdata = fairMetaDataService.
                    retrieveDataRecordMetadata(VALUEFACTORY.createIRI(
                            TEST_DATARECORD_URI));
            assertNotNull(mdata.getPublisher());
        } catch (Exception ex) {
            fail("This test is not excepted to throw any error");
        } 
    }

    /**
     * Test to retrieve non exiting distribution metadata, this test is to throw
     * an exception
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test(expected = ResourceNotFoundException.class)
    public void retrieveNonExitingDatasetDistribution() throws
            FairMetadataServiceException {
        String uri = ExampleFilesUtils.DATASET_URI + "/dummpID676";
        fairMetaDataService.retrieveDistributionMetaData(VALUEFACTORY.createIRI(uri));
    }

    /**
     * Test to retrieve distribution metadata, this test is excepted to pass
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test
    public void retrieveDatasetDistribution() throws
            FairMetadataServiceException {
        assertNotNull(fairMetaDataService.retrieveDistributionMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.DISTRIBUTION_URI)));
    }
    
    /**
     * Test existence of distribution metadata specs link
     *
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     * @throws nl.dtl.fairmetadata4j.io.MetadataException
     */
    @DirtiesContext
    @Test
    public void existenceDistributionMetaDataSpecsLink() throws 
            FairMetadataServiceException, MetadataException {
        DistributionMetadata metadata = fairMetaDataService.
                retrieveDistributionMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.DISTRIBUTION_URI));
        assertNotNull(metadata.getSpecification());
    }
    
    @DirtiesContext
    @Test
    public void updatePropagationToParents() throws MetadataException, FairMetadataServiceException {
        // given
        DistributionMetadata newDistribution = ExampleFilesUtils.getDistributionMetadata(
                TEST_DISTRIBUTION_URI, ExampleFilesUtils.DATASET_URI);
        newDistribution.setByteSize(VALUEFACTORY.createLiteral(1_000L));
        
        // when
        fairMetaDataService.storeDistributionMetaData(newDistribution);
        
        // then
        newDistribution = fairMetaDataService.retrieveDistributionMetaData(VALUEFACTORY.createIRI(TEST_DISTRIBUTION_URI));
        ZonedDateTime distributionModified = ZonedDateTime.parse(
                newDistribution.getModified().stringValue());
        
        // compare dataset timestamp with distribution timestamp
        {
            DatasetMetadata updated = fairMetaDataService.retrieveDatasetMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.DATASET_URI));
            ZonedDateTime updatedModified = ZonedDateTime.parse(
                    updated.getModified().stringValue());
            assertTrue("Distribution is modified before the dataset is modified",
                    distributionModified.isBefore(updatedModified));
        }
        // compare catalog timestamp with distribution timestamp
        {
            CatalogMetadata updated = fairMetaDataService.retrieveCatalogMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.CATALOG_URI));
            ZonedDateTime updatedModified = ZonedDateTime.parse(
                    updated.getModified().stringValue());
            assertTrue("Distribution is modified before the catalog is modified",
                    distributionModified.isBefore(updatedModified));
        }
        // compare repository timestamp with distribution timestamp
        {
            FDPMetadata updated = fairMetaDataService.retrieveFDPMetaData(VALUEFACTORY.createIRI(ExampleFilesUtils.FDP_URI));
            ZonedDateTime updatedModified = ZonedDateTime.parse(
                    updated.getModified().stringValue());
            assertTrue("Distribution is modified before the repository is modified",
                    distributionModified.isBefore(updatedModified));
        }
    }
}
