/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.domain;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Contain methods to store and access the triple store
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-05
 * @version 0.1
 */
public class StoreManagerImpl implements StoreManager {
    
    private static final Logger LOGGER = 
            LoggerFactory.getLogger(StoreManagerImpl.class);
    private final Repository repository;
    private RepositoryConnection repositoryConnection; 
    
    public StoreManagerImpl(Repository repository) throws 
            RepositoryException {
        this.repository = repository;
        this.repository.initialize();
    }
    
    /**
     * Retrieve all statements for an given URI
     * 
     * @param uri Valid RDF URI as a string
     * @return  List of RDF statements
     * @throws StoreManagerException 
     */
    
    @Override
    public RepositoryResult<Statement> retrieveResource(String uri) 
            throws StoreManagerException{
        if (uri == null || uri.isEmpty()) {
            String errorMsg = "The resource URI can't be NULL or EMPTY";
            LOGGER.error(errorMsg);
            throw (new RuntimeException(errorMsg));
        }
        
        RepositoryConnection conn;
        RepositoryResult<Statement> statements = null;
        try {
            conn = getRepositoryConnection();   
            Resource resourceSubj = (new ValueFactoryImpl()).createURI(uri);
            LOGGER.info("Get statements for the URI <" + 
                    resourceSubj.toString() + ">");
            if (conn.hasStatement(resourceSubj, null, null,false)) {
               statements = conn.getStatements(resourceSubj, null, null, false);  
               
            }  
            else {
                LOGGER.info("No statements for the URI <" + 
                        resourceSubj.toString() + ">");
            }
            repositoryConnection = conn;
        }
        catch (Exception e) {
            LOGGER.error("Error retrieving resource <" + uri + ">");
            throw (new StoreManagerException(e.getMessage()));
        }
        return statements;
    }
    
    /**
     * Method to close repository connection
     * 
     * @throws Exception 
     */
    @Override
    public void closeRepositoryConnection() throws Exception {
        
        try {            
            if ((repositoryConnection != null) && 
                    repositoryConnection.isOpen()) {
                repositoryConnection.close();            
            }
            else {
                String errorMsg = "The connection is either NULL or already "
                        + "CLOSED";
                LOGGER.error(errorMsg);            
                throw (new StoreManagerException(errorMsg));
            }
        }
        catch (RepositoryException e) {
            LOGGER.error("Error closing repository connection!");
            throw (new StoreManagerException(e.getMessage()));
        }
    } 
    
    /**
     * Repository connection to interact with the triple store
     * 
     * @return RepositoryConnection
     * @throws Exception 
     */
    private RepositoryConnection getRepositoryConnection() throws Exception {
        if (repositoryConnection == null || !repositoryConnection.isOpen()) {
            repositoryConnection = repository.getConnection();
        }
        return repositoryConnection;    
    }    
}
