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
import nl.dtls.fairdatapoint.database.db.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createUserNotFoundTestGet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("GET /metadata-schemas/:schemaUuid")
public class Detail_GET extends WebIntegrationTest {

    private URI url(UUID uuid) {
        return URI.create(format("/metadata-schemas/%s", uuid));
    }

    @Autowired
    private MetadataSchemaRepository metadataSchemaRepository;

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN:
        MetadataSchema metadataSchema = metadataSchemaRepository.findById(KnownUUIDs.SCHEMA_FDP_UUID).get();
        RequestEntity<Void> request = RequestEntity
                .get(url(KnownUUIDs.SCHEMA_FDP_UUID))
                .build();
        ParameterizedTypeReference<MetadataSchemaDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<MetadataSchemaDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        Common.compare(metadataSchema, result.getBody());
    }

    @Test
    @DisplayName("HTTP 404")
    public void res404() {
        createUserNotFoundTestGet(client, url(KnownUUIDs.NULL_UUID));
    }

}
