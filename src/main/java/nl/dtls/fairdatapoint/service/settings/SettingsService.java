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
package nl.dtls.fairdatapoint.service.settings;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nl.dtls.fairdatapoint.api.controller.settings.SettingsDefaults;
import nl.dtls.fairdatapoint.api.dto.search.SearchFilterDTO;
import nl.dtls.fairdatapoint.api.dto.settings.SettingsAutocompleteSourceDTO;
import nl.dtls.fairdatapoint.api.dto.settings.SettingsDTO;
import nl.dtls.fairdatapoint.api.dto.settings.SettingsMetricDTO;
import nl.dtls.fairdatapoint.api.dto.settings.SettingsUpdateDTO;
import nl.dtls.fairdatapoint.database.db.repository.*;
import nl.dtls.fairdatapoint.entity.settings.Settings;
import nl.dtls.fairdatapoint.entity.settings.SettingsAutocompleteSource;
import nl.dtls.fairdatapoint.entity.settings.SettingsMetric;
import nl.dtls.fairdatapoint.entity.settings.SettingsSearchFilter;
import nl.dtls.fairdatapoint.util.KnownUUIDs;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository settingsRepository;

    private final SettingsMetricRepository metricRepository;

    private final SettingsAutocompleteSourceRepository autocompleteSourceRepository;

    private final SettingsSearchFilterRepository searchFilterRepository;

    private final SettingsSearchFilterItemRepository searchFilterItemRepository;

    private final SettingsMapper mapper;

    private final SettingsCache settingsCache;

    private final EntityManager entityManager;

    private final SettingsDefaults settingsDefaults;

    public Settings getOrDefaults() {
        return settingsCache.getOrDefaults();
    }

    public SettingsDTO getCurrentSettings() {
        return mapper.toDTO(getOrDefaults());
    }

    @Transactional
    public SettingsDTO updateSettings(SettingsUpdateDTO dto) {
        final Settings oldSettings = getOrDefaults();

        // update Settings
        final Settings newSettings = mapper.fromUpdateDTO(dto, oldSettings);
        settingsRepository.saveAndFlush(newSettings);

        // update Metrics
        final List<SettingsMetric> metrics = updateMetrics(dto, newSettings);
        newSettings.setMetrics(metrics);

        // update Autocomplete Sources
        final List<SettingsAutocompleteSource> sources = updateSources(dto, newSettings);
        newSettings.setAutocompleteSources(sources);

        // update SearchFilters
        final List<SettingsSearchFilter> searchFilters = updateSearchFilters(dto, newSettings);
        newSettings.setSearchFilters(searchFilters);

        settingsCache.updateCachedSettings(newSettings);
        return mapper.toDTO(newSettings);
    }

    private List<SettingsMetric> updateMetrics(SettingsUpdateDTO dto, Settings settings) {
        // Delete old
        metricRepository.deleteAll(settings.getMetrics());
        // Add new
        final List<SettingsMetric> metrics = new ArrayList<>();
        final List<SettingsMetricDTO> dtos = dto.getMetadataMetrics();
        for (int index = 0; index < dtos.size(); index++) {
            metrics.add(mapper.fromMetricDTO(dtos.get(index), index, settings));
        }
        return metricRepository.saveAll(metrics);
    }

    private List<SettingsAutocompleteSource> updateSources(SettingsUpdateDTO dto, Settings settings) {
        // Delete old
        metricRepository.deleteAll(settings.getMetrics());
        // Add new
        final List<SettingsAutocompleteSource> metrics = new ArrayList<>();
        final List<SettingsAutocompleteSourceDTO> dtos = dto.getForms().getAutocomplete().getSources();
        for (int index = 0; index < dtos.size(); index++) {
            metrics.add(mapper.fromAutocompleteSourceDTO(dtos.get(index), index, settings));
        }
        return autocompleteSourceRepository.saveAll(metrics);
    }

    private List<SettingsSearchFilter> updateSearchFilters(SettingsUpdateDTO dto, Settings settings) {
        // Delete old
        settings
                .getSearchFilters()
                .forEach(searchFilter -> searchFilterItemRepository.deleteAll(searchFilter.getItems()));
        searchFilterRepository.deleteAll(settings.getSearchFilters());
        // Add new
        final List<SettingsSearchFilter> searchFilters = new ArrayList<>();
        final List<SearchFilterDTO> dtos = dto.getSearch().getFilters();
        for (int index = 0; index < dtos.size(); index++) {
            searchFilters.add(mapper.fromSearchFilterDTO(dtos.get(index), index, settings));
        }
        searchFilterRepository.saveAllAndFlush(searchFilters);
        for (SettingsSearchFilter searchFilter : searchFilters) {
            entityManager.refresh(searchFilter);
            searchFilterItemRepository.saveAllAndFlush(searchFilter.getItems());
        }
        return searchFilters;
    }

    @Transactional
    public SettingsDTO resetSettings() {
        final Optional<Settings> oldSettings = settingsRepository.findByUuid(KnownUUIDs.SETTINGS_UUID);

        if (oldSettings.isPresent()) {
            settingsRepository.delete(oldSettings.get());
            entityManager.flush();
        }

        final Settings newSettings = settingsRepository.saveAndFlush(settingsDefaults.defaultSettings());
        metricRepository.saveAll(settingsDefaults.defaultMetrics(newSettings));
        entityManager.flush();
        entityManager.refresh(newSettings);

        settingsCache.updateCachedSettings(newSettings);
        return mapper.toDTO(newSettings);
    }

}
