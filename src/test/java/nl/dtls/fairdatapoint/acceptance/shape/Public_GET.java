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
package nl.dtls.fairdatapoint.acceptance.shape;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.shape.ShapeDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.shape.data.ShapeFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.ShapeRepository;
import nl.dtls.fairdatapoint.entity.shape.Shape;
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

@DisplayName("GET /shapes/public")
public class Public_GET extends WebIntegrationTest {

    @Autowired
    private ShapeRepository shapeRepository;

    @Autowired
    private ShapeFixtures shapeFixtures;

    private final ParameterizedTypeReference<List<ShapeDTO>> responseType =
            new ParameterizedTypeReference<>() {
            };

    private URI url() {
        return URI.create("/shapes/public");
    }

    private Shape makeShape(Boolean published) {
        return new Shape().toBuilder()
                .uuid(UUID.randomUUID().toString())
                .name(shapeFixtures.customShape().getName())
                .definition(shapeFixtures.customShape().getDefinition())
                .published(published)
                .targetClasses(Set.of())
                .build();
    }

    @Test
    @DisplayName("HTTP 200: no published")
    public void res200_noPublished() {
        // GIVEN: prepare data
        shapeRepository.deleteAll();
        Shape shape = makeShape(false);
        shapeRepository.insert(shape);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN:
        ResponseEntity<List<ShapeDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result is an empty list", result.getBody().size(), is(equalTo(0)));
    }

    @Test
    @DisplayName("HTTP 200: published")
    public void res200_published() {
        // GIVEN: prepare data
        shapeRepository.deleteAll();
        Shape shapeNotPublished = makeShape(false);
        Shape shapePublished = makeShape(true);
        shapeRepository.insert(shapeNotPublished);
        shapeRepository.insert(shapePublished);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN:
        ResponseEntity<List<ShapeDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result is an empty list", result.getBody().size(), is(equalTo(1)));
        assertThat("UUID matches the published shape", result.getBody().get(0).getUuid(), is(equalTo(shapePublished.getUuid())));
    }
}
