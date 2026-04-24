/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.acceptance.search.query.saved;

import org.fairdatateam.fairdatapoint.WebIntegrationTest;
import org.fairdatateam.fairdatapoint.api.dto.search.SearchSavedQueryChangeDTO;
import org.fairdatateam.fairdatapoint.api.dto.search.SearchSavedQueryDTO;
import org.fairdatateam.fairdatapoint.database.mongo.migration.development.search.SearchSavedQueryFixtures;
import org.fairdatateam.fairdatapoint.database.mongo.repository.SearchSavedQueryRepository;
import org.fairdatateam.fairdatapoint.entity.search.SearchSavedQuery;
import org.fairdatateam.fairdatapoint.entity.search.SparqlQueryVariables;
import org.fairdatateam.fairdatapoint.util.KnownUUIDs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /search/query/saved/:uuid")
public class Detail_PUT extends WebIntegrationTest {

    private URI url(String uuid) {
        return URI.create("/search/query/saved/" + uuid);
    }

    @Autowired
    private SearchSavedQueryRepository searchSavedQueryRepository;

    @Autowired
    private SearchSavedQueryFixtures searchSavedQueryFixtures;

    @Test
    @DisplayName("HTTP 403: anonymous user")
    public void res403_anonymousUser() {
        // GIVEN: prepare data
        searchSavedQueryRepository.deleteAll();
        SearchSavedQuery query = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPublic01());

        // AND: prepare request
        RequestEntity<SearchSavedQueryChangeDTO> request = RequestEntity
                .put(url(query.getUuid()))
                .body(
                        SearchSavedQueryChangeDTO.builder()
                                .name("Edited query")
                                .description("")
                                .type(SearchSavedQuery.Type.PUBLIC)
                                .variables(new SparqlQueryVariables("A", "B", "C"))
                                .build()
                );
        ParameterizedTypeReference<?> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<?> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    @DisplayName("HTTP 403: non-owner user")
    public void res403_nonOwnerUser() {
        // GIVEN: prepare data
        searchSavedQueryRepository.deleteAll();
        SearchSavedQuery query = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPublic01());

        // AND: prepare request
        RequestEntity<SearchSavedQueryChangeDTO> request = RequestEntity
                .put(url(query.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .body(
                        SearchSavedQueryChangeDTO.builder()
                                .name("Edited query")
                                .description("")
                                .type(SearchSavedQuery.Type.PUBLIC)
                                .variables(new SparqlQueryVariables("A", "B", "C"))
                                .build()
                );
        ParameterizedTypeReference<?> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<?> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    @DisplayName("HTTP 200: owner")
    public void res200_owner() {
        // GIVEN: prepare data
        searchSavedQueryRepository.deleteAll();
        SearchSavedQuery query = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPublic01());

        // AND: prepare request
        RequestEntity<SearchSavedQueryChangeDTO> request = RequestEntity
                .put(url(query.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .body(
                        SearchSavedQueryChangeDTO.builder()
                                .name("Edited query")
                                .description("")
                                .type(SearchSavedQuery.Type.INTERNAL)
                                .variables(new SparqlQueryVariables("A", "B", "C"))
                                .build()
                );
        ParameterizedTypeReference<SearchSavedQueryDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<SearchSavedQueryDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(result.getBody().getUser().getUuid(), is(equalTo(KnownUUIDs.USER_ALBERT_UUID)));
        assertThat(result.getBody().getType(), is(equalTo(SearchSavedQuery.Type.INTERNAL)));
        assertThat(result.getBody().getName(), is(equalTo("Edited query")));
        assertThat(result.getBody().getVariables().prefixes(), is(equalTo("A")));
        assertThat(result.getBody().getVariables().graphPattern(), is(equalTo("B")));
        assertThat(result.getBody().getVariables().ordering(), is(equalTo("C")));
    }

    @Test
    @DisplayName("HTTP 200: admin")
    public void res200_admin() {
        // GIVEN: prepare data
        searchSavedQueryRepository.deleteAll();
        SearchSavedQuery query = searchSavedQueryRepository.save(searchSavedQueryFixtures.savedQueryPublic01());

        // AND: prepare request
        RequestEntity<SearchSavedQueryChangeDTO> request = RequestEntity
                .put(url(query.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(
                        SearchSavedQueryChangeDTO.builder()
                                .name("Edited query")
                                .description("")
                                .type(SearchSavedQuery.Type.INTERNAL)
                                .variables(new SparqlQueryVariables("A", "B", "C"))
                                .build()
                );
        ParameterizedTypeReference<SearchSavedQueryDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<SearchSavedQueryDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(result.getBody().getUser().getUuid(), is(equalTo(KnownUUIDs.USER_ALBERT_UUID)));
        assertThat(result.getBody().getType(), is(equalTo(SearchSavedQuery.Type.INTERNAL)));
        assertThat(result.getBody().getName(), is(equalTo("Edited query")));
        assertThat(result.getBody().getVariables().prefixes(), is(equalTo("A")));
        assertThat(result.getBody().getVariables().graphPattern(), is(equalTo("B")));
        assertThat(result.getBody().getVariables().ordering(), is(equalTo("C")));
    }
}
