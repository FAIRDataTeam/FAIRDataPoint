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
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaRemoteDTO;
import nl.dtls.fairdatapoint.api.dto.schema.MetadataSchemaVersionDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.schema.data.MetadataSchemaFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.MetadataSchemaRepository;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchema;
import nl.dtls.fairdatapoint.entity.schema.SemVer;
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
    private MetadataSchemaRepository metadataSchemaRepository;

    @Autowired
    private MetadataSchemaFixtures metadataSchemaFixtures;

    private final ParameterizedTypeReference<List<MetadataSchemaVersionDTO>> responseType =
            new ParameterizedTypeReference<>() {
            };

    private URI url() {
        return URI.create("/metadata-schemas/public");
    }

    private MetadataSchema makeSchema(Boolean published) {
        SemVer version = new SemVer("1.0.0");
        return MetadataSchema.builder()
                .uuid(UUID.randomUUID().toString())
                .version(version)
                .versionString(version.toString())
                .versionUuid(UUID.randomUUID().toString())
                .name(metadataSchemaFixtures.customSchema().getName())
                .definition(metadataSchemaFixtures.customSchema().getDefinition())
                .published(published)
                .previousVersionUuid(null)
                .targetClasses(Set.of())
                .build();
    }

    @Test
    @DisplayName("HTTP 200: no published")
    public void res200_noPublished() {
        // GIVEN: prepare data
        metadataSchemaRepository.deleteAll();
        MetadataSchema metadataSchema = makeSchema(false);
        metadataSchemaRepository.insert(metadataSchema);

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
        metadataSchemaRepository.deleteAll();
        MetadataSchema metadataSchemaNotPublished = makeSchema(false);
        MetadataSchema metadataSchemaPublished = makeSchema(true);
        metadataSchemaRepository.insert(metadataSchemaNotPublished);
        metadataSchemaRepository.insert(metadataSchemaPublished);

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
        assertThat("Result is an empty list", result.getBody().size(), is(equalTo(1)));
        assertThat("UUID matches the published schema", result.getBody().get(0).getUuid(), is(equalTo(metadataSchemaPublished.getUuid())));
        assertThat("UUID matches the published schema", result.getBody().get(0).getVersionUuid(), is(equalTo(metadataSchemaPublished.getVersionUuid())));
    }
}
