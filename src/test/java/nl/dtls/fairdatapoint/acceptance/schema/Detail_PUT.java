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
import nl.dtls.fairdatapoint.api.dto.error.ErrorDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaChangeDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.schema.data.MetadataSchemaFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestPut;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createUserForbiddenTestPut;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createAdminNotFoundTestPut;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /metadata-schemas/:schemaUuid")
public class Detail_PUT extends WebIntegrationTest {

    private URI url(String uuid) {
        return URI.create(format("/metadata-schemas/%s", uuid));
    }

    private MetadataSchemaChangeDTO reqDto(MetadataSchema metadataSchema) {
        return new MetadataSchemaChangeDTO(format("EDITED: %s", metadataSchema.getName()), false, metadataSchema.getDefinition());
    }

    @Autowired
    private MetadataSchemaFixtures metadataSchemaFixtures;

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN: Prepare data
        MetadataSchema metadataSchema = metadataSchemaFixtures.customSchemaEdited();
        MetadataSchemaChangeDTO reqDto = reqDto(metadataSchema);

        // AND: Prepare request
        RequestEntity<MetadataSchemaChangeDTO> request = RequestEntity
                .put(url(metadataSchema.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<MetadataSchemaDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // AND: Prepare DB
        metadataSchemaRepository.save(metadataSchema);

        // WHEN:
        ResponseEntity<MetadataSchemaDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        Common.compare(reqDto, result.getBody());
    }

    @Test
    @DisplayName("HTTP 200: Published")
    public void res200_published() {
        // GIVEN: Prepare data
        MetadataSchema metadataSchema = metadataSchemaFixtures.customSchemaEdited();
        MetadataSchemaChangeDTO reqDto = reqDto(metadataSchema);
        reqDto.setPublished(true);

        // AND: Prepare request
        RequestEntity<MetadataSchemaChangeDTO> request = RequestEntity
                .put(url(metadataSchema.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<MetadataSchemaDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // AND: Prepare DB
        metadataSchemaRepository.save(metadataSchema);

        // WHEN:
        ResponseEntity<MetadataSchemaDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        Common.compare(reqDto, result.getBody());
    }

    @Test
    @DisplayName("HTTP 400: Invalid SHACL Definition")
    public void res400_invalidShacl() {
        // GIVEN: Prepare data
        MetadataSchema metadataSchema = metadataSchemaFixtures.customSchemaEdited();
        metadataSchema.setDefinition(metadataSchema.getDefinition() + "Some random text that will break the validity of SHACL");
        MetadataSchemaChangeDTO reqDto = reqDto(metadataSchema);

        // AND: Prepare request
        RequestEntity<MetadataSchemaChangeDTO> request = RequestEntity
                .put(url(metadataSchema.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<ErrorDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // AND: Prepare DB
        metadataSchemaRepository.save(metadataSchemaFixtures.customSchema());

        // WHEN:
        ResponseEntity<ErrorDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getBody().getMessage(), is("Unable to read SHACL definition"));
    }

    @Test
    @DisplayName("HTTP 200: Edit INTERNAL schema")
    public void res200_internal() {
        // GIVEN:
        MetadataSchema metadataSchema = metadataSchemaFixtures.fdpSchema();
        MetadataSchemaChangeDTO reqDto = reqDto(metadataSchema);
        RequestEntity<MetadataSchemaChangeDTO> request = RequestEntity
                .put(url(metadataSchema.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<MetadataSchemaDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<MetadataSchemaDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        Common.compare(reqDto, result.getBody());
    }

    @Test
    @DisplayName("HTTP 403: User is not authenticated")
    public void res403_notAuthenticated() {
        MetadataSchema metadataSchema = metadataSchemaFixtures.fdpSchema();
        createNoUserForbiddenTestPut(client, url(metadataSchema.getUuid()), reqDto(metadataSchema));
    }

    @Test
    @DisplayName("HTTP 403: User is not an admin")
    public void res403_notAdmin() {
        MetadataSchema metadataSchema = metadataSchemaFixtures.fdpSchema();
        createUserForbiddenTestPut(client, url(metadataSchema.getUuid()), reqDto(metadataSchema));
    }

    @Test
    @DisplayName("HTTP 404")
    public void res404() {
        createAdminNotFoundTestPut(client, url("nonExisting"), reqDto(metadataSchemaFixtures.customSchema()));
    }

}
