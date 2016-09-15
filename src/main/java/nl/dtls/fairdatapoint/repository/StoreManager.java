/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.repository;

import java.util.List;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-05
 * @version 0.1.1
 */
public interface StoreManager {
    
    List<Statement> retrieveResource(URI uri) 
            throws StoreManagerException;
    /**
     * Store RDF from openRDF model to the repository
     * 
     * @param statements
     * @throws StoreManagerException 
     */
    void storeStatements (List<Statement> statements) throws StoreManagerException;
    /**
     * Remove a statement from the repository
     * 
     * @param rsrc
     * @param uri
     * @param value
     * @throws StoreManagerException 
     */
    void removeStatement (Resource rsrc, URI uri, Value value) 
            throws StoreManagerException;
    /**
     * Check if a statement exist in a triple store
     * 
     * @param rsrc
     * @param pred
     * @param value
     * @return
     * @throws StoreManagerException 
     */
    boolean isStatementExist(Resource rsrc, URI pred, Value value) 
            throws StoreManagerException;
}
