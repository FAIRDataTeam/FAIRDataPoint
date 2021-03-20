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
package nl.dtls.fairdatapoint.acceptance.openapi;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.index.entry.IndexEntryDTO;
import nl.dtls.fairdatapoint.utils.CustomPageImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("GET /swagger-ui.html")
public class SwaggerUI_GET extends WebIntegrationTest {

    private URI baseUrl() {
        return URI.create("/swagger-ui.html");
    }

    private URI redirectedUrl() {
        return URI.create("/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config");
    }

    @Test
    @DisplayName("HTTP 302: Redirects to Swagger UI")
    public void res302_redirectsToSwaggerUI() {
        // GIVEN
        RequestEntity<?> request = RequestEntity
                .get(baseUrl())
                .accept(MediaType.TEXT_HTML)
                .build();

        // WHEN
        ResponseEntity<String> result = client.exchange(request, new ParameterizedTypeReference<>() {});

        // THEN
        assertThat("Response code is FOUND", result.getStatusCode(), is(equalTo(HttpStatus.FOUND)));
        assertThat("Contains Location header", result.getHeaders().getLocation(), is(notNullValue()));
        assertThat("Contains correct Location header", result.getHeaders().getLocation().toString().endsWith(redirectedUrl().toString()), is(Boolean.TRUE));
    }

    @Test
    @DisplayName("HTTP 200: Swagger UI")
    public void res200_swaggerUI() {
        // GIVEN
        RequestEntity<?> request = RequestEntity
                .get(redirectedUrl())
                .accept(MediaType.TEXT_HTML)
                .build();

        // WHEN
        ResponseEntity<String> result = client.exchange(request, new ParameterizedTypeReference<>() {});

        // THEN
        assertThat("Response code is OK", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response is Swagger UI in HTML", result.getHeaders().getContentType(), is(equalTo(MediaType.TEXT_HTML)));
    }
}
