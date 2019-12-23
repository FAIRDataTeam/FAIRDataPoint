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

import nl.dtl.fairmetadata4j.model.Metric;
import nl.dtls.fairdatapoint.BaseIntegrationTest;
import nl.dtls.fairdatapoint.utils.MetadataFixtureFilesHelper;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

// TODO What is purpose of this class
public class FairMetadataMetricsServiceImplTest extends BaseIntegrationTest {

    private final ValueFactory valueFactory = SimpleValueFactory.getInstance();
    @Autowired
    private FairMetadataMetricsServiceImpl fmMetricsServiceImpl;

    /**
     * Test getMetrics with null uri, this test is excepted to throw error
     */
    @Test
    public void nullMetadataUri() {
        assertThrows(NullPointerException.class, () -> {
            fmMetricsServiceImpl.getMetrics(null);
        });
    }

    /**
     * This test is excepted to pass
     */
    @Test
    public void validMetadataUri() {
//        Map<String, String> metadataMetrics = new HashMap<>();
//        metadataMetrics.put("https://purl.org/fair-metrics/FM_F1A", "http://example.com/f1a");
//        fmMetricsServiceImpl.setMetadataMetrics(metadataMetrics);

        List<Metric> m = fmMetricsServiceImpl.getMetrics(valueFactory.createIRI(
                MetadataFixtureFilesHelper.REPOSITORY_URI));
        assertTrue(m.size() > 0);
    }

}
