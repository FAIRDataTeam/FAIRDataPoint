package nl.dtls.fairdatapoint.config;

import nl.dtl.fairmetadata4j.model.Agent;
import nl.dtls.fairdatapoint.service.pid.DefaultPIDSystemImpl;
import nl.dtls.fairdatapoint.service.pid.PIDSystem;
import nl.dtls.fairdatapoint.service.pid.PurlPIDSystemImpl;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

public class MetadataTestConfig {

    private final ValueFactory valueFactory = SimpleValueFactory.getInstance();

    @Bean(name = "publisher")
    public Agent publisher() {

        Agent publisher = new Agent();
        publisher.setUri(valueFactory.createIRI("https://www.dtls.nl"));
        publisher.setName(valueFactory.createLiteral("DTLS"));
        return publisher;
    }

    @Bean(name = "language")
    public IRI language() {

        IRI language = valueFactory.createIRI("http://id.loc.gov/vocabulary/iso639-1/en");
        return language;
    }

    @Bean(name = "license")
    public IRI license() {

        IRI license = valueFactory
                .createIRI("http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0");
        return license;
    }

    @Bean
    public PIDSystem pidSystem() {
        return new DefaultPIDSystemImpl();
    }

    @Bean
    @DependsOn({"purlBaseUrl"})
    public PurlPIDSystemImpl purlPIDSystemImpl() {
        return new PurlPIDSystemImpl();
    }

    @Bean
    public IRI purlBaseUrl() {
        return valueFactory.createIRI("http://purl.org/biosemantics-lumc/fdp");
    }

}
