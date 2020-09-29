package nl.dtls.fairdatapoint.acceptance.search;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.search.SearchQueryDTO;
import nl.dtls.fairdatapoint.api.dto.search.SearchResultDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("POST /search")
public class List_POST extends WebIntegrationTest {

    private URI url() {
        return URI.create("/search");
    }

    private SearchQueryDTO reqDto(String query) {
        return new SearchQueryDTO(query);
    }

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN: Prepare data
        SearchQueryDTO reqDto = reqDto("catalog");

        // AND: Prepare request
        RequestEntity<SearchQueryDTO> request = RequestEntity
                .post(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto);
        ParameterizedTypeReference<List<SearchResultDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<List<SearchResultDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(result.getBody().size(), is(equalTo(1)));
    }

}