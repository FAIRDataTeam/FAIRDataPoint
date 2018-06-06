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
package nl.dtls.fairdatapoint.api.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import nl.dtl.fairmetadata4j.model.Agent;
import nl.dtls.fairdatapoint.api.converter.AbstractMetadataMessageConverter;
import nl.dtls.fairdatapoint.repository.StoreManager;
import nl.dtls.fairdatapoint.repository.StoreManagerException;
import nl.dtls.fairdatapoint.repository.impl.StoreManagerImpl;
import nl.dtls.fairdatapoint.service.PIDSystem;
import nl.dtls.fairdatapoint.service.impl.DefaultPIDSystemImpl;
import nl.dtls.fairdatapoint.service.impl.PurlPIDSystemImpl;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring test context file.
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2016-02-11
 * @version 0.1
 */
@EnableWebMvc
@EnableAsync
@Configuration
@ComponentScan(basePackages = "nl.dtls.fairdatapoint.*")
public class RestApiTestContext extends WebMvcConfigurerAdapter {

    @Autowired
    private List<AbstractMetadataMessageConverter<?>> metadataConverters;

    private final ValueFactory valueFactory = SimpleValueFactory.getInstance();

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.addAll(metadataConverters);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

        for (AbstractMetadataMessageConverter<?> converter : metadataConverters) {
            converter.configureContentNegotiation(configurer);
        }
    }

    @Bean
    public Executor threadPoolTaskExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Bean(name = "repository", initMethod = "initialize", destroyMethod = "shutDown")
    public Repository repository(final Environment env) throws RepositoryException, IOException,
            RDFParseException {

        // For tests we use only in memory
        Sail store = new MemoryStore();
        return new SailRepository(store);
    }

    @Bean(name = "storeManager")
    @DependsOn({"repository"})
    public StoreManager storeManager() throws RepositoryException, StoreManagerException {
        return new StoreManagerImpl();
    }

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
    public String purlBaseUrl() {
        return "http://purl.org/biosemantics-lumc/fdp";
    }

    @Bean(name = "metadataMetrics")
    public Map<String, String> metadataMetrics(Environment env) {

        Map<String, String> metadataMetrics = new HashMap();
        metadataMetrics.put("https://purl.org/fair-metrics/FM_F1A", "http://example.com/f1a");
        return metadataMetrics;
    }
}
