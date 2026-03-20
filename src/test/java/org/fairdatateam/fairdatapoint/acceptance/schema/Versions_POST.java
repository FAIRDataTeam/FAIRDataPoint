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
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaReleaseDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.schema.data.MetadataSchemaFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaDraftRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaDraft;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.util.UUID;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestPost;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createUserForbiddenTestPost;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("POST /metadata-schemas/:schemaUuid/versions")
public class Versions_POST extends WebIntegrationTest {

    private URI url(String uuid) {
        return URI.create(format("/metadata-schemas/%s/versions", uuid));
    }

    private MetadataSchemaReleaseDTO reqDto(String version, boolean published) {
        return new MetadataSchemaReleaseDTO(
                version,
                "Some description",
                published
        );
    }

    @Autowired
    private MetadataSchemaFixtures metadataSchemaFixtures;

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private MetadataSchemaDraftRepository metadataSchemaDraftRepository;

    private final ParameterizedTypeReference<MetadataSchemaDTO> responseType =
            new ParameterizedTypeReference<>() {
            };

    @Test
    @DisplayName("HTTP 200: publish new")
    public void res200_publishNew() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        metadataSchemaDraftRepository.deleteAll();
        MetadataSchemaDraft draft = metadataSchemaFixtures.customSchemaDraft1();
        metadataSchemaDraftRepository.save(draft);
        MetadataSchemaReleaseDTO reqDto = reqDto("0.1.0", true);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url(draft.getUuid()))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDto);

        // WHEN:
        ResponseEntity<MetadataSchemaDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result is latest", result.getBody().getLatest().isLatest(), is(equalTo(true)));
        assertThat("Result is published", result.getBody().getLatest().isPublished(), is(equalTo(true)));
        assertThat("Result has correct name", result.getBody().getName(), is(equalTo(draft.getName())));
        assertThat("Result has correct version", result.getBody().getLatest().getVersion(), is(equalTo(reqDto.getVersion())));
        assertThat("Metadata schema repository has one schema", metadataSchemaRepository.count(), is(equalTo(1L)));
    }

    @Test
    @DisplayName("HTTP 200: publish newer version")
    public void res200_publishNewerVersion() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        metadataSchemaDraftRepository.deleteAll();
        MetadataSchema schemaV1 = metadataSchemaRepository.save(metadataSchemaFixtures.customSchemaV1(true));
        MetadataSchemaDraft draft = metadataSchemaFixtures.customSchemaDraft1();
        metadataSchemaDraftRepository.save(draft);
        MetadataSchemaReleaseDTO reqDto = reqDto("2.0.0", true);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url(draft.getUuid()))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDto);

        // WHEN:
        ResponseEntity<MetadataSchemaDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result is latest", result.getBody().getLatest().isLatest(), is(equalTo(true)));
        assertThat("Result is published", result.getBody().getLatest().isPublished(), is(equalTo(true)));
        assertThat("Result has correct name", result.getBody().getName(), is(equalTo(draft.getName())));
        assertThat("Result has correct version", result.getBody().getLatest().getVersion(), is(equalTo(reqDto.getVersion())));
        assertThat("Metadata schema repository has 2 schemas", metadataSchemaRepository.count(), is(equalTo(2L)));
        assertThat("Older version is still stored", metadataSchemaRepository.findByUuidAndVersionString(schemaV1.getUuid(), schemaV1.getVersionString()).isPresent(), is(true));
        assertThat("Older version is no longer the latest", metadataSchemaRepository.findByUuidAndVersionString(schemaV1.getUuid(), schemaV1.getVersionString()).get().isLatest(), is(false));
    }

    @Test
    @DisplayName("HTTP 400: not newer version")
    public void res400_notNewerVersion() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        metadataSchemaDraftRepository.deleteAll();
        metadataSchemaRepository.save(metadataSchemaFixtures.customSchemaV1(true));
        MetadataSchemaDraft draft = metadataSchemaFixtures.customSchemaDraft1();
        metadataSchemaDraftRepository.save(draft);
        MetadataSchemaReleaseDTO reqDto = reqDto("0.1.0", true);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url(draft.getUuid()))
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDto);

        // WHEN:
        ResponseEntity<MetadataSchemaDTO> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
    }

    @Test
    @DisplayName("HTTP 404")
    public void res404() {
        createNoUserForbiddenTestPost(client, url("nonExisting"), reqDto("1.0.0", true));
    }

    @Test
    @DisplayName("HTTP 403: User is not authenticated")
    public void res403_notAuthenticated() {
        createNoUserForbiddenTestPost(client, url(UUID.randomUUID().toString()), reqDto("1.0.0", true));
    }

    @Test
    @DisplayName("HTTP 403: User is not an admin")
    public void res403_notAdmin() {
        createUserForbiddenTestPost(client, url(UUID.randomUUID().toString()), reqDto("1.0.0", true));
    }
}
