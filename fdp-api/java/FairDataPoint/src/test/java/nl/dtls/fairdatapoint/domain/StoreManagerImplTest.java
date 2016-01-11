/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.domain;

import nl.dtls.fairdatapoint.api.config.RestApiConfiguration;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.springframework.beans.factory.annotation.Autowired;
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
   @Autowired 
   private StoreManager testStoreManager;   
    @Ignore
    @Test
    public void retrieveExitingResource() throws RepositoryException, 
            StoreManagerException {        
        String uri = "http://www.rdf.dtls.nl/fdp";        
        RepositoryResult<Statement> statements = 
                testStoreManager.retrieveResource(uri);
        assertNotNull(statements);
    }
    @Ignore
    @Test
    public void retrieveNonExitingResource() throws RepositoryException, 
            StoreManagerException {        
        String uri = "http://www.rdf.dtls.nl/dummy";        
        RepositoryResult<Statement> statements = 
                testStoreManager.retrieveResource(uri);
        assertNull(statements);
    }
    
    
}
