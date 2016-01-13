/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.domain;

import java.io.IOException;
import nl.dtls.fairdatapoint.api.config.RestApiConfiguration;
import nl.dtls.fairdatapoint.utils.ExampleTurtle;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-05
 * @version 0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiConfiguration.class})
public class StoreManagerImplTest {  
    
    @Test
    public void retrieveExitingResource() throws RepositoryException, 
            StoreManagerException,  
            Exception {  
        
        Sail store = new MemoryStore();
        Repository repository = new SailRepository(store);    
        StoreManager testStoreManager = new StoreManagerImpl(repository); 
        uploadRDF(repository);  
        String uri = "http://semlab1.liacs.nl:8080/fdp";             
        RepositoryResult<Statement> statements = 
                testStoreManager.retrieveResource(uri); 
        int countStatements = 0;
        while(statements != null && statements.hasNext()){  
            countStatements = countStatements + 1;
            break;
        } 
        testStoreManager.closeRepositoryConnection();
        testStoreManager.closeRepository();  
        assertTrue(countStatements > 0);
    }
    
    @Test
    public void retrieveNonExitingResource() throws RepositoryException, 
            StoreManagerException,  
            Exception {  
        
        Sail store = new MemoryStore();
        Repository repository = new SailRepository(store);    
        StoreManager testStoreManager = new StoreManagerImpl(repository); 
        uploadRDF(repository);  
        String uri = "http://semlab1.liacs.nl:8080/dummy";             
        RepositoryResult<Statement> statements = 
                testStoreManager.retrieveResource(uri); 
        int countStatements = 0;
        while(statements != null && statements.hasNext()){  
            countStatements = countStatements + 1;
            break;
        } 
        testStoreManager.closeRepositoryConnection();
        testStoreManager.closeRepository();  
        assertTrue(countStatements == 0);
    }
    
    
    private void uploadRDF (Repository repository) {
        RepositoryConnection conn = null;        
        try {
            conn = repository.getConnection();
            conn.add(ExampleTurtle.getTurtleAsFile(ExampleTurtle.FDP_METADATA), 
                    null, ExampleTurtle.FILES_RDF_FORMAT);                   
        }
        catch (RepositoryException | IOException | RDFParseException e) {
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (Exception e) {
            }
        }
    }
    
    
}
