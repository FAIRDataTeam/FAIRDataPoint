package nl.dtls.fairdatapoint.config;

import nl.dtl.fairmetadata4j.model.Agent;
import nl.dtls.fairdatapoint.service.pid.DefaultPIDSystemImpl;
import nl.dtls.fairdatapoint.service.pid.PIDSystem;
import nl.dtls.fairdatapoint.service.pid.PurlPIDSystemImpl;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetadataConfig {

    private final ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();

    @Bean(name = "publisher")
    public Agent publisher(@Value("${metadataProperties.publisherURI:}") String publisherURI,
                           @Value("${metadataProperties.publisherName:}") String publishername) {

        Agent publisher = null;
        if (!publisherURI.isEmpty() && !publishername.isEmpty()) {
            publisher = new Agent();
            publisher.setUri(VALUEFACTORY.createIRI(publisherURI));
            publisher.setName(VALUEFACTORY.createLiteral(publishername));
        }
        return publisher;
    }

    @Bean(name = "language")
    public IRI language(@Value("${metadataProperties.language:}") String languageURI) {

        IRI language = null;
        if (!languageURI.isEmpty()) {
            language = VALUEFACTORY.createIRI(languageURI);
        }
        return language;
    }

    @Bean(name = "license")
    public IRI license(@Value("${metadataProperties.license:}") String licenseURI) {

        IRI license = null;
        if (!licenseURI.isEmpty()) {
            license = VALUEFACTORY.createIRI(licenseURI);
        }
        return license;
    }

    @Bean
    public PIDSystem pidSystem(@Value("${pidSystem.type:1}") int pidSystemtype) {

        if (pidSystemtype == 2) {
            return new PurlPIDSystemImpl();
        } else {
            return new DefaultPIDSystemImpl();
        }
    }

    @Bean
    public IRI purlBaseUrl(@Value("${pidSystem.purl.baseUrl:}") String url) {
        String baseUrl = url;
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return VALUEFACTORY.createIRI(baseUrl);
    }

}
