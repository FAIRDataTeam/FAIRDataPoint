/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import java.net.MalformedURLException;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtl.fairmetadata.model.CatalogMetadata;
import nl.dtl.fairmetadata.model.DatasetMetadata;
import nl.dtl.fairmetadata.model.DistributionMetadata;
import nl.dtl.fairmetadata.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.api.repository.StoreManagerException;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * FairMetaDataServiceImpl class unit tests
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-02-08
 * @version 0.4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiTestContext.class})
@DirtiesContext
//@Ignore
public class FairMetaDataServiceImplTest { 
    
    private final static Logger LOGGER = 
            LogManager.getLogger(FairMetaDataServiceImplTest.class.getName());    
    @Autowired
    private FairMetaDataService fairMetaDataService;
    
    private final String TEST_FDP_URI = "http://example.com/fdp";
    private final String TEST_CATALOG_URI = "http://example.com/fdp/catalog";
    private final String TEST_CATALOG_ID = "catalog";
    private final String TEST_DATASET_URI = 
            "http://example.com/fdp/catalog/dataset";
    private final String TEST_DATASET_ID = "dataset";
    private final String TEST_DISTRIBUTION_ID = "distribution";
    private final String TEST_DISTRIBUTION_URI = 
            "http://example.com/fdp/catalog/dataset/distrubtion";
    
    @Before
    public void storeExampleMetadata() throws StoreManagerException, 
            MalformedURLException, DatatypeConfigurationException, 
            FairMetadataServiceException {        
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
     * Test to store FDP metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeFDPMetaData(){
        try {
            fairMetaDataService.storeFDPMetaData(
                    ExampleFilesUtils.getFDPMetadata(TEST_FDP_URI));
        } catch (Exception ex) {
            String errorMsg = "The test is not excepted to throw "
                    + "any exception";
            fail(errorMsg);
        }
    }
      
    /**
     * Test to retrieve FDP metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void retrieveFDPMetaData(){
        try {
            FDPMetadata metadata = fairMetaDataService.
                    retrieveFDPMetaData(ExampleFilesUtils.FDP_URI);
            assertNotNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is not excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to store catalog metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeCatalogMetaData(){
        try {
            fairMetaDataService.storeCatalogMetaData(
                    ExampleFilesUtils.getCatalogMetadata(TEST_CATALOG_URI, 
                            TEST_FDP_URI));
        } catch (Exception ex) {
            String errorMsg = "The test is not excepted to throw "
                    + "any exception";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve NonExiting catalog metadata, this test is excepted
     * to pass
     */
    @DirtiesContext
    @Test
    public void retrieveNonExitingCatalogMetaData(){
        try {
            String catalogURI = ExampleFilesUtils.FDP_URI + "/dummpID676";
            CatalogMetadata metadata = fairMetaDataService.
                    retrieveCatalogMetaData(catalogURI);
            assertNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is not excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve catalog metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void retrieveCatalogMetaData(){
        try {
            CatalogMetadata metadata = fairMetaDataService.
                    retrieveCatalogMetaData(ExampleFilesUtils.CATALOG_URI);
            assertNotNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is not excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    } 
    
    /**
     * Test to store dataset metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeDatasetMetaData(){
        try {
            fairMetaDataService.storeDatasetMetaData(
                    ExampleFilesUtils.getDatasetMetadata(TEST_DATASET_URI, 
                            TEST_CATALOG_URI));
        } catch (Exception ex) {
            String errorMsg = "The test is not excepted to throw "
                    + "any exception";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve NonExiting dataset metadata, this test is excepted 
     * to pass
     */
    @DirtiesContext
    @Test
    public void retrieveNonExitingdDatasetMetaData(){
        try {
            String datasetURI = ExampleFilesUtils.CATALOG_URI + "/dummpID676";
            DatasetMetadata metadata = fairMetaDataService.
                    retrieveDatasetMetaData(datasetURI);
            assertNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is not excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve dataset metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void retrieveDatasetMetaData(){
        try {
            DatasetMetadata metadata = fairMetaDataService.
                    retrieveDatasetMetaData(ExampleFilesUtils.DATASET_URI);
            assertNotNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is not excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }  
    
    /**
     * Test to store dataset distribution, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeDistributionMetaData(){
        try {
            fairMetaDataService.storeDistributionMetaData(
                    ExampleFilesUtils.getDistributionMetadata(TEST_DISTRIBUTION_URI, TEST_DATASET_URI));
        } catch (Exception ex) {
            String errorMsg = "The test is not excepted to throw "
                    + "any exception";
            fail(errorMsg);
        }
    }    
    
    /**
     * Test to retrieve non exiting distribution metadata, this test is 
     * excepted to pass
     */
    @DirtiesContext
    @Test
    public void retrieveNonExitingDatasetDistribution(){
        try {
            String distributionURI = ExampleFilesUtils.DATASET_URI + 
                    "/dummpID676";
            DistributionMetadata metadata = fairMetaDataService.
                    retrieveDistributionMetaData(distributionURI);
            assertNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is not excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }

    /**
     * Test to retrieve distribution metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void retrieveDatasetDistribution(){
        try {
            DistributionMetadata metadata = fairMetaDataService.
                    retrieveDistributionMetaData(
                            ExampleFilesUtils.DISTRIBUTION_URI);
            assertNotNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is not excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
}
