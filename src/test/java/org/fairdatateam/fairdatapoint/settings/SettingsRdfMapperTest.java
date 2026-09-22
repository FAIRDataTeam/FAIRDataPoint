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
package org.fairdatateam.fairdatapoint.settings;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.fairdatateam.fairdatapoint.migration.mongodb.development.SettingsFixtures;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.fairdatateam.fairdatapoint.search.SearchFilterType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.l;
import static org.fairdatateam.fairdatapoint.settings.SettingsRdfMapper.SETTINGS_SUBJECT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SettingsRdfMapperTest {

    private static final String ONTOLOGY =
            "/org/fairdatateam/fairdatapoint/rdf/vocabulary/fdp-ri-o.ttl";

    private static final List<String> ENDPOINTS = List.of(
            "https://home.fairdatapoint.org",
            "https://example.com/index",
            "https://example.org/another-index"
    );

    private final SettingsRdfMapper mapper = new SettingsRdfMapper();

    /** The fixture settings, with every optional part filled in and every list longer than one. */
    private Settings settings() {
        final Settings settings = SettingsFixtures.settings();
        settings.setAppTitle("My FAIR Data Point");
        settings.setAppSubtitle("All the settings, set");
        settings.setPing(SettingsPing.builder().enabled(true).endpoints(ENDPOINTS).build());
        settings.getForms().getAutocomplete().setSources(List.of(
                settings.getForms().getAutocomplete().getSources().get(0),
                SettingsAutocompleteSource
                        .builder()
                        .rdfType("https://example.com/ontology#City")
                        .sparqlEndpoint("https://example.com/sparql")
                        .sparqlQuery("SELECT ?entity ?entityLabel WHERE { ?entity a ex:City }")
                        .build()
        ));
        return settings;
    }

    @Test
    @DisplayName("Settings survive a round trip through RDF unchanged")
    public void roundTrip() {
        // GIVEN:
        final Settings settings = settings();

        // WHEN:
        final Optional<Settings> read = this.mapper.fromModel(this.mapper.toModel(settings));

        // THEN:
        assertThat(read.isPresent(), is(true));
        assertThat(read.get(), is(equalTo(settings)));
    }

    @Test
    @DisplayName("A round trip preserves the order of every list the user can order")
    public void roundTripPreservesOrder() {
        // GIVEN:
        final Settings settings = settings();

        // WHEN:
        final Settings read = this.mapper.fromModel(this.mapper.toModel(settings)).orElseThrow();

        // THEN: the metrics, the search filters and their preset values keep their order
        assertThat(
                read.getMetadataMetrics().stream().map(SettingsMetricsEntry::getMetricUri).toList(),
                contains("https://purl.org/fair-metrics/FM_F1A", "https://purl.org/fair-metrics/FM_A1.1")
        );
        assertThat(
                read.getSearchFilters().stream().map(SettingsSearchFilter::getLabel).toList(),
                contains("Type", "License", "Version")
        );
        assertThat(
                read.getSearchFilters().get(0).getPresetValues().stream()
                        .map(SettingsSearchFilterItem::getLabel).toList(),
                contains("Catalog", "Dataset", "Distribution", "Data Service", "Metadata Service", "FAIR Data Point")
        );

        // AND: so do the ping endpoints and the autocomplete sources
        assertThat(read.getPing().getEndpoints(), is(equalTo(ENDPOINTS)));
        assertThat(
                read.getForms().getAutocomplete().getSources().stream()
                        .map(SettingsAutocompleteSource::getRdfType).toList(),
                contains("https://example.com/ontology#Country", "https://example.com/ontology#City")
        );
    }

    @Test
    @DisplayName("The model describes one subject, typed as the settings of the implementation")
    public void oneTypedSubject() {
        // WHEN:
        final Model model = this.mapper.toModel(settings());

        // THEN:
        assertThat(model.filter(null, RDF.TYPE, FDPRI.SETTINGS), hasSize(1));
        assertThat(Models.subjectIRIs(model), contains(SETTINGS_SUBJECT));
        assertThat(
                SETTINGS_SUBJECT.stringValue(),
                is(equalTo("https://w3id.org/fdp/fdp-ri-o/system/settings#settings"))
        );
    }

    @Test
    @DisplayName("A search filter is one blank node with its type, label, predicate and position")
    public void searchFilterShape() {
        // WHEN:
        final Model model = this.mapper.toModel(settings());

        // THEN: the second filter of the fixtures is described as specified
        final Resource filter = filterLabelled(model, "License");
        assertThat(model.filter(filter, null, null), hasSize(6));
        assertThat(model.contains(filter, RDF.TYPE, FDPRI.SETTINGS_SEARCH_FILTER), is(true));
        assertThat(
                Models.getPropertyString(model, filter, FDPRI.FILTER_TYPE).orElseThrow(),
                is(equalTo(SearchFilterType.IRI.name()))
        );
        assertThat(
                Models.getPropertyIRI(model, filter, FDPRI.PREDICATE).orElseThrow(),
                is(equalTo(i("http://purl.org/dc/terms/license")))
        );
        final Literal queryFromRecords =
                Models.getPropertyLiteral(model, filter, FDPRI.QUERY_FROM_RECORDS).orElseThrow();
        assertThat(queryFromRecords.getDatatype(), is(equalTo(XSD.BOOLEAN)));
        assertThat(queryFromRecords.booleanValue(), is(true));
        final Literal order = Models.getPropertyLiteral(model, filter, FDPRI.ORDER).orElseThrow();
        assertThat(order.getDatatype(), is(equalTo(XSD.INTEGER)));
        assertThat(order.intValue(), is(equalTo(1)));
    }

    @Test
    @DisplayName("A preset value keeps its value, its caption and its position")
    public void presetValueShape() {
        // WHEN:
        final Model model = this.mapper.toModel(settings());

        // THEN:
        final Resource filter = filterLabelled(model, "Type");
        final List<Resource> values = List.copyOf(Models.getPropertyResources(model, filter, FDPRI.PRESET_VALUE));
        assertThat(values, hasSize(6));
        final Resource catalog = values.stream()
                .filter(value -> "Catalog".equals(Models.getPropertyString(model, value, RDFS.LABEL).orElse(null)))
                .findFirst()
                .orElseThrow();
        assertThat(
                Models.getPropertyString(model, catalog, RDF.VALUE).orElseThrow(),
                is(equalTo("http://www.w3.org/ns/dcat#Catalog"))
        );
        assertThat(Models.getPropertyLiteral(model, catalog, FDPRI.ORDER).orElseThrow().intValue(), is(equalTo(0)));
    }

    @Test
    @DisplayName("The ping endpoints are an rdf:List, so their order needs no further statement")
    public void endpointsAreAnRdfList() {
        // WHEN:
        final Model model = this.mapper.toModel(settings());

        // THEN: the list holds the endpoints as literals, in order, and ends in rdf:nil
        final Resource ping = Models.getPropertyResource(model, SETTINGS_SUBJECT, FDPRI.PING).orElseThrow();
        Resource node = Models.getPropertyResource(model, ping, FDPRI.ENDPOINT).orElseThrow();
        for (String endpoint : ENDPOINTS) {
            final Value first = Models.getProperty(model, node, RDF.FIRST).orElseThrow();
            assertThat("the endpoint is kept as a literal", first instanceof Literal, is(true));
            assertThat(first.stringValue(), is(equalTo(endpoint)));
            node = Models.getPropertyResource(model, node, RDF.REST).orElseThrow();
        }
        assertThat(node, is(equalTo((Resource) RDF.NIL)));
    }

    @Test
    @DisplayName("Ping settings without an endpoint hold the empty list")
    public void emptyEndpoints() {
        // GIVEN:
        final Settings settings = settings();
        settings.setPing(SettingsPing.builder().enabled(false).endpoints(List.of()).build());

        // WHEN:
        final Model model = this.mapper.toModel(settings);

        // THEN:
        final Resource ping = Models.getPropertyResource(model, SETTINGS_SUBJECT, FDPRI.PING).orElseThrow();
        assertThat(Models.getPropertyIRI(model, ping, FDPRI.ENDPOINT).orElseThrow(), is(equalTo(RDF.NIL)));
        assertThat(this.mapper.fromModel(model).orElseThrow().getPing().getEndpoints(), is(empty()));
    }

    @Test
    @DisplayName("A graph without settings is read as no settings at all")
    public void noSettings() {
        assertThat(this.mapper.fromModel(new LinkedHashModel()).isPresent(), is(false));
    }

    @Test
    @DisplayName("Settings that hold nothing but their type are read back, with their defaults")
    public void onlyTheType() {
        // GIVEN: settings that were written by an older version, which knew fewer properties
        final Model model = new LinkedHashModel();
        model.add(SETTINGS_SUBJECT, RDF.TYPE, FDPRI.SETTINGS);

        // WHEN:
        final Settings settings = this.mapper.fromModel(model).orElseThrow();

        // THEN: what is absent is null, and the lists are empty rather than null
        assertThat(settings.getAppTitle(), is(nullValue()));
        assertThat(settings.getAppSubtitle(), is(nullValue()));
        assertThat(settings.getMetadataMetrics(), is(empty()));
        assertThat(settings.getSearchFilters(), is(empty()));

        // AND: the ping and the forms fall back on their defaults, as they do for settings that
        // never had any
        assertThat(settings.getPing(), is(equalTo(Settings.getDefault().getPing())));
        assertThat(settings.getForms(), is(equalTo(Settings.getDefault().getForms())));
    }

    @Test
    @DisplayName("Every term the mapper writes is declared in the ontology")
    public void ontologyDeclaresEveryTerm() throws IOException {
        // GIVEN:
        final Model ontology;
        try (InputStream input = SettingsRdfMapperTest.class.getResourceAsStream(ONTOLOGY)) {
            ontology = Rio.parse(input, "", RDFFormat.TURTLE);
        }

        // WHEN:
        final Model model = this.mapper.toModel(settings());

        // THEN: every term of the implementation vocabulary that is used is described there
        final List<IRI> used = model.predicates().stream()
                .filter(predicate -> predicate.stringValue().startsWith(FDPRI.NAMESPACE))
                .toList();
        assertThat(used, is(not(empty())));
        for (IRI predicate : used) {
            assertThat(predicate.stringValue(), ontology.contains(predicate, null, null), is(true));
        }
        assertThat(FDPRI.SETTINGS.stringValue(), ontology.contains(FDPRI.SETTINGS, null, null), is(true));

        // AND: every fdp-ri: class the mapper types a blank node with is described there too
        final List<IRI> types = model
                .filter(null, RDF.TYPE, null)
                .objects()
                .stream()
                .filter(type -> type.stringValue().startsWith(FDPRI.NAMESPACE))
                .map(type -> (IRI) type)
                .distinct()
                .toList();
        assertThat(types, is(not(empty())));
        for (IRI type : types) {
            assertThat(type.stringValue(), ontology.contains(type, null, null), is(true));
        }
    }

    @Test
    @DisplayName("Two siblings with the same order are read in a deterministic, identifier-based order")
    public void sameOrderIsBrokenByIdentifier() {
        // GIVEN: two metrics with the same fdp-ri:order, added in reverse identifier order
        final Model model = new LinkedHashModel();
        model.add(SETTINGS_SUBJECT, RDF.TYPE, FDPRI.SETTINGS);
        final IRI nodeB = i("https://example.com/metric/b");
        final IRI nodeA = i("https://example.com/metric/a");
        for (IRI node : List.of(nodeB, nodeA)) {
            model.add(SETTINGS_SUBJECT, FDPRI.METRIC, node);
            model.add(node, FDPRI.ORDER, l(0));
        }
        model.add(nodeA, FDPRI.METRIC_URI, i("https://example.com/a"));
        model.add(nodeB, FDPRI.METRIC_URI, i("https://example.com/b"));

        // WHEN:
        final List<SettingsMetricsEntry> metrics = this.mapper.fromModel(model).orElseThrow().getMetadataMetrics();

        // THEN: sorted by node identifier, regardless of the order they were added in
        assertThat(
                metrics.stream().map(SettingsMetricsEntry::getMetricUri).toList(),
                contains("https://example.com/a", "https://example.com/b")
        );
    }

    @Test
    @DisplayName("Siblings without any order are all read, in a stable order")
    public void noOrderReadsEveryNode() {
        // GIVEN: two metrics, neither of which has an fdp-ri:order
        final Model model = new LinkedHashModel();
        model.add(SETTINGS_SUBJECT, RDF.TYPE, FDPRI.SETTINGS);
        final IRI nodeA = i("https://example.com/metric/a");
        final IRI nodeB = i("https://example.com/metric/b");
        model.add(SETTINGS_SUBJECT, FDPRI.METRIC, nodeA);
        model.add(SETTINGS_SUBJECT, FDPRI.METRIC, nodeB);
        model.add(nodeA, FDPRI.METRIC_URI, i("https://example.com/a"));
        model.add(nodeB, FDPRI.METRIC_URI, i("https://example.com/b"));

        // WHEN:
        final List<SettingsMetricsEntry> metrics = this.mapper.fromModel(model).orElseThrow().getMetadataMetrics();

        // THEN: both are read, sorted by their identifier so the order is stable across reads
        assertThat(metrics, hasSize(2));
        assertThat(
                metrics.stream().map(SettingsMetricsEntry::getMetricUri).toList(),
                contains("https://example.com/a", "https://example.com/b")
        );
    }

    @Test
    @DisplayName("A non-numeric order is unreadable, and the node is sorted last")
    public void nonNumericOrderSortsLast() {
        // GIVEN: a first metric with a proper order, and a second one whose order is not a number
        final Model model = new LinkedHashModel();
        model.add(SETTINGS_SUBJECT, RDF.TYPE, FDPRI.SETTINGS);
        final IRI first = i("https://example.com/metric/first");
        model.add(SETTINGS_SUBJECT, FDPRI.METRIC, first);
        model.add(first, FDPRI.ORDER, l(0));
        model.add(first, FDPRI.METRIC_URI, i("https://example.com/first"));
        final IRI second = i("https://example.com/metric/second");
        model.add(SETTINGS_SUBJECT, FDPRI.METRIC, second);
        model.add(second, FDPRI.ORDER, l("not-a-number"));
        model.add(second, FDPRI.METRIC_URI, i("https://example.com/second"));

        // WHEN:
        final List<SettingsMetricsEntry> metrics = this.mapper.fromModel(model).orElseThrow().getMetadataMetrics();

        // THEN:
        assertThat(
                metrics.stream().map(SettingsMetricsEntry::getMetricUri).toList(),
                contains("https://example.com/first", "https://example.com/second")
        );
    }

    @Test
    @DisplayName("A non-boolean enabled flag is unreadable, and ping is read as disabled")
    public void nonBooleanEnabledReadsAsDisabled() {
        // GIVEN: a ping whose fdp-ri:enabled is not a boolean literal
        final Model model = new LinkedHashModel();
        model.add(SETTINGS_SUBJECT, RDF.TYPE, FDPRI.SETTINGS);
        final IRI ping = i("https://example.com/ping");
        model.add(SETTINGS_SUBJECT, FDPRI.PING, ping);
        model.add(ping, FDPRI.ENABLED, l("not-a-boolean"));
        model.add(ping, FDPRI.ENDPOINT, RDF.NIL);

        // WHEN:
        final SettingsPing settingsPing = this.mapper.fromModel(model).orElseThrow().getPing();

        // THEN:
        assertThat(settingsPing.isEnabled(), is(false));
    }

    @Test
    @DisplayName("An unknown filter type is read as null")
    public void unknownFilterTypeReadsAsNull() {
        // GIVEN: a search filter whose fdp-ri:filterType is not one of the known values
        final Model model = new LinkedHashModel();
        model.add(SETTINGS_SUBJECT, RDF.TYPE, FDPRI.SETTINGS);
        final IRI filter = i("https://example.com/filter");
        model.add(SETTINGS_SUBJECT, FDPRI.SEARCH_FILTER, filter);
        model.add(filter, FDPRI.FILTER_TYPE, l("NOT_A_KNOWN_TYPE"));

        // WHEN:
        final SettingsSearchFilter read = this.mapper.fromModel(model).orElseThrow().getSearchFilters().get(0);

        // THEN:
        assertThat(read.getType(), is(nullValue()));
    }

    @Test
    @DisplayName("A malformed endpoint list is read as empty")
    public void malformedEndpointListReadsAsEmpty() {
        // GIVEN: an endpoint list that never reaches rdf:nil
        final Model model = new LinkedHashModel();
        model.add(SETTINGS_SUBJECT, RDF.TYPE, FDPRI.SETTINGS);
        final IRI ping = i("https://example.com/ping");
        model.add(SETTINGS_SUBJECT, FDPRI.PING, ping);
        final IRI head = i("https://example.com/ping/head");
        model.add(ping, FDPRI.ENDPOINT, head);
        model.add(head, RDF.FIRST, l("https://home.fairdatapoint.org"));

        // WHEN:
        final List<String> endpoints = this.mapper.fromModel(model).orElseThrow().getPing().getEndpoints();

        // THEN:
        assertThat(endpoints, is(empty()));
    }

    @Test
    @DisplayName("A relative predicate cannot be written as an IRI")
    public void toModelThrowsOnRelativePredicate() {
        // GIVEN: a search filter whose predicate is not an absolute IRI
        final Settings settings = settings();
        settings.setSearchFilters(List.of(SettingsSearchFilter
                .builder()
                .type(SearchFilterType.IRI)
                .label("Bad filter")
                .predicate("not-an-iri")
                .presetValues(List.of())
                .queryFromRecords(false)
                .build()
        ));

        // WHEN/THEN:
        assertThrows(IllegalArgumentException.class, () -> this.mapper.toModel(settings));
    }

    private static Resource filterLabelled(Model model, String label) {
        return Models.getPropertyResources(model, SETTINGS_SUBJECT, FDPRI.SEARCH_FILTER).stream()
                .filter(filter -> label.equals(Models.getPropertyString(model, filter, RDFS.LABEL).orElse(null)))
                .findFirst()
                .orElseThrow();
    }

}
