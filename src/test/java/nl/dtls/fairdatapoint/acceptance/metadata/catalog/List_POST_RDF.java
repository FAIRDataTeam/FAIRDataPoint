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

import nl.dtls.fairmetadata4j.model.CatalogMetadata;
import nl.dtls.fairmetadata4j.utils.MetadataUtils;
import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.database.rdf.migration.development.metadata.MetadataMigration;
import nl.dtls.fairdatapoint.utils.TestMetadataFixtures;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.acls.dao.AclRepository;
import org.springframework.security.acls.model.AclCache;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("POST /catalog (RDF)")
public class List_POST_RDF extends WebIntegrationTest {

    private enum TestType {
        STANDARD, WITHOUT_PARENT
    }

    @Autowired
    private TestMetadataFixtures testMetadataFixtures;

    @Autowired
    private AclRepository aclRepository;

    @Autowired
    private AclCache aclCache;

    @Autowired
    private MetadataMigration metadataMigration;

    private URI url() {
        return URI.create("/catalog");
    }

    private String reqDto(TestType type) throws Exception {
        switch (type) {
            case STANDARD:
                CatalogMetadata catalog3 = testMetadataFixtures.catalog3();
                return MetadataUtils.getString(catalog3, RDFFormat.TURTLE);
            case WITHOUT_PARENT:
                CatalogMetadata catalog3WithoutParent = testMetadataFixtures.catalog3();
                catalog3WithoutParent.setParentURI(null);
                return MetadataUtils.getString(catalog3WithoutParent, RDFFormat.TURTLE);
        }
        return "";
    }

    @ParameterizedTest
    @EnumSource(TestType.class)
    @DisplayName("HTTP 201")
    public void res201(TestType testType) throws Exception {
        // GIVEN:
        RequestEntity<String> request = RequestEntity
                .post(url())
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle")
                .header(HttpHeaders.ACCEPT, "text/turtle")
                .body(reqDto(testType));
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<String> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
    }

    @Test
    @DisplayName("HTTP 201 (with rerouting)")
    public void res201_withRerouting() throws Exception {
        // GIVEN: We need to clear all permissions from default FDP fixtures
        aclRepository.deleteAll();
        aclCache.clearCache();
        // AND: Prepare fixtures
        metadataMigration.importDefaultFixtures(testMetadataFixtures.alternativeInstanceUrl);
        CatalogMetadata catalog3 = testMetadataFixtures.alternative_catalog3();
        String reqDto = MetadataUtils.getString(catalog3, RDFFormat.TURTLE);
        // AND: Prepare request
        RequestEntity<String> request = RequestEntity
                .post(url())
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .header(HttpHeaders.CONTENT_TYPE, "text/turtle")
                .header(HttpHeaders.ACCEPT, "text/turtle")
                .header("x-forwarded-host", "lorentz.fair-dtls.surf-hosted.nl")
                .header("x-forwarded-proto", "https")
                .header("x-forwarded-port", "443")
                .body(reqDto);
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<String> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
    }

}
