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
package nl.dtls.fairdatapoint.acceptance.apikey;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.apikey.ApiKeyDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestGet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("GET /api-keys")
public class List_GET extends WebIntegrationTest {

    private URI url() {
        return URI.create("/api-keys");
    }

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN:
        RequestEntity<Void> request = RequestEntity
                .get(url())
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .build();
        ParameterizedTypeReference<List<ApiKeyDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<List<ApiKeyDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        List<ApiKeyDTO> body = result.getBody();
        assertThat(body.size(), is(equalTo(1)));
        body.get(0).equals(null);
    }

    @Test
    @DisplayName("HTTP 403: User is not authenticated")
    public void res403_notAuthenticated() {
        createNoUserForbiddenTestGet(client, url());
    }

}
