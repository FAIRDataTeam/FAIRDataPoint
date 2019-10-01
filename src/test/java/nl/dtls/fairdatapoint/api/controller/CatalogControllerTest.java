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
package nl.dtls.fairdatapoint.api.controller;

import nl.dtls.fairdatapoint.repository.fixtures.MetadataFixtures;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.apache.http.HttpHeaders;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CatalogControllerTest extends MetadataControllerTest {

    @Autowired
    MetadataFixtures metadataFixtures;

    /**
     * Check file extension for DataRecord layer
     *
     * @throws Exception
     */
    //TODO check in depth **
    @DirtiesContext
    @Test
    public void getContentWithFileExtDataRecord() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("GET");
        request.setRequestURI(TEST_CATALOG_PATH + ".rdf");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("application/rdf+xml", response.getContentType());
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

        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "application/trig");
        request.setRequestURI(TEST_CATALOG_PATH);

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
    }

    /**
     * Check file extension for catalog layer
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void getContentWithFileExtCatlog() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("GET");
        request.setRequestURI(TEST_CATALOG_PATH + ".rdf");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("application/rdf+xml", response.getContentType());
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

        String metadata = ExampleFilesUtils.getFileContentAsString(
                ExampleFilesUtils.CATALOG_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/fdp/catalog");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

    /**
     * Store catalog with parent URI.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void storeCatalogWithParentURI() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        String parentURI = "http://localhost:8084/dummy/fdp";

        StringBuilder tripleStr = new StringBuilder();
        tripleStr.append("<> <");
        tripleStr.append(DCTERMS.IS_PART_OF.toString());
        tripleStr.append("> <");
        tripleStr.append(parentURI);
        tripleStr.append("> .");

        String metadata = ExampleFilesUtils.getFileContentAsString(
                ExampleFilesUtils.CATALOG_METADATA_FILE) + tripleStr.toString();
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/fdp/catalog");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        String catlogUrl = response.getHeader(HttpHeaders.LOCATION);
        String catId = catlogUrl.split("/catalog/")[1];

        MockHttpServletResponse responseGet = new MockHttpServletResponse();
        MockHttpServletRequest requestGet = new MockHttpServletRequest();

        requestGet.setMethod("GET");
        requestGet.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        requestGet.setRequestURI("/fdp/catalog/" + catId);

        handler = handlerMapping.getHandler(requestGet).getHandler();
        handlerAdapter.handle(requestGet, responseGet, handler);
        assertFalse(responseGet.getContentAsString().contains(parentURI));
    }

    /**
     * Store catalog twice.
     *
     * @throws Exception
     */
    @Ignore
    @DirtiesContext
    @Test(expected = IllegalStateException.class)
    public void storeCatalogTwice() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        String metadata = ExampleFilesUtils.getFileContentAsString(
                ExampleFilesUtils.CATALOG_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/fdp/catalog");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());

        response = new MockHttpServletResponse();
        request = new MockHttpServletRequest();

        request.setServerName("localhost");
        request.setContextPath("fdp");
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/fdp/catalog");

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
        request.setRequestURI("/fdp/catalog/dumpy");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        //assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
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
     * Test url reretouring option for catalog.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void storeCatalogByURLReretouring() throws Exception {
        metadataFixtures.importDefaultFixtures("https://lorentz.fair-dtls.surf-hosted.nl/fdp");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        String metadata = ExampleFilesUtils.getFileContentAsString(
                ExampleFilesUtils.CATALOG_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.addHeader("x-forwarded-host", "lorentz.fair-dtls.surf-hosted.nl");
        request.addHeader("x-forwarded-proto", "https");
        request.addHeader("x-forwarded-port", "443");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/fdp/catalog");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

    /**
     * Test url reretouring with ports
     *
     * @throws Exception
     */
    @Ignore
    @DirtiesContext
    public void storeCatalogByURLReretouringWithPort() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        String metadata = ExampleFilesUtils.getFileContentAsString(
                ExampleFilesUtils.CATALOG_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.addHeader("x-forwarded-host", "lorentz.fair-dtls.surf-hosted.nl");
        request.addHeader("x-forwarded-proto", "https");
        request.addHeader("x-forwarded-port", "8006");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/fdp/catalog");
        request.setServerPort(8080);

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);

        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

}