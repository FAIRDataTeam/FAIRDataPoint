/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.utils.ExampleTurtleFiles;
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
            this.fairMetaDataService.retrieveFDPMetaData(null);
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
    public void retrieveFDPMetaData(){
        try {
            String actual = this.fairMetaDataService.retrieveFDPMetaData(
                    RDFFormat.TURTLE);
            assertNotNull(actual);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve catalog metadata with NULL catalogID, 
     * this test is excepted to throw IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void nullCatalogID(){
        try {
            this.fairMetaDataService.retrieveCatalogMetaData(null, 
                    RDFFormat.TURTLE);
            fail("This test is excepeted to throw IllegalArgumentException"); 
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve catalog metadata with empty catalogID, 
     * this test is excepted to throw IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void emptyCatalogID(){
        try {
            String catalogID = "";
            this.fairMetaDataService.retrieveCatalogMetaData(catalogID, 
                    RDFFormat.TURTLE);
            fail("This test is excepeted to throw IllegalArgumentException"); 
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve catalog metadata with empty catalogID, 
     * this test is excepted to throw IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void nullRDFFormatToRetrieveCatalogMetaData(){
        try {
            this.fairMetaDataService.retrieveCatalogMetaData(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID, null);
            fail("This test is excepeted to throw IllegalArgumentException"); 
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
            String actual = this.fairMetaDataService.retrieveCatalogMetaData(
                    "dummpID676", RDFFormat.TURTLE);
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
            String actual = this.fairMetaDataService.retrieveCatalogMetaData(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID, RDFFormat.TURTLE);
            assertNotNull(actual);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * Test to retrieve dataset metadata with NULL catalog or dataset IDs, 
     * this test is excepted to throw IllegalArgumentException exception 
     * 
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @Test
    public void checkNullCatalogAndDatasetIDs() 
            throws FairMetadataServiceException{
        try {
            this.fairMetaDataService.retrieveDatasetMetaData(
                    null, ExampleTurtleFiles.EXAMPLE_DATASET_ID, 
                    RDFFormat.TURTLE);
            fail("This test is excepeted to throw IllegalArgumentException"); 
        } 
        catch (IllegalArgumentException ex) {
        }
        try {
            this.fairMetaDataService.retrieveDatasetMetaData(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID, null, 
                    RDFFormat.TURTLE);
            fail("This test is excepeted to throw IllegalArgumentException"); 
        } 
        catch (IllegalArgumentException ex) {
        }
    }    
    
    /**
     * Test to retrieve dataset metadata with EMPTY catalog or dataset IDs, 
     * this test is excepted to throw IllegalArgumentException exception 
     * 
     * @throws nl.dtls.fairdatapoint.service.FairMetadataServiceException
     */
    @Test 
    public void checkEmptyCatalogAndDatasetIDs() 
            throws FairMetadataServiceException{
        try {
            this.fairMetaDataService.retrieveDatasetMetaData(
                    "", ExampleTurtleFiles.EXAMPLE_DATASET_ID, 
                    RDFFormat.TURTLE);
            fail("This test is excepeted to throw IllegalArgumentException"); 
        } 
        catch (IllegalArgumentException ex) {
        }
        try {
            this.fairMetaDataService.retrieveDatasetMetaData(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID, "", 
                    RDFFormat.TURTLE);
            fail("This test is excepeted to throw IllegalArgumentException"); 
        } 
        catch (IllegalArgumentException ex) {
        }
    }
    
    /**
     * Test to retrieve dataset metadata with empty datasetID, 
     * this test is excepted to throw IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void nullRDFFormatToRetrieveDatasetMetaData(){
        try {
            this.fairMetaDataService.retrieveDatasetMetaData(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID, 
                    ExampleTurtleFiles.EXAMPLE_DATASET_ID, null);
            fail("This test is excepeted to throw IllegalArgumentException");            
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
            String actual = this.fairMetaDataService.retrieveDatasetMetaData(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID, 
                    "dummpID7549", RDFFormat.TURTLE);
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
            String actual = this.fairMetaDataService.retrieveDatasetMetaData(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID, 
                    ExampleTurtleFiles.EXAMPLE_DATASET_ID, RDFFormat.TURTLE);
            assertNotNull(actual);
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    
}
