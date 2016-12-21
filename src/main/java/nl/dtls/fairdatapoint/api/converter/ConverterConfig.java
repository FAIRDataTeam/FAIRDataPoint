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
 * @author rajaram
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
