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
package nl.dtls.fairdatapoint.acceptance.schema;

import jakarta.persistence.EntityManager;
import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDTO;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaVersionRepository;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaState;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaType;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaVersion;
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
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("GET /metadata-schemas")
public class List_GET extends WebIntegrationTest {

    private URI url(boolean includeDrafts, boolean includeAbstract) {
        if (includeDrafts && !includeAbstract) {
            return URI.create("/metadata-schemas?drafts=true&abstract=false");
        }
        if (includeDrafts) {
            return URI.create("/metadata-schemas?drafts=true");
        }
        if (!includeAbstract) {
            return URI.create("/metadata-schemas?abstract=false");
        }
        return URI.create("/metadata-schemas");
    }

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private MetadataSchemaVersionRepository metadataSchemaVersionRepository;

    @Test
    @DisplayName("HTTP 200: regular user")
    public void res200_regularUser() {
        // GIVEN:
        List<MetadataSchemaVersion> all = metadataSchemaVersionRepository.getAllLatest();
        RequestEntity<Void> request = RequestEntity
                .get(url(false, true))
                .build();
        ParameterizedTypeReference<List<MetadataSchemaDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<List<MetadataSchemaDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        List<MetadataSchemaDTO> body = result.getBody();
        assertThat(body.size(), is(equalTo(12)));

        Common.compare(all.get(0), body.get(0));
        Common.compare(all.get(1), body.get(1));
        Common.compare(all.get(2), body.get(2));
        Common.compare(all.get(3), body.get(3));
        Common.compare(all.get(4), body.get(4));
        Common.compare(all.get(5), body.get(5));
        Common.compare(all.get(6), body.get(6));
    }

    @Test
    @DisplayName("HTTP 200: regular user, no abstract")
    public void res200_regularUserNoAbstract() {
        // GIVEN: prepare data
        List<MetadataSchemaVersion> all = metadataSchemaVersionRepository.getAllLatest().stream().filter(v -> !v.isAbstractSchema()).toList();

        // AND: prepare request
        RequestEntity<Void> request = RequestEntity
                .get(url(false, false))
                .build();
        ParameterizedTypeReference<List<MetadataSchemaDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<List<MetadataSchemaDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        List<MetadataSchemaDTO> body = result.getBody();
        assertThat(body.size(), is(equalTo(11)));

        Common.compare(all.get(0), body.get(0));
        Common.compare(all.get(1), body.get(1));
        Common.compare(all.get(2), body.get(2));
        Common.compare(all.get(3), body.get(3));
        Common.compare(all.get(4), body.get(4));
        Common.compare(all.get(5), body.get(5));
    }

    @Test
    @DisplayName("HTTP 200: admin with drafts")
    public void res200_adminDrafts() {
        List<MetadataSchemaVersion> all = metadataSchemaVersionRepository.getAllLatest();

        // AND: prepare request
        RequestEntity<Void> request = RequestEntity
                .get(url(true, true))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .build();
        ParameterizedTypeReference<List<MetadataSchemaDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<List<MetadataSchemaDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        List<MetadataSchemaDTO> body = result.getBody();
        assertThat(body.size(), is(equalTo(14)));

        Common.compare(all.get(0), body.get(0));
        Common.compare(all.get(1), body.get(1));
        Common.compare(all.get(2), body.get(2));
        Common.compare(all.get(3), body.get(3));
        Common.compare(all.get(4), body.get(4));
        Common.compare(all.get(5), body.get(5));
        Common.compare(all.get(6), body.get(6));
    }

    @Test
    @DisplayName("HTTP 403: unauthorized for drafts")
    public void res403_unauthorized() {
        // GIVEN: request
        RequestEntity<Void> request = RequestEntity
                .get(url(true, true))
                .build();
        ParameterizedTypeReference<?> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<?> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.UNAUTHORIZED)));
    }
}
