/**
 * The MIT License
 * Copyright © 2016 DTL
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

import java.util.ArrayList;
import java.util.List;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.repository.StoreManager;
import nl.dtls.fairdatapoint.repository.StoreManagerException;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * StoreManagerImpl class unit tests
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-01-05
 * @version 0.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiTestContext.class})
@DirtiesContext
public class StoreManagerImplTest {

    @Autowired
    private StoreManager testStoreManager;

    @Before
    public void storeExampleFile() throws StoreManagerException {
        List<Statement> sts = ExampleFilesUtils.
                getFileContentAsStatements(ExampleFilesUtils.VALID_TEST_FILE, 
                        "http://www.dtls.nl/test");
        testStoreManager.storeStatements(sts);
    }

    /**
     * The URI of a RDF resource can't be NULL, this test is excepted to throw
     * IllegalArgumentException
     */
    @DirtiesContext
    @Test(expected = NullPointerException.class)
    public void nullURI() {

        try {
            testStoreManager.retrieveResource(null);
            fail("No RDF statements excepted for NULL URI");
        } catch (StoreManagerException ex) {
            fail("This test is not excepted to throw StoreManagerException");
        }
    }

    /**
     * The URI of a RDF resource can't be EMPTY, this test is excepted to throw
     * IllegalArgumentException
     */
    @DirtiesContext
    @Test(expected = IllegalArgumentException.class)
    public void emptyURI() {
        String uri = "";
        try {
            ValueFactory f = SimpleValueFactory.getInstance();
            testStoreManager.retrieveResource(f.createIRI(uri));
            fail("No RDF statements excepted for NULL URI");
        } catch (StoreManagerException ex) {
            fail("The test is not excepted to throw RepositoryException or "
                    + "StoreManagerException");
        }
    }

    /**
     * The test is excepted to retrieve ZERO statements
     *
     * @throws RepositoryException
     * @throws StoreManagerException
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void retrieveNonExitingResource() throws RepositoryException,
            StoreManagerException,
            Exception {
        String uri = "http://localhost/dummy";
        ValueFactory f = SimpleValueFactory.getInstance();
        List<Statement> statements
                = testStoreManager.retrieveResource(f.createIRI(uri));
        assertTrue(statements.isEmpty());
    }

    /**
     * The test is excepted retrieve to retrieve one or more statements
     *
     * @throws RepositoryException
     * @throws StoreManagerException
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void retrieveExitingResource() throws RepositoryException,
            StoreManagerException, Exception {
        ValueFactory f = SimpleValueFactory.getInstance();
        List<Statement> statements
                = testStoreManager.retrieveResource(f.createIRI(
                        ExampleFilesUtils.TEST_SUB_URI));
        assertTrue(statements.size() > 0);
    }

    /**
     * The test is excepted to pass
     */
    @DirtiesContext
    @Test
    public void storeResource() {
        List<Statement> statements = ExampleFilesUtils.
                getFileContentAsStatements(ExampleFilesUtils.VALID_TEST_FILE, 
                        "http://www.dtls.nl/test");
        try {
            testStoreManager.storeStatements(statements);
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
            ValueFactory f = SimpleValueFactory.getInstance();
            Resource sub = f.createBNode("<http://www.dtls.nl/testSub>");
            IRI obj = f.createIRI("<http://www.dtls.nl/testObj>");
            Statement stmt = f.createStatement(sub, RDF.TYPE, obj);
            List<Statement> sts = new ArrayList();
            sts.add(stmt);
            testStoreManager.storeStatements(sts);
            testStoreManager.removeStatement(sub, RDF.TYPE, null);
        } catch (StoreManagerException ex) {
            fail("The test is not excepted to throw StoreManagerException");
        }
    }

    /**
     * The test is excepted to retrieve return false
     *
     * @throws RepositoryException
     * @throws StoreManagerException
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void checkNonExitingResource() throws RepositoryException,
            StoreManagerException,
            Exception {
        String uri = "http://localhost/dummy";
        ValueFactory f = SimpleValueFactory.getInstance();
        boolean isStatementExist = testStoreManager.isStatementExist
        (f.createIRI(uri), null, null);
        assertFalse(isStatementExist);
    }

    /**
     * The test is excepted to retrieve return true
     *
     * @throws RepositoryException
     * @throws StoreManagerException
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void checkExitingResource() throws RepositoryException,
            StoreManagerException,  Exception {
        ValueFactory f = SimpleValueFactory.getInstance();
        boolean isStatementExist = testStoreManager.isStatementExist
        (f.createIRI(ExampleFilesUtils.TEST_SUB_URI), null, null);
        assertTrue(isStatementExist);
    }

}
