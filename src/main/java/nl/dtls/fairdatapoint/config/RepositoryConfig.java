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
package nl.dtls.fairdatapoint.config;

import lombok.extern.log4j.Log4j2;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static nl.dtls.fairdatapoint.util.HttpUtil.removeLastSlash;

@Configuration
@Log4j2
public class RepositoryConfig {

    @Value("${repository.agraph.url:}")
    private String agraphUrl;

    @Value("${repository.agraph.username:}")
    private String agraphUsername;

    @Value("${repository.agraph.password:}")
    private String agraphPassword;

    @Value("${repository.graphDb.url:}")
    private String graphDbUrl;

    @Value("${repository.graphDb.repository:}")
    private String graphDbRepository;
    
    @Value("${repository.graphDb.username:}")
    private String graphDbUsername;
    
    @Value("${repository.graphDb.password:}")
    private String graphDbPassword;

    @Value("${repository.blazegraph.url:}")
    private String blazegraphUrl;

    @Value("${repository.blazegraph.repository:}")
    private String blazegraphRepository;

    @Value("${repository.native.dir:}")
    private String nativeStoreDir;

    @Bean(initMethod = "initialize", destroyMethod = "shutDown")
    public Repository repository(@Value("${repository.type:1}") int storeType, ApplicationContext context)
            throws RepositoryException {

        Repository repository = switch (storeType) {
            case 1 -> getInMemoryStore();
            case 2 -> getNativeStore();
            case 3 -> getAgraphRepository();
            case 4 -> getGraphDBRepository();
            case 5 -> getBlazeGraphRepository();
            default -> null;
        };

        if (repository == null) {
            log.error("Failed to configure a RDF repository");
            SpringApplication.exit(context);
            System.exit(1);
        } else {
            log.info("Successfully configure a RDF repository");
        }
        return repository;
    }

    private Repository getInMemoryStore() {
        log.info("Setting up InMemory Store");
        Sail store = new MemoryStore();
        SailRepository repository = new SailRepository(store);
        return repository;
    }

    private Repository getNativeStore() {
        log.info("Setting up Native Store");
        if (!nativeStoreDir.isEmpty()) {
            File dataDir = new File(nativeStoreDir);
            return new SailRepository(new NativeStore(dataDir));
        }
        log.warn("'nativeStoreDir' is empty");
        return null;
    }

    private Repository getAgraphRepository() {
        log.info("Setting up Allegro Graph Store");
        if (!agraphUrl.isEmpty()) {
            SPARQLRepository repository = new SPARQLRepository(agraphUrl);
            if (!agraphUsername.isEmpty() && !agraphPassword.isEmpty()) {
                repository.setUsernameAndPassword(agraphUsername, agraphPassword);
            }
            return repository;
        }
        log.warn("'agraphUrl' is empty");
        return null;
    }

    private Repository getBlazeGraphRepository() {
        log.info("Setting up Blaze Graph Store");
        if (!blazegraphUrl.isEmpty()) {
            blazegraphUrl = removeLastSlash(blazegraphUrl);
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
            return new SPARQLRepository(url);
        }
        log.warn("'blazegraphUrl' is empty");
        return null;
    }

    private Repository getGraphDBRepository() {
        log.info("Setting up GraphDB Store");
        try {
            if (!graphDbUrl.isEmpty() && !graphDbRepository.isEmpty()) {
                final RepositoryManager repositoryManager;
                if (!graphDbUsername.isEmpty() && !graphDbPassword.isEmpty()) {
                    repositoryManager = RemoteRepositoryManager.getInstance(graphDbUrl, graphDbUsername, graphDbPassword);
                } else {
                    repositoryManager = RemoteRepositoryManager.getInstance(graphDbUrl);
                }
                
                return repositoryManager.getRepository(graphDbRepository);
            }
        } catch (RepositoryConfigException | RepositoryException e) {
            log.error("Failed to connect to GraphDB");
        }
        return null;
    }

}
