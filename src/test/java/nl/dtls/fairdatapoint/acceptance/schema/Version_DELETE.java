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
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaExtensionRepository;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaUsageRepository;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaVersionRepository;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaState;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaType;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaVersion;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestDelete;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createUserForbiddenTestDelete;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createAdminNotFoundTestDelete;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("DELETE /metadata-schemas/:schemaUuid/versions/:version")
public class Version_DELETE extends WebIntegrationTest {

    private URI url(UUID uuid, String version) {
        return URI.create(format("/metadata-schemas/%s/versions/%s", uuid.toString(), version));
    }

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private MetadataSchemaVersionRepository metadataSchemaVersionRepository;

    @Test
    @DisplayName("HTTP 200: delete single")
    public void res200_deleteSingle() {
        // GIVEN: Prepare data
        MetadataSchema schema = metadataSchemaRepository.findByUuid(Common.SCHEMA_SIMPLE_UUID).get();
        MetadataSchemaVersion schemaV1 = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_SIMPLE_V1_UUID).get();

        // AND: Prepare request
        RequestEntity<Void> request = RequestEntity
                .delete(url(schema.getUuid(), schemaV1.getVersion()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .build();
        ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
        assertThat(metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_SIMPLE_V1_UUID).isPresent(), is(false));
        assertThat(metadataSchemaRepository.findByUuid(Common.SCHEMA_SIMPLE_UUID).isPresent(), is(false));
    }

    @Test
    @DisplayName("HTTP 200: delete latest")
    public void res200_deleteLatest() {
        // GIVEN: Prepare data
        MetadataSchema schema = metadataSchemaRepository.findByUuid(Common.SCHEMA_MULTI_UUID).get();
        MetadataSchemaVersion schemaV1 = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_MULTI_V1_UUID).get();
        MetadataSchemaVersion schemaV2 = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_MULTI_V2_UUID).get();
        MetadataSchemaVersion schemaV3 = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_MULTI_V3_UUID).get();

        // AND: Prepare request
        RequestEntity<Void> request = RequestEntity
                .delete(url(schema.getUuid(), schemaV3.getVersion()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .build();
        ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
        assertThat(metadataSchemaRepository.findAll().isEmpty(), is(false));

        assertThat(metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_MULTI_V1_UUID).isPresent(), is(true));
        assertThat(metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_MULTI_V2_UUID).isPresent(), is(true));
        assertThat(metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_MULTI_V3_UUID).isPresent(), is(false));
        assertThat(metadataSchemaVersionRepository.getLatestBySchemaUuid(Common.SCHEMA_MULTI_UUID).isPresent(), is(true));
        assertThat(metadataSchemaVersionRepository.getLatestBySchemaUuid(Common.SCHEMA_MULTI_UUID).get().getVersion(), is(equalTo(schemaV2.getVersion())));
        assertThat(metadataSchemaVersionRepository.getLatestBySchemaUuid(Common.SCHEMA_MULTI_UUID).get().isLatest(), is(true));
    }

    @Test
    @DisplayName("HTTP 200: delete non-latest")
    public void res200_deleteNonLatest() {
        // GIVEN: Prepare data
        MetadataSchema schema = metadataSchemaRepository.findByUuid(Common.SCHEMA_MULTI_UUID).get();
        MetadataSchemaVersion schemaV1 = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_MULTI_V1_UUID).get();
        MetadataSchemaVersion schemaV2 = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_MULTI_V2_UUID).get();
        MetadataSchemaVersion schemaV3 = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_MULTI_V3_UUID).get();

        // AND: Prepare request
        RequestEntity<Void> request = RequestEntity
                .delete(url(schema.getUuid(), schemaV2.getVersion()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .build();
        ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<Void> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
        assertThat(metadataSchemaRepository.findAll().isEmpty(), is(false));

        assertThat(metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_MULTI_V2_UUID).isPresent(), is(false));
        assertThat(metadataSchemaVersionRepository.getLatestBySchemaUuid(Common.SCHEMA_MULTI_UUID).isPresent(), is(true));
        assertThat(metadataSchemaVersionRepository.getLatestBySchemaUuid(Common.SCHEMA_MULTI_UUID).get().getVersion(), is(equalTo(schemaV3.getVersion())));
        assertThat(metadataSchemaVersionRepository.getLatestBySchemaUuid(Common.SCHEMA_MULTI_UUID).get().isLatest(), is(true));
    }

    @Test
    @DisplayName("HTTP 404")
    public void res404() {
        createAdminNotFoundTestDelete(client, url(UUID.randomUUID(), "1.0.0"));
    }

    @Test
    @DisplayName("HTTP 403: User is not authenticated")
    public void res403_notAuthenticated() {
        createNoUserForbiddenTestDelete(client, url(UUID.randomUUID(), "1.0.0"));
    }

    @Test
    @DisplayName("HTTP 403: User is not an admin")
    public void res403_notAdmin() {
        createUserForbiddenTestDelete(client, url(UUID.randomUUID(), "1.0.0"));
    }
}
