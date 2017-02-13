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
package nl.dtls.fairdatapoint.api.controller;

import java.net.MalformedURLException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import nl.dtl.fairmetadata4j.io.MetadataException;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.repository.StoreManagerException;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.FairMetadataServiceException;
import nl.dtls.fairdatapoint.service.impl.FairMetaDataServiceImpl;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * MetadataController class unit tests
 *
 * @author Rajaram Kaliyaperumal
 * @since 2016-02-11
 * @version 0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiTestContext.class})
@DirtiesContext
public class MetadataControllerTest {

    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    @Autowired
    private FairMetaDataService fairMetaDataService;
    private final String TEST_FDP_PATH = "/";
    private final String TEST_CATALOG_PATH = TEST_FDP_PATH + "catalog/"
            + ExampleFilesUtils.CATALOG_ID;
    private final String TEST_DATASET_PATH = TEST_FDP_PATH + "dataset/"
            + ExampleFilesUtils.DATASET_ID;
    private final String TEST_DISTRIBUTION_PATH = TEST_FDP_PATH
            + "distribution/" + ExampleFilesUtils.DISTRIBUTION_ID;
    private final static Logger LOGGER
            = LogManager.getLogger(FairMetaDataServiceImpl.class.getName());

    @Before
    public void storeExampleMetadata() throws StoreManagerException,
            MalformedURLException, DatatypeConfigurationException,
            FairMetadataServiceException, MetadataException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        LOGGER.info("Storing example FDP metadata for service layer tests");        
        request.setRequestURI(TEST_FDP_PATH);
        String fdpUri = request.getRequestURL().toString();
        fairMetaDataService.storeFDPMetaData(ExampleFilesUtils.
                getFDPMetadata(fdpUri));
        LOGGER.info("Storing example catalog metadata for service layer tests");
        request.setRequestURI(TEST_CATALOG_PATH);
        String cUri = request.getRequestURL().toString();
        fairMetaDataService.storeCatalogMetaData(ExampleFilesUtils.
                getCatalogMetadata(cUri, fdpUri));
        LOGGER.info("Storing example dataset metadata for service layer tests");
        request.setRequestURI(TEST_DATASET_PATH);
        String dUri = request.getRequestURL().toString();
        fairMetaDataService.storeDatasetMetaData(ExampleFilesUtils.
                getDatasetMetadata(dUri, cUri));
        LOGGER.info("Storing example distribution "
                + "metadata for service layer tests");
        request.setRequestURI(TEST_DISTRIBUTION_PATH);
        String disUri = request.getRequestURL().toString();
        fairMetaDataService.storeDistributionMetaData(
                ExampleFilesUtils.getDistributionMetadata(disUri, dUri));

    }
    /**
     * Check unsupported accept header.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = HttpMediaTypeNotAcceptableException.class)
    public void unsupportedAcceptHeaderRepostory() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "application/trig");
        request.setRequestURI(TEST_FDP_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
    }

    /**
     * Check unsupported accept header.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = HttpMediaTypeNotAcceptableException.class)
    public void unsupportedAcceptHeaderCatalog() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "application/trig");
        request.setRequestURI(TEST_CATALOG_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
    }

    /**
     * Check unsupported accept header.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = HttpMediaTypeNotAcceptableException.class)
    public void unsupportedAcceptHeaderDataset() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "application/trig");
        request.setRequestURI(TEST_DATASET_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
    }

    /**
     * Check unsupported accept header.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = HttpMediaTypeNotAcceptableException.class)
    public void unsupportedAcceptHeaderDistribution() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "application/trig");
        request.setRequestURI(TEST_DISTRIBUTION_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
    }

    /**
     * The default content type is text/turtle, when the accept header is not
     * set the default content type is served. This test is excepted to pass.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void noAcceptHeader() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        request.setMethod("GET");

        request.setRequestURI(TEST_FDP_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        request.setRequestURI(TEST_CATALOG_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        request.setRequestURI(TEST_DATASET_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        request.setRequestURI(TEST_DISTRIBUTION_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    /**
     * Check supported accept headers.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void supportedAcceptHeaders() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");

        request.setRequestURI(TEST_FDP_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        request.setRequestURI(TEST_CATALOG_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        request.setRequestURI(TEST_DATASET_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        request.setRequestURI(TEST_DISTRIBUTION_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }
    
    /**
     * Check supported accept headers.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void htmlAcceptHeaders() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/html");

        request.setRequestURI(TEST_FDP_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        request.setRequestURI(TEST_CATALOG_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        request.setRequestURI(TEST_DATASET_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());

        request.setRequestURI(TEST_DISTRIBUTION_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    /**
     * Store catalog.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void storeCatalog() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        String metadata = ExampleFilesUtils.getFileContentAsString(
                ExampleFilesUtils.CATALOG_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("id", "cat1");
        request.setRequestURI("/catalog");
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

    /**
     * Store catalog twice.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeCatalogTwice() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        String metadata = ExampleFilesUtils.getFileContentAsString(
                ExampleFilesUtils.CATALOG_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("id", "cat1");
        request.setRequestURI("/catalog");
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());

        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        request.setServerName("localhost");
        request.setContextPath("fdp");
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("id", "cat1");
        request.setRequestURI("/catalog");
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
    }

    /**
     * Check non existing catalog.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = ResourceNotFoundException.class)
    public void nonExistingCatalog() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI("/catalog/dumpy");
        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
    }

    /**
     * Check existing catalog.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void existingCatalog() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_CATALOG_PATH);
        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    /**
     * Store dataset.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void storeDataset() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        String metadata = ExampleFilesUtils.getFileContentAsString(
                ExampleFilesUtils.DATASET_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("id", "dat1");
        request.setRequestURI("/dataset");
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

    /**
     * Store dataset twice.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDatasetTwice() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        String metadata = ExampleFilesUtils.getFileContentAsString(
                ExampleFilesUtils.DATASET_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("id", "dat1");
        request.setRequestURI("/dataset");
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());

        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        request.setServerName("localhost");
        request.setContextPath("fdp");
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("id", "dat1");
        request.setRequestURI("/dataset");
        handler = handlerMapping.getHandler(request).getHandler();        
        handlerAdapter.handle(request, response, handler);
    }

    /**
     * Check non existing dataset.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = ResourceNotFoundException.class)
    public void nonExistingDataset() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI("/dataset/dumpy");
        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
    }

    /**
     * Check existing Dataset.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void existingDataset() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_DATASET_PATH);
        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    /**
     * Store distribution.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void storeDistribution() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        String metadata = ExampleFilesUtils.getFileContentAsString(
                ExampleFilesUtils.DISTRIBUTION_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("id", "dis1");
        request.setRequestURI("/distribution");
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

    /**
     * Store distribution twice.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeDistributionTwice() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        Object handler;
        String metadata = ExampleFilesUtils.getFileContentAsString(
                ExampleFilesUtils.DISTRIBUTION_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("id", "dis1");
        request.setRequestURI("/distribution");
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());

        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();
        request.setServerName("localhost");
        request.setContextPath("fdp");
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.addParameter("id", "dis1");
        request.setRequestURI("/distribution");
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
    }

    /**
     * Check non existing Content.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test(expected = ResourceNotFoundException.class)
    public void nonExistingContentDistribution() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI("/distribution/dummy");
        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
    }

    /**
     * Check existing Content.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void existingContentDistribution() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_DISTRIBUTION_PATH);
        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

}
