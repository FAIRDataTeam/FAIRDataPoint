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
package org.fairdatateam.fairdatapoint.search;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.DCAT;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.fairdatateam.fairdatapoint.WebIntegrationTest;
import org.fairdatateam.fairdatapoint.rdf.metadata.MetadataRdfRepositoryException;
import org.fairdatateam.fairdatapoint.rdf.system.SystemGraphStore;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fairdatateam.fairdatapoint.common.util.SpringResourceReader.loadResource;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.l;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

public class SearchServiceSystemGraphTest extends WebIntegrationTest {

    private static final String TITLE = "System graph probe of the search service";

    private static final IRI RECORD = i("http://example.com/system-graph-probe");

    /** The sub-graph of the system graph seeded by {@link #findByLiteralSkipsSystemGraphs()}. */
    private static final IRI TEST_SYSTEM_GRAPH = FDPRI.systemGraph("test");

    /** Number of distinct {@code GRAPH ?variable} patterns expected in each query file. */
    private static final Map<String, Integer> EXPECTED_GRAPH_VARIABLES = Map.of(
            "/sparql/findChildTitles.sparql", 2,
            "/sparql/findEntityByLiteral.sparql", 4,
            "/sparql/findObjectsForPredicate.sparql", 2,
            "/sparql/getDatasetThemesForCatalog.sparql", 2,
            "/sparql/queryTemplate.sparql", 3
    );

    private static final Pattern GRAPH_VARIABLE = Pattern.compile("(?i)GRAPH\\s+\\?(\\w+)");

    @Autowired
    private SearchService searchService;

    @Autowired
    private Repository repository;

    @AfterEach
    public void clearProbeGraphs() {
        // 'removeAll' no longer wipes the system graphs (see AbstractMetadataRdfRepository), so
        // this test cleans up after itself instead of relying on it
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.clear(TEST_SYSTEM_GRAPH);
            conn.clear(RECORD);
        }
    }

    @Test
    @DisplayName("'findByLiteral' does not return what is only in a system graph")
    public void findByLiteralSkipsSystemGraphs() throws MetadataRdfRepositoryException {
        // GIVEN: a record-looking resource in a sub-graph of the system graph
        storeProbe(TEST_SYSTEM_GRAPH);

        // WHEN:
        final List<SearchResult> results = searchService.findByLiteral(l(TITLE));

        // THEN:
        assertThat(results, is(empty()));
    }

    @Test
    @DisplayName("'findByLiteral' returns the same resource from a record graph")
    public void findByLiteralFindsRecordGraphs() throws MetadataRdfRepositoryException {
        // GIVEN: the same resource, this time in a graph of its own, i.e. as a record
        storeProbe(RECORD);

        // WHEN:
        final List<SearchResult> results = searchService.findByLiteral(l(TITLE));

        // THEN:
        assertThat(results, hasSize(1));
    }

    @Test
    @DisplayName("every graph variable of the search queries is filtered against the system graphs")
    public void queriesFilterEveryGraphVariable() {
        for (Map.Entry<String, Integer> expectation : EXPECTED_GRAPH_VARIABLES.entrySet()) {
            final String queryFile = expectation.getKey();
            final String query = loadResource(queryFile);
            final Matcher graphVariables = GRAPH_VARIABLE.matcher(query);
            final Set<String> variables = new HashSet<>();
            while (graphVariables.find()) {
                final String variable = graphVariables.group(1);
                variables.add(variable);
                assertThat(query, containsString(SystemGraphStore.excludeSystemGraphs(variable)));
            }
            // an unwrapped 'GRAPH ?variable' pattern added later without a matching FILTER would
            // otherwise pass silently: the count catches a missing exclusion as well as a missing pattern
            assertThat(queryFile, variables, hasSize(expectation.getValue()));
        }
    }

    private void storeProbe(IRI graph) {
        try (RepositoryConnection conn = repository.getConnection()) {
            conn.add(RECORD, RDF.TYPE, DCAT.CATALOG, graph);
            conn.add(RECORD, DCTERMS.TITLE, l(TITLE), graph);
        }
    }

}
