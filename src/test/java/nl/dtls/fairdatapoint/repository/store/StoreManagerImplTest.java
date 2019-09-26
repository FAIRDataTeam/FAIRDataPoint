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
package nl.dtls.fairdatapoint.repository.store;

import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * StoreManagerImpl class unit tests
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @version 0.2
 * @since 2016-01-05
 */
@DirtiesContext
public class StoreManagerImplTest extends BaseIntegrationTest {

    private final ValueFactory f = SimpleValueFactory.getInstance();
    private final List<Statement> STATEMENTS = ExampleFilesUtils.
            getFileContentAsStatements(ExampleFilesUtils.VALID_TEST_FILE,
                    "http://www.dtls.nl/test");
    private final IRI TESTSUB = f.createIRI("http://www.dtls.nl/testSub");
    private final IRI TESTOBJ = f.createIRI("http://www.dtls.nl/testObj");
    private final Statement TESTSTMT = f.createStatement(TESTSUB, RDF.TYPE, TESTOBJ);
    @Autowired
    private StoreManager testStoreManager;
    @Mock
    private Repository repository;
    @InjectMocks
    private StoreManagerImpl mockStoreManager;

    @Before
    public void storeExampleFile() throws StoreManagerException {

        testStoreManager.storeStatements(STATEMENTS, f.createIRI(ExampleFilesUtils.TEST_SUB_URI));
        MockitoAnnotations.initMocks(this);
    }

    /**
     * The URI of a RDF resource can't be NULL, this test is excepted to throw
     * IllegalArgumentException
     *
     * @throws StoreManagerException
     */
    @DirtiesContext
    @Test(expected = NullPointerException.class)
    public void nullURI() throws StoreManagerException {
        testStoreManager.retrieveResource(null);
    }

    /**
     * The URI of a RDF resource can't be EMPTY, this test is excepted to throw
     * IllegalArgumentException
     *
     * @throws StoreManagerException
     */
    @DirtiesContext
    @Test(expected = IllegalArgumentException.class)
    public void emptyURI() throws StoreManagerException {

        String uri = "";
        testStoreManager.retrieveResource(f.createIRI(uri));
    }

    /**
     * This test is excepted to throw execption
     *
     * @throws StoreManagerException
     */
    @DirtiesContext
    @Test(expected = IllegalArgumentException.class)
    public void emptyInvalidURI() throws StoreManagerException {

        String uri = "...";
        testStoreManager.retrieveResource(f.createIRI(uri));
    }

    /**
     * The test is excepted to retrieve ZERO statements
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void retrieveNonExitingResource() throws Exception {

        String uri = "http://localhost/dummy";
        List<Statement> statements = testStoreManager.retrieveResource(f.createIRI(uri));
        assertTrue(statements.isEmpty());
    }

    /**
     * The test is excepted retrieve to retrieve one or more statements
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void retrieveExitingResource() throws Exception {

        List<Statement> statements = testStoreManager.retrieveResource(
                f.createIRI(ExampleFilesUtils.TEST_SUB_URI));
        assertTrue(statements.size() > 0);
    }

    /**
     * The test is excepted to throw error
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = StoreManagerException.class)
    public void retrieveResourceCatchBlock() throws Exception {

        when(repository.getConnection()).thenThrow(RepositoryException.class);
        mockStoreManager.retrieveResource(f.createIRI(ExampleFilesUtils.TEST_SUB_URI));
    }

    /**
     * The test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeResource() {

        try {
            testStoreManager.storeStatements(STATEMENTS);
        } catch (StoreManagerException ex) {
            fail("The test is not excepted to throw StoreManagerException");
        }
    }

    /**
     * The test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void deleteRource() {

        try {
            List<Statement> sts = new ArrayList();
            sts.add(TESTSTMT);
            testStoreManager.storeStatements(sts);
            testStoreManager.removeStatement(TESTSUB, RDF.TYPE, null);
        } catch (StoreManagerException ex) {
            fail("The test is not excepted to throw StoreManagerException");
        }
    }

    /**
     * The test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeStatement() {

        try {
            testStoreManager.storeStatements(STATEMENTS, TESTSUB);
        } catch (StoreManagerException ex) {
            fail("The test is not excepted to throw StoreManagerException");
        }
    }

    /**
     * The test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeStatementWithoutCtxt() {

        try {
            testStoreManager.storeStatements(STATEMENTS, null);
        } catch (StoreManagerException ex) {
            fail("The test is not excepted to throw StoreManagerException");
        }
    }

    /**
     * The test is excepted to retrieve return false
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void checkNonExitingResource() throws Exception {

        String uri = "http://localhost/dummy";
        boolean isStatementExist = testStoreManager.isStatementExist(f.createIRI(uri), null, null);
        assertFalse(isStatementExist);
    }

    /**
     * The test is excepted to retrieve return true
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void checkExitingResource() throws Exception {

        boolean isStatementExist = testStoreManager.isStatementExist(
                f.createIRI(ExampleFilesUtils.TEST_SUB_URI), null, null);
        assertTrue(isStatementExist);
    }

    /**
     * Check exception handling of delete resource method
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = StoreManagerException.class)
    public void checkExceptionsDeleteResourceMethod() throws Exception {

        when(repository.getConnection()).thenThrow(RepositoryException.class);
        mockStoreManager.removeResource(null);
    }

    /**
     * Check exception handling of remove statement method
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = StoreManagerException.class)
    public void checkExceptionsRemoveStatementMethod() throws Exception {

        when(repository.getConnection()).thenThrow(RepositoryException.class);
        mockStoreManager.removeStatement(null, null, null);
    }

    /**
     * Check exception handling of isStatementExist method
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = StoreManagerException.class)
    public void checkExceptionsIsStatementMethod() throws Exception {

        when(repository.getConnection()).thenThrow(RepositoryException.class);
        mockStoreManager.isStatementExist(f.createIRI(ExampleFilesUtils.TEST_SUB_URI), null, null);
    }

    /**
     * Check exception handling of storeStatement method
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = StoreManagerException.class)
    public void checkExceptionsStoreStatementMethod() throws Exception {

        when(repository.getConnection()).thenThrow(RepositoryException.class);
        mockStoreManager.storeStatements(STATEMENTS);
    }

    /**
     * Test non exist fdp uri
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = NullPointerException.class)
    public void getFdpUriForNullUri() throws Exception {
        testStoreManager.getFDPIri(null);
    }

    /**
     * Test non exist fdp uri
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void getNonExistFdpUri() throws Exception {
        assertNull(testStoreManager.getFDPIri(f.createIRI(ExampleFilesUtils.FDP_URI + "/dummy")));
    }

    /**
     * Test existing fdp uri
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void getExistingFdpUri() throws Exception {
        List<Statement> stmt = ExampleFilesUtils.getFileContentAsStatements(
                ExampleFilesUtils.FDP_URI_FILE, ExampleFilesUtils.FDP_URI);
        IRI fdpUri = f.createIRI(ExampleFilesUtils.FDP_URI);
        testStoreManager.storeStatements(stmt, fdpUri);

        assertEquals(fdpUri, testStoreManager.getFDPIri(f.createIRI(ExampleFilesUtils.FDP_URI)));
        assertEquals(fdpUri, testStoreManager.getFDPIri(
                f.createIRI(ExampleFilesUtils.CATALOG_URI)));
        assertEquals(fdpUri, testStoreManager.getFDPIri(
                f.createIRI(ExampleFilesUtils.DATASET_URI)));
        assertEquals(fdpUri, testStoreManager.getFDPIri(
                f.createIRI(ExampleFilesUtils.DISTRIBUTION_URI)));
    }
}
