/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.api.domain.CatalogMetadata;
import nl.dtls.fairdatapoint.api.domain.DatasetMetadata;
import nl.dtls.fairdatapoint.api.domain.DistributionMetadata;
import nl.dtls.fairdatapoint.api.domain.FDPMetadata;
import nl.dtls.fairdatapoint.api.domain.MetadataExeception;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
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
 * @version 0.3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiTestContext.class})
@DirtiesContext
public class FairMetaDataServiceImplTest { 
    
    @Autowired
    private FairMetaDataService fairMetaDataService;
    
    /**
     * Test to store FDP metadata, this test is excepted to pass
     */
    @Test
    public void storeFDPMetaData(){
        try {
            String exampleFDPURL = "http://example.com/fdp";
            FDPMetadata fdpMetaData = new FDPMetadata(exampleFDPURL);
            fairMetaDataService.storeFDPMetaData(fdpMetaData);
        } catch (Exception ex) {
            String errorMsg = "The test is excepted to throw "
                    + "any exception";
            fail(errorMsg);
        }
    }
      
    /**
     * Test to retrieve FDP metadata, this test is excepted to pass
     */
    @Test
    public void retrieveFDPMetaData(){
        try {
            FDPMetadata metadata = fairMetaDataService.
                    retrieveFDPMetaData(ExampleFilesUtils.FDP_URI);
            assertNotNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to store catalog metadata, this test is excepted to pass
     */
    @Test
    public void storeCatalogMetaData(){
        try {
            String cMetadata = ExampleFilesUtils.getFileContentAsString(
                    ExampleFilesUtils.CATALOG_METADATA_FILE);
            CatalogMetadata metadata = new CatalogMetadata(cMetadata, 
                    ExampleFilesUtils.CATALOG_ID, ExampleFilesUtils.FDP_URI, 
                    ExampleFilesUtils.FILE_FORMAT);
            fairMetaDataService.storeCatalogMetaData(metadata);
        } catch (MetadataExeception | DatatypeConfigurationException | 
                FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "any exception";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve NonExiting catalog metadata, this test is excepted
     * to pass
     */
    @Test
    public void retrieveNonExitingCatalogMetaData(){
        try {
            String catalogURI = ExampleFilesUtils.FDP_URI + "/dummpID676";
            CatalogMetadata metadata = fairMetaDataService.
                    retrieveCatalogMetaData(catalogURI);
            assertNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve catalog metadata, this test is excepted to pass
     */
    @Test
    public void retrieveCatalogMetaData(){
        try {
            CatalogMetadata metadata = fairMetaDataService.
                    retrieveCatalogMetaData(ExampleFilesUtils.CATALOG_URI);
            assertNotNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    } 
    
    /**
     * Test to store dataset metadata, this test is excepted to pass
     */
    @Test
    public void storeDatasetMetaData(){
        try {
            String dMetadata = ExampleFilesUtils.getFileContentAsString(
                    ExampleFilesUtils.DATASET_METADATA_FILE);
            DatasetMetadata metadata = new DatasetMetadata(dMetadata, 
                    ExampleFilesUtils.DATASET_ID, ExampleFilesUtils.CATALOG_URI, 
                    ExampleFilesUtils.FILE_FORMAT);
            fairMetaDataService.storeDatasetMetaData(metadata);
        } catch (MetadataExeception | DatatypeConfigurationException | 
                FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "any exception";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve NonExiting dataset metadata, this test is excepted 
     * to pass
     */
    @Test
    public void retrieveNonExitingdDatasetMetaData(){
        try {
            String datasetURI = ExampleFilesUtils.CATALOG_URI + "/dummpID676";
            DatasetMetadata metadata = fairMetaDataService.
                    retrieveDatasetMetaData(datasetURI);
            assertNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve dataset metadata, this test is excepted to pass
     */
    @Test
    public void retrieveDatasetMetaData(){
        try {
            DatasetMetadata metadata = fairMetaDataService.
                    retrieveDatasetMetaData(ExampleFilesUtils.DATASET_URI);
            assertNotNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }  
    
    /**
     * Test to store dataset distribution, this test is excepted to pass
     */
    @Test
    public void storeDistributionMetaData(){
        try {
            String distMetadata = ExampleFilesUtils.getFileContentAsString(
                    ExampleFilesUtils.DISTRIBUTION_METADATA_FILE);
            DistributionMetadata metadata = new DistributionMetadata(
                    distMetadata, ExampleFilesUtils.DISTRIBUTION_ID, 
                    ExampleFilesUtils.DATASET_URI, 
                    ExampleFilesUtils.FILE_FORMAT);
            fairMetaDataService.storeDistributionMetaData(metadata);
        } catch (MetadataExeception | DatatypeConfigurationException | 
                FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "any exception";
            fail(errorMsg);
        }
    }    
    
    /**
     * Test to retrieve non exiting distribution metadata, this test is 
     * excepted to pass
     */
    @Test
    public void retrieveNonExitingDatasetDistribution(){
        try {
            String distributionURI = ExampleFilesUtils.DATASET_URI + 
                    "/dummpID676";
            DistributionMetadata metadata = fairMetaDataService.
                    retrieveDistributionMetaData(distributionURI);
            assertNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }

    /**
     * Test to retrieve distribution metadata, this test is excepted to pass
     */
    @Test
    public void retrieveDatasetDistribution(){
        try {
            DistributionMetadata metadata = fairMetaDataService.
                    retrieveDistributionMetaData(
                            ExampleFilesUtils.DISTRIBUTION_URI);
            assertNotNull(metadata);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
}
