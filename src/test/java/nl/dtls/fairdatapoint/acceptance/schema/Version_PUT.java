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
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaUpdateDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaVersionDTO;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaVersionRepository;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaState;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaType;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaVersion;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestPut;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createUserForbiddenTestPut;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createAdminNotFoundTestPut;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /metadata-schemas/:schemaUuid/versions/:version")
public class Version_PUT extends WebIntegrationTest {

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private MetadataSchemaVersionRepository metadataSchemaVersionRepository;

    private URI url(UUID uuid, String version) {
        return URI.create(format("/metadata-schemas/%s/versions/%s", uuid, version));
    }

    private MetadataSchemaUpdateDTO reqDto() {
        return new MetadataSchemaUpdateDTO(
                "Fixed name",
                "Fixed description",
                true
        );
    }

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN: Prepare data
        MetadataSchemaVersion schema = createCustomSchema();
        MetadataSchemaUpdateDTO reqDto = reqDto();

        // AND: Prepare request
        RequestEntity<MetadataSchemaUpdateDTO> request = RequestEntity
                .put(url(schema.getSchema().getUuid(), schema.getVersion()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<MetadataSchemaVersionDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<MetadataSchemaVersionDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(result.getBody().getName(), is(equalTo(reqDto.getName())));
        assertThat(result.getBody().getDescription(), is(equalTo(reqDto.getDescription())));
        assertThat(result.getBody().isPublished(), is(equalTo(reqDto.isPublished())));
        assertThat(result.getBody().getDefinition(), is(equalTo(schema.getDefinition())));
        assertThat(result.getBody().isAbstractSchema(), is(equalTo(schema.isAbstractSchema())));
    }

    private MetadataSchemaVersion createCustomSchema() {
        final MetadataSchema schema = metadataSchemaRepository.saveAndFlush(
                MetadataSchema.builder()
                        .uuid(UUID.randomUUID())
                        .build()
        );

        return metadataSchemaVersionRepository.saveAndFlush(
                MetadataSchemaVersion.builder()
                        .uuid(UUID.randomUUID())
                        .schema(schema)
                        .version("0.1.0")
                        .name("Custom schema")
                        .state(MetadataSchemaState.DRAFT)
                        .description("")
                        .definition("")
                        .targetClasses(List.of())
                        .type(MetadataSchemaType.CUSTOM)
                        .abstractSchema(false)
                        .published(false)
                        .build()
        );
    }


    @Test
    @DisplayName("HTTP 404")
    public void res404() {
        createAdminNotFoundTestPut(client, url(KnownUUIDs.NULL_UUID, "1.0.0"), reqDto());
    }

    @Test
    @DisplayName("HTTP 403: User is not authenticated")
    public void res403_notAuthenticated() {
        createNoUserForbiddenTestPut(client, url(UUID.randomUUID(), "1.0.0"), reqDto());
    }

    @Test
    @DisplayName("HTTP 403: User is not an admin")
    public void res403_notAdmin() {
        createUserForbiddenTestPut(client, url(UUID.randomUUID(), "1.0.0"), reqDto());
    }
}
