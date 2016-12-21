package nl.dtls.fairdatapoint.api.config;

import java.util.List;
import nl.dtls.fairdatapoint.api.converter.AbstractMetadataMessageConverter;
import nl.dtls.fairdatapoint.repository.StoreManager;
import nl.dtls.fairdatapoint.repository.StoreManagerException;
import nl.dtls.fairdatapoint.repository.impl.StoreManagerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring context file.
 *
 * @author Rajaram Kaliyaperumal
 * @since 2015-11-19
 * @version 0.2
 */
@EnableWebMvc
@Configuration
@Import(ApplicationSwaggerConfig.class)
@ComponentScan(basePackages = "nl.dtls.fairdatapoint.*")
@PropertySource({"${fdp.server.conf:classpath:/conf/fdp-server.properties}",
    "${fdp.tripleStore.conf:classpath:/conf/triple-store.properties}"})
public class RestApiContext extends WebMvcConfigurerAdapter {

    private final static Logger LOGGER
            = LogManager.getLogger(RestApiContext.class);

    @Autowired
    private List<AbstractMetadataMessageConverter<?>> metadataConverters;
    
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

    @Bean(name = "repository", initMethod = "initialize",
            destroyMethod = "shutDown")
    public Repository repository(Environment env)
            throws RepositoryException {
        String storeURL = env.getProperty("store-url");
        int storeType = env.getProperty("store-type", Integer.class);
        Repository repository;
        if (storeType == 2) {
            repository = new SPARQLRepository(storeURL);
            LOGGER.info("HTTP triple store initialize");
        } else { // In memory is the default store
            Sail store = new MemoryStore();
            repository = new SailRepository(store);
            LOGGER.info("Inmemory triple store initialize");
        }
        return repository;
    }

    @Bean(name = "storeManager")
    @DependsOn({"repository"})
    public StoreManager storeManager() throws RepositoryException,
            StoreManagerException {
        return new StoreManagerImpl();
    }

    @Bean(name = "properties")
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.setOrder(Integer.MIN_VALUE + 1).
                addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.setOrder(Integer.MIN_VALUE + 2).
                addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Override
    public void configureDefaultServletHandling(
            final DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
