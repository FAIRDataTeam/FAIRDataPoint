/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.rio.RDFFormat;
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
     * The RDFFormat can't be NULL, this test is excepted to throw 
     * IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void nullRDFFormat(){
        try {
            this.fairMetaDataService.retrieveMetaData(
                    ExampleFilesUtils.FDP_URI, null);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve FDP metadata, this test is excepted to pass
     */
    @Test
    public void retrieveMetaData(){
        try {
            String actual = this.fairMetaDataService.retrieveMetaData(
                    ExampleFilesUtils.FDP_URI, RDFFormat.TURTLE);
            assertNotNull(actual);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve NonExiting catalog metadata, this test is excepted \
     * to pass
     */
    @Test
    public void retrieveNonExitingCatalogMetaData(){
        try {
            String catalogURI = ExampleFilesUtils.FDP_URI + "/dummpID676";
            String actual = this.fairMetaDataService.retrieveMetaData(
                    catalogURI, RDFFormat.TURTLE);
            assertNull(actual);
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
            String actual = this.fairMetaDataService.retrieveMetaData(
                    ExampleFilesUtils.CATALOG_URI, RDFFormat.TURTLE);
            assertNotNull(actual);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
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
            String actual = this.fairMetaDataService.retrieveMetaData(
                    datasetURI, RDFFormat.TURTLE);
            assertNull(actual);
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
            String actual = this.fairMetaDataService.retrieveMetaData(
                    ExampleFilesUtils.DATASET_URI, RDFFormat.TURTLE);
            assertNotNull(actual);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
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
            String distributionURI = ExampleFilesUtils.DATASET_URI + "/dummpID676";
            String actual = this.fairMetaDataService.
                    retrieveMetaData(distributionURI, RDFFormat.TURTLE);
            assertNull(actual);
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
            String actual = this.fairMetaDataService.
                    retrieveMetaData(ExampleFilesUtils.DISTRIBUTION_URI, 
                            RDFFormat.TURTLE);
            assertNotNull(actual);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
}
