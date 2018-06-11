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
package nl.dtls.fairdatapoint.api.config;

import static org.eclipse.rdf4j.rio.RDFFormat.TURTLE;
import java.io.IOException;
import java.util.List;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import java.io.File;
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
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring context file.
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @since 2015-11-19
 * @version 0.2
 */
@EnableWebMvc
@EnableAsync
@Configuration
@Import(ApplicationSwaggerConfig.class)
@ComponentScan(basePackages = "nl.dtls.fairdatapoint.*")
public class RestApiContext extends WebMvcConfigurerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestApiContext.class);

    @Autowired
    private List<AbstractMetadataMessageConverter<?>> metadataConverters;

    private final ValueFactory VALUEFACTORY = SimpleValueFactory.getInstance();

    @Value("${store.native.dir:}")
    private String nativeStoreDir;

    @Value("${store.agraph.url:}")
    private String agraphUrl;

    @Value("${store.agraph.username:}")
    private String agraphUsername;

    @Value("${store.agraph.password:}")
    private String agraphPassword;

    @Value("${store.graphDb.url:}")
    private String graphDbUrl;

    @Value("${store.graphDb.repository:}")
    private String graphDbRepository;

    @Value("${store.blazegraph.url:}")
    private String blazegraphUrl;

    @Value("${store.blazegraph.repository:}")
    private String blazegraphRepository;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.addAll(metadataConverters);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.parseMediaType(TURTLE.getDefaultMIMEType()));
        for (AbstractMetadataMessageConverter<?> converter : metadataConverters) {
            converter.configureContentNegotiation(configurer);
        }
    }

    @Bean(destroyMethod = "shutdownNow")
    public Executor threadPoolTaskExecutor(@Value("${threadPoolSize:4}") int threadPoolSize) {
        return Executors.newFixedThreadPool(threadPoolSize);
    }

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

    @Bean(name = "repository", initMethod = "initialize", destroyMethod = "shutDown")
    public Repository repository(@Value("${store.type:1}") int storeType)
            throws RepositoryException {

        Repository repository = null;
        if (storeType == 3) { // Allegrograph as a backend store
            repository = getAgraphRepository();
        } else if (storeType == 4) { // GraphDB as a backend store
            repository = getGraphDBRepository();
        } else if (storeType == 5) {    // Blazegraph as a backend store
            repository = getBlazeGraphRepository();
        } else if (storeType == 2 && !nativeStoreDir.isEmpty()) { // Native store
            File dataDir = new File(nativeStoreDir);
            LOGGER.info("Initializing native store");
            repository = new SailRepository(new NativeStore(dataDir));
        }
        // In memory is the default store
        if (storeType == 1 || repository == null) {
            Sail store = new MemoryStore();
            repository = new SailRepository(store);
            LOGGER.info("Initializing inmemory store");
        }
        return repository;
    }
    
    @Bean
    @DependsOn({"purlBaseUrl"})
    public PIDSystem pidSystem(@Value("${pidSystem.type:1}") int pidSystemtype) {

        if (pidSystemtype == 2) {
            return new PurlPIDSystemImpl();
        } else {
            return new DefaultPIDSystemImpl();
        }
    }

    @Bean
    public String purlBaseUrl(@Value("${pidSystem.purl.baseUrl:}") String url) {
        String baseUrl = url; 
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    /**
     * Get allegrograph repository
     *
     * @return SPARQLRepository
     */
    private Repository getAgraphRepository() {

        SPARQLRepository sRepository = null;
        if (!agraphUrl.isEmpty()) {
            LOGGER.info("Initializing allegrograph repository");
            sRepository = new SPARQLRepository(agraphUrl);
            if (!agraphUsername.isEmpty() && !agraphPassword.isEmpty()) {
                sRepository.setUsernameAndPassword(agraphUsername, agraphPassword);
            }
        }
        return sRepository;
    }

    /**
     * Get blazegraph repository
     *
     * @return SPARQLRepository
     */
    private Repository getBlazeGraphRepository() {

        SPARQLRepository sRepository = null;
        if (!blazegraphUrl.isEmpty()) {
            LOGGER.info("Initializing blazegraph repository");
            if (blazegraphUrl.endsWith("/")) {
                blazegraphUrl = blazegraphUrl.substring(0, blazegraphUrl.length() - 1);
            }
            // Build url for blazegraph (Eg: http://localhost:8079/bigdata/namespace/test1/sparql)
            StringBuilder sb = new StringBuilder();
            sb.append(blazegraphUrl);
            sb.append("/namespace/");
            if (!blazegraphRepository.isEmpty()) {
                sb.append(blazegraphRepository);
            } else {
                sb.append("kb");
            }
            sb.append("/sparql");
            String url = sb.toString();
            sRepository = new SPARQLRepository(url);
        }
        return sRepository;
    }

    /**
     * Get graphDB repository
     *
     * @return Repository
     */
    private Repository getGraphDBRepository() {

        Repository repository = null;
        try {
            if (!graphDbUrl.isEmpty() && !graphDbRepository.isEmpty()) {
                LOGGER.info("Initializing graphDB repository");
                RepositoryManager repositoryManager = new RemoteRepositoryManager(graphDbUrl);
                repositoryManager.initialize();
                repository = repositoryManager.getRepository(graphDbRepository);
            }
        } catch (RepositoryConfigException | RepositoryException e) {
            LOGGER.error("Initializing graphDB repository");
        }
        return repository;
    }

    @Bean(name = "storeManager")
    @DependsOn({"repository"})
    public StoreManager storeManager() throws RepositoryException, StoreManagerException {
        return new StoreManagerImpl();
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {

        registry.setOrder(Integer.MIN_VALUE + 1).addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.setOrder(Integer.MIN_VALUE + 2).
                addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void configureDefaultServletHandling(final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(handlebars());
    }

    @Bean
    public ViewResolver handlebars() {

        HandlebarsViewResolver viewResolver = new HandlebarsViewResolver();

        // add handlebars helper to get a label's literal without datatype
        viewResolver.registerHelper("literal", new Helper<Literal>() {
            @Override
            public Object apply(Literal literal, Options options) throws IOException {
                return literal.getLabel();
            }
        });

        viewResolver.setPrefix("/WEB-INF/templates/");
        viewResolver.setSuffix(".hbs");
        viewResolver.setFailOnMissingFile(false);

        return viewResolver;
    }
}
