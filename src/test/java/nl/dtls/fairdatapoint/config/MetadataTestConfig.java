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
package nl.dtls.fairdatapoint.config;

import nl.dtl.fairmetadata4j.model.Agent;
import nl.dtls.fairdatapoint.service.pid.DefaultPIDSystemImpl;
import nl.dtls.fairdatapoint.service.pid.PIDSystem;
import nl.dtls.fairdatapoint.service.pid.PurlPIDSystemImpl;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

@TestConfiguration
public class MetadataTestConfig {

    private final ValueFactory VF = SimpleValueFactory.getInstance();

    @Bean(name = "publisher")
    public Agent publisher() {
        Agent publisher = new Agent();
        publisher.setUri(VF.createIRI("https://www.dtls.nl"));
        publisher.setName(VF.createLiteral("DTLS"));
        return publisher;
    }

    @Bean(name = "language")
    public IRI language() {
        return VF.createIRI("http://id.loc.gov/vocabulary/iso639-1/en");
    }

    @Bean(name = "license")
    public IRI license() {
        return VF.createIRI("http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0");
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
        return VF.createIRI("http://purl.org/biosemantics-lumc/fdp");
    }

}
