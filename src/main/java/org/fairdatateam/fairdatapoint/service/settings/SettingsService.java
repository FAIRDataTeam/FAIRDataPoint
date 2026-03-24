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
package org.fairdatateam.fairdatapoint.service.settings;

import org.fairdatateam.fairdatapoint.api.dto.settings.SettingsDTO;
import org.fairdatateam.fairdatapoint.api.dto.settings.SettingsUpdateDTO;
import org.fairdatateam.fairdatapoint.database.mongo.repository.SettingsRepository;
import org.fairdatateam.fairdatapoint.entity.settings.Settings;
import org.fairdatateam.fairdatapoint.entity.settings.SettingsSearchFilter;
import org.fairdatateam.fairdatapoint.service.search.SearchFilterCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SettingsService {

    @Autowired
    private SettingsRepository repository;

    @Autowired
    private SettingsMapper mapper;

    @Autowired
    private SettingsCache settingsCache;

    @Autowired
    private SearchFilterCache searchFilterCache;

    public Settings getOrDefaults() {
        return settingsCache.getOrDefaults();
    }

    public SettingsDTO getCurrentSettings() {
        return mapper.toDTO(getOrDefaults());
    }

    public SettingsDTO updateSettings(SettingsUpdateDTO dto) {
        final Settings oldSettings = getOrDefaults();
        final Settings newSettings = repository.save(mapper.fromUpdateDTO(dto, getOrDefaults()));
        handleSearchFiltersChange(oldSettings, newSettings);
        settingsCache.updateCachedSettings(newSettings);
        return mapper.toDTO(newSettings);
    }

    private void handleSearchFiltersChange(Settings oldSettings, Settings newSettings) {
        final Set<String> oldPredicateUris =
                oldSettings
                        .getSearchFilters()
                        .stream()
                        .map(SettingsSearchFilter::getPredicate)
                        .collect(Collectors.toSet());
        final Set<String> newPredicateUris =
                newSettings
                        .getSearchFilters()
                        .stream()
                        .map(SettingsSearchFilter::getPredicate)
                        .collect(Collectors.toSet());
        oldPredicateUris
                .stream()
                .filter(predicate -> !newPredicateUris.contains(predicate))
                .forEach(searchFilterCache::clearFilter);
        newPredicateUris
                .stream()
                .filter(predicate -> !oldPredicateUris.contains(predicate))
                .forEach(searchFilterCache::clearFilter);
    }

    public SettingsDTO resetSettings() {
        return updateSettings(mapper.toUpdateDTO(Settings.getDefault()));
    }

}
