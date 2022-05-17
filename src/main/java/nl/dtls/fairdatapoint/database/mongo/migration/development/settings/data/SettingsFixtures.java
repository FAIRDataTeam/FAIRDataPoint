package nl.dtls.fairdatapoint.database.mongo.migration.development.settings.data;

import nl.dtls.fairdatapoint.entity.search.SearchFilterType;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import nl.dtls.fairdatapoint.entity.settings.SettingsSearchFilter;
import nl.dtls.fairdatapoint.entity.settings.SettingsSearchFilterItem;

import java.util.Collections;
import java.util.List;

public class SettingsFixtures {

    public static Settings settings() {
        Settings settings = Settings.getDefault();
        settings.setSearchFilters(List.of(
                SEARCH_FILTER_TYPE,
                SEARCH_FILTER_LICENSE,
                SEARCH_FILTER_VERSION
        ));
        return settings;
    }

    private static final SettingsSearchFilter SEARCH_FILTER_TYPE = SettingsSearchFilter
                .builder()
                .type(SearchFilterType.IRI)
                .label("Type")
                .predicate("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
                .queryFromRecords(false)
                .presetValues(List.of(
                        SettingsSearchFilterItem.builder().label("Catalog").value("http://www.w3.org/ns/dcat#Catalog").build(),
                        SettingsSearchFilterItem.builder().label("Dataset").value("http://www.w3.org/ns/dcat#Dataset").build(),
                        SettingsSearchFilterItem.builder().label("Distribution").value("http://www.w3.org/ns/dcat#Distribution").build(),
                        SettingsSearchFilterItem.builder().label("Data Service").value("http://www.w3.org/ns/dcat#DataService").build(),
                        SettingsSearchFilterItem.builder().label("Metadata Service").value("http://www.w3.org/ns/dcat#MetadataService").build(),
                        SettingsSearchFilterItem.builder().label("FAIR Data Point").value("https://w3id.org/fdp/fdp-o#FAIRDataPoint").build()
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
                .predicate("http://purl.org/dc/terms/hasVersion")
                .queryFromRecords(true)
                .presetValues(Collections.emptyList())
                .build();
}
