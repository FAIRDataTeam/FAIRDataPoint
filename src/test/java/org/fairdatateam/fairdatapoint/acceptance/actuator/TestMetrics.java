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
package org.fairdatateam.fairdatapoint.acceptance.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureMockMvc
class TestMetrics {

    final String metricName = "http.server.requests";
    final UriComponentsBuilder metricsUriBuilder = UriComponentsBuilder.fromPath("/actuator/metrics");
    final String tagName = "http.url";
    final String uriVisited = "/meta";

    // https://docs.spring.io/spring-framework/reference/testing/mockmvc/assertj.html
    @Autowired
    private MockMvcTester mockMvc;

    @Test
    // test requests are automatically authenticated due to existing security principal (from AuthHelper.java ?)
    @WithAnonymousUser
    void onlyAuthenticatedUsersCanAccessMetrics() {
        MvcTestResult testResult = mockMvc.get()
                .uri(metricsUriBuilder.build().toUri())
                .exchange();
        // todo: should actually be 401 UNAUTHORIZED (see #704)
        assertThat(testResult).hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    void onlyExposesHttpServerRequestsMetric() {
        // visit some endpoint to initialize the "http.server.requests" metric
        mockMvc.get().uri(uriVisited).exchange();
        // get metrics list
        MvcTestResult testResult = mockMvc.get()
                .uri(metricsUriBuilder.build().toUri())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        // results should only include the "http.server.requests" (this only appears if endpoints have been visited)
        assertThat(testResult)
                .hasStatusOk()
                .hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .bodyJson()
                .extractingPath("$.names")
                .asArray()
                .containsExactly(metricName);
    }

    @Test
    void listsVisitedEndpointUris() {
        // visit some endpoint to initialize the "http.server.requests" metric
        mockMvc.get().uri(uriVisited).exchange();
        // get the available metrics for "http.server.requests"
        MvcTestResult testResult = mockMvc.get()
                .uri(metricsUriBuilder.pathSegment(metricName).build().toUri())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        // the visited endpoint should be included in the available tag values for "http.url"
        assertThat(testResult).hasStatusOk().hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON);
        assertThat(testResult).bodyJson().extractingPath("$.name").isEqualTo(metricName);
        assertThat(testResult).bodyJson().extractingPath("$.availableTags").asArray()
                .filteredOn("tag", tagName)
                .flatExtracting("values")
                .contains(uriVisited);
    }

    @Test
    void endpointVisitsAreCounted() {
        // visit some endpoint a number of times
        final int visitCount = 3;
        for (int i = 0; i < visitCount; i++) {
            mockMvc.get().uri(uriVisited).exchange();
        }
        // get the available metrics for this endpoint
        URI endpointMetricsUri = metricsUriBuilder
                .pathSegment(metricName)
                .queryParam("tag", "%s:%s".formatted(tagName, uriVisited))
                .build().toUri();
        MvcTestResult testResult = mockMvc.get()
                .uri(endpointMetricsUri)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
        // the COUNT statistic value for this endpoint should match the actual number of visits
        assertThat(testResult).hasStatusOk().hasContentTypeCompatibleWith(MediaType.APPLICATION_JSON);
        assertThat(testResult).bodyJson().extractingPath("$.name").isEqualTo(metricName);
        assertThat(testResult).bodyJson().extractingPath("$.measurements").asArray()
                .filteredOn("statistic", "COUNT")
                .extracting("value")
                .contains((double) visitCount);
    }
}
