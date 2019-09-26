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
package nl.dtls.fairdatapoint.service.metadatametrics;

import com.google.common.base.Preconditions;
import nl.dtl.fairmetadata4j.model.Metric;
import nl.dtls.fairdatapoint.service.metadata.MetadataServiceImpl;
import org.apache.commons.codec.digest.DigestUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FairMetadataMetricsServiceImpl implements FairMetadataMetricsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataServiceImpl.class);

    private static final ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();

    private Map<String, String> metadataMetrics;

    /**
     * Setter method to initiate metadata metrics and its value.
     *
     * @param metadataMetrics Set of metrics for metadata
     */
    @Autowired
    @Qualifier("metadataMetrics")
    public void setMetadataMetrics(Map<String, String> metadataMetrics) {
        this.metadataMetrics = metadataMetrics;
    }

    /**
     * This method returns list of fair metrics for metadata
     *
     * @param metadataURI metadata URI
     * @return List of fair metrics
     */
    @Override
    public List<Metric> getMetrics(@Nonnull IRI metadataURI) {
        Preconditions.checkNotNull(metadataURI, "Metadata URI must not be null.");
        List<Metric> metrics = new ArrayList<>();

        metadataMetrics.forEach((metric, metricValue) -> {

            // Create metric uri
            StringBuilder metricUri = new StringBuilder(metadataURI.toString());
            metricUri.append("/metrics/");
            metricUri.append(DigestUtils.md5Hex(metric));

            addMetric(metrics, metricUri.toString(), metric, metricValue);
        });
        return metrics;
    }

    /**
     * We are using this method to reduce the NPath complexity measure. This method add a FM to the
     * list if the metric valueUri URI is provided.
     *
     * @param metrics  List<Mertic> object
     * @param uri      Metric uri
     * @param typeUri  Metric typeUri uri
     * @param valueUri Metric valueUri uri
     */
    private void addMetric(List<Metric> metrics, String uri, String typeUri, String valueUri) {
        try {
            Preconditions.checkNotNull(uri, "Metadata URI must not be null.");
            Preconditions.checkState(!uri.isEmpty(), "Metadata URI must not be empty.");
            Preconditions.checkNotNull(typeUri, "Type URI must not be null.");
            Preconditions.checkState(!typeUri.isEmpty(), "Type URI must not be empty.");
            Preconditions.checkNotNull(valueUri, "Value URI must not be null.");
            Preconditions.checkState(!valueUri.isEmpty(), "Value URI must not be empty.");

            Metric m = new Metric();
            m.setUri(VALUEFACTORY.createIRI(uri));
            m.setMetricType(VALUEFACTORY.createIRI(typeUri));
            m.setValue(VALUEFACTORY.createIRI(valueUri));
            metrics.add(m);
        } catch (Exception e) {
            LOGGER.error("Error adding metrics {}", e.getMessage());
        }
    }
}
