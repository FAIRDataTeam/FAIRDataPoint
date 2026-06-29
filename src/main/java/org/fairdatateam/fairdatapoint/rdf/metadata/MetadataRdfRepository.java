/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.rdf.metadata;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.query.BindingSet;

import java.util.List;
import java.util.Map;

public interface MetadataRdfRepository {

    List<Statement> find(IRI context) throws MetadataRdfRepositoryException;

    Map<String, String> findChildTitles(IRI parent, IRI relation)
            throws MetadataRdfRepositoryException;

    boolean checkExistence(Resource subject, IRI predicate, Value object)
            throws MetadataRdfRepositoryException;

    void save(List<Statement> statements, IRI context) throws MetadataRdfRepositoryException;

    void removeAll() throws MetadataRdfRepositoryException;

    void remove(IRI uri) throws MetadataRdfRepositoryException;

    void removeStatement(Resource subject, IRI predicate, Value object, IRI context)
            throws MetadataRdfRepositoryException;

    List<BindingSet> runSparqlQuery(String queryString, Map<String, Value> bindings) throws MetadataRdfRepositoryException;
}
