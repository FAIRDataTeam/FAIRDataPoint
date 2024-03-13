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
import nl.dtls.fairdatapoint.service.member.MemberMapper;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createUserNotFoundTestPut;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /dataset/:datasetId/members/:userUuid")
public class Detail_PUT extends WebIntegrationTest {
    // TODO: fixtures

    @Autowired
    private MemberMapper memberMapper;

    private URI url(String datasetId, String userUuid) {
        return URI.create(format("/dataset/%s/members/%s", datasetId, userUuid));
    }

    private MemberCreateDTO reqDto() {
        return new MemberCreateDTO(null);
    }

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        create_res200(ALBERT_TOKEN);
    }

    @Test
    @DisplayName("HTTP 200: User is an admin")
    public void res200_admin() {
        create_res200(ADMIN_TOKEN);
    }

    private void create_res200(String token) {
        // GIVEN:
        RequestEntity<MemberCreateDTO> request = RequestEntity
                .put(url("dataset-1", KnownUUIDs.USER_NIKOLA_UUID.toString()))
                .header(HttpHeaders.AUTHORIZATION, token)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto());
        ParameterizedTypeReference<MemberDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // AND: prepare expectation
        MemberDTO expDto = memberMapper.toDTO(null, null);

        // WHEN:
        ResponseEntity<MemberDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(result.getBody(), is(equalTo(expDto)));
    }

    @Test
    @DisplayName("HTTP 400: User doesn't exist")
    public void res400_nonExistingUser() {
        // GIVEN:
        RequestEntity<MemberCreateDTO> request = RequestEntity
                .put(url("dataset-1", "nonExisting"))
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
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
                .put(url("dataset-2", KnownUUIDs.USER_NIKOLA_UUID.toString()))
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .accept(MediaType.APPLICATION_JSON)
                .body(reqDto());
        ParameterizedTypeReference<ErrorDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<ErrorDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
    }

    @Test
    @DisplayName("HTTP 404: non-existing dataset")
    public void res404_nonExistingCatalog() {
        createUserNotFoundTestPut(client, url("nonExisting", KnownUUIDs.USER_ALBERT_UUID.toString()), reqDto());
    }

}
