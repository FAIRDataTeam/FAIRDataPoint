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
package nl.dtls.fairdatapoint.acceptance.metadata.common;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.servlet.http.HttpServletResponse;

import static nl.dtls.fairdatapoint.acceptance.metadata.TestMetadataFixtures.*;
import static org.eclipse.rdf4j.rio.RDFFormat.TURTLE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GeneralControllerTest extends MetadataControllerTest {

    /**
     * The default content type is text/turtle, when the accept header is not set the default
     * content type is served. This test is excepted to pass.
     *
     * @throws Exception
     */
    @DirtiesContext
    @Test
    public void noAcceptHeader() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("GET");
        request.setRequestURI(TEST_FDP_PATH);
        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(TURTLE.getDefaultMIMEType(), response.getContentType());

        request.setRequestURI(TEST_CATALOG_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(TURTLE.getDefaultMIMEType(), response.getContentType());

        request.setRequestURI(TEST_DATASET_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(TURTLE.getDefaultMIMEType(), response.getContentType());

        request.setRequestURI(TEST_DISTRIBUTION_PATH);
        handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);
        assertEquals(TURTLE.getDefaultMIMEType(), response.getContentType());
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

        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_FDP_PATH);
        Object handler = handlerMapping.getHandler(request).getHandler();
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

    @DirtiesContext
    @Test
    public void testOncePerRequestFilter() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.setMethod("GET");
        request.addHeader(HttpHeaders.ACCEPT, "text/turtle");
        request.setRequestURI(TEST_FDP_PATH);

        Object handler = handlerMapping.getHandler(request).getHandler();
        handlerAdapter.handle(request, response, handler);

        MockFilterChain filterChain1 = new MockFilterChain();
        MockFilterChain filterChain2 = new MockFilterChain();
        loggingFilter.doFilterInternal(request, response, filterChain1);
        corsFilter.doFilterInternal(request, response, filterChain2);

        assertTrue(response.containsHeader(HttpHeaders.SERVER));

    }

    @DirtiesContext
    @Test
    public void getRequestsAreSecured() throws Exception {
        // GIVEN:
        String reqBody = "";
        MockHttpServletRequestBuilder request = get("/fdp");

        // WHEN:
        ResultActions result = mockMvc.perform(request);

        // THEN:
        result.andExpect(status().isOk());
    }

    @DirtiesContext
    @Test
    public void optionsRequestsAreSecured() throws Exception {
        // GIVEN:
        String reqBody = "";
        MockHttpServletRequestBuilder request = options("/fdp");

        // WHEN:
        ResultActions result = mockMvc.perform(request);

        // THEN:
        result.andExpect(status().isOk());
    }

}
