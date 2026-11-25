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
package org.fairdatateam.fairdatapoint.index.event;

import lombok.extern.slf4j.Slf4j;
import org.fairdatateam.fairdatapoint.index.RepositoryMetadata;
import org.fairdatateam.fairdatapoint.index.http.Exchange;
import org.fairdatateam.fairdatapoint.index.http.ExchangeDirection;
import org.fairdatateam.fairdatapoint.index.http.ExchangeState;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntryService;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.DCAT3;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDP;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.R3D;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class MetadataRetrievalService {

    private static final Integer VERSION = 1;

    private static final List<IRI> REPOSITORY_TYPES = List.of(
            R3D.REPOSITORY,
            FDP.METADATASERVICE
    );

    private static final Map<IRI, String> MAPPING = Map.of(
            DCTERMS.TITLE, "title",
            DCTERMS.DESCRIPTION, "description",
            DCAT3.VERSION, "version",
            DCTERMS.PUBLISHER, "publisher",
            R3D.COUNTRY, "country"
    );

    private static final MediaType TURTLE = MediaType.parseMediaType(RDFFormat.TURTLE.getDefaultMIMEType());

    private final RestClient restClient;

    /**
     * Constructor (autowired)
     */
    public MetadataRetrievalService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public boolean shouldRetrieve(Event triggerEvent, Duration rateLimitWait) {
        if (triggerEvent.getRelatedTo() == null) {
            return false;
        }
        final Instant lastRetrieval = triggerEvent.getRelatedTo().getLastRetrievalTime();
        if (lastRetrieval == null) {
            return true;
        }
        return Duration.between(lastRetrieval, Instant.now()).compareTo(rateLimitWait) > 0;
    }

    public Iterable<Event> prepareEvents(
            Event triggerEvent, IndexEntryService indexEntryService
    ) {
        final ArrayList<Event> events = new ArrayList<>();
        if (triggerEvent.getType() == EventType.IncomingPing) {
            events.add(new Event(VERSION, triggerEvent,
                    triggerEvent.getRelatedTo(), new MetadataRetrieval()));
        }
        else if (triggerEvent.getType() == EventType.AdminTrigger) {
            if (triggerEvent.getAdminTrigger().getClientUrl() == null) {
                indexEntryService.getAllEntries().forEach(
                        entry -> {
                            events.add(
                                    new Event(VERSION, triggerEvent,
                                            entry, new MetadataRetrieval())
                            );
                        }
                );
            }
            else {
                events.add(new Event(VERSION, triggerEvent,
                        triggerEvent.getRelatedTo(), new MetadataRetrieval()));
            }
        }
        return events;
    }

    /**
     * Performs the actual HTTP request that retrieves metadata from an indexed FDP, and returns the result in an
     * {@code Exchange} object. The resulting response body, headers, etc., are copied over to the
     * {@code Exchange.response}. The {@code ExchangeState} is updated based on the request outcome.
     * Note that the HTTP request execution is synchronous, blocking until the HTTP response is obtained.
     */
    public Exchange retrieveRepositoryMetadata(String clientUrl) {
        final Exchange exchange = new Exchange(ExchangeDirection.OUTGOING);
        try {
            final URI clientURI = new URI(clientUrl);
            // execute request (blocking)
            restClient.method(HttpMethod.GET)
                    .uri(clientURI)
                    .headers(httpHeaders -> httpHeaders.setAccept(List.of(TURTLE)))
                    .exchange((request, response) -> {
                        // update exchange with request info
                        exchange.getRequest().setFromHttpRequest(request);
                        exchange.setState(ExchangeState.Requested);
                        // update exchange with response info
                        exchange.getResponse().setFromConvertibleClientHttpResponse(response);
                        if (response.getStatusCode().is2xxSuccessful()) {
                            exchange.setState(ExchangeState.Retrieved);
                        }
                        else {
                            exchange.setState(ExchangeState.Failed);
                        }
                        return exchange.getResponse().getBody();
                    });
        }
        catch (URISyntaxException | ResourceAccessException exception) {
            log.error("Metadata retrieval failed", exception);
            exchange.setState(ExchangeState.Failed);
            exchange.setError(exception.getMessage());
        }
        return exchange;
    }

    public Optional<RepositoryMetadata> parseRepositoryMetadata(
            String metadata
    ) throws IOException {
        final RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
        final StatementCollector collector = new StatementCollector();
        parser.setRDFHandler(collector);

        parser.parse(new StringReader(metadata), String.valueOf(StandardCharsets.UTF_8));
        final ArrayList<Statement> statements = new ArrayList<>(collector.getStatements());

        return findRepository(statements)
                .map(repository -> extractRepositoryMetadata(statements, repository));
    }

    private static RepositoryMetadata extractRepositoryMetadata(
            ArrayList<Statement> statements, Resource repository
    ) {
        final RepositoryMetadata repositoryMetadata = new RepositoryMetadata();
        repositoryMetadata.setMetadataVersion(VERSION);
        repositoryMetadata.setRepositoryUri(repository.toString());

        Value publisher = null;
        for (Statement statement : statements) {
            if (statement.getSubject().equals(repository)) {
                if (MAPPING.containsKey(statement.getPredicate())) {
                    repositoryMetadata.getMetadata()
                            .put(MAPPING.get(
                                    statement.getPredicate()),
                                    statement.getObject().stringValue());
                }
                if (statement.getPredicate().equals(DCTERMS.PUBLISHER)) {
                    publisher = statement.getObject();
                }
            }
        }

        if (publisher != null) {
            for (Statement statement : statements) {
                if (statement.getSubject().equals(publisher)) {
                    if (statement.getPredicate().equals(FOAF.NAME)) {
                        repositoryMetadata.getMetadata()
                                .put("publisherName", statement.getObject().stringValue());
                    }
                }
            }
        }

        return repositoryMetadata;
    }

    private static Optional<Resource> findRepository(ArrayList<Statement> statements) {
        for (Statement statement : statements) {
            if (statement.getPredicate().equals(RDF.TYPE)
                    && REPOSITORY_TYPES.stream().anyMatch(statement.getObject()::equals)) {
                return Optional.of(statement.getSubject());
            }
        }
        return Optional.empty();
    }
}
