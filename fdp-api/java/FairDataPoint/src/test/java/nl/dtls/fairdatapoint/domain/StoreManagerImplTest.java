/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.domain;

import nl.dtls.fairdatapoint.utils.ExampleTurtleFiles;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StoreManagerImpl class unit tests
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-05
 * @version 0.1
 */
public class StoreManagerImplTest {  
    
    private static final Logger LOGGER = 
            LoggerFactory.getLogger(StoreManagerImplTest.class);
    
    /**
     * The triple repository can't be NULL, this test is excepted to throw 
     * NullPointer exception 
     */
    @Test(expected = NullPointerException.class) 
     public void exceptionForNullRepository(){
        try {
            Repository repository = new SailRepository(null);
            new StoreManagerImpl(repository);
        } catch (RepositoryException ex) {
            LOGGER.error(ex.getMessage());
            fail("The test is not excepted to throw RepositoryException");
        }
     }
     
     /**
      * The URI of a RDF resource can't be NULL, this test is excepted 
      * to throw IllegalArgumentException
      */
     @Test(expected = IllegalArgumentException.class) 
     public void nullURI() {         
         Sail store = new MemoryStore();       
         String uri = null;
         Repository repository = new SailRepository(store);            
         StoreManager testStoreManager;   
         try {
             testStoreManager = new StoreManagerImpl(repository);            
             if (testStoreManager.retrieveResource(uri).hasNext()) {                
                 fail("No RDF statements excepted for NULL URI");            
             }        
         } catch (RepositoryException | StoreManagerException ex) {            
             LOGGER.error(ex.getMessage());            
             fail("The test is not excepted to throw RepositoryException or "
                    + "StoreManagerException");        
         }
     }
     
     /**
      * The URI of a RDF resource can't be EMPTY, this test is excepted 
      * to throw IllegalArgumentException
      */
     @Test(expected = IllegalArgumentException.class) 
     public void emptyURI(){
         Sail store = new MemoryStore();       
         String uri = "";
         Repository repository = new SailRepository(store);            
         StoreManager testStoreManager;   
         try {
             testStoreManager = new StoreManagerImpl(repository);            
             if (testStoreManager.retrieveResource(uri).hasNext()) {                
                 fail("No RDF statements excepted for NULL URI");            
             }        
         } catch (RepositoryException | StoreManagerException ex) {            
             LOGGER.error(ex.getMessage());            
             fail("The test is not excepted to throw RepositoryException or "
                    + "StoreManagerException");        
         }
     }
    
     /**
      * The test is excepted to retrieve ZERO statements
      * 
      * @throws RepositoryException
      * @throws StoreManagerException
      * @throws Exception 
      */
    @Test
    public void retrieveNonExitingResource() throws RepositoryException, 
            StoreManagerException,  
            Exception {  
        
        Sail store = new MemoryStore();
        Repository repository = new SailRepository(store);    
        StoreManager testStoreManager = new StoreManagerImpl(repository); 
        ExampleTurtleFiles.storeTurtleFileToTripleStore(repository, 
                ExampleTurtleFiles.FDP_METADATA, null, null); 
        String uri = "http://semlab1.liacs.nl:8080/dummy";             
        RepositoryResult<Statement> statements = 
                testStoreManager.retrieveResource(uri); 
        int countStatements = 0;
        while(statements != null && statements.hasNext()){  
            countStatements = countStatements + 1;
            break;
        } 
        testStoreManager.closeRepositoryConnection();
        closeRepository(repository);  
        assertTrue(countStatements == 0);
    }   
    
    /**
     * The test is excepted retrieve to retrieve one or more statements
     * @throws RepositoryException
     * @throws StoreManagerException
     * @throws Exception 
     */
    @Test
    public void retrieveExitingResource() throws RepositoryException, 
            StoreManagerException,  
            Exception {  
        
        Sail store = new MemoryStore();
        Repository repository = new SailRepository(store);    
        StoreManager testStoreManager = new StoreManagerImpl(repository); 
        ExampleTurtleFiles.storeTurtleFileToTripleStore(repository, 
                ExampleTurtleFiles.FDP_METADATA, null, null);  
        String uri = "http://semlab1.liacs.nl:8080/fdp";             
        RepositoryResult<Statement> statements = 
                testStoreManager.retrieveResource(uri); 
        int countStatements = 0;
        while(statements != null && statements.hasNext()){  
            countStatements = countStatements + 1;
            break;
        } 
        testStoreManager.closeRepositoryConnection();
        closeRepository(repository);  
        assertTrue(countStatements > 0);
    }
    
    /**
     * Null RepositoryConnection connection can't be closed, the test is 
     * excepted to throw exception
     * 
     * @throws Exception 
     */
    @Test(expected = StoreManagerException.class)    
    public void closeNullRepositoryConnection() throws Exception {             
        Sail store = new MemoryStore();
        Repository repository = new SailRepository(store);    
        StoreManager testStoreManager = new StoreManagerImpl(repository);  
        testStoreManager.closeRepositoryConnection();        
    }
    
    /**
     * This test is attempt to close opened repositoryConnection, the test is
     * excepted to pass
     * 
     * @throws Exception 
     */
    @Test    
    public void closeOpenedRepositoryConnection() throws Exception {             
        Sail store = new MemoryStore();
        Repository repository = new SailRepository(store);    
        StoreManager testStoreManager = new StoreManagerImpl(repository); 
        String uri = "http://semlab1.liacs.nl:8080/dummy";
        testStoreManager.retrieveResource(uri);
        testStoreManager.closeRepositoryConnection(); 
        return;
    }
    
    /**
     * Method to close the repository
     * 
     * @throws Exception 
     */
    private void closeRepository(Repository repository) throws Exception {
            
        try {                
            if (repository != null) {                    
                repository.shutDown();                
            }            
        }            
        catch (Exception e) {                
            LOGGER.error("Error closing repository!");                
            throw (new Exception(e.getMessage()));            
        }
    } 
    
}
