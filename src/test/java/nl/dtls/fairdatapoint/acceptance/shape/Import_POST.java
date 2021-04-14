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
import nl.dtls.fairdatapoint.api.dto.shape.ShapeRemoteDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.shape.data.ShapeFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.ShapeRepository;
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
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@DisplayName("GET /shapes/import")
public class Import_POST extends WebIntegrationTest {

    @Autowired
    private ShapeRepository shapeRepository;

    @Autowired
    private ShapeFixtures shapeFixtures;

    private final ParameterizedTypeReference<List<ShapeDTO>> responseType =
            new ParameterizedTypeReference<>() {
            };

    private URI url() {
        return URI.create("/shapes/import");
    }

    private ShapeRemoteDTO shapeRemoteDTO1() {
        return new ShapeRemoteDTO(
                "http://example.com",
                UUID.randomUUID().toString(),
                shapeFixtures.customShape().getName(),
                shapeFixtures.customShape().getDefinition()
        );
    }

    private ShapeRemoteDTO shapeRemoteDTO2() {
        return new ShapeRemoteDTO(
                "http://example.com",
                UUID.randomUUID().toString(),
                shapeFixtures.customShapeEdited().getName(),
                shapeFixtures.customShapeEdited().getDefinition()
        );
    }

    @Test
    @DisplayName("HTTP 200: empty import")
    public void res200_emptyImport() {
        // GIVEN: prepare data
        shapeRepository.deleteAll();
        List<ShapeRemoteDTO> reqDTOs = Collections.emptyList();

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTOs);

        // WHEN:
        ResponseEntity<List<ShapeDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result is an empty list", result.getBody().size(), is(equalTo(0)));
        assertThat("Shape repository is empty", shapeRepository.count(), is(equalTo(0L)));
    }

    @Test
    @DisplayName("HTTP 200: single import")
    public void res200_singleImport() {
        // GIVEN: prepare data
        shapeRepository.deleteAll();
        ShapeRemoteDTO shape = shapeRemoteDTO1();
        List<ShapeRemoteDTO> reqDTOs = Collections.singletonList(shape);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTOs);

        // WHEN:
        ResponseEntity<List<ShapeDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result contains one shape", result.getBody().size(), is(equalTo(1)));
        assertThat("Shape is in the shape repository", shapeRepository.count(), is(equalTo(1L)));
    }

    @Test
    @DisplayName("HTTP 200: multiple import")
    public void res200_multipleImport() {
        // GIVEN: prepare data
        shapeRepository.deleteAll();
        ShapeRemoteDTO shape1 = shapeRemoteDTO1();
        ShapeRemoteDTO shape2 = shapeRemoteDTO2();
        List<ShapeRemoteDTO> reqDTOs = Arrays.asList(shape1, shape2);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDTOs);

        // WHEN:
        ResponseEntity<List<ShapeDTO>> result = client.exchange(request, responseType);

        // THEN
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response body is not null", result.getBody(), is(notNullValue()));
        assertThat("Result contains one shape", result.getBody().size(), is(equalTo(2)));
        assertThat("Shape is in the shape repository", shapeRepository.count(), is(equalTo(2L)));
    }

    @Test
    @DisplayName("HTTP 403: no token")
    public void res403_noToken() {
        // GIVEN: prepare data
        shapeRepository.deleteAll();
        ShapeRemoteDTO shape = shapeRemoteDTO1();
        List<ShapeRemoteDTO> reqDTOs = Collections.singletonList(shape);

        // AND: prepare request
        RequestEntity<?> request = RequestEntity
                .post(url())
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDTOs);

        // WHEN
        ResponseEntity<Void> result = client.exchange(request, new ParameterizedTypeReference<>() {});

        // THEN:
        assertThat("Correct response code is received", result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
        assertThat("Shape repository stays empty", shapeRepository.count(), is(equalTo(0L)));
    }

    @Test
    @DisplayName("HTTP 403: non-admin token")
    public void res403_nonAdminToken() {
        // GIVEN: prepare data
        shapeRepository.deleteAll();
        ShapeRemoteDTO shape1 = shapeRemoteDTO1();
        ShapeRemoteDTO shape2 = shapeRemoteDTO2();
        List<ShapeRemoteDTO> reqDTOs = Arrays.asList(shape1, shape2);

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
        assertThat("Shape repository stays empty", shapeRepository.count(), is(equalTo(0L)));
    }
}
