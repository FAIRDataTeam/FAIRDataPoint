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
package nl.dtls.fairdatapoint.acceptance.common;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static nl.dtls.fairdatapoint.WebIntegrationTest.ALBERT_TOKEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class ForbiddenTest {

    public static <T> void createForbiddenTest(TestRestTemplate client, RequestEntity<T> request) {
        // GIVEN:
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<String> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    public static <T> void createNoUserForbiddenTestGet(TestRestTemplate client, URI url) {
        createForbiddenTest(
                client,
                RequestEntity.get(url).build()
        );
    }

    public static <T> void createNoUserForbiddenTestPost(TestRestTemplate client, URI url, T object) {
        createForbiddenTest(
                client,
                RequestEntity.post(url).body(object)
        );
    }

    public static <T> void createNoUserForbiddenTestPut(TestRestTemplate client, URI url, T object) {
        createForbiddenTest(
                client,
                RequestEntity.put(url).body(object)
        );
    }

    public static <T> void createNoUserForbiddenTestDelete(TestRestTemplate client, URI url) {
        createForbiddenTest(
                client,
                RequestEntity.delete(url).build()
        );
    }

    public static void createUserForbiddenTestGet(TestRestTemplate client, URI url) {
        createForbiddenTest(
                client,
                RequestEntity.get(url).header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN).build()
        );
    }

    public static <T> void createUserForbiddenTestPost(TestRestTemplate client, URI url, T object) {
        createForbiddenTest(
                client,
                RequestEntity.post(url).header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN).body(object)
        );
    }

    public static <T> void createUserForbiddenTestPut(TestRestTemplate client, URI url, T object) {
        createForbiddenTest(
                client,
                RequestEntity.put(url).header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN).body(object)
        );
    }

    public static <T> void createUserForbiddenTestDelete(TestRestTemplate client, URI url) {
        createForbiddenTest(
                client,
                RequestEntity.delete(url).header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN).build()
        );
    }
}
