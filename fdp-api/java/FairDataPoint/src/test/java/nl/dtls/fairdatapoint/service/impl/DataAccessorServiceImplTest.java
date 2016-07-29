/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.service.DataAccessorService;
import nl.dtls.fairdatapoint.service.DataAccessorServiceException;
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
 * DataAccessorServiceImpl class unit tests
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-02-08
 * @version 0.3
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiTestContext.class})
@DirtiesContext
public class DataAccessorServiceImplTest {
    
    @Autowired
    private DataAccessorService dataAccessorService;
    
    /**
     * The RDFFormat can't be NULL, this test is excepted to throw 
     * IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void nullRDFFormat(){
        try {
            this.dataAccessorService.retrieveDatasetDistribution( 
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID, 
                    ExampleTurtleFiles.EXAMPLE_DATASET_ID,
                    ExampleTurtleFiles.EXAMPLE_DISTRIBUTION_ID,
                    null);
        } catch (DataAccessorServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "DataAccessorServiceException";
            fail(errorMsg);
        }
    }
    
    /**
     * To test NULL IDs parameters, this test is excepted to 
     * IllegalArgumentException
     * 
     * @throws DataAccessorServiceException 
     */
    @Test
    public void checkNullIDsForRetrieveDatasetDistribution() throws 
            DataAccessorServiceException{ 
        String errorMsg = "The test is excepted to throw "
                + "IllegalArgumentException";
        try {
            this.dataAccessorService.retrieveDatasetDistribution(null,
                    ExampleTurtleFiles.EXAMPLE_DATASET_ID, 
                    ExampleTurtleFiles.EXAMPLE_DISTRIBUTION_ID, 
                    RDFFormat.TURTLE);
            fail(errorMsg);
        }
        catch (IllegalArgumentException e) {            
        }
        try {
            this.dataAccessorService.retrieveDatasetDistribution(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID,
                    null, ExampleTurtleFiles.EXAMPLE_DISTRIBUTION_ID, 
                    RDFFormat.TURTLE);
            fail(errorMsg);
        }
        catch (IllegalArgumentException e) {            
        }
        try {
            this.dataAccessorService.retrieveDatasetDistribution(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID,
                    ExampleTurtleFiles.EXAMPLE_DATASET_ID, null, 
                    RDFFormat.TURTLE);
            fail(errorMsg);
        }
        catch (IllegalArgumentException e) {            
        }    
    }
    
    /**
     * To test empty IDs parameters, this test is excepted to 
     * IllegalArgumentException
     * 
     * @throws DataAccessorServiceException 
     */
    @Test
    public void checkEmptyIDsForRetrieveDatasetDistribution() throws 
            DataAccessorServiceException{ 
        String errorMsg = "The test is excepted to throw "
                + "IllegalArgumentException";
        try {
            this.dataAccessorService.retrieveDatasetDistribution("",
                    ExampleTurtleFiles.EXAMPLE_DATASET_ID, 
                    ExampleTurtleFiles.EXAMPLE_DISTRIBUTION_ID, 
                    RDFFormat.TURTLE);
            fail(errorMsg);
        }
        catch (IllegalArgumentException e) {            
        }
        try {
            this.dataAccessorService.retrieveDatasetDistribution(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID,
                    "", ExampleTurtleFiles.EXAMPLE_DISTRIBUTION_ID, 
                    RDFFormat.TURTLE);
            fail(errorMsg);
        }
        catch (IllegalArgumentException e) {            
        }
        try {
            this.dataAccessorService.retrieveDatasetDistribution(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID,
                    ExampleTurtleFiles.EXAMPLE_DATASET_ID, "", 
                    RDFFormat.TURTLE);
            fail(errorMsg);
        }
        catch (IllegalArgumentException e) {            
        }    
    }
    
    /**
     * Test to retrieve non exiting distribution metadata, this test is 
     * excepted to pass
     */
    @Test
    public void retrieveNonExitingDatasetDistribution(){
        try {
            String actual = this.dataAccessorService.
                    retrieveDatasetDistribution(
                            ExampleTurtleFiles.EXAMPLE_CATALOG_ID,                    
                            ExampleTurtleFiles.EXAMPLE_DATASET_ID,                   
                            "dummpyID5645",                     
                            RDFFormat.TURTLE);
            assertNull(actual);
        } catch (DataAccessorServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "DataAccessorServiceException";
            fail(errorMsg);
        }
    }

    /**
     * Test to retrieve distribution metadata, this test is excepted to pass
     */
    @Test
    public void retrieveDatasetDistribution(){
        try {
            String actual = this.dataAccessorService.
                    retrieveDatasetDistribution(
                            ExampleTurtleFiles.EXAMPLE_CATALOG_ID,                    
                            ExampleTurtleFiles.EXAMPLE_DATASET_ID,                   
                            ExampleTurtleFiles.EXAMPLE_DISTRIBUTION_ID,                     
                            RDFFormat.TURTLE);
            assertNotNull(actual);
        } catch (DataAccessorServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "DataAccessorServiceException";
            fail(errorMsg);
        }
    }
    
}
