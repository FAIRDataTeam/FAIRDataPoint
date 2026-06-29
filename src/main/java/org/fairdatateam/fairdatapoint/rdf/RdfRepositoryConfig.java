/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.rdf;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

import static org.fairdatateam.fairdatapoint.util.HttpUtil.removeLastSlash;

@Slf4j
@Configuration
public class RdfRepositoryConfig {

    @Autowired
    private RdfRepositoryProperties rdfRepositoryProperties;

    @Bean(initMethod = "init", destroyMethod = "shutDown")
    public Repository repository(ApplicationContext context)
            throws RepositoryException {

        final Repository repository = switch (rdfRepositoryProperties.getType()) {
            case RdfRepositoryProperties.TYPE_IN_MEMORY -> getInMemoryStore();
            case RdfRepositoryProperties.TYPE_NATIVE -> getNativeStore();
            case RdfRepositoryProperties.TYPE_ALLEGRO -> getAgraphRepository();
            case RdfRepositoryProperties.TYPE_GRAPHDB -> getGraphDBRepository();
            case RdfRepositoryProperties.TYPE_BLAZEGRAPH -> getBlazeGraphRepository();
            default -> null;
        };

        if (repository == null) {
            log.error("Failed to configure a RDF repository");
            SpringApplication.exit(context);
            System.exit(1);
        }
        else {
            log.info("Successfully configure a RDF repository");
        }
        return repository;
    }

    private Repository getInMemoryStore() {
        log.info("Setting up InMemory Store");
        final Sail store = new MemoryStore();
        return new SailRepository(store);
    }

    private Repository getNativeStore() {
        log.info("Setting up Native Store");
        if (!rdfRepositoryProperties.getNativeRepo().getDir().isEmpty()) {
            final File dataDir = new File(rdfRepositoryProperties.getNativeRepo().getDir());
            return new SailRepository(new NativeStore(dataDir));
        }
        log.warn("'repository.native.dir' is empty");
        return null;
    }

    private Repository getAgraphRepository() {
        log.info("Setting up Allegro Graph Store");
        if (!rdfRepositoryProperties.getAgraph().getUrl().isEmpty()) {
            final SPARQLRepository repository =
                    new SPARQLRepository(rdfRepositoryProperties.getAgraph().getUrl());
            if (!rdfRepositoryProperties.getAgraph().getUsername().isEmpty()
                    && !rdfRepositoryProperties.getAgraph().getPassword().isEmpty()) {
                repository.setUsernameAndPassword(
                        rdfRepositoryProperties.getAgraph().getUsername(),
                        rdfRepositoryProperties.getAgraph().getPassword()
                );
            }
            return repository;
        }
        log.warn("'repository.agraph.url' is empty");
        return null;
    }

    private Repository getBlazeGraphRepository() {
        log.info("Setting up Blaze Graph Store");
        String blazegraphUrl = rdfRepositoryProperties.getBlazegraph().getUrl();
        if (!blazegraphUrl.isEmpty()) {
            blazegraphUrl = removeLastSlash(blazegraphUrl);
            // Build url for blazegraph (Eg: http://localhost:8079/bigdata/namespace/test1/sparql)
            final StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(blazegraphUrl);
            urlBuilder.append("/namespace/");
            if (!rdfRepositoryProperties.getBlazegraph().getRepository().isEmpty()) {
                urlBuilder.append(rdfRepositoryProperties.getBlazegraph().getRepository());
            }
            else {
                urlBuilder.append("kb");
            }
            urlBuilder.append("/sparql");
            return new SPARQLRepository(urlBuilder.toString());
        }
        log.warn("'repository.blazegraph.url' is empty");
        return null;
    }

    private Repository getGraphDBRepository() {
        log.info("Setting up GraphDB Store");
        try {
            System.setProperty("org.eclipse.rdf4j.rio.binary.format_version", "1");
            if (!rdfRepositoryProperties.getGraphDb().getUrl().isEmpty()
                    && !rdfRepositoryProperties.getGraphDb().getRepository().isEmpty()) {
                final RepositoryManager repositoryManager;
                if (!rdfRepositoryProperties.getGraphDb().getUsername().isEmpty()
                        && !rdfRepositoryProperties.getGraphDb().getPassword().isEmpty()) {
                    repositoryManager = RemoteRepositoryManager.getInstance(
                            rdfRepositoryProperties.getGraphDb().getUrl(),
                            rdfRepositoryProperties.getGraphDb().getUsername(),
                            rdfRepositoryProperties.getGraphDb().getPassword()
                    );
                }
                else {
                    repositoryManager = RemoteRepositoryManager.getInstance(
                            rdfRepositoryProperties.getGraphDb().getUrl()
                    );
                }
                return repositoryManager.getRepository(
                        rdfRepositoryProperties.getGraphDb().getRepository()
                );
            }
            log.warn("'repository.graphDb.url' or 'repository.graphDb.repository' is empty");
        }
        catch (RepositoryConfigException | RepositoryException exception) {
            log.error("Failed to connect to GraphDB");
        }
        return null;
    }

}
