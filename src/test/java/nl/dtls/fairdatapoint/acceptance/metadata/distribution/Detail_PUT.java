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
package nl.dtls.fairdatapoint.acceptance.metadata.distribution;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.util.RdfIOUtil;
import nl.dtls.fairdatapoint.utils.TestRdfMetadataFixtures;
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

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestPut;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createUserNotFoundTestGet;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataGetter.getUri;
import static nl.dtls.fairdatapoint.entity.metadata.MetadataSetter.*;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.l;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /distribution/:distributionId")
public class Detail_PUT extends WebIntegrationTest {

    @Autowired
    private TestRdfMetadataFixtures testMetadataFixtures;

    private URI url(String id) {
        return URI.create(format("/distribution/%s", id));
    }

    private String reqDto(Model distribution) {
        IRI uri = getUri(distribution);
        setTitle(distribution, uri, l("EDITED: Some title"));
        setDescription(distribution, uri, l("EDITED: Some description"));
        setVersion(distribution, uri, l("99.0"));
        setLicence(distribution, uri, i("http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0/EDITED"));
        setLanguage(distribution, uri, i("http://id.loc.gov/vocabulary/iso639-1/en/EDITED"));
        setMediaType(distribution, uri, l("text/edited"));
        setDownloadURL(distribution, uri, i("http://example.com/edited"));
        setAccessURL(distribution, uri, i("http://example.com/edited"));
        return RdfIOUtil.write(distribution, RDFFormat.TURTLE);
    }

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        create_res200(ALBERT_TOKEN);
    }

    @Test
    @DisplayName("HTTP 200: User is an admin")
    public void res200_admin() {
        create_res200(ADMIN_TOKEN);
    }

    private void create_res200(String token) {
        // GIVEN:
        RequestEntity<String> request = RequestEntity
                .put(url("distribution-1"))
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle")
                .header(HttpHeaders.ACCEPT, "text/turtle")
                .body(reqDto(testMetadataFixtures.c1_d1_distribution1()));
        ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
    }

    @Test
    @DisplayName("HTTP 403: Anonymous access")
    public void res403_anonymous() {
        createNoUserForbiddenTestPut(client, url("distribution-1"), reqDto(testMetadataFixtures.c1_d1_distribution1()));
    }

    @Test
    @DisplayName("HTTP 403: User is not an owner")
    public void res403_non_Owner() {
        // GIVEN:
        RequestEntity<String> request = RequestEntity
                .put(url("distribution-2"))
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle")
                .header(HttpHeaders.ACCEPT, "text/turtle")
                .body(reqDto(testMetadataFixtures.c1_d1_distribution2()));
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
