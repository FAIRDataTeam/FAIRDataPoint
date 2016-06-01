/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.domain;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import nl.dtls.fairdatapoint.utils.ExampleTurtleFiles;
import static nl.dtls.fairdatapoint.utils.ExampleTurtleFiles.EXAMPLE_FILES_BASE_URI;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
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
public class StoreManagerImpl implements StoreManager, InitializingBean {
    
    private static final Logger LOGGER = 
            LoggerFactory.getLogger(StoreManagerImpl.class);
    @Autowired
    @Qualifier("repository")
    private org.openrdf.repository.Repository repository;  
    @Autowired
    @Qualifier("baseURI")
    private String rdfBaseURI;
    @Autowired 
    @Qualifier("prepopulateStore")               
    private boolean prepopulateStore;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        
        if (prepopulateStore) {
            // Load example ttl files from utils package to the inmemory store
            for (String fileName:ExampleTurtleFiles.
                    getExampleTurtleFileNames()) {                
                storeRDF(ExampleTurtleFiles.getTurtleAsString(fileName), 
                        null, rdfBaseURI); 
            } 
        } else { 
            LOGGER.info("FDP api is not prepopulated");
        }
    }
    
    
    
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
        RepositoryConnection conn = null;        
        try {
            /**
             * we are using simple string replacement to replace the base uri of 
             * RDF file. In future we should use more elegant code.  
             */
            if(baseURI != null && !baseURI.isEmpty()) {                
                content = content.replaceAll(EXAMPLE_FILES_BASE_URI, baseURI);
            } else {
                baseURI = EXAMPLE_FILES_BASE_URI;
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
     * @throws nl.dtls.fairdatapoint.domain.StoreManagerException
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
