/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.repository.impl;

import java.util.ArrayList;
import java.util.List;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.api.repository.StoreManager;
import nl.dtls.fairdatapoint.api.repository.StoreManagerException;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
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
    StoreManager testStoreManager;

    @Before
    public void storeExampleFile() throws StoreManagerException {
        List<Statement> sts = ExampleFilesUtils.
                getFileContentAsStatements(ExampleFilesUtils.VALID_TEST_FILE, 
                        "http://www.dtls.nl/test");
        testStoreManager.storeRDF(sts);
    }

    /**
     * The URI of a RDF resource can't be NULL, this test is excepted to throw
     * IllegalArgumentException
     */
    @DirtiesContext
    @Test(expected = IllegalArgumentException.class)
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
            testStoreManager.retrieveResource(uri);
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
        List<Statement> statements
                = testStoreManager.retrieveResource(uri);
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
            StoreManagerException,
            Exception {
        List<Statement> statements
                = testStoreManager.retrieveResource(ExampleFilesUtils.TEST_SUB_URI);
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
            testStoreManager.storeRDF(statements);
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
            Resource sub = new URIImpl("<http://www.dtls.nl/testSub>");
            URI obj = new URIImpl("<http://www.dtls.nl/testObj>");
            Statement stmt = new StatementImpl(sub, RDF.TYPE, obj);
            List<Statement> sts = new ArrayList();
            sts.add(stmt);
            testStoreManager.storeRDF(sts);
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
        boolean isStatementExist
                = testStoreManager.isStatementExist(new URIImpl(uri), null, null);
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
            StoreManagerException,
            Exception {
        boolean isStatementExist
                = testStoreManager.isStatementExist(new URIImpl(
                                ExampleFilesUtils.TEST_SUB_URI), null, null);
        assertTrue(isStatementExist);
    }

}
