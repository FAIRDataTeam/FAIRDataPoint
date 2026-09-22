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
package org.fairdatateam.fairdatapoint.acceptance.reset;

import org.fairdatateam.fairdatapoint.WebIntegrationTest;
import org.fairdatateam.fairdatapoint.reset.ResetDTO;
import org.fairdatateam.fairdatapoint.security.membership.MembershipDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

// Resetting memberships now goes through MembershipRepository#deleteAllInBatch followed by the
// factory rows being re-inserted in the same transaction (see ResetService#clearMemberships); this
// exercises that sequence twice in a row to make sure it is idempotent.
@DisplayName("POST /reset")
public class Reset_POST extends WebIntegrationTest {

    private static final int EXPECTED_MEMBERSHIP_COUNT = 2;

    private URI resetUrl() {
        return URI.create("/reset");
    }

    private URI membershipsUrl() {
        return URI.create("/memberships");
    }

    // metadata resets also clear and restore memberships (see
    // ResetService#resetToFactoryDefaults), and unlike a users reset, it leaves the admin fixture
    // user - the one this test authenticates as - in place
    private ResetDTO metadataResetRequest() {
        return new ResetDTO(false, true, false, false);
    }

    @Test
    @DisplayName("Resetting twice in a row leaves exactly the two factory memberships in place")
    public void res204_resetTwiceRestoresFactoryMemberships() {
        // GIVEN: a reset request
        final RequestEntity<ResetDTO> request = RequestEntity
                .post(resetUrl())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(metadataResetRequest());

        // WHEN: it runs once
        final ResponseEntity<Void> firstResult = client.exchange(request, Void.class);

        // THEN: it succeeds
        assertThat(firstResult.getStatusCode().is2xxSuccessful(), is(equalTo(true)));

        // WHEN: it runs a second time in a row
        final ResponseEntity<Void> secondResult = client.exchange(request, Void.class);

        // THEN: it succeeds again
        assertThat(secondResult.getStatusCode().is2xxSuccessful(), is(equalTo(true)));

        // AND: exactly the two factory memberships are present, not duplicates or leftovers
        final RequestEntity<Void> membershipsRequest = RequestEntity
                .get(membershipsUrl())
                .header(HttpHeaders.AUTHORIZATION, ADMIN_TOKEN)
                .build();
        final ParameterizedTypeReference<List<MembershipDTO>> responseType = new ParameterizedTypeReference<>() {
        };
        final ResponseEntity<List<MembershipDTO>> membershipsResult =
                client.exchange(membershipsRequest, responseType);
        assertThat(membershipsResult.getStatusCode(), is(equalTo(HttpStatus.OK)));
        assertThat(membershipsResult.getBody().size(), is(equalTo(EXPECTED_MEMBERSHIP_COUNT)));
    }

}
