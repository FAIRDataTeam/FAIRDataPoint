/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.domain;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.RepositoryResult;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-05
 * @version 0.1
 */
public interface StoreManager {
    
    public RepositoryResult<Statement> retrieveResource(String uri) 
            throws StoreManagerException;
    /**
     * Store string RDF to the repository
     * 
     * @param content RDF as a string
     * @param contextURI context URI as a string
     * @param baseURI   base URI as a string
     * @throws StoreManagerException 
     */
    public void storeRDF (String content, String contextURI, String baseURI) 
            throws StoreManagerException; 
    /**
	 
         * Closes the StoreManager and the underlying Repository.
	 * 
	 * @throws Exception	 
    */	
    public void closeRepositoryConnection() throws Exception;
}
