/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import nl.dtls.fairdatapoint.domain.StoreManager;
import nl.dtls.fairdatapoint.domain.StoreManagerImpl;
import nl.dtls.fairdatapoint.service.DataAccessorService;
import nl.dtls.fairdatapoint.service.DataAccessorServiceException;
import nl.dtls.fairdatapoint.utils.ExampleTurtleFiles;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;

/**
 * DataAccessorServiceImpl class unit tests
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-02-08
 * @version 0.2
 */
public class DataAccessorServiceImplTest {
    private Repository repository;
    private StoreManager storeManager;
    private DataAccessorService dataAccessorService;
    
    /**
     * Pre populate the SailRepository with the example metadata triples
     * 
     * @throws org.openrdf.repository.RepositoryException
     */
    @Before
    public void setUp() throws RepositoryException {
        Sail store = new MemoryStore();
        this.repository = new SailRepository(store);
        this.storeManager = new StoreManagerImpl(repository);
        this.dataAccessorService = new DataAccessorServiceImpl(
                this.storeManager, ExampleTurtleFiles.BASE_URI);
        ExampleTurtleFiles.storeTurtleFileToTripleStore(repository, 
                        ExampleTurtleFiles.FDP_METADATA, null, null); 
        for (String catalog : ExampleTurtleFiles.CATALOG_METADATA) {                    
            ExampleTurtleFiles.storeTurtleFileToTripleStore(repository, 
                    catalog, null, null);                 
        }                
        for (String dataset : ExampleTurtleFiles.DATASET_METADATA) {
                    
            ExampleTurtleFiles.storeTurtleFileToTripleStore(repository,                       
                    dataset, null, null); 
        }                 
        for (String distribution : ExampleTurtleFiles.DATASET_DISTRIBUTIONS) {                    
            ExampleTurtleFiles.storeTurtleFileToTripleStore(repository,                       
                    distribution, null, null);                
        }
    }
    /**
     * After all tests close the SailRepository
     * 
     * @throws RepositoryException 
     */
    @After
    public void tearDown() throws RepositoryException {        
        this.repository.shutDown();
    }
    
    /**
     * The StoreManager can't be NULL, this test is excepted to throw 
     * IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void nullStoreManager(){
        new DataAccessorServiceImpl(null, ExampleTurtleFiles.BASE_URI);
    }
    
    /**
     * The base URI can't be NULL, this test is excepted to throw 
     * IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void nullBaseURI(){
        new DataAccessorServiceImpl(this.storeManager, null);
    }
    
    /**
     * The base URI can't be EMPTY, this test is excepted to throw 
     * IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void emptyBaseURI(){
        new DataAccessorServiceImpl(this.storeManager, "");
    }
    
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
