/**
 * The MIT License
 * Copyright © 2016 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.repository.impl;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import nl.dtls.fairdatapoint.repository.StoreManager;
import nl.dtls.fairdatapoint.repository.StoreManagerException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
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

    private static final Logger LOGGER
            = LoggerFactory.getLogger(StoreManagerImpl.class);
    @Autowired
    @Qualifier("repository")
    private org.eclipse.rdf4j.repository.Repository repository;

    /**
     * Retrieve all statements for an given URI
     *
     * @param uri Valid RDF URI as a string
     * @return List of RDF statements
     * @throws StoreManagerException
     */
    @Override
    public List<Statement> retrieveResource(@Nonnull IRI uri)
            throws StoreManagerException {
        Preconditions.checkNotNull(uri, "URI must not be null.");
        LOGGER.info("Get statements for the URI <"+ uri.toString() + ">"); 
        RepositoryConnection conn = null;        
        try {            
            conn = getRepositoryConnection();
            RepositoryResult<Statement> queryResult = conn.getStatements(uri, 
                    null, null, false);                
            List<Statement> statements = new ArrayList();
            while (queryResult.hasNext()) {                    
                statements.add(queryResult.next());                
            }            
            return statements;
        } catch (RepositoryException ex) {
            LOGGER.error("Error retrieving resource <" + uri.toString() + ">");
            throw (new StoreManagerException(ex.getMessage()));
        } finally {
            try {
                closeRepositoryConnection(conn);
            } catch (StoreManagerException e) {
                LOGGER.error("Error closing connection", e);
                throw (new StoreManagerException(e.getMessage()));
            }
        }
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
    public boolean isStatementExist(Resource rsrc, IRI pred, Value value)
            throws StoreManagerException {
        RepositoryConnection conn = null;
        try {
            conn = getRepositoryConnection();
            LOGGER.info("Check if statements exists");
            return conn.hasStatement(rsrc, pred, value, false);
        } catch (RepositoryException ex) {
            LOGGER.error("Error checking statement's existence");
            throw (new StoreManagerException(ex.getMessage()));
        } finally {
            try {
                closeRepositoryConnection(conn);
            } catch (StoreManagerException e) {
                LOGGER.error("Error closing connection", e);
                throw (new StoreManagerException(e.getMessage()));
            }
        }
    }

    /**
     * Store string RDF to the repository
     *
     * @throws StoreManagerException
     */
    @Override
    public void storeStatements(List<Statement> statements) throws
            StoreManagerException {
        RepositoryConnection conn = null;
        try {
            conn = getRepositoryConnection();
            conn.add(statements);
        } catch (RepositoryException ex) {
            LOGGER.error("Error storing RDF", ex);
            throw (new StoreManagerException(ex.getMessage()));
        } finally {
            try {
                closeRepositoryConnection(conn);
            } catch (StoreManagerException e) {
                LOGGER.error("Error closing connection", e);
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
    public void removeStatement(Resource rsrc, IRI pred, Value value) throws
            StoreManagerException {
        RepositoryConnection conn = null;
        try {
            conn = getRepositoryConnection();
            conn.remove(rsrc, pred, value);
            //conn.remove(statement);
        } catch (RepositoryException ex) {
            LOGGER.error("Error storing RDF", ex);
            throw (new StoreManagerException(ex.getMessage()));
        } finally {
            try {
                closeRepositoryConnection(conn);
            } catch (StoreManagerException e) {
                LOGGER.error("Error closing connection", e);
                throw (new StoreManagerException(e.getMessage()));

            }
        }
    }

    /**
     * Method to close repository connection
     *
     * @throws nl.dtls.fairdatapoint.repository.StoreManagerException
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

    @Override
    public void removeResource(IRI uri) throws StoreManagerException {
        removeStatement(uri, null, null);
    }

}
