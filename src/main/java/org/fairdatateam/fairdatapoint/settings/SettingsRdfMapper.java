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

import lombok.extern.slf4j.Slf4j;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.util.ModelException;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.util.RDFCollections;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.fairdatateam.fairdatapoint.search.SearchFilterType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.bn;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.l;

/**
 * Translates the {@link Settings} of this installation to the RDF kept in the settings sub-graph
 * of the system graph, and back.
 *
 * <p>The graph describes one subject, {@link #SETTINGS_SUBJECT}; every nested structure is a blank
 * node below it, typed with the class its property range declares in {@code fdp-ri-o.ttl}. Wherever
 * the order of the settings is visible to the user it is written down, as {@code fdp-ri:order}
 * (counted from zero) or, for the ping endpoints, as an {@code rdf:List}; reading them back restores
 * exactly the order that was saved. Reading does not depend on the type of a node: only on reaching
 * it through the expected property, so that a graph that somehow lost its types is still read back.
 *
 * <pre>
 * &lt;https://w3id.org/fdp/fdp-ri-o/system/settings#settings&gt;
 *     a fdp-ri:Settings ;
 *     fdp-ri:searchFilter [
 *         a fdp-ri:SettingsSearchFilter ;
 *         fdp-ri:filterType "IRI" ;
 *         rdfs:label "License" ;
 *         fdp-ri:predicate &lt;http://purl.org/dc/terms/license&gt; ;
 *         fdp-ri:queryFromRecords true ;
 *         fdp-ri:order 1
 *     ] .
 * </pre>
 *
 * <p>A property that is absent from the graph is read back as {@code null}, except for the lists:
 * those are read back as an empty list, because RDF cannot tell an absent list from an empty one.
 */
@Slf4j
@Component
public class SettingsRdfMapper {

    /** The single subject the settings graph describes. */
    public static final IRI SETTINGS_SUBJECT = i(FDPRI.SETTINGS_GRAPH.stringValue() + "#settings");

    /** Order of a node that has none; such a node is sorted after the nodes that have one. */
    private static final int NO_ORDER = Integer.MAX_VALUE;

    /**
     * Describes the settings as the content of the settings graph.
     *
     * @param settings the settings to describe
     * @return the model to store, without a context: the caller decides which graph it goes into
     */
    public Model toModel(Settings settings) {
        final Model model = new LinkedHashModel();
        model.add(SETTINGS_SUBJECT, RDF.TYPE, FDPRI.SETTINGS);
        addLiteral(model, SETTINGS_SUBJECT, FDPRI.APP_TITLE, settings.getAppTitle());
        addLiteral(model, SETTINGS_SUBJECT, FDPRI.APP_SUBTITLE, settings.getAppSubtitle());
        addMetrics(model, settings.getMetadataMetrics());
        addPing(model, settings.getPing());
        addSearchFilters(model, settings.getSearchFilters());
        addAutocomplete(model, settings.getForms().getAutocomplete());
        return model;
    }

    /**
     * Reads back the settings from the content of the settings graph.
     *
     * @param model the statements of the settings graph
     * @return the settings, or empty if the graph holds no {@code fdp-ri:Settings}
     */
    public Optional<Settings> fromModel(Model model) {
        if (!model.contains(SETTINGS_SUBJECT, RDF.TYPE, FDPRI.SETTINGS)) {
            return Optional.empty();
        }
        return Optional.of(Settings
                .builder()
                .appTitle(string(model, SETTINGS_SUBJECT, FDPRI.APP_TITLE))
                .appSubtitle(string(model, SETTINGS_SUBJECT, FDPRI.APP_SUBTITLE))
                .metadataMetrics(metrics(model))
                .ping(ping(model))
                .searchFilters(searchFilters(model))
                .forms(forms(model))
                .build()
        );
    }

    private static void addMetrics(Model model, List<SettingsMetricsEntry> metrics) {
        addOrdered(model, SETTINGS_SUBJECT, FDPRI.METRIC, FDPRI.SETTINGS_METRICS_ENTRY, metrics, (node, metric) -> {
            addIri(model, node, FDPRI.METRIC_URI, metric.getMetricUri());
            addIri(model, node, FDPRI.RESOURCE_URI, metric.getResourceUri());
        });
    }

    private static void addPing(Model model, SettingsPing ping) {
        if (ping == null) {
            return;
        }
        final Resource node = bn();
        model.add(SETTINGS_SUBJECT, FDPRI.PING, node);
        model.add(node, RDF.TYPE, FDPRI.SETTINGS_PING);
        model.add(node, FDPRI.ENABLED, l(ping.isEnabled()));
        final List<String> endpoints = ping.getEndpoints() == null ? List.of() : ping.getEndpoints();
        if (endpoints.isEmpty()) {
            model.add(node, FDPRI.ENDPOINT, RDF.NIL);
            return;
        }
        final Resource head = bn();
        RDFCollections.asRDF(endpoints.stream().map(endpoint -> (Value) l(endpoint)).toList(), head, model);
        model.add(node, FDPRI.ENDPOINT, head);
    }

    private static void addSearchFilters(Model model, List<SettingsSearchFilter> filters) {
        addOrdered(model, SETTINGS_SUBJECT, FDPRI.SEARCH_FILTER, FDPRI.SETTINGS_SEARCH_FILTER, filters,
                (node, filter) -> {
                    if (filter.getType() != null) {
                        model.add(node, FDPRI.FILTER_TYPE, l(filter.getType().name()));
                    }
                    addLiteral(model, node, RDFS.LABEL, filter.getLabel());
                    addIri(model, node, FDPRI.PREDICATE, filter.getPredicate());
                    model.add(node, FDPRI.QUERY_FROM_RECORDS, l(filter.isQueryFromRecords()));
                    addPresetValues(model, node, filter.getPresetValues());
                }
        );
    }

    private static void addPresetValues(Model model, Resource filter, List<SettingsSearchFilterItem> values) {
        addOrdered(model, filter, FDPRI.PRESET_VALUE, FDPRI.SETTINGS_SEARCH_FILTER_ITEM, values, (node, value) -> {
            addLiteral(model, node, RDF.VALUE, value.getValue());
            addLiteral(model, node, RDFS.LABEL, value.getLabel());
        });
    }

    private static void addAutocomplete(Model model, SettingsFormsAutocomplete autocomplete) {
        if (autocomplete == null) {
            return;
        }
        final Resource node = bn();
        model.add(SETTINGS_SUBJECT, FDPRI.AUTOCOMPLETE, node);
        model.add(node, RDF.TYPE, FDPRI.SETTINGS_AUTOCOMPLETE);
        if (autocomplete.getSearchNamespace() != null) {
            model.add(node, FDPRI.SEARCH_NAMESPACE, l(autocomplete.getSearchNamespace()));
        }
        addAutocompleteSources(model, node, autocomplete.getSources());
    }

    private static void addAutocompleteSources(
            Model model, Resource autocomplete, List<SettingsAutocompleteSource> sources
    ) {
        addOrdered(model, autocomplete, FDPRI.AUTOCOMPLETE_SOURCE, FDPRI.SETTINGS_AUTOCOMPLETE_SOURCE, sources,
                (node, source) -> {
                    addIri(model, node, FDPRI.SOURCE_RDF_TYPE, source.getRdfType());
                    addIri(model, node, FDPRI.SPARQL_ENDPOINT, source.getSparqlEndpoint());
                    addLiteral(model, node, FDPRI.SPARQL_QUERY, source.getSparqlQuery());
                }
        );
    }

    private static void addLiteral(Model model, Resource subject, IRI predicate, String value) {
        if (value != null) {
            model.add(subject, predicate, l(value));
        }
    }

    /**
     * Adds a statement whose object is the given IRI, if there is one.
     *
     * @param model the model to add to
     * @param subject subject of the statement
     * @param predicate predicate of the statement
     * @param value the IRI, as it is held by the settings
     * @throws IllegalArgumentException if the value is not an absolute IRI
     */
    private static void addIri(Model model, Resource subject, IRI predicate, String value) {
        if (value != null) {
            model.add(subject, predicate, i(value));
        }
    }

    /**
     * Creates one blank node per item of an ordered list, links it to its subject, types it, fills
     * it in with the given callback and writes its {@code fdp-ri:order}.
     *
     * @param model the model to add to
     * @param subject subject the nodes hang on
     * @param predicate property relating the subject to each node
     * @param type class the node is typed with
     * @param items the items to write, in order; nothing is written if this is {@code null}
     * @param fill fills in the node with the properties specific to one item
     * @param <T> type of the items
     */
    private static <T> void addOrdered(
            Model model, Resource subject, IRI predicate, IRI type, List<T> items, BiConsumer<Resource, T> fill
    ) {
        if (items == null) {
            return;
        }
        for (int index = 0; index < items.size(); index++) {
            final T item = items.get(index);
            final Resource node = bn();
            model.add(subject, predicate, node);
            model.add(node, RDF.TYPE, type);
            fill.accept(node, item);
            model.add(node, FDPRI.ORDER, l(index));
        }
    }

    private static List<SettingsMetricsEntry> metrics(Model model) {
        return readOrdered(model, SETTINGS_SUBJECT, FDPRI.METRIC, node -> metric(model, node));
    }

    private static SettingsMetricsEntry metric(Model model, Resource node) {
        return SettingsMetricsEntry
                .builder()
                .metricUri(string(model, node, FDPRI.METRIC_URI))
                .resourceUri(string(model, node, FDPRI.RESOURCE_URI))
                .build();
    }

    private static SettingsPing ping(Model model) {
        final Optional<Resource> node = Models.getPropertyResource(model, SETTINGS_SUBJECT, FDPRI.PING);
        if (node.isEmpty()) {
            return Settings.getDefault().getPing();
        }
        return SettingsPing
                .builder()
                .enabled(Boolean.TRUE.equals(bool(model, node.get(), FDPRI.ENABLED)))
                .endpoints(endpoints(model, node.get()))
                .build();
    }

    private static List<String> endpoints(Model model, Resource ping) {
        final Optional<Resource> head = Models.getPropertyResource(model, ping, FDPRI.ENDPOINT);
        if (head.isEmpty()) {
            return List.of();
        }
        try {
            return RDFCollections
                    .asValues(model, head.get(), new ArrayList<Value>())
                    .stream()
                    .map(Value::stringValue)
                    .toList();
        }
        catch (ModelException exception) {
            log.warn("The settings graph holds a malformed list of ping endpoints; it is read as empty", exception);
            return List.of();
        }
    }

    private static List<SettingsSearchFilter> searchFilters(Model model) {
        return readOrdered(model, SETTINGS_SUBJECT, FDPRI.SEARCH_FILTER, node -> searchFilter(model, node));
    }

    private static SettingsSearchFilter searchFilter(Model model, Resource node) {
        return SettingsSearchFilter
                .builder()
                .type(filterType(model, node))
                .label(string(model, node, RDFS.LABEL))
                .predicate(string(model, node, FDPRI.PREDICATE))
                .presetValues(presetValues(model, node))
                .queryFromRecords(Boolean.TRUE.equals(bool(model, node, FDPRI.QUERY_FROM_RECORDS)))
                .build();
    }

    private static List<SettingsSearchFilterItem> presetValues(Model model, Resource filter) {
        return readOrdered(model, filter, FDPRI.PRESET_VALUE, node -> presetValue(model, node));
    }

    private static SettingsSearchFilterItem presetValue(Model model, Resource node) {
        return SettingsSearchFilterItem
                .builder()
                .value(string(model, node, RDF.VALUE))
                .label(string(model, node, RDFS.LABEL))
                .build();
    }

    private static SettingsForms forms(Model model) {
        final Optional<Resource> node = Models.getPropertyResource(model, SETTINGS_SUBJECT, FDPRI.AUTOCOMPLETE);
        if (node.isEmpty()) {
            return null;
        }
        return SettingsForms
                .builder()
                .autocomplete(SettingsFormsAutocomplete
                        .builder()
                        .searchNamespace(bool(model, node.get(), FDPRI.SEARCH_NAMESPACE))
                        .sources(autocompleteSources(model, node.get()))
                        .build()
                )
                .build();
    }

    private static List<SettingsAutocompleteSource> autocompleteSources(Model model, Resource autocomplete) {
        return readOrdered(model, autocomplete, FDPRI.AUTOCOMPLETE_SOURCE, node -> autocompleteSource(model, node));
    }

    private static SettingsAutocompleteSource autocompleteSource(Model model, Resource node) {
        return SettingsAutocompleteSource
                .builder()
                .rdfType(string(model, node, FDPRI.SOURCE_RDF_TYPE))
                .sparqlEndpoint(string(model, node, FDPRI.SPARQL_ENDPOINT))
                .sparqlQuery(string(model, node, FDPRI.SPARQL_QUERY))
                .build();
    }

    private static SearchFilterType filterType(Model model, Resource filter) {
        return Models
                .getPropertyString(model, filter, FDPRI.FILTER_TYPE)
                .flatMap(SettingsRdfMapper::toFilterType)
                .orElse(null);
    }

    private static Optional<SearchFilterType> toFilterType(String name) {
        return Arrays
                .stream(SearchFilterType.values())
                .filter(type -> type.name().equals(name))
                .findFirst();
    }

    /**
     * The nodes reached from a subject through a property, read in the order they were saved in
     * and mapped to the object they describe.
     *
     * @param model the statements of the settings graph
     * @param subject subject the nodes hang on
     * @param predicate property the nodes hang on
     * @param read reads the object described by one node
     * @param <T> type of the objects read
     * @return the objects, ordered by {@code fdp-ri:order}; nodes without one come last, ordered by
     *     their identifier so that reading the same graph twice yields the same order
     */
    private static <T> List<T> readOrdered(Model model, Resource subject, IRI predicate, Function<Resource, T> read) {
        return ordered(model, subject, predicate).stream().map(read).toList();
    }

    private static List<Resource> ordered(Model model, Resource subject, IRI predicate) {
        return Models
                .getPropertyResources(model, subject, predicate)
                .stream()
                .sorted(Comparator
                        .comparingInt((Resource node) -> order(model, node))
                        .thenComparing(Value::stringValue)
                )
                .toList();
    }

    private static int order(Model model, Resource node) {
        final Optional<Literal> order = Models.getPropertyLiteral(model, node, FDPRI.ORDER);
        if (order.isEmpty()) {
            return NO_ORDER;
        }
        try {
            return order.get().intValue();
        }
        catch (NumberFormatException exception) {
            log.warn("The settings graph holds an unreadable order '{}'; the node is sorted last", order.get());
            return NO_ORDER;
        }
    }

    private static String string(Model model, Resource subject, IRI predicate) {
        return Models.getPropertyString(model, subject, predicate).orElse(null);
    }

    private static Boolean bool(Model model, Resource subject, IRI predicate) {
        final Optional<Literal> value = Models.getPropertyLiteral(model, subject, predicate);
        if (value.isEmpty()) {
            return null;
        }
        try {
            return value.get().booleanValue();
        }
        catch (IllegalArgumentException exception) {
            log.warn("The settings graph holds an unreadable boolean '{}'; it is read as absent", value.get());
            return null;
        }
    }

}
