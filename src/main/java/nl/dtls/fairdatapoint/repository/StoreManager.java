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
package nl.dtls.fairdatapoint.repository;

import java.util.List;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;

/**
 * An interface for store manager
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2016-01-05
 * @version 0.1
 */
public interface StoreManager {

    List<Statement> retrieveResource(IRI uri) throws StoreManagerException;

    /**
     * Store RDF from openRDF model to the repository
     *
     * @param statements
     * @param iri context uri
     * @throws StoreManagerException
     */
    void storeStatements(List<Statement> statements, IRI... iri) throws StoreManagerException;

    /**
     * Remove a statement from the repository
     *
     * @param rsrc
     * @param uri
     * @param value
     * @throws StoreManagerException
     */
    void removeStatement(Resource rsrc, IRI uri, Value value) throws StoreManagerException;

    /**
     * Check if a statement exist in a triple store
     *
     * @param rsrc
     * @param pred
     * @param value
     * @return
     * @throws StoreManagerException
     */
    boolean isStatementExist(Resource rsrc, IRI pred, Value value) throws StoreManagerException;

    /**
     * Remove a RDF resource from the store
     *
     * @param uri
     * @throws StoreManagerException
     */
    void removeResource(IRI uri) throws StoreManagerException;

    /**
     * Get fdp URI for given URI
     *
     * @param uri any metadata URI
     * @return IRI object
     * @throws StoreManagerException
     */
    IRI getFDPIri(IRI uri) throws StoreManagerException;
}
