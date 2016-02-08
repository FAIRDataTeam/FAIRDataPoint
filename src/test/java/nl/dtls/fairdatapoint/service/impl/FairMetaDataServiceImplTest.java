/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.impl;

import nl.dtls.fairdatapoint.api.config.RestApiConfiguration;
import nl.dtls.fairdatapoint.domain.StoreManager;
import nl.dtls.fairdatapoint.domain.StoreManagerImpl;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.utils.ExampleTurtleFiles;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * FairMetaDataService class unit tests
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-02-08
 * @version 0.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiConfiguration.class})
public class FairMetaDataServiceImplTest {    
    private Repository repository;
    private StoreManager storeManager;
    private FairMetaDataService fairMetaDataService;
    
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
        this.fairMetaDataService = new FairMetaDataServiceImpl(
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
        new FairMetaDataServiceImpl(null, ExampleTurtleFiles.BASE_URI);
    }
    
    /**
     * The base URI can't be NULL, this test is excepted to throw 
     * IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void nullBaseURI(){
        new FairMetaDataServiceImpl(this.storeManager, null);
    }
    
    /**
     * The base URI can't be EMPTY, this test is excepted to throw 
     * IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void emptyBaseURI(){
        new FairMetaDataServiceImpl(this.storeManager, "");
    }
    
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
            assertTrue(actual.contains(ExampleTurtleFiles.BASE_URI));
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
     * Test to retrieve dataset metadata with NULL datasetID, 
     * this test is excepted to throw IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void nullDatasetID(){
        try {
            this.fairMetaDataService.retrieveDatasetMetaData(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID, null, 
                    RDFFormat.TURTLE);
            fail("This test is excepeted to throw IllegalArgumentException"); 
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }    
    
    /**
     * Test to retrieve dataset metadata metadata with empty datasetID, 
     * this test is excepted to throw IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void emptyDatasetID(){
        try {
            String datasetID = "";
            this.fairMetaDataService.retrieveDatasetMetaData(
                    ExampleTurtleFiles.EXAMPLE_CATALOG_ID, datasetID, 
                    RDFFormat.TURTLE);                
            fail("This test is excepeted to throw IllegalArgumentException");            
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
        }
    }
    /**
     * Test to retrieve dataset metadata with NULL datasetID, 
     * this test is excepted to throw IllegalArgumentException exception 
     */
    @Test(expected = IllegalArgumentException.class) 
    public void validDatasetIDAndNullCatalogID(){
        try {
            this.fairMetaDataService.retrieveDatasetMetaData(
                    null, ExampleTurtleFiles.EXAMPLE_DATASET_ID, 
                    RDFFormat.TURTLE);
            fail("This test is excepeted to throw IllegalArgumentException"); 
        } catch (FairMetadataServiceException ex) {
            String errorMsg = "The test is excepted to throw "
                    + "FairMetadataServiceException";
            fail(errorMsg);
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
     * Test to retrieve catalog metadata, this test is excepted to pass
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
