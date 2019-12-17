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
package nl.dtls.fairdatapoint.acceptance.metadata.catalog;

import nl.dtls.fairdatapoint.acceptance.metadata.common.MetadataControllerTest;
import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.MetadataMigration;
import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.data.MetadataFixtures;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.utils.MetadataFixtureFilesHelper;
import org.apache.http.HttpHeaders;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.acls.dao.AclRepository;
import org.springframework.security.acls.model.AclCache;
import org.springframework.test.annotation.DirtiesContext;

import javax.servlet.http.HttpServletResponse;

import static nl.dtls.fairdatapoint.utils.MetadataFixtureLoader.TEST_CATALOG_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CatalogControllerTest extends MetadataControllerTest {

    @Autowired
    private MetadataFixtures metadataFixtures;

    @Autowired
    private AclRepository aclRepository;

    @Autowired
    private AclCache aclCache;

    @Autowired
    private MetadataMigration metadataMigration;

    @DirtiesContext
    @Test
    public void storeCatalog() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        String metadata = MetadataFixtureFilesHelper.getFileContentAsString(
                MetadataFixtureFilesHelper.CATALOG_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/catalog");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

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

        String metadata = MetadataFixtureFilesHelper.getFileContentAsString(
                MetadataFixtureFilesHelper.CATALOG_METADATA_FILE) + tripleStr.toString();
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/catalog");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        String catlogUrl = response.getHeader(HttpHeaders.LOCATION);
        String catId = catlogUrl.split("/catalog/")[1];

        MockHttpServletResponse responseGet = new MockHttpServletResponse();
        MockHttpServletRequest requestGet = new MockHttpServletRequest();

        requestGet.setMethod("GET");
        requestGet.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        requestGet.setRequestURI("/catalog/" + catId);

        handler = handlerMapping.getHandler(requestGet).getHandler();
        handlerAdapter.handle(requestGet, responseGet, handler);
        assertFalse(responseGet.getContentAsString().contains(parentURI));
    }

    @Disabled
    @DirtiesContext
    @Test
    public void storeCatalogTwice() throws Exception {
        assertThrows(IllegalStateException.class, () -> {

            MockHttpServletResponse response = new MockHttpServletResponse();
            MockHttpServletRequest request = new MockHttpServletRequest();

            String metadata = MetadataFixtureFilesHelper.getFileContentAsString(
                    MetadataFixtureFilesHelper.CATALOG_METADATA_FILE);
            request.setMethod("POST");
            request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
            request.setContent(metadata.getBytes());
            request.setRequestURI("/catalog");

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
            request.setRequestURI("/catalog");

            handler = handlerMapping.getHandler(request).getHandler();
            handlerAdapter.handle(request, response, handler);
        });
    }

    @DirtiesContext
    @Test
    public void checkNonExistingCatalog() throws Exception {
        assertThrows(ResourceNotFoundException.class, () -> {
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockHttpServletRequest request = new MockHttpServletRequest();

            request.setMethod("GET");
            request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
            request.setRequestURI("/catalog/dumpy");

            Object handler = handlerMapping.getHandler(request).getHandler();
            handlerAdapter.handle(request, response, handler);
            //assertEquals(HttpServletResponse.SC_NOT_FOUND, response.getStatus());
        });
    }

    @DirtiesContext
    @Test
    public void checkExistingCatalog() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_CATALOG_PATH);

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }

    @DirtiesContext
    @Test
    public void storeCatalogByURLReretouring() throws Exception {
        // We need to clear all permissions from default FDP fixtures
        aclRepository.deleteAll();
        aclCache.clearCache();
        metadataMigration.importDefaultFixtures("https://lorentz.fair-dtls.surf-hosted.nl/fdp");

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        String metadata = MetadataFixtureFilesHelper.getFileContentAsString(
                MetadataFixtureFilesHelper.CATALOG_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.addHeader("x-forwarded-host", "lorentz.fair-dtls.surf-hosted.nl");
        request.addHeader("x-forwarded-proto", "https");
        request.addHeader("x-forwarded-port", "443");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/catalog");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

    @Disabled
    @DirtiesContext
    public void storeCatalogByURLReretouringWithPort() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        String metadata = MetadataFixtureFilesHelper.getFileContentAsString(
                MetadataFixtureFilesHelper.CATALOG_METADATA_FILE);
        request.setMethod("POST");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.addHeader("x-forwarded-host", "lorentz.fair-dtls.surf-hosted.nl");
        request.addHeader("x-forwarded-proto", "https");
        request.addHeader("x-forwarded-port", "8006");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/catalog");
        request.setServerPort(8080);

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);

        assertEquals(HttpServletResponse.SC_CREATED, response.getStatus());
    }

}
