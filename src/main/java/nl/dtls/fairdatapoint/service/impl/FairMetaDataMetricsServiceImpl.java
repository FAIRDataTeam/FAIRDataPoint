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

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import nl.dtl.fairmetadata4j.model.Metric;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service layer for fair metric
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2018-01-16
 * @version 0.1
 */
@Service("fairMetaDataMetricsServiceImpl")
public class FairMetaDataMetricsServiceImpl {
    
    private static final Logger LOGGER = LogManager.getLogger(FairMetaDataServiceImpl.class
            .getName());
    
    private final ValueFactory valueFactory = SimpleValueFactory.getInstance();
    
    @Value("${metadataFM.F1.A:nil}")
    private String mdFmF1A;
    
    @Value("${metadataFM.F1.B:nil}")
    private String mdFmF1B;
    
    @Value("${metadataFM.A1.A:nil}")
    private String mdFmA11;
    
    @Value("${metadataFM.A2:nil}")
    private String mdFmA2;
    
    /**
     * This method returns list of fair metrics for metadata
     * 
     * @param metadataURI metadata URI
     * @return List of fair metrics
     */
    public List<Metric> getMetrics(@Nonnull IRI metadataURI) {
        
        Preconditions.checkNotNull(metadataURI, "Metadata URI must not be null.");  
        List<Metric> metrics = new ArrayList();
        
        addMetric(metrics, metadataURI.toString().concat("/mdFmF1A"), 
                "https://raw.githubusercontent.com/FAIRMetrics/Metrics/master/FM_F1A", mdFmF1A);
        
        addMetric(metrics, metadataURI.toString().concat("/mdFmF1B"), 
                "https://raw.githubusercontent.com/FAIRMetrics/Metrics/master/FM_F1B", mdFmF1B);
        
        addMetric(metrics, metadataURI.toString().concat("/mdFmA1_1"), 
                "https://raw.githubusercontent.com/FAIRMetrics/Metrics/master/FM_A1.1", mdFmA11);
        
        addMetric(metrics, metadataURI.toString().concat("/mdFmA2"), 
                "https://raw.githubusercontent.com/FAIRMetrics/Metrics/master/FM_A2", mdFmA2);
        return metrics;
    }
    
    /**
     * We are using this method to reduce the NPath complexity measure. This method add a FM to the
     * list if the metric valueUri URI is provided.
     * 
     * @param metrics   List<Mertic> object
     * @param uri   Metric uri
     * @param typeUri  Metric typeUri uri
     * @param valueUri Metric valueUri uri
     */
    private void addMetric(List<Metric> metrics, String uri, String typeUri, String valueUri) { 
        
        try{
            Preconditions.checkNotNull(uri, "Metadata URI must not be null.");  
            Preconditions.checkState(!uri.isEmpty(), "Metadata URI must not be empty.");  
            Preconditions.checkNotNull(typeUri, "Type URI must not be null.");  
            Preconditions.checkState(!typeUri.isEmpty(), "Type URI must not be empty."); 
            Preconditions.checkNotNull(valueUri, "Value URI must not be null.");  
            Preconditions.checkState(!valueUri.isEmpty(), "Value URI must not be empty.");  
            
            Metric m = new Metric();
            m.setUri(valueFactory.createIRI(uri));
            m.setMetricType(valueFactory.createIRI(typeUri));
            m.setValue(valueFactory.createIRI(valueUri));
            metrics.add(m);
        } catch (Exception e){
            LOGGER.info(e.getMessage());            
        }            
    } 
}
