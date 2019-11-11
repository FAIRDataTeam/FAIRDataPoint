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
package nl.dtls.fairdatapoint.acceptance.metadata.dataset.member;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.error.ErrorDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberCreateDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.database.mongo.fixtures.MembershipFixtures;
import nl.dtls.fairdatapoint.database.mongo.fixtures.UserFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.Common.createNotFoundTestPut;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /fdp/catalog/:catalogId/members/:userUuid")
public class Detail_PUT extends WebIntegrationTest {

    @Autowired
    private UserFixtures userFixtures;

    @Autowired
    private MembershipFixtures membershipFixtures;

    private URI url(String catalogId, String userUuid) {
        return URI.create(format("/fdp/catalog/%s/members/%s", catalogId, userUuid));
    }

    private MemberCreateDTO reqDto() {
        return new MemberCreateDTO(membershipFixtures.owner().getUuid());
    }

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN:
        RequestEntity<MemberCreateDTO> request = RequestEntity
                .put(url("catalog-1", userFixtures.nikola().getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .body(reqDto());
        ParameterizedTypeReference<MemberDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<MemberDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
    }

    @Test
    @DisplayName("HTTP 400: User doesn't exist")
    public void res400_nonExistingUser() {
        // GIVEN:
        RequestEntity<MemberCreateDTO> request = RequestEntity
                .put(url("catalog-1", "nonExisting"))
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .body(reqDto());
        ParameterizedTypeReference<ErrorDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<ErrorDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getBody().getMessage(), is(equalTo("User doesn't exist")));
    }

    @Test
    @DisplayName("HTTP 403: Current user has to be an owner of the resource")
    public void res403() {
        // GIVEN:
        RequestEntity<MemberCreateDTO> request = RequestEntity
                .put(url("catalog-1", userFixtures.nikola().getUuid()))
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .body(reqDto());
        ParameterizedTypeReference<ErrorDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<ErrorDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    @DisplayName("HTTP 404: non-existing catalog")
    public void res404_nonExistingCatalog() {
        createNotFoundTestPut(client, url("nonExisting", userFixtures.albert().getUuid()), reqDto());
    }

}
