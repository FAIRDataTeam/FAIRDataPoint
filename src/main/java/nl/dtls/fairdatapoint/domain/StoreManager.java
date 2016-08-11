/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.domain;

import java.util.List;
import org.openrdf.model.Statement;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-05
 * @version 0.1.1
 */
public interface StoreManager {
    
    List<Statement> retrieveResource(String uri) 
            throws StoreManagerException;
    /**
     * Store RDF from openRDF model to the repository
     * 
     * @param model OpenRDF model
     * @throws StoreManagerException 
     */
    void storeRDF (org.openrdf.model.Model model) throws StoreManagerException;
    /**
     * Remove a statement from the repository
     * 
     * @param statement
     * @throws StoreManagerException 
     */
    void removeStatement (Statement statement) throws StoreManagerException;
}
