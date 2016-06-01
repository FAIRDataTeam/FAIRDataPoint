package nl.dtls.fairdatapoint.api.config;


import nl.dtls.fairdatapoint.domain.StoreManager;
import nl.dtls.fairdatapoint.domain.StoreManagerException;
import nl.dtls.fairdatapoint.domain.StoreManagerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Spring context file.
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

    @Bean(name="repository", initMethod = "initialize",
            destroyMethod = "shutDown")
    public Repository repository( Environment env)
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
    @DependsOn({"repository", "prepopulateStore", "baseURI"})
    public StoreManager storeManager() throws RepositoryException,
            StoreManagerException {
        return new StoreManagerImpl();
    }

    @Bean(name = "properties")
    public static PropertySourcesPlaceholderConfigurer
        propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "baseURI")
    public String baseURI(final Environment env)  {
        String rdfBaseURI = env.getRequiredProperty("base-uri");
        return rdfBaseURI;
    }

    @Bean(name = "prepopulateStore")
    public boolean prepopulateStore(final Environment env)  {
        boolean rdfBaseURI = Boolean.valueOf(
                env.getProperty("store-prepopulate", "false"));
        return rdfBaseURI;
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry
            registry) {
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