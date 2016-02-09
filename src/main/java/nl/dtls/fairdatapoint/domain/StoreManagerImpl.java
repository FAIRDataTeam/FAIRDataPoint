/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.domain;

import java.io.IOException;
import java.io.StringReader;
import nl.dtls.fairdatapoint.utils.ExampleTurtleFiles;
import static nl.dtls.fairdatapoint.utils.ExampleTurtleFiles.BASE_URI;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openrdf.rio.RDFParseException;

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
            throws StoreManagerException {
        
        if (uri == null || uri.isEmpty()) {
            String errorMsg = "The resource URI can't be NULL or EMPTY";
            LOGGER.error(errorMsg);
            throw (new IllegalArgumentException(errorMsg));
        }        
        RepositoryConnection conn;
        RepositoryResult<Statement> statements = null;
        try {
            conn = getRepositoryConnection();   
            URI resourceSubj = new URIImpl(uri);
            LOGGER.info("Get statements for the URI <" + 
                    resourceSubj.toString() + ">");
            if (conn.hasStatement(resourceSubj, null, null,false)) {
               statements = conn.getStatements(resourceSubj, null, null, false);  
               
            } else {
                LOGGER.info("No statements for the URI <" + 
                        resourceSubj.toString() + ">");
            }
            repositoryConnection = conn;
        } catch (RepositoryException ex) {            
            LOGGER.error("Error retrieving resource <" + uri + ">");
            throw (new StoreManagerException(ex.getMessage()));
        }
        return statements;
    }
    
    /**
     * Store string RDF to the repository
     * 
     * @param content RDF as a string
     * @param contextURI context URI as a string
     * @param baseURI   base URI as a string
     * @throws StoreManagerException 
     */
    @Override
    public void storeRDF (String content, String contextURI, String baseURI) 
            throws StoreManagerException {
        RepositoryConnection conn;        
        try {
            /**
             * we are using simple string replacement to replace the base uri of 
             * RDF file. In future we should use more elegant code.  
             */
            if(baseURI != null && !baseURI.isEmpty()) {                
                content = content.replaceAll(BASE_URI, baseURI);
            } else {
                baseURI = BASE_URI;
            }
            StringReader reader = new StringReader(content);
            conn = getRepositoryConnection();
            if (contextURI == null || !contextURI.isEmpty()) {
                conn.add(reader, baseURI, 
                        ExampleTurtleFiles.FILES_RDF_FORMAT);       
            } else {
                URI context = new URIImpl(contextURI);
                conn.add(reader, baseURI, 
                        ExampleTurtleFiles.FILES_RDF_FORMAT, context); 
            }
        } catch (RepositoryException ex) {
            LOGGER.error("Error storing RDF",ex);
            LOGGER.info("Content \n" + content);
            throw (new StoreManagerException(ex.getMessage()));
        }  catch (IOException ex) {
            LOGGER.error("Error reading RDF",ex);
            throw (new StoreManagerException(ex.getMessage()));
        } catch (RDFParseException ex) {
            LOGGER.error("Error parsing RDF",ex);
            LOGGER.info("Content \n" + content);
            throw (new StoreManagerException(ex.getMessage()));
        }
        finally {
            try {
                closeRepositoryConnection();
            } catch (StoreManagerException e) {                
                LOGGER.error("Error closing connection",e); 
                throw (new StoreManagerException(e.getMessage()));
                
            }
        }
    }
    
    /**
     * Method to close repository connection
     * 
     * @throws nl.dtls.fairdatapoint.domain.StoreManagerException
     */
    @Override
    public void closeRepositoryConnection() throws StoreManagerException {
        
        try {            
            if ((repositoryConnection != null) && 
                    repositoryConnection.isOpen()) {
                repositoryConnection.close();            
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
            if (repositoryConnection == null || !repositoryConnection.isOpen()) {
                repositoryConnection = repository.getConnection();
            }
            return repositoryConnection;
        } catch (RepositoryException ex) {
            LOGGER.error("Error creating repository connection!");
            throw (new StoreManagerException(ex.getMessage()));
        }
    }    
}
