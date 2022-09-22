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
package nl.dtls.fairdatapoint.service.form.autocomplete.retrieval;

import nl.dtls.fairdatapoint.entity.forms.RdfEntitySourceType;
import nl.dtls.fairdatapoint.entity.settings.SettingsAutocompleteSource;
import nl.dtls.fairdatapoint.service.settings.SettingsService;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RdfEntitiesSparqlRetriever implements RdfEntitiesRetriever {

    private static final String KEY_URI = "entity";
    private static final String KEY_LABEL = "entityLabel";

    @Autowired
    private SettingsService settingsService;

    @Override
    public Map<String, String> retrieve(String rdfType) {
        return settingsService
                .getOrDefaults()
                .getForms()
                .getAutocomplete()
                .getSources()
                .stream()
                .filter(source -> source.getRdfType().equals(rdfType))
                .findFirst()
                .map(this::retrieve)
                .orElse(null);
    }

    private Map<String, String> retrieve(SettingsAutocompleteSource source) {
        final Repository repository = new SPARQLRepository(source.getSparqlEndpoint());
        try (RepositoryConnection conn = repository.getConnection()) {
            final TupleQuery query = conn.prepareTupleQuery(source.getSparqlQuery());
            final TupleQueryResult result = query.evaluate();
            final Map<String, String> entities = new HashMap<>();
            result
                    .stream()
                    .filter(bindings -> bindings.hasBinding(KEY_LABEL) && bindings.hasBinding(KEY_URI))
                    .forEach(bindings -> {
                        entities.put(
                                bindings.getValue(KEY_URI).stringValue(),
                                bindings.getValue(KEY_LABEL).stringValue()
                        );
                    });
            return entities;
        }
        catch (Exception exception) {
            return null;
        }
    }

    @Override
    public RdfEntitySourceType getSourceType() {
        return RdfEntitySourceType.SPARQL;
    }

    public static void main(String[] args) {
        // TODO: remove (just for dev/testing out)
        final RdfEntitiesSparqlRetriever retriever = new RdfEntitiesSparqlRetriever();
        final Map<String, String> result = retriever.retrieve(
                SettingsAutocompleteSource
                        .builder()
                        .rdfType("https://example.com/ontology#Country")
                        .sparqlEndpoint("https://query.wikidata.org/sparql")
                        .sparqlQuery("""
                                SELECT DISTINCT ?entity ?entityLabel
                                WHERE {
                                  ?entity wdt:P31 wd:Q6256 .
                                  SERVICE wikibase:label { bd:serviceParam wikibase:language "en" }
                                }
                                """)
                        .build()
        );
        if (result != null) {
            result.forEach((entity, label) -> {
                System.out.printf("%s => %s%n", entity, label);
            });
        }
    }
}
