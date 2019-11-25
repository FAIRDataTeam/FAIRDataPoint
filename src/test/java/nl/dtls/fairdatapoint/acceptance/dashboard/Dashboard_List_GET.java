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

import nl.dtl.fairmetadata4j.model.CatalogMetadata;
import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardCatalogDTO;
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardDatasetDTO;
import nl.dtls.fairdatapoint.api.dto.dashboard.DashboardDistributionDTO;
import nl.dtls.fairdatapoint.database.mongo.migration.development.user.data.UserFixtures;
import nl.dtls.fairdatapoint.service.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.List;

import static nl.dtls.fairdatapoint.acceptance.Common.createNoUserForbiddenTestGet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class Dashboard_List_GET extends WebIntegrationTest {

    private URI url() {
        return URI.create("/fdp/dashboard");
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
        ParameterizedTypeReference<List<DashboardCatalogDTO>> responseType = new ParameterizedTypeReference<>() {
        };
        String nikolaUuid = userFixtures.nikola().getUuid();
        memberService.deleteMember("catalog-1", CatalogMetadata.class, nikolaUuid);


        // WHEN:
        ResponseEntity<List<DashboardCatalogDTO>> result = client.exchange(request, responseType);

        // THEN:
        // status
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));

        // catalog
        assertThat(result.getBody().size(), is(equalTo(1)));
        DashboardCatalogDTO catalog = result.getBody().get(0);
        assertThat(catalog.getDatasets().size(), is(equalTo(1)));
        assertThat(catalog.getMembership().isPresent(), is(false));

        // dataset
        DashboardDatasetDTO dataset = catalog.getDatasets().get(0);
        assertThat(dataset.getIdentifier(), is(equalTo("dataset-1")));
        assertThat(dataset.getDistributions().size(), is(equalTo(1)));
        assertThat(dataset.getMembership().isPresent(), is(true));

        // distribution
        DashboardDistributionDTO distribution = dataset.getDistributions().get(0);
        assertThat(distribution.getIdentifier(), is(equalTo("distribution-1")));
        assertThat(distribution.getMembership().isPresent(), is(true));
    }

    @Test
    public void res403() {
        createNoUserForbiddenTestGet(client, url());
    }

}
