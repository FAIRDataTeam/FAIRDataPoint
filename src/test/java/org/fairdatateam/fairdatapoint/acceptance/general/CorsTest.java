/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.acceptance.general;

import org.eclipse.jetty.http.HttpHeader;
import org.fairdatateam.fairdatapoint.Profiles;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies Cross-Origin Resource Sharing (CORS) configuration. For more information see
 * <a href="https://docs.spring.io/spring-security/reference/servlet/integrations/cors.html">spring security CORS</a>
 * and <a href="https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html">spring web mvc CORS</a>.
 */
@ActiveProfiles(Profiles.TESTING)
@AutoConfigureMockMvc
@SpringBootTest
public class CorsTest {

    private final String otherOrigin = "https://other.origin";
    private final String uri = "/";

    private final MockMvcTester mockMvc;

    // todo: do we need to enable the security filter chain as well, to test http.cors()?
    //  https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/setup.html

    /**
     * Constructor
     */
    @Autowired
    public CorsTest(MockMvcTester mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void normalRequestYieldsNoAccessControlHeaders() {
        // request without Origin header
        MvcTestResult testResult = mockMvc.options().uri(uri).exchange();
        assertThat(testResult).hasStatusOk()
                .doesNotContainHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    @Test
    public void corsPreflightRequestFailsIfAccessControlRequestMethodHeaderMissing() {
        // request with Origin header, but without Access-Control-Request-Method header
        MvcTestResult testResult = mockMvc.options()
                .uri(uri)
                .header(HttpHeaders.ORIGIN, otherOrigin)
                .exchange();
        assertThat(testResult).hasStatus(HttpStatus.FORBIDDEN)
                .hasBodyTextEqualTo("Invalid CORS request");
    }

    @Test
    public void corsPreflightRequestYieldsExpectedAccessControlHeaders() {
        // valid CORS request, with both required headers
        MvcTestResult testResult = mockMvc.options()
                .uri(uri)
                // this preflight says: "I plan to make a GET request with this header from this origin."
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET)
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, HttpHeaders.AUTHORIZATION)
                .header(HttpHeaders.ORIGIN, otherOrigin)
                .exchange();
        List.of(
                // note that Access-Control-Allow-Headers is only returned if the request
                // contains Access-Control-Request-Headers
                HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
                HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS
        ).forEach(header -> assertThat(testResult).hasStatusOk().containsHeader(header));

    }

}
