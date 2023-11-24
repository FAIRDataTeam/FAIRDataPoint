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

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaVersionDTO;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaVersionRepository;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaVersion;
import nl.dtls.fairdatapoint.entity.schema.SemVer;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("GET /metadata-schemas/public")
public class Public_GET extends WebIntegrationTest {

    @Autowired
    private MetadataSchemaVersionRepository metadataSchemaVersionRepository;

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    private final ParameterizedTypeReference<List<MetadataSchemaVersionDTO>> responseType =
            new ParameterizedTypeReference<>() {
            };

    private URI url() {
        return URI.create("/metadata-schemas/public");
    }

    @Test
    @DisplayName("HTTP 200: no published")
    public void res200_noPublished() {
        // GIVEN: prepare data
        final MetadataSchemaVersion published1 = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_SIMPLE_V1_UUID).get();
        final MetadataSchemaVersion published2 = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_INTERNAL_V1_UUID).get();

        metadataSchemaVersionRepository.deleteAll(List.of(published1, published2));
        metadataSchemaRepository.deleteAll(List.of(published1.getSchema(), published2.getSchema()));

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN:
        ResponseEntity<List<MetadataSchemaVersionDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result is an empty list", result.getBody().size(), is(equalTo(0)));
    }

    @Test
    @DisplayName("HTTP 200: published")
    public void res200_published() {
        // GIVEN: prepare data
        final MetadataSchemaVersion published1 = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_SIMPLE_V1_UUID).get();
        final MetadataSchemaVersion published2 = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_INTERNAL_V1_UUID).get();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN:
        ResponseEntity<List<MetadataSchemaVersionDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result is an empty list", result.getBody().size(), is(equalTo(2)));
        assertThat("UUID matches the published schema", result.getBody().get(0).getUuid(), is(equalTo(published1.getSchema().getUuid())));
        assertThat("UUID matches the published schema", result.getBody().get(0).getVersionUuid(), is(equalTo(published1.getUuid())));
        assertThat("UUID matches the published schema", result.getBody().get(1).getUuid(), is(equalTo(published2.getSchema().getUuid())));
        assertThat("UUID matches the published schema", result.getBody().get(1).getVersionUuid(), is(equalTo(published2.getUuid())));
    }
}
