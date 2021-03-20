package nl.dtls.fairdatapoint.acceptance.openapi;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("GET /v3/api-docs")
public class OpenApiDocs_GET extends WebIntegrationTest {

    private URI url() {
        return URI.create("/v3/api-docs");
    }

    @Test
    @DisplayName("HTTP 200: API Docs")
    public void res200_apiDocs() {
        // GIVEN
        RequestEntity<?> request = RequestEntity
                .get(url())
                .accept(MediaType.APPLICATION_JSON)
                .build();

        // WHEN
        ResponseEntity<String> result = client.exchange(request, new ParameterizedTypeReference<>() {});

        // THEN
        assertThat("Response code is OK", result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat("Response is JSON", result.getHeaders().getContentType(), is(equalTo(MediaType.APPLICATION_JSON)));
    }
}
