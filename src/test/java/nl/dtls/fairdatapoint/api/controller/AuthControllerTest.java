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

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.AuthDTO;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends WebIntegrationTest {

    @Test
    public void postTokenSuccess() throws Exception {
        // GIVEN:
        AuthDTO reqDto = new AuthDTO("user", "password");
        String reqBody = objectMapper.writeValueAsString(reqDto);
        MockHttpServletRequestBuilder request =
                post("/token")
                        .contentType(MediaType.APPLICATION_JSON.toString())
                        .content(reqBody);

        // WHEN:
        ResultActions result = mockMvc.perform(request);

        // THEN:
        result.andExpect(status().isOk());
    }

    @Test
    public void postTokenBadCredentials() throws Exception {
        // GIVEN:
        AuthDTO reqDto = new AuthDTO("nonExistingUser", "badPassword");
        String reqBody = objectMapper.writeValueAsString(reqDto);
        MockHttpServletRequestBuilder request =
                post("/token")
                        .contentType(MediaType.APPLICATION_JSON.toString())
                        .content(reqBody);

        // WHEN:
        ResultActions result = mockMvc.perform(request);

        // THEN:
        result.andExpect(status().isUnauthorized());
    }

}
