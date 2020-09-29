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
package nl.dtls.fairdatapoint.acceptance.general;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.database.mongo.migration.development.apikey.data.ApiKeyFixtures;
import nl.dtls.fairdatapoint.util.RdfIOUtil;
import nl.dtls.fairdatapoint.utils.TestRdfMetadataFixtures;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class SecurityTest extends WebIntegrationTest {

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    @Autowired
    private ApiKeyFixtures apiKeyFixtures;

    @Test
    public void postRequestsAreSecured() {
        // GIVEN: Prepare data
        String reqDto = RdfIOUtil.write(testMetadataFixtures.c1_d1_distribution1(), RDFFormat.TURTLE);
        // AND: Prepare request
        RequestEntity<String> request = RequestEntity
                .post(URI.create("/distribution"))
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle")
                .body(reqDto);
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<String> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    public void putRequestsAreSecured() {
        // GIVEN: Prepare data
        String reqDto = RdfIOUtil.write(testMetadataFixtures.c1_d1_distribution1(), RDFFormat.TURTLE);
        // AND: Prepare request
        RequestEntity<String> request = RequestEntity
                .put(URI.create("/distribution"))
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle")
                .body(reqDto);
        ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    public void apiKeyIsWorking() {
        // GIVEN: Prepare data
        String reqDto = RdfIOUtil.write(testMetadataFixtures.c1_d1_distribution1(), RDFFormat.TURTLE);
        // AND: Prepare request
        RequestEntity<String> request = RequestEntity
                .post(URI.create("/distribution"))
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle")
                .header(HttpHeaders.ACCEPT, "text/turtle")
                .header(HttpHeaders.AUTHORIZATION, format("Bearer %s", apiKeyFixtures.ALBERT_API_KEY))
                .body(reqDto);
        ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
    }

}