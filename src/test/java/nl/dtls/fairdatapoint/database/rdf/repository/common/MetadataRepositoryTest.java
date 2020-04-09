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

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.database.rdf.repository.exception.MetadataRepositoryException;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepository;
import nl.dtls.fairdatapoint.database.rdf.repository.generic.GenericMetadataRepositoryImpl;
import nl.dtls.fairdatapoint.utils.TestMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

// TODO Get rid of DirtiesContext
@DirtiesContext
public class MetadataRepositoryTest extends WebIntegrationTest {

    @Value("${instance.url}")
    private String instanceUrl;

    private final ValueFactory f = SimpleValueFactory.getInstance();

    private List<Statement> STATEMENTS;

    private final IRI TESTSUB = f.createIRI("http://www.dtls.nl/testSub");

    private final IRI TESTOBJ = f.createIRI("http://www.dtls.nl/testObj");

    private final Statement TESTSTMT = f.createStatement(TESTSUB, RDF.TYPE, TESTOBJ);

    @Autowired
    private GenericMetadataRepository metadataRepository;

    @Mock
    private Repository repository;

    @InjectMocks
    private GenericMetadataRepositoryImpl mockMetadataRepository;

    @Autowired
    private TestMetadataFixtures testMetadataFixtures;

    @BeforeEach
    public void storeExampleFile() throws MetadataRepositoryException {
        STATEMENTS = new ArrayList<>(testMetadataFixtures.repositoryMetadata());

        metadataRepository.storeStatements(STATEMENTS, f.createIRI(instanceUrl));
        MockitoAnnotations.initMocks(this);
    }

    /**
     * The URI of a RDF resource can't be NULL, this test is excepted to throw
     * IllegalArgumentException
     */
    @DirtiesContext
    @Test
    public void nullURI() throws MetadataRepositoryException {
        assertThrows(NullPointerException.class, () -> {
            metadataRepository.retrieveResource(null);
        });
    }

    /**
     * The URI of a RDF resource can't be EMPTY, this test is excepted to throw
     * IllegalArgumentException
     */
    @DirtiesContext
    @Test
    public void emptyURI() throws MetadataRepositoryException {
        assertThrows(IllegalArgumentException.class, () -> {

            String uri = "";
            metadataRepository.retrieveResource(f.createIRI(uri));
        });
    }

    /**
     * This test is excepted to throw execption
     */
    @DirtiesContext
    @Test
    public void emptyInvalidURI() throws MetadataRepositoryException {
        assertThrows(IllegalArgumentException.class, () -> {
            String uri = "...";
            metadataRepository.retrieveResource(f.createIRI(uri));
        });
    }

    /**
     * The test is excepted to retrieve ZERO statements
     */
    @DirtiesContext
    @Test
    public void retrieveNonExitingResource() throws Exception {

        String uri = "http://localhost/dummy";
        List<Statement> statements = metadataRepository.retrieveResource(f.createIRI(uri));
        assertTrue(statements.isEmpty());
    }

    /**
     * The test is excepted retrieve to retrieve one or more statements
     */
    @DirtiesContext
    @Test
    public void retrieveExitingResource() throws Exception {

        List<Statement> statements = metadataRepository.retrieveResource(
                f.createIRI(instanceUrl));
        assertTrue(statements.size() > 0);
    }

    /**
     * The test is excepted to throw error
     */
    @DirtiesContext
    @Test
    public void retrieveResourceCatchBlock() throws Exception {
        assertThrows(MetadataRepositoryException.class, () -> {

            when(repository.getConnection()).thenThrow(RepositoryException.class);
            mockMetadataRepository.retrieveResource(f.createIRI(instanceUrl));
        });
    }

    /**
     * The test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeResource() {
        try {
            metadataRepository.storeStatements(STATEMENTS);
        } catch (MetadataRepositoryException ex) {
            fail("The test is not excepted to throw MetadataRepositoryException");
        }
    }

    /**
     * The test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void deleteRource() {
        try {
            List<Statement> sts = new ArrayList<>();
            sts.add(TESTSTMT);
            metadataRepository.storeStatements(sts);
            metadataRepository.removeStatement(TESTSUB, RDF.TYPE, null);
        } catch (MetadataRepositoryException ex) {
            fail("The test is not excepted to throw MetadataRepositoryException");
        }
    }

    /**
     * The test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeStatement() {
        try {
            metadataRepository.storeStatements(STATEMENTS, TESTSUB);
        } catch (MetadataRepositoryException ex) {
            fail("The test is not excepted to throw MetadataRepositoryException");
        }
    }

    /**
     * The test is excepted to retrieve return false
     */
    @DirtiesContext
    @Test
    public void checkNonExitingResource() throws Exception {
        String uri = "http://localhost/dummy";
        boolean isStatementExist = metadataRepository.isStatementExist(f.createIRI(uri), null, null);
        assertFalse(isStatementExist);
    }

    /**
     * The test is excepted to retrieve return true
     */
    @DirtiesContext
    @Test
    public void checkExitingResource() throws Exception {
        boolean isStatementExist = metadataRepository.isStatementExist(
                f.createIRI(instanceUrl), null, null);
        assertTrue(isStatementExist);
    }

    /**
     * Check exception handling of delete resource method
     */
    @DirtiesContext
    @Test
    public void checkExceptionsDeleteResourceMethod() throws Exception {
        assertThrows(MetadataRepositoryException.class, () -> {
            when(repository.getConnection()).thenThrow(RepositoryException.class);
            mockMetadataRepository.removeResource(null);
        });
    }

    /**
     * Check exception handling of remove statement method
     */
    @DirtiesContext
    @Test
    public void checkExceptionsRemoveStatementMethod() throws Exception {
        assertThrows(MetadataRepositoryException.class, () -> {
            when(repository.getConnection()).thenThrow(RepositoryException.class);
            mockMetadataRepository.removeStatement(null, null, null);
        });
    }

    /**
     * Check exception handling of isStatementExist method
     */
    @DirtiesContext
    @Test
    public void checkExceptionsIsStatementMethod() throws Exception {
        assertThrows(MetadataRepositoryException.class, () -> {
            when(repository.getConnection()).thenThrow(RepositoryException.class);
            mockMetadataRepository.isStatementExist(f.createIRI(instanceUrl),
                    null, null);
        });
    }

    /**
     * Check exception handling of storeStatement method
     */
    @DirtiesContext
    @Test
    public void checkExceptionsStoreStatementMethod() throws Exception {
        assertThrows(MetadataRepositoryException.class, () -> {
            when(repository.getConnection()).thenThrow(RepositoryException.class);
            mockMetadataRepository.storeStatements(STATEMENTS);
        });
    }

}
