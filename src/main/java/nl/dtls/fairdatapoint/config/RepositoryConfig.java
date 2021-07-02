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
import nl.dtls.fairdatapoint.config.properties.RepositoryProperties;
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
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RepositoryProperties repositoryProperties;

    @Bean(initMethod = "initialize", destroyMethod = "shutDown")
    public Repository repository(ApplicationContext context)
            throws RepositoryException {

        Repository repository = switch (repositoryProperties.getType()) {
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
        return new SailRepository(store);
    }

    private Repository getNativeStore() {
        log.info("Setting up Native Store");
        if (!repositoryProperties.getNativeRepo().getDir().isEmpty()) {
            File dataDir = new File(repositoryProperties.getNativeRepo().getDir());
            return new SailRepository(new NativeStore(dataDir));
        }
        log.warn("'repository.native.dir' is empty");
        return null;
    }

    private Repository getAgraphRepository() {
        log.info("Setting up Allegro Graph Store");
        if (!repositoryProperties.getAgraph().getUrl().isEmpty()) {
            SPARQLRepository repository = new SPARQLRepository(repositoryProperties.getAgraph().getUrl());
            if (!repositoryProperties.getAgraph().getUsername().isEmpty() &&
                    !repositoryProperties.getAgraph().getPassword().isEmpty()) {
                repository.setUsernameAndPassword(
                        repositoryProperties.getAgraph().getUsername(),
                        repositoryProperties.getAgraph().getPassword()
                );
            }
            return repository;
        }
        log.warn("'repository.agraph.url' is empty");
        return null;
    }

    private Repository getBlazeGraphRepository() {
        log.info("Setting up Blaze Graph Store");
        String blazegraphUrl = repositoryProperties.getBlazegraph().getUrl();
        if (!blazegraphUrl.isEmpty()) {
            blazegraphUrl = removeLastSlash(blazegraphUrl);
            // Build url for blazegraph (Eg: http://localhost:8079/bigdata/namespace/test1/sparql)
            StringBuilder sb = new StringBuilder();
            sb.append(blazegraphUrl);
            sb.append("/namespace/");
            if (!repositoryProperties.getBlazegraph().getRepository().isEmpty()) {
                sb.append(repositoryProperties.getBlazegraph().getRepository());
            } else {
                sb.append("kb");
            }
            sb.append("/sparql");
            String url = sb.toString();
            return new SPARQLRepository(url);
        }
        log.warn("'repository.blazegraph.url' is empty");
        return null;
    }

    private Repository getGraphDBRepository() {
        log.info("Setting up GraphDB Store");
        try {
            if (!repositoryProperties.getGraphDb().getUrl().isEmpty() &&
                    !repositoryProperties.getGraphDb().getRepository().isEmpty()) {
                final RepositoryManager repositoryManager;
                if (!repositoryProperties.getGraphDb().getUsername().isEmpty() &&
                        !repositoryProperties.getGraphDb().getPassword().isEmpty()) {
                    repositoryManager = RemoteRepositoryManager.getInstance(
                            repositoryProperties.getGraphDb().getUrl(),
                            repositoryProperties.getGraphDb().getUsername(),
                            repositoryProperties.getGraphDb().getPassword()
                    );
                } else {
                    repositoryManager = RemoteRepositoryManager.getInstance(
                            repositoryProperties.getGraphDb().getUrl()
                    );
                }
                
                return repositoryManager.getRepository(
                        repositoryProperties.getGraphDb().getRepository()
                );
            }
            log.warn("'repository.graphDb.url' or 'repository.graphDb.repository' is empty");
        } catch (RepositoryConfigException | RepositoryException e) {
            log.error("Failed to connect to GraphDB");
        }
        return null;
    }

}
