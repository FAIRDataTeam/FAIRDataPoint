/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.repository.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import nl.dtls.fairdatapoint.api.repository.StoreManager;
import nl.dtls.fairdatapoint.api.repository.StoreManagerException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * Contain methods to store and access the triple store
 * 
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-05
 * @version 0.2
 */
@Repository("storeManager")
public class StoreManagerImpl implements StoreManager {
    
    private static final Logger LOGGER = 
            LoggerFactory.getLogger(StoreManagerImpl.class);
    @Autowired
    @Qualifier("repository")
    private org.openrdf.repository.Repository repository;  
    
    
    
    /**
     * Retrieve all statements for an given URI
     * 
     * @param uri Valid RDF URI as a string
     * @return  List of RDF statements
     * @throws StoreManagerException 
     */
    
    @Override
    public List<Statement> retrieveResource(String uri) 
            throws StoreManagerException {
        
        if (uri == null || uri.isEmpty()) {
            String errorMsg = "The resource URI can't be NULL or EMPTY";
            LOGGER.error(errorMsg);
            throw (new IllegalArgumentException(errorMsg));
        }        
        RepositoryConnection conn = null;
        List<Statement> statements = new ArrayList();
        try {
            conn = getRepositoryConnection();   
            URI resourceSubj = new URIImpl(uri);
            LOGGER.info("Get statements for the URI <" + 
                    resourceSubj.toString() + ">");
            if (conn.hasStatement(resourceSubj, null, null,false)) {
               RepositoryResult<Statement> queryResult = conn.getStatements(
                       resourceSubj, null, null, false); 
               while(queryResult.hasNext()) {
                   statements.add(queryResult.next());                  
               }
               
            } else {
                LOGGER.info("No statements for the URI <" + 
                        resourceSubj.toString() + ">");
            }
        } catch (RepositoryException ex) {            
            LOGGER.error("Error retrieving resource <" + uri + ">");
            throw (new StoreManagerException(ex.getMessage()));
        }
        finally {
            try {
                closeRepositoryConnection(conn);
            } catch (StoreManagerException e) {                
                LOGGER.error("Error closing connection",e); 
                throw (new StoreManagerException(e.getMessage()));
                
            }
        }
        return statements;
    } 
    
    /**
     * Check if a statement exist in a triple store
     * 
     * @param rsrc
     * @param pred
     * @param value
     * @return
     * @throws StoreManagerException 
     */
    @Override
    public boolean isStatementExist(Resource rsrc, URI pred, Value value) 
            throws StoreManagerException { 
        
        boolean isStatementExist = false;       
        RepositoryConnection conn = null; 
        try {
            conn = getRepositoryConnection();  
            LOGGER.info("Check if statements exists");
           isStatementExist = conn.hasStatement(rsrc, pred, value, false);
            
        } catch (RepositoryException ex) {            
            LOGGER.error("Error checking statement's existence");
            throw (new StoreManagerException(ex.getMessage()));
        }
        finally {
            try {
                closeRepositoryConnection(conn);
            } catch (StoreManagerException e) {                
                LOGGER.error("Error closing connection",e); 
                throw (new StoreManagerException(e.getMessage()));
                
            }
        }
        return isStatementExist;
    } 
    
    /**
     * Store string RDF to the repository
     * 
     * @throws StoreManagerException 
     */
    @Override
    public void storeRDF (List<Statement> statements) throws 
            StoreManagerException {
        RepositoryConnection conn = null;
        try {
            conn = getRepositoryConnection();
            Iterator<Statement> st = statements.iterator();
            while(st.hasNext()) {
                conn.add(st.next());
            }              
        } catch (RepositoryException ex) {
            LOGGER.error("Error storing RDF",ex);
            throw (new StoreManagerException(ex.getMessage()));
        }  
        finally {
            try {
                closeRepositoryConnection(conn);
            } catch (StoreManagerException e) {                
                LOGGER.error("Error closing connection",e); 
                throw (new StoreManagerException(e.getMessage()));
                
            }
        }
    }
    
    /**
     * Remove a statement from the repository
     * 
     * @param pred
     * @throws StoreManagerException 
     */
    @Override
    public void removeStatement (Resource rsrc, URI pred, Value value) throws 
            StoreManagerException {
        RepositoryConnection conn = null;
        try {
            conn = getRepositoryConnection();
            conn.remove(rsrc, pred, value);
            //conn.remove(statement);
        } catch (RepositoryException ex) {
            LOGGER.error("Error storing RDF",ex);
            throw (new StoreManagerException(ex.getMessage()));
        }  
        finally {
            try {
                closeRepositoryConnection(conn);
            } catch (StoreManagerException e) {                
                LOGGER.error("Error closing connection",e); 
                throw (new StoreManagerException(e.getMessage()));
                
            }
        }
    }
    
    /**
     * Method to close repository connection
     * 
     * @throws nl.dtls.fairdatapoint.api.repository.StoreManagerException
     */
    private void closeRepositoryConnection(RepositoryConnection conn) throws 
            StoreManagerException {
        
        try {            
            if ((conn != null) && conn.isOpen()) {
                conn.close();            
            } else {
                String errorMsg = "The connection is either NULL or already "
                        + "CLOSED";
                LOGGER.error(errorMsg);            
                throw (new StoreManagerException(errorMsg));
            }
        } catch (RepositoryException ex) {
            LOGGER.error("Error closing repository connection!");
            throw (new StoreManagerException(ex.getMessage()));
        }
    } 
    
    /**
     * Repository connection to interact with the triple store
     * 
     * @return RepositoryConnection
     * @throws Exception 
     */
    private RepositoryConnection getRepositoryConnection() 
            throws StoreManagerException {
        try {
            return this.repository.getConnection();
        } catch (RepositoryException ex) {
            LOGGER.error("Error creating repository connection!");
            throw (new StoreManagerException(ex.getMessage()));
        }
    }

}