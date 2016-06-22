package nl.dtls.fairdatapoint.api.config;

import com.lyncode.builder.ListBuilder;
import com.lyncode.xoai.services.impl.UTCDateProvider;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import nl.dtls.fairdatapoint.aoipmh.Context;
import nl.dtls.fairdatapoint.aoipmh.InMemoryItem;
import nl.dtls.fairdatapoint.aoipmh.InMemoryItemRepository;
import nl.dtls.fairdatapoint.aoipmh.InMemorySetRepository;
import nl.dtls.fairdatapoint.aoipmh.RepositoryConfiguration;
import nl.dtls.fairdatapoint.aoipmh.handlers.ErrorHandler;
import nl.dtls.fairdatapoint.aoipmh.handlers.GetRecordHandler;
import nl.dtls.fairdatapoint.aoipmh.handlers.Identify;
import nl.dtls.fairdatapoint.aoipmh.handlers.IdentifyHandler;
import nl.dtls.fairdatapoint.aoipmh.handlers.ListIdentifiersHandler;
import nl.dtls.fairdatapoint.aoipmh.handlers.ListMetadataFormatsHandler;
import nl.dtls.fairdatapoint.aoipmh.handlers.ListRecordsHandler;
import nl.dtls.fairdatapoint.aoipmh.handlers.ListSetsHandler;
import nl.dtls.fairdatapoint.domain.StoreManager;
import nl.dtls.fairdatapoint.domain.StoreManagerException;
import nl.dtls.fairdatapoint.domain.StoreManagerImpl;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
 * Spring context file
 * 
 * @author Rajaram Kaliyaperumal
 * @author Shamanou van Leeuwen
 * @since 2015-11-19
 * @version 0.2
 */
@EnableWebMvc
@Configuration
@Import(ApplicationSwaggerConfig.class)
@ComponentScan(basePackages = "nl.dtls.fairdatapoint.*")
@PropertySource({"${fdp.server.conf:classpath:/conf/fdp-server.properties}", 
    "${fdp.tripleStore.conf:classpath:/conf/triple-store.properties}"})
public class RestApiContext extends WebMvcConfigurerAdapter{  
    private final static Logger LOGGER = LogManager.getLogger(RestApiContext.class);    
    
    @Bean(name="sparqlRepository", initMethod = "initialize", 
            destroyMethod = "shutDown")    
    public org.openrdf.repository.Repository sparqlRepository( Environment env) throws RepositoryException { 
        String storeURL = env.getProperty("store-url");
        int storeType = env.getProperty("store-type", Integer.class);
        org.openrdf.repository.Repository sparqlRepository;
        if (storeType == 2) {
            sparqlRepository = new SPARQLRepository(storeURL); 
            LOGGER.info("HTTP triple store initialize");
        } else { // In memory is the default store
            Sail store = new MemoryStore();  
            sparqlRepository = new SailRepository(store);
            LOGGER.info("Inmemory triple store initialize");
        }
        return sparqlRepository;
    } 
    
    @Bean(name="context")
    public Context context(Environment env) throws TransformerConfigurationException{
        return new Context().withMetadataFormat(env.getRequiredProperty("metadataNamespace"), env.getRequiredProperty("metadataPrefix"), TransformerFactory.newInstance().newTransformer());
    }
     
    @Bean(name="repositoryConfiguration")
    public RepositoryConfiguration repositoryConfiguration(){
        RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration();
        return repositoryConfiguration;
    }
    
    @Bean(name="identifyHandler")
    public IdentifyHandler identifyhandler(Environment env) throws TransformerConfigurationException{
        return new IdentifyHandler(this.context(env), this.repository(env));
    }
    
    @Bean(name="listSetsHandler")
    public ListSetsHandler listSetsHandler(Environment env) throws TransformerConfigurationException{
        return new ListSetsHandler(this.context(env), this.repository(env));
    }

    @Bean(name="listMetadataFormatsHandler")
    public ListMetadataFormatsHandler listMetaDataFormatsHandler(Environment env) throws TransformerConfigurationException{
        return new ListMetadataFormatsHandler(this.context(env), this.repository(env));        
    }
    
    @Bean(name="repository")
    public nl.dtls.fairdatapoint.aoipmh.Repository repository(Environment env){
        nl.dtls.fairdatapoint.aoipmh.Repository r = new nl.dtls.fairdatapoint.aoipmh.Repository();
        InMemoryItemRepository inMemoryItemRepository = new InMemoryItemRepository();
        InMemorySetRepository inMemorySetRepository = new InMemorySetRepository();
            
        if (env.getRequiredProperty("randomRepository").equals("True")){
            inMemoryItemRepository = inMemoryItemRepository.withRandomItems(10);
            inMemorySetRepository = inMemorySetRepository.withRandomSets(10);
        } else {
            String[] sets = env.getRequiredProperty("sets").trim().split(",");
            String[] items = env.getRequiredProperty("items").trim().split(",");
            for(String set : sets){
                String[] setMapping = set.split(":");
                inMemorySetRepository = inMemorySetRepository.withSet(setMapping[0], setMapping[1]);
            } for (String item: items){
                String[] itemMapping = item.split(":");
                InMemoryItem inMemoryitem = new InMemoryItem();
                inMemoryitem = inMemoryitem.withIdentifier(itemMapping[0]).with("sets",new ListBuilder<String>().add(itemMapping[1]).build()).with("deleted", false).with("datestamp", new Date());
                inMemoryItemRepository = inMemoryItemRepository.withItem(inMemoryitem);
            }
        }
        r = r.withItemRepository(inMemoryItemRepository).withSetRepository(inMemorySetRepository);
        return r;
    }
    
    @Bean(name="description")
    public String description(Environment env){
        String description = env.getRequiredProperty("description").trim();
        return description;
    }
    
    @Bean(name="protocolVersion")
    public String protocolVersion(Environment env){
        String protocolVersion = env.getRequiredProperty("protocolVersion");
        return protocolVersion;
    }
    
    @Bean(name="listIdentifiersHandler")
    public ListIdentifiersHandler listIdentifiersHandler(Environment env) throws TransformerConfigurationException{
      return new ListIdentifiersHandler(this.context(env), this.repository(env));  
    }
  
    @Bean(name="getRecordHandler")
    public GetRecordHandler getRecordHandler(Environment env) throws TransformerConfigurationException{
        return new GetRecordHandler(this.context(env), this.repository(env));
    }
    
    @Bean(name="errorHandler")
    public ErrorHandler errorHandler(){
        return new ErrorHandler();
    }
    
    @Bean(name="utcDateProvider")
    public UTCDateProvider utcDateProvider(){
        return new UTCDateProvider();
    }
    
    @Bean(name="listRecordsHandler")
    public ListRecordsHandler listRecordsHandler(Environment env) throws TransformerConfigurationException{
        return new ListRecordsHandler(this.context(env), this.repository(env));
    }
    
    @Bean(name = "storeManager")
    @DependsOn({"sparqlRepository","prepopulateStore", "baseURI"})
    public StoreManager storeManager() throws RepositoryException, 
            StoreManagerException { 
        return new StoreManagerImpl();
    }
    
    @Bean(name = "properties")
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
        
    @Bean(name = "baseURI")    
    public String baseURI(Environment env)  {     
        String rdfBaseURI = env.getRequiredProperty("baseUri");
        return rdfBaseURI;
    } 
    
    @Bean(name="repositoryName")
    public String repositoryName(Environment env){
        String name = env.getRequiredProperty("repositoryName");
        return name;
    }
    
    @Bean(name="adminEmails")
    public ArrayList<String> adminEmails(Environment env)  {
        ArrayList<String> out = new ArrayList<>();
        String[] emails = env.getRequiredProperty("adminEmails").split(",");
        for(String email: emails){
            out.add(email.trim());
        }
        return out;
    }

    @Bean(name = "identifier")    
    public String identifier(Environment env)  {     
        String identifier = env.getRequiredProperty("identifier");
        return identifier;
    } 
    
    @Bean(name="identify")
    public Identify identify(){
        return new Identify();
    }
    
    @Bean(name = "prepopulateStore")    
    public boolean prepopulateStore(Environment env)  {     
        boolean rdfBaseURI = Boolean.valueOf(
                env.getProperty("store-prepopulate", "false"));
        return rdfBaseURI;
    } 
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        super.configureDefaultServletHandling(configurer); 
    }
}