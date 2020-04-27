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
package nl.dtls.fairdatapoint.acceptance.dashboard;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardItemDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.user.data.UserFixtures;
import nl.dtls.fairdatapoint.entity.metadata.Metadata;
import nl.dtls.fairdatapoint.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.common.ForbiddenTest.createNoUserForbiddenTestGet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class List_GET extends WebIntegrationTest {

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    private URI url() {
        return URI.create("/dashboard");
    }

    @Autowired
    private MemberService memberService;

    @Autowired
    private UserFixtures userFixtures;

    @Test
    public void res200() {
        // GIVEN:
        RequestEntity<Void> request = RequestEntity
                .get(url())
                .header(HttpHeaders.AUTHORIZATION, NIKOLA_TOKEN)
                .build();
        ParameterizedTypeReference<List<DashboardItemDTO>> responseType = new ParameterizedTypeReference<>() {
        };
        String nikolaUuid = userFixtures.nikola().getUuid();
        memberService.deleteMember(format("%s/catalog/catalog-1", persistentUrl), Metadata.class, nikolaUuid);

        // WHEN:
        ResponseEntity<List<DashboardItemDTO>> result = client.exchange(request, responseType);

        // THEN:
        // status
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));

        // catalog
        assertThat(result.getBody().size(), is(equalTo(1)));
        DashboardItemDTO catalog = result.getBody().get(0);
        assertThat(catalog.getChildren().size(), is(equalTo(1)));
        assertThat(catalog.getMembership().isPresent(), is(false));

        // dataset
        DashboardItemDTO dataset = catalog.getChildren().get(0);
        assertThat(dataset.getUri(), is(equalTo(format("%s/dataset/dataset-1", persistentUrl))));
        assertThat(dataset.getChildren().size(), is(equalTo(1)));
        assertThat(dataset.getMembership().isPresent(), is(true));

        // distribution
        DashboardItemDTO distribution = dataset.getChildren().get(0);
        assertThat(distribution.getUri(), is(equalTo(format("%s/distribution/distribution-1", persistentUrl))));
        assertThat(distribution.getMembership().isPresent(), is(true));
    }

    @Test
    public void res403() {
        createNoUserForbiddenTestGet(client, url());
    }

}
