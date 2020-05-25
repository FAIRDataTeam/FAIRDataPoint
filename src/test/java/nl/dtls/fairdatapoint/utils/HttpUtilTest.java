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
package nl.dtls.fairdatapoint.utils;

import org.eclipse.rdf4j.model.IRI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.servlet.http.HttpServletRequest;

import static nl.dtls.fairdatapoint.util.HttpUtil.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpUtilTest {

    @ParameterizedTest
    @CsvSource({
            "http://fairdatapoint.com/,http://purl.org/fairdatapoint/test",
            "http://fairdatapoint.com/catalog/catalog-1,http://purl.org/fairdatapoint/test/catalog/catalog-1",
            "https://fairdatapoint.com/catalog/catalog-1/,http://purl.org/fairdatapoint/test/catalog/catalog-1",
    })
    public void getRequestURLTest(String url, String expected) {
        // GIVEN: Prepare request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer(url));
        String persistentUrl = "http://purl.org/fairdatapoint/test";

        // WHEN:
        String result = getRequestURL(request, persistentUrl);

        // THEN:
        assertThat(result, is(equalTo(expected)));
    }

    @Test
    public void generateNewIRITest() {
        // GIVEN: Prepare request
        String url = "http://fairdatapoint.com/";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer(url));
        String persistentUrl = "http://purl.org/fairdatapoint/test";

        // WHEN:
        IRI result = generateNewIRI(request, persistentUrl);

        // THEN:
        assertThat(result.stringValue().length(), is(equalTo(71)));
        assertThat(result.stringValue().startsWith(persistentUrl), is(equalTo(true)));
    }

    @ParameterizedTest
    @CsvSource({
            "http://example.com/,http://example.com",
            "http://example.com,http://example.com",
            "example.com/,example.com",
            "example.com/foo,example.com/foo",
            "example.com/foo/,example.com/foo",
    })
    public void removeLastSlashTest(String url, String expected) {
        assertThat(removeLastSlash(url), is(equalTo(expected)));
    }

    @ParameterizedTest
    @CsvSource({
            "http://example.com,example.com",
            "https://example.com,example.com",
            "example.com,example.com",
    })
    public void removeProtocolTest(String url, String expected) {
        assertThat(removeProtocol(url), is(equalTo(expected)));
    }

}
