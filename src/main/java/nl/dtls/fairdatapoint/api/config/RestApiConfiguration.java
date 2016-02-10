package nl.dtls.fairdatapoint.api.config;


import java.net.URISyntaxException;
import nl.dtls.fairdatapoint.domain.StoreManager;
import nl.dtls.fairdatapoint.domain.StoreManagerException;
import nl.dtls.fairdatapoint.domain.StoreManagerImpl;
import nl.dtls.fairdatapoint.service.DataAccessorService;
import nl.dtls.fairdatapoint.service.FairMetaDataService;
import nl.dtls.fairdatapoint.service.impl.DataAccessorServiceImpl;
import nl.dtls.fairdatapoint.service.impl.FairMetaDataServiceImpl;
import nl.dtls.fairdatapoint.utils.ExampleTurtleFiles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 *
 * @author Rajaram Kaliyaperumal
 * @since 2015-11-19
 * @version 0.1.1
 */
@EnableWebMvc
@Configuration
@Import(ApplicationSwaggerConfig.class)
@ComponentScan(basePackages = "nl.dtls.fairdatapoint.api.controller")
@PropertySource({"${fdp.server.conf:classpath:/conf/fdp-server.properties}", 
    "${fdp.tripleStore.conf:classpath:/conf/triple-store.properties}"})
public class RestApiConfiguration extends WebMvcConfigurerAdapter {  
    private final static Logger LOGGER 
            = LogManager.getLogger(RestApiConfiguration.class);
    @Value("${base-uri}")
    private String METADATA_RDF_BASE_URI;
    @Value("${store-type}")
    private String TRIPLE_STORE_TYPE;
    @Value("${store-prepopulate}")
    private String TRIPLE_STORE_PREPOPULATE;
    @Value("${store-url}")
    private String TRIPLE_STORE_URL;
    
    @Bean    
    public Repository repository() throws RepositoryException { 
        Repository repository;
        if (Integer.parseInt(TRIPLE_STORE_TYPE) == 2) {
            repository = new SPARQLRepository(TRIPLE_STORE_URL);        
        } else { // In memory is the default store
            Sail store = new MemoryStore();  
            repository = new SailRepository(store);
        }        
        return repository;
    } 
    
    @Bean    
    public StoreManager storeManager() throws RepositoryException, 
            StoreManagerException {          
        
        StoreManager storeManager = new StoreManagerImpl(repository());
        // Only in memory store is pre populated
        if (Integer.parseInt(TRIPLE_STORE_TYPE) != 2 &&                 
                Boolean.valueOf(TRIPLE_STORE_PREPOPULATE)) {
            // FDP metadata    
            storeManager.storeRDF(ExampleTurtleFiles.                        
                    getTurtleAsString(ExampleTurtleFiles.FDP_METADATA),                        
                    null, METADATA_RDF_BASE_URI);             
            // catalogs metadata    
            for (String catalog : ExampleTurtleFiles.CATALOG_METADATA) {                    
                storeManager.storeRDF(ExampleTurtleFiles.
                        getTurtleAsString(catalog),null, METADATA_RDF_BASE_URI);                
            }
            // datasets metadata    
            for (String dataset : ExampleTurtleFiles.DATASET_METADATA) {
                storeManager.storeRDF(ExampleTurtleFiles.                        
                        getTurtleAsString(dataset),null, METADATA_RDF_BASE_URI);
            } 
            // distributions metadata
            for (String distribution :                         
                    ExampleTurtleFiles.DATASET_DISTRIBUTIONS) {                    
                storeManager.storeRDF(ExampleTurtleFiles.                        
                        getTurtleAsString(distribution),null, 
                        METADATA_RDF_BASE_URI);                
            } 
        }else {                
            LOGGER.info("FDP api is not prepopulated, "                        
                    + "if you would like to prepopulated the api with "                        
                    + "content, please set 'store-prepopulate' "                        
                    + "property value to true");            
        }
        return storeManager;
    } 
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer 
        propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
        
    @Bean    
    public String baseURI()  { 
        return this.METADATA_RDF_BASE_URI;
    }     
        
    @Bean
    public FairMetaDataService fairMetaDataServiceImpl() 
            throws URISyntaxException, RepositoryException, 
            StoreManagerException {      
        return new FairMetaDataServiceImpl(storeManager(), baseURI());
    }
    
    @Bean
    public DataAccessorService fairDataAccessorService() 
            throws URISyntaxException, RepositoryException, 
            StoreManagerException {
        return new DataAccessorServiceImpl(storeManager(), baseURI());
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        super.configureDefaultServletHandling(configurer); 
    }    
    
}