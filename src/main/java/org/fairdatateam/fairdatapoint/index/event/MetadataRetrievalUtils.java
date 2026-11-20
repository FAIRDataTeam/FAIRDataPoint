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

import org.fairdatateam.fairdatapoint.index.RepositoryMetadata;
import org.fairdatateam.fairdatapoint.index.entry.IndexEntry;
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
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MetadataRetrievalUtils {

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

    // todo: replace by RestClient (#1035)
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    /**
     * Returns true if the event has an index entry and the retrieval rate limit has not been exceeded.
     */
    public static boolean shouldRetrieve(Event triggerEvent, Duration rateLimitWait) {
        final IndexEntry indexEntry = triggerEvent.getRelatedTo();
        if (indexEntry == null) {
            return false;
        }
        final Instant lastRetrieval = indexEntry.getLastRetrievalTime();
        if (lastRetrieval == null) {
            return true;
        }
        return Duration.between(lastRetrieval, Instant.now()).compareTo(rateLimitWait) > 0;
    }

    /**
     * Returns a list of metadata retrieval Events. If the trigger event is an incoming ping or an admin trigger with a
     * clientUrl, the list contains a single event. If the trigger event is an admin trigger without clientUrl, the list
     * contains events for all registered index entries (FDPs).
     */
    public static Iterable<Event> prepareEvents(
            Event triggerEvent, IndexEntryService indexEntryService
    ) {
        final ArrayList<Event> events = new ArrayList<>();
        if (triggerEvent.getType() == EventType.IncomingPing) {
            // create a single retrieval event for the index entry from the incoming ping event
            events.add(new Event(VERSION, triggerEvent, triggerEvent.getRelatedTo(), new MetadataRetrieval()));
        }
        else if (triggerEvent.getType() == EventType.AdminTrigger) {
            if (triggerEvent.getAdminTrigger().getClientUrl() == null) {
                // create retrieval events for *all* registered index entries
                indexEntryService.getAllEntries().forEach(
                        entry -> {
                            events.add(new Event(VERSION, triggerEvent, entry, new MetadataRetrieval()));
                        }
                );
            }
            else {
                // create a single retrieval event for the index entry from the admin trigger event
                events.add(new Event(VERSION, triggerEvent, triggerEvent.getRelatedTo(), new MetadataRetrieval()));
            }
        }
        return events;
    }

    /**
     * Performs the actual HTTP request that retrieves metadata from an indexed FDP.
     * This method is synchronous, blocking until the HTTP response is obtained.
     * The resulting response body, headers, etc., are copied over to the {@code Exchange.response} in the specified
     * {@code retrievalEvent}. Also updates the {@code ExchangeState}.
     */
    public static void retrieveRepositoryMetadata(Event retrievalEvent, Duration timeout) {
        if (retrievalEvent.getType() != EventType.MetadataRetrieval) {
            throw new IllegalArgumentException("Invalid event type");
        }
        final Exchange ex = new Exchange(ExchangeDirection.OUTGOING);
        retrievalEvent.getMetadataRetrieval().setExchange(ex);
        try {
            // define request
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(retrievalEvent.getRelatedTo().getClientUrl()))
                    .timeout(timeout)
                    .header(HttpHeaders.ACCEPT, RDFFormat.TURTLE.getDefaultMIMEType())
                    .GET().build();
            ex.getRequest().setFromHttpRequest(request);
            ex.setState(ExchangeState.Requested);
            // execute request (blocking)
            final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            ex.getResponse().setFromHttpResponse(response);
            ex.setState(ExchangeState.Retrieved);
        }
        catch (InterruptedException exception) {
            ex.setState(ExchangeState.Timeout);
            ex.setError("Timeout");
        }
        catch (IllegalArgumentException exception) {
            ex.setState(ExchangeState.Failed);
            ex.setError("Invalid URI: " + exception.getMessage());
        }
        catch (IOException exception) {
            ex.setState(ExchangeState.Failed);
            ex.setError("IO error: " + exception.getMessage());
        }
    }

    public static Optional<RepositoryMetadata> parseRepositoryMetadata(
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
