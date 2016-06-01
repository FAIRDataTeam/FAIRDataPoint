/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.domain;

import java.util.List;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.utils.ExampleTurtleFiles;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * StoreManagerImpl class unit tests
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-05
 * @version 0.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiTestContext.class})
@DirtiesContext
public class StoreManagerImplTest {  
    
    @Autowired
    StoreManager testStoreManager;
     
     /**
      * The URI of a RDF resource can't be NULL, this test is excepted 
      * to throw IllegalArgumentException
      */
     @Test(expected = IllegalArgumentException.class)      
     public void nullURI() {           
             
        try {
            testStoreManager.retrieveResource(null); 
            fail("No RDF statements excepted for NULL URI");
        } catch (StoreManagerException ex) {
            fail("This test is not excepted to throw StoreManagerException"); 
        }
     }  
     
     /**
      * The URI of a RDF resource can't be EMPTY, this test is excepted 
      * to throw IllegalArgumentException
      */
     @Test(expected = IllegalArgumentException.class)
     public void emptyURI(){
         String uri = "";  
         try {
             testStoreManager.retrieveResource(uri);  
             fail("No RDF statements excepted for NULL URI");       
         } catch (StoreManagerException ex) {             
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
           
        testStoreManager.storeRDF(ExampleTurtleFiles.
                getTurtleAsString(ExampleTurtleFiles.EXAMPLE_FDP_METADATA_FILE), null, null); 
        String uri = "http://www.dtls.nl/dummy";             
        List<Statement> statements = 
                testStoreManager.retrieveResource(uri); 
        assertTrue(statements.isEmpty());
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
        
        this.testStoreManager.storeRDF(ExampleTurtleFiles.
                getTurtleAsString(ExampleTurtleFiles.EXAMPLE_FDP_METADATA_FILE), null, null);            
        List<Statement> statements = 
                this.testStoreManager.retrieveResource(ExampleTurtleFiles.FDP_URI); 
        assertTrue(statements.size() > 0);
    }    
}
