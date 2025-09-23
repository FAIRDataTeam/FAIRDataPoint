/**
 * The MIT License
 * Copyright © 2016-2024 FAIR Data Team
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
package org.fairdatapoint.service.bootstrap.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.fairdatapoint.database.db.repository.*;
import org.fairdatapoint.entity.settings.Settings;
import org.fairdatapoint.entity.settings.SettingsSearchFilter;
import org.fairdatapoint.service.bootstrap.BootstrapContext;
import org.fairdatapoint.service.bootstrap.fixtures.SettingsFixture;
import org.fairdatapoint.service.settings.SettingsMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.stream.IntStream;

@Slf4j
@Component
public class SettingsBootstrapper extends AbstractBootstrapper {
    private final ObjectMapper objectMapper;
    private final SettingsRepository settingsRepository;
    private final SettingsMetricRepository settingsMetricRepository;
    private final SettingsAutocompleteSourceRepository settingsAutocompleteSourceRepository;
    private final SettingsSearchFilterRepository settingsSearchFilterRepository;
    private final SettingsSearchFilterItemRepository settingsSearchFilterItemRepository;
    private final SettingsMapper settingsMapper;

    public SettingsBootstrapper(ObjectMapper objectMapper,
                                SettingsRepository settingsRepository,
                                SettingsMetricRepository settingsMetricRepository,
                                SettingsAutocompleteSourceRepository settingsAutocompleteSourceRepository,
                                SettingsSearchFilterRepository settingsSearchFilterRepository,
                                SettingsSearchFilterItemRepository settingsSearchFilterItemRepository,
                                SettingsMapper settingsMapper) {
        super(objectMapper);
        this.objectMapper = objectMapper;
        this.settingsRepository = settingsRepository;
        this.settingsMetricRepository = settingsMetricRepository;
        this.settingsAutocompleteSourceRepository = settingsAutocompleteSourceRepository;
        this.settingsSearchFilterRepository = settingsSearchFilterRepository;
        this.settingsSearchFilterItemRepository = settingsSearchFilterItemRepository;
        this.settingsMapper = settingsMapper;
    }

    public void bootstrapFromJson(Path resourcePath, BootstrapContext context) {
        if (!resourcePath.getFileName().toString().equals("settings.json")) {
            log.warn("Skipping file {}: only settings.json is supported for settings bootstrapping", resourcePath);
            return;
        }
        try {
            final SettingsFixture settingsFixture =
                    objectMapper.readValue(resourcePath.toFile(), SettingsFixture.class);
            final Settings settings = settingsRepository.saveAndFlush(
                    settingsMapper.fromSettingsFixture(settingsFixture)
            );
            // Metrics
            settingsMetricRepository.saveAll(
                    IntStream.range(0, settingsFixture.getMetrics().size())
                            .mapToObj(index -> {
                                final var metricFixture = settingsFixture.getMetrics().get(index);
                                return settingsMapper.fromMetricDTO(metricFixture, index, settings);
                            })
                            .toList()
            );
            // Autocomplete sources
            settingsAutocompleteSourceRepository.saveAll(
                    IntStream.range(0, settingsFixture.getAutocompleteSources().size())
                            .mapToObj(index -> {
                                final var sourceFixture = settingsFixture.getAutocompleteSources().get(index);
                                return settingsMapper.fromAutocompleteSourceDTO(sourceFixture, index, settings);
                            })
                            .toList()
            );
            // Search filters
            settingsFixture.getSearchFilters().forEach(filterFixture -> {
                final SettingsSearchFilter searchFilter = settingsSearchFilterRepository.saveAndFlush(
                        settingsMapper.fromSearchFilterDTO(filterFixture, 0, settings)
                );
                // Filter items
                settingsSearchFilterItemRepository.saveAll(
                        IntStream.range(0, filterFixture.getValues().size())
                                .mapToObj(index -> {
                                    final var itemFixture = filterFixture.getValues().get(index);
                                    return settingsMapper.fromSearchFilterItemDTO(itemFixture, index, searchFilter);
                                })
                                .toList()
                );
            });
        }
        catch (java.io.IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    protected JpaRepository getRepository() {
        return settingsRepository;
    }
}
