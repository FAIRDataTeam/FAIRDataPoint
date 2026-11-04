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
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.net.WWWFormCodec;
import org.eclipse.rdf4j.http.client.apache5.ApacheHC5HttpClientResponse;
import org.eclipse.rdf4j.http.client.apache5.ApacheHC5RDF4JHttpClient;
import org.eclipse.rdf4j.http.client.spi.RDF4JHttpClient;
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

import static org.fairdatateam.fairdatapoint.common.util.HttpUtil.removeLastSlash;

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
            case RdfRepositoryProperties.TYPE_VIRTUOSO -> getVirtuosoRepository();
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

    private Repository getVirtuosoRepository() {
        log.info("Setting up Virtuoso Store");
        String virtuosoUrl = rdfRepositoryProperties.getVirtuoso().getUrl();
        if (!virtuosoUrl.isEmpty()) {
            virtuosoUrl = removeLastSlash(virtuosoUrl);
            final SPARQLRepository repository =
                    new SPARQLRepository(virtuosoUrl + "/sparql-auth");
            final String username = rdfRepositoryProperties.getVirtuoso().getUsername();
            final String password = rdfRepositoryProperties.getVirtuoso().getPassword();
            if (!username.isEmpty() && !password.isEmpty()) {
                final URI uri = URI.create(virtuosoUrl);
                final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(
                        new AuthScope(uri.getHost(), uri.getPort()),
                        new UsernamePasswordCredentials(username, password.toCharArray())
                );
                final RequestConfig requestConfig = RequestConfig.DEFAULT;
                final CloseableHttpClient httpClient = HttpClients.custom()
                        .setDefaultCredentialsProvider(credentialsProvider)
                        .build();
                final RDF4JHttpClient rdf4jHttpClient =
                        primeAndWrapVirtuosoHttpClient(httpClient, requestConfig, virtuosoUrl + "/sparql-auth");
                repository.setHttpClient(rdf4jHttpClient);
            }
            return repository;
        }
        log.warn("'repository.virtuoso.url' is empty");
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

    /**
     * RDF4J's own ApacheHC5RDF4JHttpClient always calls httpClient.executeOpen(host, request, null)
     * -- a fresh, throwaway HttpContext on every single call, with no auth-state reuse across
     * requests. Combined with Virtuoso's behaviour of closing the connection as soon as it reads
     * the headers of an unauthenticated request it doesn't like (rather than waiting to read the
     * body first), this meant every non-trivial request -- e.g. the initial repository-init
     * transaction -- hit a real NoHttpResponseException: the client sent the full body on the
     * first, unauthenticated attempt, Virtuoso hung up before responding, and only a retry (after
     * a long timeout) got through. Confirmed live: this is the same category of problem already
     * solved for cde-box-daemon's own hand-rolled Digest client (Sextans project, unrelated repo)
     * by probing with an empty body first.
     * <p>
     * Fix: reuse a single, mutable HttpClientContext (not RDF4J's transient null) across every
     * request via a thin wrapper subclassing RDF4J's own adapter, and prime that context's
     * AuthCache with one cheap, bodyless GET before the repository is ever used for real
     * SPARQL traffic. Once a context's AuthCache has a successful entry for a host, Apache
     * HttpClient sends the Authorization header preemptively on every subsequent request through
     * that same context -- standard, documented HttpClient behaviour -- so no later request ever
     * risks sending a real payload unauthenticated again.
     * <p>
     * Separately: RDF4J's SPARQLConnection always appends "; " after every SPARQL Update statement
     * it sends. Harmless for GraphDB/Blazegraph/AllegroGraph, but Virtuoso's stricter SPARQL
     * compiler rejects it outright (SP030 syntax error). An HttpRequestInterceptor registered via
     * addRequestInterceptorFirst() was tried first to strip it, and was confirmed (via direct
     * logging of the rewritten body) to correctly remove the trailing semicolon -- yet Virtuoso
     * still hung up with NoHttpResponseException on every real (non-trivial) request. The
     * remaining explanation is a Content-Length/body mismatch: HttpClient5's classic engine
     * appears to fix the Content-Length header from the request's original entity at a point not
     * reliably ordered relative to custom request interceptors, so a body swapped out inside an
     * interceptor can end up shorter than the header already promised, and Virtuoso hangs waiting
     * for bytes that never arrive. Doing the same rewrite here instead, directly on the
     * ClassicHttpRequestBase returned by buildRequest() and strictly before it is ever handed to
     * the connection, removes that ambiguity entirely.
     */
    private RDF4JHttpClient primeAndWrapVirtuosoHttpClient(
            final CloseableHttpClient httpClient,
            final RequestConfig requestConfig,
            final String virtuosoSparqlAuthUrl
    ) {
        final HttpClientContext context = HttpClientContext.create();
        try {
            // A bare GET to /sparql-auth does not trigger Virtuoso's Digest challenge -- only
            // writes are protected there. POST a harmless no-op SPARQL Update (same method, path,
            // and content-type as the real traffic that follows) so the exact same auth realm
            // actually gets challenged and cached.
            final HttpPost warmup = new HttpPost(virtuosoSparqlAuthUrl);
            warmup.setEntity(new UrlEncodedFormEntity(
                    List.of(new BasicNameValuePair("update", "CLEAR GRAPH <urn:fdp:virtuoso-auth-warmup>")),
                    StandardCharsets.UTF_8
            ));
            httpClient.execute(warmup, context, response -> {
                EntityUtils.consumeQuietly(response.getEntity());
                return null;
            });
        }
        catch (IOException exception) {
            log.warn("Failed to prime Virtuoso Digest auth cache -- the first real request "
                    + "may pay the cost of an extra round trip instead", exception);
        }
        return new VirtuosoRDF4JHttpClient(httpClient, requestConfig, context);
    }

    private static final class VirtuosoRDF4JHttpClient extends ApacheHC5RDF4JHttpClient {

        private final CloseableHttpClient httpClient;

        private final HttpContext context;

        VirtuosoRDF4JHttpClient(
                final CloseableHttpClient httpClient,
                final RequestConfig requestConfig,
                final HttpContext context
        ) {
            super(httpClient, 1, requestConfig);
            this.httpClient = httpClient;
            this.context = context;
        }

        @Override
        public org.eclipse.rdf4j.http.client.spi.HttpResponse execute(
                final org.eclipse.rdf4j.http.client.spi.HttpRequest request
        ) throws IOException {
            final HttpUriRequestBase hcRequest = buildRequest(request);
            stripTrailingSparqlUpdateSemicolon(hcRequest);
            final ClassicHttpResponse hcResponse = httpClient.executeOpen(null, hcRequest, context);
            return new ApacheHC5HttpClientResponse(hcResponse);
        }

        private static void stripTrailingSparqlUpdateSemicolon(final ClassicHttpRequest request) throws IOException {
            final HttpEntity entity = request.getEntity();
            if (entity == null || entity.getContentType() == null
                    || !entity.getContentType().startsWith("application/x-www-form-urlencoded")) {
                return;
            }
            final String body;
            try {
                body = EntityUtils.toString(entity);
            }
            catch (org.apache.hc.core5.http.ParseException exception) {
                throw new IOException("Failed to read SPARQL Update request body", exception);
            }
            final List<NameValuePair> params = new ArrayList<>(
                    WWWFormCodec.parse(body, StandardCharsets.UTF_8)
            );
            final List<NameValuePair> rebuilt = new ArrayList<>();
            for (final NameValuePair param : params) {
                if ("update".equals(param.getName()) && param.getValue() != null) {
                    rebuilt.add(new BasicNameValuePair(param.getName(), param.getValue().replaceFirst(";\\s*$", "")));
                }
                else {
                    rebuilt.add(param);
                }
            }
            request.setEntity(new UrlEncodedFormEntity(rebuilt, StandardCharsets.UTF_8));
        }
    }

}
