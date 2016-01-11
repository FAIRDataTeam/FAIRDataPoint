/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.domain;

import java.io.File;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import nl.dtls.fairdatapoint.utils.ExampleTurtle;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-05
 * @version 0.1
 */
public class StoreManagerImpl implements StoreManager {
    
    private static final Logger LOGGER = 
            LoggerFactory.getLogger(StoreManagerImpl.class);
    private Repository repository;
    private RepositoryConnection repositoryConnection; 
    
    public StoreManagerImpl(Repository repository) throws 
            RepositoryException {
        this.repository = repository;
        this.repository.initialize();
        if (this.repository.getClass() == SailRepository.class) {
            
            try {
                uploadRDF(ExampleTurtle.getTurtleAsFile(
                        ExampleTurtle.FDP_METADATA), 
                        ExampleTurtle.FILES_RDF_FORMAT);
                uploadRDF(ExampleTurtle.getTurtleAsFile(
                        ExampleTurtle.CATALOG_METADATA), 
                        ExampleTurtle.FILES_RDF_FORMAT);
                uploadRDF(ExampleTurtle.getTurtleAsFile(
                        ExampleTurtle.DATASET_METADATA), 
                        ExampleTurtle.FILES_RDF_FORMAT);
            } catch (StoreManagerException ex) {
                LOGGER.debug("Error loading example turtle files");
            }
            
        }
    }

    @Override
    public RepositoryResult<Statement> retrieveResource(String uri) 
            throws StoreManagerException{
        RepositoryConnection conn = null;
        RepositoryResult<Statement> statements = null;
        try {
            conn = getRepositoryConnection();   
            Resource resourceSubj = (new ValueFactoryImpl()).createURI(uri);
            LOGGER.info("Get statements for the URI " + 
                    resourceSubj.toString());
            if (conn.hasStatement(resourceSubj, null, null,false)) {
               statements = conn.getStatements(resourceSubj, null, null, false);  
               
            }                       
        }
        catch (Exception e) {
            LOGGER.error("Error retrieving resource <" + uri + ">");
            throw (new StoreManagerException(e.getMessage()));
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (Exception e) {
                LOGGER.error("Error closing repository connection!");
                throw (new StoreManagerException(e.getMessage()));
            }
        }
        return statements;
    }

    @Override
    public void close() throws Exception {
        
        try {            
            if ((repositoryConnection != null) && 
                    repositoryConnection.isOpen()) {
                repositoryConnection.close();            
            }        
        }
        catch (RepositoryException e) {
            LOGGER.error("Error closing repository connection!");
            throw (new StoreManagerException(e.getMessage()));
        }
        finally {
            repositoryConnection = null;

            try {
                if (repository != null) {
                    repository.shutDown();
                }
            }
            catch (Exception e) {
                LOGGER.error("Error closing repository!");
                throw (new StoreManagerException(e.getMessage()));
            }
            finally {
                repository = null;
            }
        }
    }
    
    /**
     * Repository connection to do SPARQL queries
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
    
    private void uploadRDF (File rdfFile, RDFFormat format) 
            throws StoreManagerException{
        RepositoryConnection conn = null;
        try {
            conn = getRepositoryConnection();   
            conn.add(rdfFile, "", format);
            LOGGER.info(rdfFile.getName() + " file uploaded to the store");                      
        }
        catch (Exception e) {
            LOGGER.error("Error uploading <" + rdfFile.getName() + 
                    "> file to the store");
            throw (new StoreManagerException(e.getMessage()));
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (Exception e) {
                LOGGER.error("Error closing repository connection!");
                throw (new StoreManagerException(e.getMessage()));
            }
        }
    }
    
}
