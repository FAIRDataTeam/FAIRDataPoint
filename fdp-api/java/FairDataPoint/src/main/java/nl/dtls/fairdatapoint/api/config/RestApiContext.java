package nl.dtls.fairdatapoint.api.config;


import com.lyncode.builder.ListBuilder;
import com.lyncode.xoai.services.impl.UTCDateProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import nl.dtls.fairdatapoint.aoipmh.Condition;
import nl.dtls.fairdatapoint.aoipmh.Context;
import nl.dtls.fairdatapoint.aoipmh.Filter;
import nl.dtls.fairdatapoint.aoipmh.FilterResolver;
import nl.dtls.fairdatapoint.aoipmh.InMemoryItem;
import nl.dtls.fairdatapoint.aoipmh.InMemoryItemRepository;
import nl.dtls.fairdatapoint.aoipmh.InMemorySetRepository;
import nl.dtls.fairdatapoint.aoipmh.ItemIdentifier;
import nl.dtls.fairdatapoint.aoipmh.RepositoryConfiguration;
import nl.dtls.fairdatapoint.aoipmh.Set;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openrdf.model.Statement;
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
    @DependsOn({"repository", "prepopulateStore", "baseUri"})
    public StoreManager storeManager() throws RepositoryException,
            StoreManagerException {
        return new StoreManagerImpl();
    }

    @Bean(name = "properties")
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "baseUri")
    public String baseUri(final Environment env)  {
        String rdfBaseURI = env.getRequiredProperty("baseUri");
        return rdfBaseURI;
    }

    @Bean(name = "prepopulateStore")
    public boolean prepopulateStore(final Environment env)  {
        boolean rdfBaseURI = Boolean.valueOf(
                env.getProperty("store-prepopulate", "false"));
        return rdfBaseURI;
    }

    @Bean(name="repositoryName")
    public String repositoryName(Environment env){
        String fdpURI = baseUri(env).concat("fdp");
        String title = "Nameless FairDataPoint";
        try {
            List<Statement> statements = storeManager().retrieveResource(fdpURI);
            for(Statement s: statements){
                if(s.getPredicate().stringValue().equals("http://purl.org/dc/terms/title")){
                    return s.getObject().stringValue();
                }
            }
        } catch (RepositoryException | StoreManagerException ex) {
            return title;
        }
        return title;
    }
    
    @Bean(name="adminEmails")
    public ArrayList<String> adminEmails(Environment env)  {
        ArrayList<String> out = new ArrayList<>();
        String[] emails = env.getRequiredProperty("aoiAdminEmails").split(",");
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
    
        @Bean(name="context")
    public Context context(Environment env) throws TransformerConfigurationException{
        String[] sets = env.getRequiredProperty("sets").trim().split(",");
        Context context = new Context().withMetadataFormat("http://www.openarchives.org/OAI/2.0/oai_dc/",
                "http://www.openarchives.org/OAI/2.0/oai_dc.xsd","aoi_dc", TransformerFactory.newInstance().newTransformer());
        for (String x: sets){
            Set set;
            set = new Set(x.split(":")[0]).withName(x.split(":")[1]).withCondition(new Condition() {
                @Override
                public Filter getFilter(FilterResolver filterResolver) {
                    return new Filter() {
                        @Override
                        public boolean isItemShown(ItemIdentifier item) {
                            return false;
                        }
                    };
                }
            });
            context.withSet(set);
        }
        return context;
    }
     
    @Bean(name="repositoryConfiguration")
    public RepositoryConfiguration repositoryConfiguration(){
        RepositoryConfiguration repositoryConfiguration = new RepositoryConfiguration();
        return repositoryConfiguration;
    }
    
    @Bean(name="identifyHandler")
    public IdentifyHandler identifyhandler(Environment env) throws TransformerConfigurationException{
        return new IdentifyHandler(this.context(env), this.aoiRepository(env));
    }
    
    @Bean(name="listSetsHandler")
    public ListSetsHandler listSetsHandler(Environment env) throws TransformerConfigurationException{
        return new ListSetsHandler(this.context(env), this.aoiRepository(env));
    }

    @Bean(name="listMetadataFormatsHandler")
    public ListMetadataFormatsHandler listMetaDataFormatsHandler(Environment env) throws TransformerConfigurationException{
        return new ListMetadataFormatsHandler(this.context(env), this.aoiRepository(env));        
    }
    
    @Bean(name="aoiRepository")
    public nl.dtls.fairdatapoint.aoipmh.Repository aoiRepository(Environment env){
        nl.dtls.fairdatapoint.aoipmh.Repository r = new nl.dtls.fairdatapoint.aoipmh.Repository();
        InMemoryItemRepository inMemoryItemRepository = new InMemoryItemRepository();
        InMemorySetRepository inMemorySetRepository = new InMemorySetRepository();
            
        if (env.getRequiredProperty("randomRepository").equals("True")){
            inMemoryItemRepository = inMemoryItemRepository.withRandomItems(10);
            inMemorySetRepository = inMemorySetRepository.withRandomSets(10);
        } else {
            String[] items = env.getRequiredProperty("records").trim().split(",");
            for (String x: items){
                String[] itemMapping = x.split(";");
                InMemoryItem item = new InMemoryItem().with("deleted",false).with("datestamp",new Date());
                for (String y: itemMapping){
                    String[] field = y.split(":");
                    if (field[0].equals("set")){
                        item.with("sets",new ListBuilder<String>().add(field[1]).build());      
                    }if (field[0].equals("creators")){
                        item.with("creator",field[1]);
                    }if (field[0].equals("title")){
                        item.with("title",field[1]);
                    }if (field[0].equals("subject")){
                        item.with("subject",field[1]);
                    }if (field[0].equals("description")){
                        item.with("description",field[1]);
                    }if (field[0].equals("type")){
                        item.with("type",field[1]);
                    }if (field[0].equals("identifier")){
                        item.with("identifier",field[1]);
                    }
                }
                inMemoryItemRepository.withItem(item);
            }
        }
        r = r.withItemRepository(inMemoryItemRepository).withSetRepository(inMemorySetRepository);
        return r;
    }
    
    @Bean(name="protocolVersion")
    public String protocolVersion(Environment env){
        return env.getRequiredProperty("protocolVersion");
    }
    
    @Bean(name="listIdentifiersHandler")
    public ListIdentifiersHandler listIdentifiersHandler(Environment env) throws TransformerConfigurationException{
      return new ListIdentifiersHandler(this.context(env), this.aoiRepository(env));  
    }
  
    @Bean(name="getRecordHandler")
    public GetRecordHandler getRecordHandler(Environment env) throws TransformerConfigurationException{
        return new GetRecordHandler(this.context(env), this.aoiRepository(env));
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
        return new ListRecordsHandler(this.context(env), this.aoiRepository(env));
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