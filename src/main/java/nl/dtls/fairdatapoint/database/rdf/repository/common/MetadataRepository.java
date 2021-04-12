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
package nl.dtls.fairdatapoint.database.rdf.repository.common;

import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.entity.search.SearchResult;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.query.BindingSet;

import java.util.List;
import java.util.Map;

public interface MetadataRepository {

    List<Resource> findResources() throws MetadataRepositoryException;

    List<Statement> find(IRI context) throws MetadataRepositoryException;

    List<SearchResult> findByLiteral(Literal query) throws MetadataRepositoryException;

    Map<String, String> findChildTitles(IRI parent, IRI relation) throws MetadataRepositoryException;

    boolean checkExistence(Resource subject, IRI predicate, Value object) throws MetadataRepositoryException;

    void save(List<Statement> statements, IRI context) throws MetadataRepositoryException;

    void removeAll() throws MetadataRepositoryException;

    void remove(IRI uri) throws MetadataRepositoryException;

    void removeStatement(Resource subject, IRI predicate, Value object, IRI context) throws MetadataRepositoryException;

    List<BindingSet> runSparqlQuery(String queryName, Class repositoryType, Map<String, Value> bindings) throws MetadataRepositoryException;

}
