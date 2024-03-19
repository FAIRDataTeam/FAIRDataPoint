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
import java.util.List;

import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestGet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@DisplayName("GET /users")
public class List_GET extends WebIntegrationTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    private URI url() {
        return URI.create("/users");
    }

    @Test
    @DisplayName("HTTP 200")
    public void res200() {
        // GIVEN:
        UserAccount userAdmin = userAccountRepository.findByUuid(KnownUUIDs.USER_ADMIN_UUID).get();
        UserAccount userAlbert = userAccountRepository.findByUuid(KnownUUIDs.USER_ALBERT_UUID).get();
        UserAccount userNikola = userAccountRepository.findByUuid(KnownUUIDs.USER_NIKOLA_UUID).get();
        UserAccount userIsaac = userAccountRepository.findByUuid(KnownUUIDs.USER_ISAAC_UUID).get();

        RequestEntity<Void> request = RequestEntity
                .get(url())
                .header(HttpHeaders.AUTHORIZATION, ALBERT_TOKEN)
                .build();
        ParameterizedTypeReference<List<UserDTO>> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<List<UserDTO>> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        List<UserDTO> body = result.getBody();
        assertThat(body.size(), is(equalTo(4)));
        Common.compare(userAdmin, body.get(0));
        Common.compare(userAlbert, body.get(1));
        Common.compare(userNikola, body.get(2));
        Common.compare(userIsaac, body.get(3));
    }

    @Test
    @DisplayName("HTTP 403")
    public void res403() {
        createNoUserForbiddenTestGet(client, url());
    }

}
