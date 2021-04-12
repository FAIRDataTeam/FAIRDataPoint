package nl.dtls.fairdatapoint.acceptance.metadata.dataset;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createUserNotFoundTestGetRDF;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("GET /dataset/:datasetId/page/distribution")
public class Detail_Page_GET extends WebIntegrationTest {

    private URI url(String id) {
        return URI.create(format("/dataset/%s/page/distribution", id));
    }

    @Test
    @DisplayName("HTTP 200: Published")
    public void res200() {
        // GIVEN:
        RequestEntity<Void> request = RequestEntity
                .get(url("dataset-1"))
                .header(HttpHeaders.ACCEPT, "text/turtle")
                .build();
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<String> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
    }

    @Test
    @DisplayName("HTTP 200: Draft (User is logged in)")
    public void res200_draft() {
        // GIVEN:
        RequestEntity<Void> request = RequestEntity
                .get(url("dataset-2"))
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .header(HttpHeaders.ACCEPT, "text/turtle")
                .build();
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<String> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
    }

    @Test
    @DisplayName("HTTP 403: Draft (User is not logged in)")
    public void res403_draft() {
        // GIVEN:
        RequestEntity<Void> request = RequestEntity
                .get(url("dataset-2"))
                .header(HttpHeaders.ACCEPT, "text/turtle")
                .build();
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<String> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
        assertThat(result.getBody(), is(equalTo("You are not allow to view this record in state DRAFT")));
    }

    @Test
    @DisplayName("HTTP 404")
    public void res404() {
        createUserNotFoundTestGetRDF(client, url("nonExisting"));
    }
}
