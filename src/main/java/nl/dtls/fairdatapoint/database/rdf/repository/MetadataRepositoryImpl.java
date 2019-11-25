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
package nl.dtls.fairdatapoint.database.rdf.repository;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.util.List;

@Service
public class MetadataRepositoryImpl implements MetadataRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataRepositoryImpl.class);

    private static final ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();

    private static final String GETFDPURIQUERY = "getFdpIri.sparql";

    @Autowired
    private Repository repository;

    public List<Statement> retrieveResource(@Nonnull IRI uri) throws MetadataRepositoryException {
        Preconditions.checkNotNull(uri, "URI must not be null.");
        LOGGER.info("Get statements for the URI {}", uri.toString());

        try (RepositoryConnection conn = repository.getConnection()) {
            RepositoryResult<Statement> queryResult = conn.getStatements(null, null, null, uri);
            return Iterations.asList(queryResult);
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Error retrieve resource :" + e.getMessage());
        }
    }

    public boolean isStatementExist(Resource rsrc, IRI pred, Value value) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            LOGGER.info("Check if statements exists");
            return conn.hasStatement(rsrc, pred, value, false);
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Error check statement existence :" + e.getMessage());
        }
    }

    public void storeStatements(List<Statement> statements, IRI... cntx) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            if (cntx != null) {
                conn.add(statements, cntx);
            } else {
                conn.add(statements);
            }

        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Error storing statements :" + e.getMessage());
        }
    }

    public void removeStatement(Resource rsrc, IRI pred, Value value) throws MetadataRepositoryException {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.remove(rsrc, pred, value);
        } catch (RepositoryException e) {
            throw (new MetadataRepositoryException("Error removing statement"));
        }
    }

    public void removeResource(IRI uri) throws MetadataRepositoryException {
        removeStatement(uri, null, null);
    }

    public IRI getFDPIri(IRI uri) throws MetadataRepositoryException {
        Preconditions.checkNotNull(uri, "URI must not be null.");
        LOGGER.info("Get fdp uri for the given uri {}", uri.toString());

        try (RepositoryConnection conn = repository.getConnection()) {

            URL fileURL = MetadataRepositoryImpl.class.getResource(GETFDPURIQUERY);
            String queryString = Resources.toString(fileURL, Charsets.UTF_8);
            TupleQuery query = conn.prepareTupleQuery(queryString);
            query.setBinding("iri", uri);

            IRI fdpIri = null;
            List<BindingSet> resultSet = QueryResults.asList(query.evaluate());
            for (BindingSet solution : resultSet) {
                fdpIri = VALUEFACTORY.createIRI(solution.getValue("fdp").stringValue());
            }
            return fdpIri;
        } catch (RepositoryException e) {
            throw new MetadataRepositoryException("Error retrieve fdp uri :" + e.getMessage());
        } catch (IOException e) {
            throw new MetadataRepositoryException("Error reading getFdpIri.sparql file :" + e.getMessage());
        }
    }

}
