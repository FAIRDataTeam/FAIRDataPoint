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
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaDraftDTO;
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaVersionRepository;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaVersion;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.util.UUID;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.*;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createAdminNotFoundTestGet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("GET /metadata-schemas/:schemaUuid/draft")
public class Draft_GET extends WebIntegrationTest {

    private URI url(UUID uuid) {
        return URI.create(format("/metadata-schemas/%s/draft", uuid));
    }

    @Autowired
    private MetadataSchemaVersionRepository metadataSchemaVersionRepository;

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN: Prepare data
        MetadataSchemaVersion draft = metadataSchemaVersionRepository.findByUuid(Common.SCHEMA_MULTI_DRAFT_DRAFT_UUID).get();

        // AND: Prepare request
        RequestEntity<Void> request = RequestEntity
                .get(url(draft.getSchema().getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .build();
        ParameterizedTypeReference<MetadataSchemaDraftDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<MetadataSchemaDraftDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        Common.compare(draft, result.getBody());
    }

    @Test
    @DisplayName("HTTP 404")
    public void res404() {
        createAdminNotFoundTestGet(client, url(KnownUUIDs.NULL_UUID));
    }

    @Test
    @DisplayName("HTTP 403: User is not authenticated")
    public void res403_notAuthenticated() {
        createNoUserForbiddenTestGet(client, url(UUID.randomUUID()));
    }

    @Test
    @DisplayName("HTTP 403: User is not an admin")
    public void res403_notAdmin() {
        createUserForbiddenTestGet(client, url(UUID.randomUUID()));
    }
}
