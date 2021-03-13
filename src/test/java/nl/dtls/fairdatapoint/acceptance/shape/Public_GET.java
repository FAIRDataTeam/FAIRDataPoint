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
