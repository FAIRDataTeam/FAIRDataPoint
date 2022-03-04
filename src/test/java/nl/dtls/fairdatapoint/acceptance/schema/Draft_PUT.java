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
import nl.dtls.fairdatapoint.api.dto.resource.ResourceDefinitionChangeDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaChangeDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDraftDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaUpdateDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.schema.data.MetadataSchemaFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaDraftRepository;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaDraft;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.*;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createUserForbiddenTestGet;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createAdminNotFoundTestGet;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createAdminNotFoundTestPut;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /metadata-schemas/:schemaUuid/draft")
public class Draft_PUT extends WebIntegrationTest {

    private URI url(String uuid) {
        return URI.create(format("/metadata-schemas/%s/draft", uuid));
    }

    private MetadataSchemaChangeDTO reqDto() {
        return new MetadataSchemaChangeDTO(
                "Updated schema draft",
                "Description of changes",
                false,
                "# no SHACL",
                Collections.emptyList()
        );
    }

    @Autowired
    private MetadataSchemaFixtures metadataSchemaFixtures;

    @Autowired
    private MetadataSchemaDraftRepository metadataSchemaDraftRepository;

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN: Prepare data
        MetadataSchemaDraft draft = metadataSchemaFixtures.customSchemaDraft1();
        metadataSchemaDraftRepository.deleteAll();
        metadataSchemaDraftRepository.save(draft);
        MetadataSchemaChangeDTO reqDto = reqDto();

        // AND: Prepare request
        RequestEntity<MetadataSchemaChangeDTO> request = RequestEntity
                .put(url(draft.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<MetadataSchemaDraftDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<MetadataSchemaDraftDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(result.getBody().getName(), is(equalTo(reqDto.getName())));
        assertThat(result.getBody().getDescription(), is(equalTo(reqDto.getDescription())));
        assertThat(result.getBody().getDefinition(), is(equalTo(reqDto.getDefinition())));
        assertThat(result.getBody().isAbstractSchema(), is(equalTo(reqDto.isAbstractSchema())));
        assertThat(result.getBody().getExtendsSchemaUuids(), is(equalTo(reqDto.getExtendsSchemaUuids())));
        // TODO: test with extends that does not exist (and that does)
        // TODO: test with extends cycle
    }

    @Test
    @DisplayName("HTTP 404")
    public void res404() {
        createAdminNotFoundTestPut(client, url("nonExisting"), reqDto());
    }

    @Test
    @DisplayName("HTTP 403: User is not authenticated")
    public void res403_notAuthenticated() {
        createNoUserForbiddenTestPut(client, url(UUID.randomUUID().toString()), reqDto());
    }

    @Test
    @DisplayName("HTTP 403: User is not an admin")
    public void res403_notAdmin() {
        createUserForbiddenTestPut(client, url(UUID.randomUUID().toString()), reqDto());
    }
}
