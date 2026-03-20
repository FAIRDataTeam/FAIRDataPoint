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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.metadata.metric;

import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.database.mongo.repository.SettingsRepository;
import nl.dtls.fairdatapoint.entity.metadata.Metric;
import nl.dtls.fairdatapoint.service.settings.SettingsCache;
import org.eclipse.rdf4j.model.IRI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class MetricsMetadataServiceTest extends BaseIntegrationTest {

    @Autowired
    private MetricsMetadataService metricsMetadataService;

    @Test
    public void generateMetricsWorks() {
        // GIVEN:
        IRI entityUri = i("http://localhost");

        // WHEN:
        List<Metric> result = metricsMetadataService.generateMetrics(entityUri);

        // THEN:
        assertThat(result.size(), is(equalTo(2)));
        assertThat(result.get(0).getValue(), is(equalTo(i("https://www.ietf.org/rfc/rfc3986.txt"))));
        assertThat(result.get(0).getMetricType(), is(equalTo(i("https://www.ietf.org/rfc/rfc3986.txt"))));
        assertThat(result.get(1).getValue(), is(equalTo(i("https://www.wikidata.org/wiki/Q8777"))));
        assertThat(result.get(1).getMetricType(), is(equalTo(i("https://www.wikidata.org/wiki/Q8777"))));
    }

}
