package nl.dtls.fairdatapoint.api.config;


import java.net.URISyntaxException;
import nl.dtls.fairdatapoint.domain.StoreManager;
import nl.dtls.fairdatapoint.domain.StoreManagerImpl;
import nl.dtls.fairdatapoint.service.DataAccessorService;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.impl.DataAccessorServiceImpl;
import nl.dtls.fairdatapoint.service.impl.FairMetaDataServiceImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sparql.SPARQLRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2015-11-19
 * @version 0.1
 */
@EnableWebMvc
@Configuration
@Import(ApplicationSwaggerConfig.class)
@ComponentScan(basePackages = "nl.dtls.fairdatapoint.api.controller")
@PropertySource(value = {"classpath:/config/fdp-server.properties", 
    "classpath:/config/triple-store.properties"})
public class RestApiConfiguration extends WebMvcConfigurerAdapter {    
    @Value("${base-uri}")
    private String METADATA_RDF_BASE_URI;
    @Value("${store-type}")
    private String TRIPLE_STORE_TYPE;
    @Value("${store-url}")
    private String TRIPLE_STORE_URL;
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer 
        propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    @Bean
    public String defaultControllerMsg() {
        return "Method not implemented yet!!!";
    }
    @Bean
    public StoreManager tripleStoreManagerImp() throws RepositoryException {
        Repository repository;    
        StoreManager storeManager;        
        repository = new SPARQLRepository(TRIPLE_STORE_URL);
        storeManager = new StoreManagerImpl(repository);
        return storeManager;
    }
    
    @Bean
    public StoreManager inMemoryStoreManagerImp() throws RepositoryException {
        Sail store = new MemoryStore();
        Repository repository;  
        repository = new SailRepository(store);    
        return new StoreManagerImpl(repository);
    }
    @Bean
    public FairMetaDataService fairMetaDataServiceImpl() 
            throws URISyntaxException, RepositoryException {
        FairMetaDataService fdpService = new FairMetaDataServiceImpl(
                tripleStoreManagerImp(), METADATA_RDF_BASE_URI);
        return fdpService;
    }
    
    @Bean
    public DataAccessorService fairDataAccessorService() 
            throws URISyntaxException, RepositoryException {
        DataAccessorService dataAccessorService = new DataAccessorServiceImpl(
                tripleStoreManagerImp(), METADATA_RDF_BASE_URI);
        return dataAccessorService;
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}