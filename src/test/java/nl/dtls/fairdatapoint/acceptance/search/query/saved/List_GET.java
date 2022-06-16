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
package nl.dtls.fairdatapoint.acceptance.search.query.saved;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.search.SearchSavedQueryDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.search.SearchSavedQueryFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.SearchSavedQueryRepository;
import nl.dtls.fairdatapoint.entity.search.SearchSavedQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("GET /search/query/saved")
public class List_GET extends WebIntegrationTest {

    private URI url() {
        return URI.create("/search/query/saved");
    }

    @Autowired
    private SearchSavedQueryRepository searchSavedQueryRepository;

    @Autowired
    private SearchSavedQueryFixtures searchSavedQueryFixtures;

    @Test
    @DisplayName("HTTP 200: anonymous user")
    public void res200_anonymousUser() {
        // GIVEN: prepare data
        searchSavedQueryRepository.deleteAll();
        SearchSavedQuery q1 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPublic01());
        SearchSavedQuery q2 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryInternal01());
        SearchSavedQuery q3 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPrivate01());

        // AND: prepare request
        RequestEntity<Void> request = RequestEntity
                .get(url())
                .build();
        ParameterizedTypeReference<List<SearchSavedQueryDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<List<SearchSavedQueryDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        List<SearchSavedQueryDTO> body = result.getBody();
        assertThat(body.size(), is(equalTo(1)));
        assertThat(body.get(0).getUuid(), is(equalTo(q1.getUuid())));
    }

    @Test
    @DisplayName("HTTP 200: non-owner user")
    public void res200_nonOwnerUser() {
        // GIVEN: prepare data
        searchSavedQueryRepository.deleteAll();
        SearchSavedQuery q1 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPublic01());
        SearchSavedQuery q2 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryInternal01());
        SearchSavedQuery q3 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPrivate01());

        // AND: prepare request
        RequestEntity<Void> request = RequestEntity
                .get(url())
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .build();
        ParameterizedTypeReference<List<SearchSavedQueryDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<List<SearchSavedQueryDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        List<SearchSavedQueryDTO> body = result.getBody();
        assertThat(body.size(), is(equalTo(2)));
        assertThat(body.get(0).getUuid(), is(equalTo(q1.getUuid())));
        assertThat(body.get(1).getUuid(), is(equalTo(q2.getUuid())));
    }

    @Test
    @DisplayName("HTTP 200: owner user")
    public void res200_ownerUser() {
        // GIVEN: prepare data
        searchSavedQueryRepository.deleteAll();
        SearchSavedQuery q1 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPublic01());
        SearchSavedQuery q2 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryInternal01());
        SearchSavedQuery q3 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPrivate01());

        // AND: prepare request
        RequestEntity<Void> request = RequestEntity
                .get(url())
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .build();
        ParameterizedTypeReference<List<SearchSavedQueryDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<List<SearchSavedQueryDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        List<SearchSavedQueryDTO> body = result.getBody();
        assertThat(body.size(), is(equalTo(3)));
        assertThat(body.get(0).getUuid(), is(equalTo(q1.getUuid())));
        assertThat(body.get(1).getUuid(), is(equalTo(q2.getUuid())));
        assertThat(body.get(2).getUuid(), is(equalTo(q3.getUuid())));
    }

    @Test
    @DisplayName("HTTP 200: admin")
    public void res200_admin() {
        // GIVEN: prepare data
        searchSavedQueryRepository.deleteAll();
        SearchSavedQuery q1 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPublic01());
        SearchSavedQuery q2 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryInternal01());
        SearchSavedQuery q3 = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPrivate01());

        // AND: prepare request
        RequestEntity<Void> request = RequestEntity
                .get(url())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .build();
        ParameterizedTypeReference<List<SearchSavedQueryDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<List<SearchSavedQueryDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        List<SearchSavedQueryDTO> body = result.getBody();
        assertThat(body.size(), is(equalTo(3)));
        assertThat(body.get(0).getUuid(), is(equalTo(q1.getUuid())));
        assertThat(body.get(1).getUuid(), is(equalTo(q2.getUuid())));
        assertThat(body.get(2).getUuid(), is(equalTo(q3.getUuid())));
    }
}
