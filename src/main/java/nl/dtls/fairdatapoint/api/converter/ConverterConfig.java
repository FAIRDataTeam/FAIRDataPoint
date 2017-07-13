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
package nl.dtls.fairdatapoint.api.converter;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2016-09-19
 * @version 0.1
 */
@Configuration
public class ConverterConfig {
    @Bean
    public FdpMetadataConverter turtleFdpMetadata() {
        return new FdpMetadataConverter(RDFFormat.TURTLE);
    }
    
    @Bean
    public FdpMetadataConverter jsonldFdpMetadata() {
        return new FdpMetadataConverter(RDFFormat.JSONLD);
    }
    
    @Bean
    public FdpMetadataConverter n3FdpMetadata() {
        return new FdpMetadataConverter(RDFFormat.N3);
    }
    
    @Bean
    public FdpMetadataConverter rdfxmlFdpMetadata() {
        return new FdpMetadataConverter(RDFFormat.RDFXML);
    }
    
    @Bean
    public CatalogMetadataConverter turtleCatalogMetadata() {
        return new CatalogMetadataConverter(RDFFormat.TURTLE);
    }
    
    @Bean
    public CatalogMetadataConverter jsonldCatalogMetadata() {
        return new CatalogMetadataConverter(RDFFormat.JSONLD);
    }
    
    @Bean
    public CatalogMetadataConverter n3CatalogMetadata() {
        return new CatalogMetadataConverter(RDFFormat.N3);
    }
    
    @Bean
    public CatalogMetadataConverter rdfxmlCatalogMetadata() {
        return new CatalogMetadataConverter(RDFFormat.RDFXML);
    }
    
    @Bean
    public DatasetMetadataConverter turtleDatasetMetadata() {
        return new DatasetMetadataConverter(RDFFormat.TURTLE);
    }
    
    @Bean
    public DatasetMetadataConverter jsonldDatasetMetadata() {
        return new DatasetMetadataConverter(RDFFormat.JSONLD);
    }
    
    @Bean
    public DatasetMetadataConverter n3DatasetMetadata() {
        return new DatasetMetadataConverter(RDFFormat.N3);
    }
    
    @Bean
    public DatasetMetadataConverter rdfxmlDatasetMetadata() {
        return new DatasetMetadataConverter(RDFFormat.RDFXML);
    }
    
    @Bean
    public DataRecordMetadataConverter turtleDataRecordMetadata() {
        return new DataRecordMetadataConverter(RDFFormat.TURTLE);
    }
    
    @Bean
    public DataRecordMetadataConverter jsonldDataRecordMetadata() {
        return new DataRecordMetadataConverter(RDFFormat.JSONLD);
    }
    
    @Bean
    public DataRecordMetadataConverter n3DataRecordMetadata() {
        return new DataRecordMetadataConverter(RDFFormat.N3);
    }
    
    @Bean
    public DataRecordMetadataConverter rdfxmlDataRecordMetadata() {
        return new DataRecordMetadataConverter(RDFFormat.RDFXML);
    }
    
    @Bean
    public DistributionMetadataConverter turtleDistributionMetadata() {
        return new DistributionMetadataConverter(RDFFormat.TURTLE);
    }
    
    @Bean
    public DistributionMetadataConverter jsonldDistributionMetadata() {
        return new DistributionMetadataConverter(RDFFormat.JSONLD);
    }
    
    @Bean
    public DistributionMetadataConverter n3DistributionMetadata() {
        return new DistributionMetadataConverter(RDFFormat.N3);
    }
    
    @Bean
    public DistributionMetadataConverter rdfxmlDistributionMetadata() {
        return new DistributionMetadataConverter(RDFFormat.RDFXML);
    }
}
