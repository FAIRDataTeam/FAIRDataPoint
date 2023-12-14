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
package nl.dtls.fairdatapoint.acceptance.metadata;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.in;
import static org.hamcrest.core.Is.is;
import java.net.URI;
import nl.dtls.fairdatapoint.WebIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;

public class SingpostingTest extends WebIntegrationTest {
    private URI url(String id) {
        return URI.create(format("/catalog/%s", id));
    }

    @Test
    public void testCatalogSignpostingHeaders() {
        // GIVEN:
        var request = RequestEntity
                .get(url("catalog-1"))
                .header(HttpHeaders.ACCEPT, "text/turtle")
                .build();
        var responseType = new ParameterizedTypeReference<String>() {};

        // WHEN:
        var result = client.exchange(request, responseType);

        // THEN:
        var linkHeaderMatcher = is(in(result.getHeaders().get(HttpHeaders.LINK)));
        assertThat("<http://example.com/publisher>; rel=\"author\"", linkHeaderMatcher);
        assertThat("<http://localhost:8088/catalog/catalog-1>; rel=\"cite-as\"", linkHeaderMatcher);
        assertThat("<http://www.w3.org/ns/dcat#Catalog>; rel=\"type\"", linkHeaderMatcher);
        assertThat("<http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0>; rel=\"license\"", linkHeaderMatcher);
    }
}
