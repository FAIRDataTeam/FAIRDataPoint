/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.config;

import java.io.IOException;
import nl.dtls.fairdatapoint.repository.StoreManager;
import nl.dtls.fairdatapoint.repository.StoreManagerException;
import nl.dtls.fairdatapoint.repository.impl.StoreManagerImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Spring test context file. 
 * @author Rajaram Kaliyaperumal
 * @since 2016-02-11
 * @version 0.1
 */
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = "nl.dtls.fairdatapoint.*")
public class RestApiTestContext {
    @Bean(name="repository", initMethod = "initialize",
            destroyMethod = "shutDown")
    public Repository repository(final Environment env)
            throws RepositoryException, IOException, RDFParseException {
        // For tets we use only in memory
        Sail store = new MemoryStore();
        return new SailRepository(store);
    }

    @Bean(name = "storeManager")
    @DependsOn({"repository"})
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
        String rdfBaseURI = env.getRequiredProperty("baseUri");
        return rdfBaseURI;
    }
}
