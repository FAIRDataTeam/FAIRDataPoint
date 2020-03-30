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
import nl.dtls.fairdatapoint.service.rdf.RdfFileService;
import nl.dtls.fairdatapoint.utils.TestMetadataFixtures;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createUserNotFoundTestGet;
import static nl.dtls.fairmetadata4j.accessor.MetadataGetter.getUri;
import static nl.dtls.fairmetadata4j.accessor.MetadataSetter.*;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.i;
import static nl.dtls.fairmetadata4j.util.ValueFactoryHelper.l;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /catalog/:catalogId")
public class Detail_PUT extends WebIntegrationTest {

    @Autowired
    private TestMetadataFixtures testMetadataFixtures;

    @Autowired
    private RdfFileService rdfFileService;

    private URI url(String id) {
        return URI.create(format("/catalog/%s", id));
    }

    private String reqDto() {
        Model catalog = testMetadataFixtures.catalog1();
        IRI uri = getUri(catalog);
        setTitle(catalog, uri, l("EDITED: Some title"));
        setDescription(catalog, uri, l("EDITED: Some description"));
        setVersion(catalog, uri, l("99.0"));
        setLicence(catalog, uri, i("http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0/EDITED"));
        setLanguage(catalog, uri, i("http://id.loc.gov/vocabulary/iso639-1/en/EDITED"));
        setThemeTaxonomies(catalog, uri, List.of(i("https://purl.org/example#theme/EDITED")));
        return rdfFileService.write(catalog, RDFFormat.TURTLE);
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
        RequestEntity<String> request = RequestEntity
                .put(url("catalog-1"))
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle")
                .header(HttpHeaders.ACCEPT, "text/turtle")
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
        RequestEntity<String> request = RequestEntity
                .put(url("catalog-1"))
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle")
                .header(HttpHeaders.ACCEPT, "text/turtle")
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
