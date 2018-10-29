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
package nl.dtls.fairdatapoint.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.dtl.fairmetadata4j.model.Metric;
import nl.dtls.fairdatapoint.api.config.RestApiTestContext;
import nl.dtls.fairdatapoint.utils.ExampleFilesUtils;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * FairMetaDataMetricsServiceImpl class unit tests
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2018-01-19
 * @version 0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {RestApiTestContext.class})
public class FairMetaDataMetricsServiceImplTest {

    @Autowired
    private FairMetaDataMetricsServiceImpl fmMetricsServiceImpl;

    private final ValueFactory valueFactory = SimpleValueFactory.getInstance();

    /**
     * Test getMetrics with null uri, this test is excepted to throw error
     */
    @Test(expected = NullPointerException.class)
    public void nullMetdataUri() throws Exception {
        fmMetricsServiceImpl.getMetrics(null);
    }

    /**
     * This test is excepted to pass
     */
    @Test
    public void validMetdataUri() throws Exception {
        
        Map<String, String> metadataMetrics = new HashMap();
        metadataMetrics.put("https://purl.org/fair-metrics/FM_F1A", "http://example.com/f1a");        
        fmMetricsServiceImpl.setMetadataMetrics(metadataMetrics);

        List<Metric> m = fmMetricsServiceImpl.getMetrics(valueFactory.createIRI(
                ExampleFilesUtils.FDP_URI));
        assertTrue(m.size() > 0);
    }

}
