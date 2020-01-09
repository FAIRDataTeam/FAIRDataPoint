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
package nl.dtls.fairdatapoint.acceptance.metadata.catalog;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.metadata.CatalogMetadataChangeDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;
import static java.util.Optional.of;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createUserNotFoundTestGet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /catalog/:catalogId")
public class Detail_PUT extends WebIntegrationTest {

    private URI url(String id) {
        return URI.create(format("/catalog/%s", id));
    }

    private CatalogMetadataChangeDTO reqDto() {
        return new CatalogMetadataChangeDTO(
                "EDITED: Some title",
                of("EDITED: Some description"),
                "99.0",
                of("http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0/EDITED"),
                of("http://id.loc.gov/vocabulary/iso639-1/en/EDITED"),
                List.of("https://purl.org/example#theme/EDITED")
        );
    }

    @Test
    @DisplayName("HTTP 204")
    public void res204() {
        create_res204(ALBERT_TOKEN);
    }

    @Test
    @DisplayName("HTTP 204: User is an admin")
    public void res204_admin() {
        create_res204(ADMIN_TOKEN);
    }

    private void create_res204(String token) {
        // GIVEN:
        RequestEntity<CatalogMetadataChangeDTO> request = RequestEntity
                .put(url("catalog-1"))
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(reqDto());
        ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
    }

    @Test
    @DisplayName("HTTP 403")
    public void res403() {
        // GIVEN:
        RequestEntity<CatalogMetadataChangeDTO> request = RequestEntity
                .put(url("catalog-1"))
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .body(reqDto());
        ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    @DisplayName("HTTP 404")
    public void res404() {
        createUserNotFoundTestGet(client, url("nonExisting"));
    }

}
