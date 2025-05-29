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
import nl.dtls.fairdatapoint.database.mongo.migration.development.schema.data.MetadataSchemaFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DisplayName("GET /metadata-schemas/import")
public class Import_POST extends WebIntegrationTest {

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private MetadataSchemaFixtures metadataSchemaFixtures;

    private final ParameterizedTypeReference<List<MetadataSchemaVersionDTO>> responseType =
            new ParameterizedTypeReference<>() {
            };

    private URI url() {
        return URI.create("/metadata-schemas/import");
    }

    private MetadataSchemaVersionDTO schemaPublicDTO1() {
        String schemaUuid = UUID.randomUUID().toString();
        String versionUuid = UUID.randomUUID().toString();
        return MetadataSchemaVersionDTO.builder()
                .uuid(schemaUuid)
                .versionUuid(versionUuid)
                .previousVersionUuid(null)
                .importedFrom("http://example.com/remote-fdp")
                .origin("http://example.com/remote-fdp")
                .version("1.0.0")
                .name(metadataSchemaFixtures.customSchema().getName())
                .description(metadataSchemaFixtures.customSchema().getDescription())
                .definition(metadataSchemaFixtures.customSchema().getDefinition())
                .abstractSchema(metadataSchemaFixtures.customSchema().isAbstractSchema())
                .type(metadataSchemaFixtures.customSchema().getType())
                .targetClasses(metadataSchemaFixtures.customSchema().getTargetClasses())
                .extendsSchemaUuids(Collections.emptyList())
                .build();
    }

    private MetadataSchemaVersionDTO schemaPublicDTO2() {
        String schemaUuid = UUID.randomUUID().toString();
        String versionUuid = UUID.randomUUID().toString();
        return MetadataSchemaVersionDTO.builder()
                .uuid(schemaUuid)
                .versionUuid(versionUuid)
                .previousVersionUuid(null)
                .importedFrom("http://example.com/remote-fdp")
                .origin("http://example.com/other-remote-fdp")
                .version("1.2.3")
                .name(metadataSchemaFixtures.customSchema().getName())
                .description(metadataSchemaFixtures.customSchema().getDescription())
                .definition(metadataSchemaFixtures.customSchema().getDefinition())
                .abstractSchema(metadataSchemaFixtures.customSchema().isAbstractSchema())
                .type(metadataSchemaFixtures.customSchema().getType())
                .targetClasses(metadataSchemaFixtures.customSchema().getTargetClasses())
                .extendsSchemaUuids(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("HTTP 200: empty import")
    public void res200_emptyImport() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        List<MetadataSchemaVersionDTO> reqDTOs = Collections.emptyList();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTOs);

        // WHEN:
        ResponseEntity<List<MetadataSchemaVersionDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result is an empty list", result.getBody().size(), is(equalTo(0)));
        assertThat("Metadata schema repository is empty", metadataSchemaRepository.count(), is(equalTo(0L)));
    }

    @Test
    @DisplayName("HTTP 200: single import")
    public void res200_singleImport() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        MetadataSchemaVersionDTO schema = schemaPublicDTO1();
        List<MetadataSchemaVersionDTO> reqDTOs = Collections.singletonList(schema);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTOs);

        // WHEN:
        ResponseEntity<List<MetadataSchemaVersionDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result contains one schema", result.getBody().size(), is(equalTo(1)));
        assertThat("Schema is in the metadata schema repository", metadataSchemaRepository.count(), is(equalTo(1L)));
    }

    @Test
    @DisplayName("HTTP 200: multiple import")
    public void res200_multipleImport() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        MetadataSchemaVersionDTO schema1 = schemaPublicDTO1();
        MetadataSchemaVersionDTO schema2 = schemaPublicDTO2();
        List<MetadataSchemaVersionDTO> reqDTOs = Arrays.asList(schema1, schema2);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTOs);

        // WHEN:
        ResponseEntity<List<MetadataSchemaVersionDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result contains one schema", result.getBody().size(), is(equalTo(2)));
        assertThat("Schema is in the metadata schema repository", metadataSchemaRepository.count(), is(equalTo(2L)));
    }

    @Test
    @DisplayName("HTTP 200: import versions of the same schema")
    public void res200_multipleVersions() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        MetadataSchemaVersionDTO schema1 = schemaPublicDTO1();
        MetadataSchemaVersionDTO schema2 = schemaPublicDTO2();
        schema2.setPreviousVersionUuid(schema1.getVersionUuid());
        schema2.setUuid(schema1.getUuid());
        List<MetadataSchemaVersionDTO> reqDTOs = Arrays.asList(schema1, schema2);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTOs);

        // WHEN:
        ResponseEntity<List<MetadataSchemaVersionDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result contains one schema", result.getBody().size(), is(equalTo(2)));
        assertThat("Schema is in the metadata schema repository", metadataSchemaRepository.count(), is(equalTo(2L)));
    }

    @Test
    @DisplayName("HTTP 200: import with extends")
    public void res200_withExtends() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        MetadataSchemaVersionDTO schema1 = schemaPublicDTO1();
        MetadataSchemaVersionDTO schema2 = schemaPublicDTO2();
        schema2.setExtendsSchemaUuids(Collections.singletonList(schema1.getUuid()));
        List<MetadataSchemaVersionDTO> reqDTOs = Arrays.asList(schema1, schema2);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTOs);

        // WHEN:
        ResponseEntity<List<MetadataSchemaVersionDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result contains one schema", result.getBody().size(), is(equalTo(2)));
        assertThat("Schema is in the metadata schema repository", metadataSchemaRepository.count(), is(equalTo(2L)));
    }

    @Test
    @DisplayName("HTTP 400: missing extends")
    public void res400_missingExtends() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        MetadataSchemaVersionDTO schema1 = schemaPublicDTO1();
        schema1.setExtendsSchemaUuids(Collections.singletonList(UUID.randomUUID().toString()));
        List<MetadataSchemaVersionDTO> reqDTOs = Collections.singletonList(schema1);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTOs);

        // WHEN:
        ResponseEntity<Void> result = client.exchange(request, new ParameterizedTypeReference<>() {});

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat("Nothings changes in metadata schema repository", metadataSchemaRepository.count(), is(equalTo(0L)));
    }

    @Test
    @DisplayName("HTTP 403: no token")
    public void res403_noToken() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        MetadataSchemaVersionDTO schema = schemaPublicDTO1();
        List<MetadataSchemaVersionDTO> reqDTOs = Collections.singletonList(schema);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDTOs);

        // WHEN
        ResponseEntity<Void> result = client.exchange(request, new ParameterizedTypeReference<>() {});

        // THEN:
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
        assertThat("Metadata schema repository stays empty", metadataSchemaRepository.count(), is(equalTo(0L)));
    }

    @Test
    @DisplayName("HTTP 403: non-admin token")
    public void res403_nonAdminToken() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        MetadataSchemaVersionDTO schema1 = schemaPublicDTO1();
        MetadataSchemaVersionDTO schema2 = schemaPublicDTO2();
        List<MetadataSchemaVersionDTO> reqDTOs = Arrays.asList(schema1, schema2);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .body(reqDTOs);

        // WHEN
        ResponseEntity<Void> result = client.exchange(request, new ParameterizedTypeReference<>() {});

        // THEN:
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
        assertThat("Metadata schema repository stays empty", metadataSchemaRepository.count(), is(equalTo(0L)));
    }
}
