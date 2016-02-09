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
    private StoreManager STORE_MANAGER;
    
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
        if (this.STORE_MANAGER == null) {      
            Repository repository = new SPARQLRepository(TRIPLE_STORE_URL);
            this.STORE_MANAGER = new StoreManagerImpl(repository);    
        }
        return this.STORE_MANAGER;
    }
    
    @Bean
    public StoreManager inMemoryStoreManagerImp() throws RepositoryException, 
            StoreManagerException {
        if (this.STORE_MANAGER == null) {
            Sail store = new MemoryStore();  
            Repository repository = new SailRepository(store);    
            this.STORE_MANAGER = new StoreManagerImpl(repository);
            if(Boolean.valueOf(TRIPLE_STORE_PREPOPULATE)) {
                this.STORE_MANAGER.storeRDF(ExampleTurtleFiles.
                        getTurtleAsString(ExampleTurtleFiles.FDP_METADATA),
                        null, METADATA_RDF_BASE_URI);             
                for (String catalog : ExampleTurtleFiles.CATALOG_METADATA) {
                    this.STORE_MANAGER.storeRDF(ExampleTurtleFiles.
                        getTurtleAsString(catalog),null, METADATA_RDF_BASE_URI);
                }
                for (String dataset : ExampleTurtleFiles.DATASET_METADATA) {
                    this.STORE_MANAGER.storeRDF(ExampleTurtleFiles.
                        getTurtleAsString(dataset),null, METADATA_RDF_BASE_URI); 
                } 
                for (String distribution : 
                        ExampleTurtleFiles.DATASET_DISTRIBUTIONS) {
                    this.STORE_MANAGER.storeRDF(ExampleTurtleFiles.
                        getTurtleAsString(distribution),null, 
                        METADATA_RDF_BASE_URI);
                }                
            }
            else {
                LOGGER.info("FDP api is not prepopulated, "
                        + "if you would like to prepopulated the api with "
                        + "content, please set 'store-prepopulate' "
                        + "property value to true");
            } 
        }
        return this.STORE_MANAGER;
    }
    private StoreManager getStoreManager() throws RepositoryException, 
            StoreManagerException {        
        if (Integer.parseInt(TRIPLE_STORE_TYPE) == 2) {
            return tripleStoreManagerImp();
        }
        else {
         return  inMemoryStoreManagerImp();
        }        
    } 
    @Bean
    public FairMetaDataService fairMetaDataServiceImpl() 
            throws URISyntaxException, RepositoryException, 
            StoreManagerException {
        FairMetaDataService fdpService = new FairMetaDataServiceImpl(
                getStoreManager(), METADATA_RDF_BASE_URI);       
        return fdpService;
    }
    
    @Bean
    public DataAccessorService fairDataAccessorService() 
            throws URISyntaxException, RepositoryException, 
            StoreManagerException {
        DataAccessorService dataAccessorService = new DataAccessorServiceImpl(
                getStoreManager(), METADATA_RDF_BASE_URI);
        return dataAccessorService;
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