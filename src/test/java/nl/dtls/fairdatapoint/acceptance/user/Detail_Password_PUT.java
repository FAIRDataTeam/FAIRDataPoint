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
package nl.dtls.fairdatapoint.acceptance.user;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.user.UserDTO;
import nl.dtls.fairdatapoint.api.dto.user.UserPasswordDTO;
import nl.dtls.fairdatapoint.database.db.repository.UserAccountRepository;
import nl.dtls.fairdatapoint.entity.user.UserAccount;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.UUID;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestPut;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createUserForbiddenTestPut;
import static nl.dtls.fairdatapoint.acceptance.common.NotFoundTest.createAdminNotFoundTestPut;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("PUT /users/:userUuid/password")
public class Detail_Password_PUT extends WebIntegrationTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    private URI url(UUID uuid) {
        return URI.create(format("/users/%s/password", uuid));
    }

    private UserPasswordDTO reqDto() {
        return new UserPasswordDTO("newPassword");
    }

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN:
        UserAccount user = userAccountRepository.findByUuid(KnownUUIDs.USER_ALBERT_UUID).get();
        RequestEntity<UserPasswordDTO> request = RequestEntity
                .put(url(user.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .body(reqDto());
        ParameterizedTypeReference<UserDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<UserDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        Common.compare(user, result.getBody());
    }

    @Test
    @DisplayName("HTTP 403: User is not authenticated")
    public void res403_notAuthenticated() {
        UserAccount user = userAccountRepository.findByUuid(KnownUUIDs.USER_ALBERT_UUID).get();
        createNoUserForbiddenTestPut(client, url(user.getUuid()), reqDto());
    }

    @Test
    @DisplayName("HTTP 403: User is not an admin")
    public void res403_user() {
        UserAccount user = userAccountRepository.findByUuid(KnownUUIDs.USER_ALBERT_UUID).get();
        createUserForbiddenTestPut(client, url(user.getUuid()), reqDto());
    }

    @Test
    @DisplayName("HTTP 404")
    public void res404() {
        createAdminNotFoundTestPut(client, url(KnownUUIDs.NULL_UUID), reqDto());
    }

}
