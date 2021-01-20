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