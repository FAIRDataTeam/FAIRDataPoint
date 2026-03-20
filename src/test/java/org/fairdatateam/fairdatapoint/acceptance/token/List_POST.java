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
package nl.dtls.fairdatapoint.acceptance.token;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.acceptance.user.Common;
import nl.dtls.fairdatapoint.api.dto.auth.AuthDTO;
import nl.dtls.fairdatapoint.api.dto.auth.TokenDTO;
import nl.dtls.fairdatapoint.api.dto.error.ErrorDTO;
import nl.dtls.fairdatapoint.api.dto.user.UserDTO;
import nl.dtls.fairdatapoint.entity.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("POST /tokens")
public class List_POST extends WebIntegrationTest {

    @Test
    @DisplayName("HTTP 200: login")
    public void res200_login() {
        // GIVEN:
        AuthDTO reqDto = new AuthDTO("albert.einstein@example.com", "password");
        RequestEntity<AuthDTO> request = RequestEntity
                .post(URI.create("/tokens"))
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<TokenDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<TokenDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(result.getBody(), is(notNullValue()));
        assertThat(result.getBody().getToken(), is(anything()));
    }

    @Test
    @DisplayName("HTTP 401: bad credentials")
    public void res401_badCredentials() {
        // GIVEN:
        AuthDTO reqDto = new AuthDTO("nonExistingUser@example.com", "badPassword");
        RequestEntity<AuthDTO> request = RequestEntity
                .post(URI.create("/tokens"))
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<ErrorDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<ErrorDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.UNAUTHORIZED)));
        assertThat(result.getBody(), is(notNullValue()));
        assertThat(result.getBody().getMessage(), is("Invalid username/password supplied"));
    }

    @Test
    @DisplayName("HTTP 200: login and get current")
    public void res200() {
        //= Login (get token)
        // GIVEN:
        AuthDTO reqDto = new AuthDTO("albert.einstein@example.com", "password");
        RequestEntity<AuthDTO> request1 = RequestEntity
                .post(URI.create("/tokens"))
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<TokenDTO> responseType1 = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<TokenDTO> result1 = client.exchange(request1, responseType1);

        // THEN:
        assertThat(result1.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(result1.getBody(), is(notNullValue()));
        assertThat(result1.getBody().getToken(), is(anything()));

        //= Get current user with the token
        // GIVEN:
        RequestEntity<Void> request2 = RequestEntity
                .get(URI.create("/users/current"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + result1.getBody().getToken())
                .build();
        ParameterizedTypeReference<UserDTO> responseType2 = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<UserDTO> result2 = client.exchange(request2, responseType2);

        // THEN:
        assertThat(result2.getStatusCode(), is(equalTo(HttpStatus.OK)));
    }
}
