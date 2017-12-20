/**
 * The MIT License
 * Copyright © 2017 DTL
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * Contain methods to store data and retrieve data from the triple store
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2016-01-05
 * @version 0.2
 */
@Repository("storeManager")
public class StoreManagerImpl implements StoreManager {

    private final static Logger LOGGER
            = LogManager.getLogger(StoreManagerImpl.class);
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
        LOGGER.info("Get statements for the URI <" + uri.toString() + ">");
        try (RepositoryConnection conn = getRepositoryConnection()) {
            RepositoryResult<Statement> queryResult = conn.getStatements(uri,
                    null, null, false);
            List<Statement> statements = new ArrayList();
            while (queryResult.hasNext()) {
                statements.add(queryResult.next());
            }
            return statements;
        } catch (RepositoryException e) {
            throw (new StoreManagerException("Error retrieve resource :"
                    + e.getMessage()));
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
        try (RepositoryConnection conn = getRepositoryConnection()) {
            LOGGER.info("Check if statements exists");
            return conn.hasStatement(rsrc, pred, value, false);
        } catch (RepositoryException e) {
            throw (new StoreManagerException("Error check statement existence :"
                    + e.getMessage()));
        }
    }

    /**
     * Store string RDF to the repository
     *
     * @param cntx context uri
     */
    @Override
    public void storeStatements(List<Statement> statements, IRI... cntx) throws
            StoreManagerException {
        try (RepositoryConnection conn = getRepositoryConnection()) {
            if(cntx != null) {
              conn.add(statements, cntx);  
            } else {
                conn.add(statements); 
            }
            
        } catch (RepositoryException e) {
            throw (new StoreManagerException("Error storing statements :"
                    + e.getMessage()));
        }
    }

    /**
     * Remove a statement from the repository
     *
     * @param pred
     */
    @Override
    public void removeStatement(Resource rsrc, IRI pred, Value value) throws 
            StoreManagerException {
        try (RepositoryConnection conn = getRepositoryConnection()) {
            conn.remove(rsrc, pred, value);
        } catch (RepositoryException e) {
            throw (new StoreManagerException("Error removing statement"));
        }
    }

    /**
     * Repository connection to interact with the triple store
     *
     * @return RepositoryConnection
     * @throws Exception
     */
    private RepositoryConnection getRepositoryConnection()
            throws RepositoryException {
        return this.repository.getConnection();
    }

    @Override
    public void removeResource(IRI uri) throws StoreManagerException {
        removeStatement(uri, null, null);
    }

}
