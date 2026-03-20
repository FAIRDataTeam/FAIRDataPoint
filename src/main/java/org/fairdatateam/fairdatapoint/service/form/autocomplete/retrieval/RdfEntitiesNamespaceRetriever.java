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

import lombok.SneakyThrows;
import nl.dtls.fairdatapoint.entity.forms.RdfEntitySourceType;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static nl.dtls.fairdatapoint.util.RdfUtil.getStringObjectBy;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Component
public class RdfEntitiesNamespaceRetriever implements RdfEntitiesRetriever {

    private static final String SEP_FRAGMENT = "#";

    @Override
    public Map<String, String> retrieve(String rdfType) {
        return retrieve(getNamespaceUrl(rdfType), i(rdfType));
    }

    private Map<String, String> retrieve(URL namespaceUrl, IRI rdfType) {
        final Repository repository = new SailRepository(new MemoryStore());
        final Map<String, String> entities = new HashMap<>();
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.add(namespaceUrl);
            conn.getStatements(null, RDF.TYPE, rdfType)
                    .forEach(statement -> {
                        final Resource subject = statement.getSubject();
                        final Model model = QueryResults.asModel(
                                conn.getStatements(subject, null, null)
                        );
                        final String label = getStringObjectBy(model, subject, RDFS.LABEL);
                        if (label != null) {
                            entities.put(subject.stringValue(), label);
                        }
                    });
            return entities;
        }
        catch (Exception exception) {
            return null;
        }
    }

    @SneakyThrows
    private URL getNamespaceUrl(String rdfType) {
        if (rdfType.contains(SEP_FRAGMENT)) {
            return new URL(rdfType.split(SEP_FRAGMENT)[0]);
        }
        return new URL(rdfType.substring(0, rdfType.lastIndexOf("/")));
    }

    @Override
    public RdfEntitySourceType getSourceType() {
        return RdfEntitySourceType.NAMESPACE;
    }

    public static void main(String[] args) {
        // TODO: remove (just for dev/testing out)
        final RdfEntitiesNamespaceRetriever retriever = new RdfEntitiesNamespaceRetriever();
        final Map<String, String> result = retriever.retrieve("http://purl.org/dc/terms/AgentClass");
        if (result != null) {
            result.forEach((entity, label) -> {
                System.out.printf("%s => %s%n", entity, label);
            });
        }
    }
}
