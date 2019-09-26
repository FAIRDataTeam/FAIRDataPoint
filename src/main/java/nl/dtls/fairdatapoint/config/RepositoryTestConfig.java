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

import nl.dtls.fairdatapoint.repository.store.StoreManager;
import nl.dtls.fairdatapoint.repository.store.StoreManagerImpl;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.File;

@Configuration
public class RepositoryTestConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(RepositoryTestConfig.class);

    @Value("${store.agraph.url:}")
    private String agraphUrl;

    @Value("${store.agraph.username:}")
    private String agraphUsername;

    @Value("${store.agraph.password:}")
    private String agraphPassword;

    @Value("${store.graphDb.url:}")
    private String graphDbUrl;

    @Value("${store.graphDb.repository:}")
    private String graphDbRepository;

    @Value("${store.blazegraph.url:}")
    private String blazegraphUrl;

    @Value("${store.blazegraph.repository:}")
    private String blazegraphRepository;

    @Value("${store.native.dir:}")
    private String nativeStoreDir;

    @Bean(name = "repository", initMethod = "initialize", destroyMethod = "shutDown")
    public Repository repository(@Value("${store.type:1}") int storeType)
            throws RepositoryException {

        Repository repository = null;
        if (storeType == 3) { // Allegrograph as a backend store
            repository = getAgraphRepository();
        } else if (storeType == 4) { // GraphDB as a backend store
            repository = getGraphDBRepository();
        } else if (storeType == 5) {    // Blazegraph as a backend store
            repository = getBlazeGraphRepository();
        } else if (storeType == 2 && !nativeStoreDir.isEmpty()) { // Native store
            File dataDir = new File(nativeStoreDir);
            LOGGER.info("Initializing native store");
            repository = new SailRepository(new NativeStore(dataDir));
        }
        // In memory is the default store
        if (storeType == 1 || repository == null) {
            Sail store = new MemoryStore();
            repository = new SailRepository(store);
            LOGGER.info("Initializing inmemory store");
        }
        return repository;
    }

    /**
     * Get allegrograph repository
     *
     * @return SPARQLRepository
     */
    private Repository getAgraphRepository() {

        SPARQLRepository sRepository = null;
        if (!agraphUrl.isEmpty()) {
            LOGGER.info("Initializing allegrograph repository");
            sRepository = new SPARQLRepository(agraphUrl);
            if (!agraphUsername.isEmpty() && !agraphPassword.isEmpty()) {
                sRepository.setUsernameAndPassword(agraphUsername, agraphPassword);
            }
        }
        return sRepository;
    }

    /**
     * Get blazegraph repository
     *
     * @return SPARQLRepository
     */
    private Repository getBlazeGraphRepository() {

        SPARQLRepository sRepository = null;
        if (!blazegraphUrl.isEmpty()) {
            LOGGER.info("Initializing blazegraph repository");
            if (blazegraphUrl.endsWith("/")) {
                blazegraphUrl = blazegraphUrl.substring(0, blazegraphUrl.length() - 1);
            }
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
            sRepository = new SPARQLRepository(url);
        }
        return sRepository;
    }

    /**
     * Get graphDB repository
     *
     * @return Repository
     */
    private Repository getGraphDBRepository() {

        Repository repository = null;
        try {
            if (!graphDbUrl.isEmpty() && !graphDbRepository.isEmpty()) {
                LOGGER.info("Initializing graphDB repository");
                RepositoryManager repositoryManager = new RemoteRepositoryManager(graphDbUrl);
                repositoryManager.initialize();
                repository = repositoryManager.getRepository(graphDbRepository);
            }
        } catch (RepositoryConfigException | RepositoryException e) {
            LOGGER.error("Initializing graphDB repository");
        }
        return repository;
    }

    @Bean(name = "storeManager")
    @DependsOn({"repository"})
    public StoreManager storeManager() throws RepositoryException {
        return new StoreManagerImpl();
    }

}
