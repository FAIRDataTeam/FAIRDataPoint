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
package org.fairdatateam.fairdatapoint.rdf.vocabulary;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;

/**
 * Vocabulary of the FAIR Data Point reference implementation (FDP-RI-O).
 *
 * <p>Terms that describe how <em>this</em> implementation stores its own configuration in the
 * triple store: record state, metadata schema versions, resource definitions, settings and the
 * RDF-migration log. Anything implementation-agnostic belongs in FDP-O ({@link FDP}) instead.
 * The ontology itself is shipped as {@code fdp-ri-o.ttl} next to this class on the classpath.
 */
public final class FDPRI {

    public static final String NAMESPACE = "https://w3id.org/fdp/fdp-ri-o#";

    public static final String PREFIX = "fdp-ri";

    /**
     * IRI prefix shared by the system graph and all of its sub-graphs. Search excludes every graph
     * whose IRI starts with it from its results.
     */
    public static final String SYSTEM_GRAPH_PREFIX = "https://w3id.org/fdp/fdp-ri-o/system";

    /**
     * Named graph in the main repository that holds the implementation's own configuration.
     * Search and the SPARQL endpoint exclude it from their results.
     */
    public static final IRI SYSTEM_GRAPH = i(SYSTEM_GRAPH_PREFIX);

    /** Sub-graph holding the state (draft or published) of every record. */
    public static final IRI STATE_GRAPH = systemGraph("state");

    /** Class of the states a record can be in. */
    public static final IRI RECORD_STATE = i(NAMESPACE + "RecordState");

    /** State of a record that is only visible to authenticated users. */
    public static final IRI DRAFT = i(NAMESPACE + "Draft");

    /** State of a record that is visible to everybody. */
    public static final IRI PUBLISHED = i(NAMESPACE + "Published");

    /** Relates a record to its {@link #RECORD_STATE}. */
    public static final IRI HAS_STATE = i(NAMESPACE + "hasState");

    /** Sub-graph holding the log of the RDF migrations that were applied to this installation. */
    public static final IRI MIGRATIONS_GRAPH = systemGraph("migrations");

    /** Class of the entries of the RDF-migration log. */
    public static final IRI APPLIED_MIGRATION = i(NAMESPACE + "AppliedMigration");

    /** Number that identifies an applied migration and orders it among the others. */
    public static final IRI MIGRATION_NUMBER = i(NAMESPACE + "migrationNumber");

    /** Moment at which a migration was applied. */
    public static final IRI APPLIED_AT = i(NAMESPACE + "appliedAt");

    /** Sub-graph holding the settings of this installation. */
    public static final IRI SETTINGS_GRAPH = systemGraph("settings");

    /** Class of the settings of one installation; the settings graph holds exactly one instance. */
    public static final IRI SETTINGS = i(NAMESPACE + "Settings");

    /**
     * Class of a metrics entry of the settings. The instances are the blank nodes reached from the
     * settings through {@link #METRIC}; their type is not asserted, it follows from that range.
     */
    public static final IRI SETTINGS_METRICS_ENTRY = i(NAMESPACE + "SettingsMetricsEntry");

    /** Class of the pinging part of the settings, see {@link #SETTINGS_METRICS_ENTRY}. */
    public static final IRI SETTINGS_PING = i(NAMESPACE + "SettingsPing");

    /** Class of a search filter of the settings, see {@link #SETTINGS_METRICS_ENTRY}. */
    public static final IRI SETTINGS_SEARCH_FILTER = i(NAMESPACE + "SettingsSearchFilter");

    /** Class of a preset value of a search filter, see {@link #SETTINGS_METRICS_ENTRY}. */
    public static final IRI SETTINGS_SEARCH_FILTER_ITEM = i(NAMESPACE + "SettingsSearchFilterItem");

    /** Class of the autocompletion part of the settings, see {@link #SETTINGS_METRICS_ENTRY}. */
    public static final IRI SETTINGS_AUTOCOMPLETE = i(NAMESPACE + "SettingsAutocomplete");

    /** Class of a source of the autocompletion, see {@link #SETTINGS_METRICS_ENTRY}. */
    public static final IRI SETTINGS_AUTOCOMPLETE_SOURCE = i(NAMESPACE + "SettingsAutocompleteSource");

    /** Title of the installation, shown by the client instead of the configured one. */
    public static final IRI APP_TITLE = i(NAMESPACE + "appTitle");

    /** Subtitle of the installation, shown by the client instead of the configured one. */
    public static final IRI APP_SUBTITLE = i(NAMESPACE + "appSubtitle");

    /** Relates the settings to one of the FAIR metrics they advertise. */
    public static final IRI METRIC = i(NAMESPACE + "metric");

    /** Metric that a {@link #METRIC} entry advertises. */
    public static final IRI METRIC_URI = i(NAMESPACE + "metricUri");

    /** Resource that documents how a {@link #METRIC} entry is fulfilled. */
    public static final IRI RESOURCE_URI = i(NAMESPACE + "resourceUri");

    /** Position of a node among its siblings, counted from zero. */
    public static final IRI ORDER = i(NAMESPACE + "order");

    /** Relates the settings to how this installation pings the FAIR Data Point indexes. */
    public static final IRI PING = i(NAMESPACE + "ping");

    /** Whether the pinging is switched on. */
    public static final IRI ENABLED = i(NAMESPACE + "enabled");

    /** Ordered list of the indexes that are pinged. */
    public static final IRI ENDPOINT = i(NAMESPACE + "endpoint");

    /** Relates the settings to one of the filters offered by the search. */
    public static final IRI SEARCH_FILTER = i(NAMESPACE + "searchFilter");

    /** Whether a search filter matches IRIs or literals. */
    public static final IRI FILTER_TYPE = i(NAMESPACE + "filterType");

    /** Predicate whose values a search filter filters on. */
    public static final IRI PREDICATE = i(NAMESPACE + "predicate");

    /** Whether the values offered by a search filter are collected from the records. */
    public static final IRI QUERY_FROM_RECORDS = i(NAMESPACE + "queryFromRecords");

    /** Relates a search filter to one of the values it offers out of the box. */
    public static final IRI PRESET_VALUE = i(NAMESPACE + "presetValue");

    /** Relates the settings to how the forms of the client autocomplete their fields. */
    public static final IRI AUTOCOMPLETE = i(NAMESPACE + "autocomplete");

    /** Whether the autocompletion also searches the namespace of the entered term. */
    public static final IRI SEARCH_NAMESPACE = i(NAMESPACE + "searchNamespace");

    /** Relates the autocompletion settings to one of the sources it queries. */
    public static final IRI AUTOCOMPLETE_SOURCE = i(NAMESPACE + "autocompleteSource");

    /** Type of the entities an autocomplete source provides. */
    public static final IRI SOURCE_RDF_TYPE = i(NAMESPACE + "rdfType");

    /** SPARQL endpoint queried by an autocomplete source. */
    public static final IRI SPARQL_ENDPOINT = i(NAMESPACE + "sparqlEndpoint");

    /** Query sent to the SPARQL endpoint of an autocomplete source. */
    public static final IRI SPARQL_QUERY = i(NAMESPACE + "sparqlQuery");

    private FDPRI() {
    }

    /**
     * Sub-graph of the system graph for one area of the configuration, e.g. {@code state}.
     *
     * @param area name of the area, used as the last segment of the graph IRI
     * @return the IRI of the sub-graph
     */
    public static IRI systemGraph(String area) {
        return i(SYSTEM_GRAPH_PREFIX + "/" + area);
    }

    /**
     * Whether a context is the system graph or one of its sub-graphs, i.e. it holds the
     * configuration of the implementation rather than a record.
     *
     * @param context a context, e.g. as returned by
     *     {@link org.eclipse.rdf4j.repository.RepositoryConnection#getContextIDs()}
     * @return true if the context is a system graph
     */
    public static boolean isSystemGraph(Resource context) {
        return context instanceof IRI iri && iri.stringValue().startsWith(SYSTEM_GRAPH_PREFIX);
    }

}
