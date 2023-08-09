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
package nl.dtls.fairdatapoint.database.mongo.migration.development.settings.data;

import nl.dtls.fairdatapoint.entity.search.SearchFilterType;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import nl.dtls.fairdatapoint.entity.settings.SettingsAutocompleteSource;
import nl.dtls.fairdatapoint.entity.settings.SettingsSearchFilter;
import nl.dtls.fairdatapoint.entity.settings.SettingsSearchFilterItem;

import java.util.Collections;
import java.util.List;

public class SettingsFixtures {

    private static final SettingsSearchFilter SEARCH_FILTER_TYPE = SettingsSearchFilter
            .builder()
            .type(SearchFilterType.IRI)
            .label("Type")
            .predicate("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
            .queryFromRecords(false)
            .presetValues(List.of(
                    SettingsSearchFilterItem.builder()
                            .label("Catalog")
                            .value("http://www.w3.org/ns/dcat#Catalog")
                            .build(),
                    SettingsSearchFilterItem.builder()
                            .label("Dataset")
                            .value("http://www.w3.org/ns/dcat#Dataset")
                            .build(),
                    SettingsSearchFilterItem.builder()
                            .label("Distribution")
                            .value("http://www.w3.org/ns/dcat#Distribution")
                            .build(),
                    SettingsSearchFilterItem.builder()
                            .label("Data Service")
                            .value("http://www.w3.org/ns/dcat#DataService")
                            .build(),
                    SettingsSearchFilterItem.builder()
                            .label("Metadata Service")
                            .value("http://www.w3.org/ns/dcat#MetadataService")
                            .build(),
                    SettingsSearchFilterItem.builder()
                            .label("FAIR Data Point")
                            .value("https://w3id.org/fdp/fdp-o#FAIRDataPoint")
                            .build()
            ))
            .build();

    private static final SettingsSearchFilter SEARCH_FILTER_LICENSE = SettingsSearchFilter
            .builder()
            .type(SearchFilterType.IRI)
            .label("License")
            .predicate("http://purl.org/dc/terms/license")
            .queryFromRecords(true)
            .presetValues(Collections.emptyList())
            .build();

    private static final SettingsSearchFilter SEARCH_FILTER_VERSION = SettingsSearchFilter
            .builder()
            .type(SearchFilterType.LITERAL)
            .label("Version")
            .predicate("http://www.w3.org/ns/dcat#version")
            .queryFromRecords(true)
            .presetValues(Collections.emptyList())
            .build();

    private static final SettingsAutocompleteSource AUTOCOMPLETE_SOURCE_COUNTRY = SettingsAutocompleteSource
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
            .build();

    public static Settings settings() {
        final Settings settings = Settings.getDefault();
        settings.setSearchFilters(List.of(
                SEARCH_FILTER_TYPE,
                SEARCH_FILTER_LICENSE,
                SEARCH_FILTER_VERSION
        ));
        settings.getForms().getAutocomplete().setSources(List.of(
                AUTOCOMPLETE_SOURCE_COUNTRY
        ));
        return settings;
    }
}
