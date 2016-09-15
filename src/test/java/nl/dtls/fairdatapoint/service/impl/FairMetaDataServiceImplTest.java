/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import java.net.MalformedURLException;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtl.fairmetadata.io.MetadataException;
import nl.dtl.fairmetadata.model.CatalogMetadata;
import nl.dtl.fairmetadata.model.DatasetMetadata;
import nl.dtl.fairmetadata.model.DistributionMetadata;
import nl.dtl.fairmetadata.model.FDPMetadata;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.repository.StoreManagerException;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
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
public class FairMetaDataServiceImplTest { 
    
    private final static Logger LOGGER = 
            LogManager.getLogger(FairMetaDataServiceImplTest.class.getName());    
    @Autowired
    private FairMetaDataService fairMetaDataService;
    
    private final String TEST_FDP_URI = "http://example.com/fdp";
    private final String TEST_CATALOG_URI = "http://example.com/fdp/catalog";
    private final String TEST_DATASET_URI = 
            "http://example.com/fdp/catalog/dataset";
    private final String TEST_DISTRIBUTION_URI = 
            "http://example.com/fdp/catalog/dataset/distrubtion";
    
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
     * Test to store FDP metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeFDPMetaData(){
        try {
            fairMetaDataService.storeFDPMetaData(ExampleFilesUtils.
                    getFDPMetadata(TEST_FDP_URI));
        } catch (Exception ex) {                           
            fail("The test is not excepted to throw any exception");
        }
    }
      
    /**
     * Test to retrieve FDP metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void retrieveFDPMetaData(){
        try {
            FDPMetadata metadata = fairMetaDataService.retrieveFDPMetaData(
                    ExampleFilesUtils.FDP_URI);
            assertNotNull(metadata);
        } catch (FairMetadataServiceException ex) {             
            fail("The test is not excepted to throw any exception");
        }
    }
    
    /**
     * Test to store catalog metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeCatalogMetaData(){
        try {
            fairMetaDataService.storeCatalogMetaData(ExampleFilesUtils.
                    getCatalogMetadata(TEST_CATALOG_URI, 
                            ExampleFilesUtils.FDP_URI));
        } catch (Exception ex) {
            fail("The test is not excepted to throw any exception");
        }
    }
    
    /**
     * Test to retrieve NonExiting catalog metadata, this test is excepted
     * to throw an exception
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void retrieveNonExitingCatalogMetaData() throws 
            FairMetadataServiceException {            
        String uri = ExampleFilesUtils.FDP_URI + "/dummpID676";
        fairMetaDataService.retrieveCatalogMetaData(uri);            
        fail("This test is execpeted to throw IllegalStateException");
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
            fail("The test is not excepted to throw any exception");
        }
    } 
    
    /**
     * Test to store dataset metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeDatasetMetaData(){
        try {
            fairMetaDataService.storeDatasetMetaData(ExampleFilesUtils.
                    getDatasetMetadata(TEST_DATASET_URI, 
                            ExampleFilesUtils.CATALOG_URI));
        } catch (Exception ex) {            
            fail("The test is not excepted to throw any exception");
        }
    }
    
    /**
     * Test to retrieve NonExiting dataset metadata, this test is excepted 
     * to throw an exception
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void retrieveNonExitingdDatasetMetaData() throws 
            FairMetadataServiceException{ 
        String uri = ExampleFilesUtils.CATALOG_URI + "/dummpID676";            
        fairMetaDataService.retrieveDatasetMetaData(uri);            
        fail("This test is execpeted to throw IllegalStateException");        
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
            fail("The test is not excepted to throw any exception");
        }
    }  
    
    /**
     * Test to store dataset distribution, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeDistributionMetaData(){
        try {
            fairMetaDataService.storeDistributionMetaData(ExampleFilesUtils.
                    getDistributionMetadata(TEST_DISTRIBUTION_URI, 
                            ExampleFilesUtils.DATASET_URI));
        } catch (Exception ex) {
            fail("The test is not excepted to throw any exception");
        }
    }    
    
    /**
     * Test to retrieve non exiting distribution metadata, this test is 
     * to throw an exception
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void retrieveNonExitingDatasetDistribution() throws 
            FairMetadataServiceException{        
        String uri = ExampleFilesUtils.DATASET_URI + "/dummpID676";
        fairMetaDataService.retrieveDistributionMetaData(uri);
        fail("This test is execpeted to throw IllegalStateException");        
    }

    /**
     * Test to retrieve distribution metadata, this test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void retrieveDatasetDistribution(){
        try {
            DistributionMetadata metadata = fairMetaDataService.
                    retrieveDistributionMetaData(ExampleFilesUtils.
                            DISTRIBUTION_URI);
            assertNotNull(metadata);
        } catch (FairMetadataServiceException ex) {            
            fail("The test is not excepted to throw any exception");
        }
    }
    
}
