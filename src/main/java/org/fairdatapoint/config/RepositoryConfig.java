/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
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
package org.fairdatapoint.config;

import lombok.extern.slf4j.Slf4j;
import org.fairdatapoint.config.properties.RepositoryConnectionProperties;
import org.fairdatapoint.config.properties.RepositoryProperties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
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
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.fairdatapoint.util.HttpUtil.removeLastSlash;

@Slf4j
@Configuration
public class RepositoryConfig {

    @Autowired
    private RepositoryProperties repositoryProperties;

    @Bean(initMethod = "init", destroyMethod = "shutDown", name = "mainRepository")
    public Repository mainRepository(ApplicationContext context) throws RepositoryException {
        return prepareRepository(context, repositoryProperties.getMain());
    }

    @Bean(initMethod = "init", destroyMethod = "shutDown", name = "draftsRepository")
    public Repository draftsRepository(ApplicationContext context) throws RepositoryException {
        return prepareRepository(context, repositoryProperties.getDrafts());
    }

    public Repository prepareRepository(ApplicationContext context, RepositoryConnectionProperties properties)
            throws RepositoryException {

        final Repository repository = switch (properties.getType()) {
            case RepositoryConnectionProperties.TYPE_IN_MEMORY -> getInMemoryStore();
            case RepositoryConnectionProperties.TYPE_NATIVE -> getNativeStore(properties);
            case RepositoryConnectionProperties.TYPE_ALLEGRO -> getAgraphRepository(properties);
            case RepositoryConnectionProperties.TYPE_GRAPHDB -> getGraphDBRepository(properties);
            case RepositoryConnectionProperties.TYPE_BLAZEGRAPH -> getBlazeGraphRepository(properties);
            case RepositoryConnectionProperties.TYPE_VIRTUOSO -> getVirtuosoRepository(properties);
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

    private Repository getNativeStore(RepositoryConnectionProperties properties) {
        log.info("Setting up Native Store");
        if (!properties.getNativeRepo().getDir().isEmpty()) {
            final File dataDir = new File(properties.getNativeRepo().getDir());
            return new SailRepository(new NativeStore(dataDir));
        }
        log.warn("'repository.native.dir' is empty");
        return null;
    }

    private Repository getAgraphRepository(RepositoryConnectionProperties properties) {
        log.info("Setting up Allegro Graph Store");
        if (!properties.getAgraph().getUrl().isEmpty()) {
            final SPARQLRepository repository =
                    new SPARQLRepository(properties.getAgraph().getUrl());
            if (!properties.getAgraph().getUsername().isEmpty()
                    && !properties.getAgraph().getPassword().isEmpty()) {
                repository.setUsernameAndPassword(
                        properties.getAgraph().getUsername(),
                        properties.getAgraph().getPassword()
                );
            }
            return repository;
        }
        log.warn("'repository.agraph.url' is empty");
        return null;
    }

    private Repository getBlazeGraphRepository(RepositoryConnectionProperties properties) {
        log.info("Setting up Blaze Graph Store");
        String blazegraphUrl = properties.getBlazegraph().getUrl();
        if (!blazegraphUrl.isEmpty()) {
            blazegraphUrl = removeLastSlash(blazegraphUrl);
            // Build url for blazegraph (Eg: http://localhost:8079/bigdata/namespace/test1/sparql)
            final StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(blazegraphUrl);
            urlBuilder.append("/namespace/");
            if (!properties.getBlazegraph().getRepository().isEmpty()) {
                urlBuilder.append(properties.getBlazegraph().getRepository());
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

    private Repository getVirtuosoRepository(RepositoryConnectionProperties properties) {
        log.info("Setting up Virtuoso Store");
        String virtuosoUrl = properties.getVirtuoso().getUrl();
        if (!virtuosoUrl.isEmpty()) {
            virtuosoUrl = removeLastSlash(virtuosoUrl);
            final SPARQLRepository repository =
                    new SPARQLRepository(virtuosoUrl + "/sparql-auth");
            final String username = properties.getVirtuoso().getUsername();
            final String password = properties.getVirtuoso().getPassword();
            if (!username.isEmpty() && !password.isEmpty()) {
                final URI uri = URI.create(virtuosoUrl);
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                        new AuthScope(uri.getHost(), uri.getPort()),
                        new UsernamePasswordCredentials(username, password)
                );
                final HttpClient httpClient = HttpClients.custom()
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .addInterceptorFirst(new StripTrailingSparqlUpdateSemicolonInterceptor())
                        .build();
                repository.setHttpClient(httpClient);
            }
            return repository;
        }
        log.warn("'repository.virtuoso.url' is empty");
        return null;
    }

    /**
     * Virtuoso's /sparql-auth endpoint requires real HTTP Digest auth and rejects Basic auth
     * outright. RDF4J's SPARQLRepository has no Digest option built in, so this builds a plain
     * Apache HttpClient with credentials on an unseeded CredentialsProvider instead, letting
     * HttpClient's own native Digest support negotiate normally.
     * <p>
     * RDF4J's SPARQLConnection always appends "; " after every SPARQL Update statement it sends.
     * Harmless for GraphDB/Blazegraph/AllegroGraph, but Virtuoso's stricter SPARQL compiler
     * rejects it outright (SP030 syntax error). This interceptor strips the trailing semicolon
     * from the "update" form parameter before the request is sent -- registered via
     * addRequestInterceptorFirst specifically, since running after HttpClient's own
     * content-length interceptor (which would compute Content-Length from the pre-stripped
     * entity) causes a body/header mismatch.
     */
    private static final class StripTrailingSparqlUpdateSemicolonInterceptor implements HttpRequestInterceptor {
        @Override
        public void process(final HttpRequest request, final HttpContext context) throws IOException {
            if (!(request instanceof HttpEntityEnclosingRequest enclosingRequest)) {
                return;
            }
            final HttpEntity entity = enclosingRequest.getEntity();
            if (entity == null || entity.getContentType() == null
                    || !entity.getContentType().getValue().startsWith("application/x-www-form-urlencoded")) {
                return;
            }
            final List<NameValuePair> params = URLEncodedUtils.parse(entity);
            final List<NameValuePair> rebuilt = new ArrayList<>();
            for (final NameValuePair param : params) {
                if ("update".equals(param.getName()) && param.getValue() != null) {
                    rebuilt.add(new BasicNameValuePair(param.getName(), param.getValue().replaceFirst(";\\s*$", "")));
                }
                else {
                    rebuilt.add(param);
                }
            }
            EntityUtils.consume(entity);
            enclosingRequest.setEntity(new UrlEncodedFormEntity(rebuilt, StandardCharsets.UTF_8));
        }
    }

    private Repository getGraphDBRepository(RepositoryConnectionProperties properties) {
        log.info("Setting up GraphDB Store");
        try {
            System.setProperty("org.eclipse.rdf4j.rio.binary.format_version", "1");
            if (!properties.getGraphDb().getUrl().isEmpty()
                    && !properties.getGraphDb().getRepository().isEmpty()) {
                final RepositoryManager repositoryManager;
                if (!properties.getGraphDb().getUsername().isEmpty()
                        && !properties.getGraphDb().getPassword().isEmpty()) {
                    repositoryManager = RemoteRepositoryManager.getInstance(
                            properties.getGraphDb().getUrl(),
                            properties.getGraphDb().getUsername(),
                            properties.getGraphDb().getPassword()
                    );
                }
                else {
                    repositoryManager = RemoteRepositoryManager.getInstance(
                            properties.getGraphDb().getUrl()
                    );
                }
                return repositoryManager.getRepository(
                        properties.getGraphDb().getRepository()
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
