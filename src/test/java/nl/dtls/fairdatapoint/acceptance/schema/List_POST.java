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
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaChangeDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDraftDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.schema.data.MetadataSchemaFixtures;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.util.Collections;

import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestPost;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createUserForbiddenTestPost;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("POST /metadata-schemas")
public class List_POST extends WebIntegrationTest {

    @Autowired
    private MetadataSchemaFixtures metadataSchemaFixtures;

    private URI url() {
        return URI.create("/metadata-schemas");
    }

    private MetadataSchemaChangeDTO reqDto(MetadataSchema metadataSchema) {
        return MetadataSchemaChangeDTO.builder()
                .name(metadataSchema.getName())
                .description("")
                .abstractSchema(false)
                .definition(metadataSchema.getDefinition())
                .extendsSchemaUuids(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN: Prepare data
        MetadataSchema metadataSchema = metadataSchemaFixtures.customSchema();
        MetadataSchemaChangeDTO reqDto = reqDto(metadataSchema);

        // AND: Prepare request
        RequestEntity<MetadataSchemaChangeDTO> request = RequestEntity
                .post(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<MetadataSchemaDraftDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<MetadataSchemaDraftDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        Common.compare(reqDto, result.getBody());
    }

    @Test
    @DisplayName("HTTP 403: User is not authenticated")
    public void res403_notAuthenticated() {
        createNoUserForbiddenTestPost(client, url(), reqDto(metadataSchemaFixtures.customSchema()));
    }

    @Test
    @DisplayName("HTTP 403: User is not an admin")
    public void res403_notAdmin() {
        createUserForbiddenTestPost(client, url(), reqDto(metadataSchemaFixtures.customSchema()));
    }

}
