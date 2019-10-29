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
package nl.dtls.fairdatapoint.acceptance.metadata.repository;

import nl.dtls.fairdatapoint.acceptance.metadata.common.MetadataControllerTest;
import org.apache.http.HttpHeaders;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;

public class FdpControllerTest extends MetadataControllerTest {

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

        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "application/trig");
        request.setRequestURI(TEST_FDP_PATH);

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
    }


    /**
     * Check file extension for repostory layer
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void getContentWithFileExtRepo() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("GET");
        request.setRequestURI(TEST_FDP_PATH + ".ttl");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("text/turtle", response.getContentType());
    }

    /**
     * Check url ends with /
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void checkURLFilter() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("GET");
        request.setRequestURI(TEST_FDP_PATH + "/");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        assertEquals("text/turtle", response.getContentType());
    }


    /**
     * Update repository metadata.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void updateRepositoryMetadata() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        String metadata = "<> <http://purl.org/dc/terms/title> \"Test update\" .";
        request.setMethod("PATCH");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "text/turtle");
        request.setContent(metadata.getBytes());
        request.setRequestURI("/fdp");

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    }


}
