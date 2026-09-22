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

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.fairdatateam.fairdatapoint.migration.mongodb.development.SettingsFixtures;
import org.fairdatateam.fairdatapoint.rdf.system.SystemGraphStore;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.fairdatateam.fairdatapoint.settings.SettingsRdfMapper.SETTINGS_SUBJECT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class SettingsRepositoryTest {

    private Repository repository;

    private SettingsRepository settingsRepository;

    @BeforeEach
    public void setup() {
        this.repository = new SailRepository(new MemoryStore());
        this.repository.init();
        this.settingsRepository =
                new SettingsRepository(new SystemGraphStore(this.repository), new SettingsRdfMapper());
    }

    @AfterEach
    public void teardown() {
        this.repository.shutDown();
    }

    @Test
    @DisplayName("An installation that never saved its settings has none")
    public void findWithoutSettings() {
        assertThat(this.settingsRepository.find(), is(equalTo(Optional.empty())));
    }

    @Test
    @DisplayName("'save' stores the settings in the settings graph and 'find' reads them back")
    public void saveAndFind() {
        // GIVEN:
        final Settings settings = SettingsFixtures.settings();

        // WHEN:
        this.settingsRepository.save(settings);

        // THEN:
        assertThat(this.settingsRepository.find(), is(equalTo(Optional.of(settings))));

        // AND: everything was written into the settings graph, nothing outside of it
        assertThat(graph().contains(SETTINGS_SUBJECT, RDF.TYPE, FDPRI.SETTINGS), is(true));
        assertThat(everything().contexts(), contains((Resource) FDPRI.SETTINGS_GRAPH));
    }

    @Test
    @DisplayName("'save' replaces the settings that were saved before, leaving nothing of them")
    public void saveReplaces() {
        // GIVEN: settings with three search filters
        this.settingsRepository.save(SettingsFixtures.settings());

        // WHEN: settings without any are saved
        final Settings settings = Settings.getDefault();
        settings.setAppTitle("Replaced");
        this.settingsRepository.save(settings);

        // THEN:
        final Settings read = this.settingsRepository.find().orElseThrow();
        assertThat(read, is(equalTo(settings)));
        assertThat(read.getSearchFilters(), is(empty()));
        assertThat(graph().filter(null, FDPRI.SEARCH_FILTER, null), is(empty()));
    }

    @Test
    @DisplayName("'delete' removes the settings, so that the installation falls back on the defaults")
    public void delete() {
        // GIVEN:
        this.settingsRepository.save(SettingsFixtures.settings());

        // WHEN:
        this.settingsRepository.delete();

        // THEN:
        assertThat(this.settingsRepository.find(), is(equalTo(Optional.empty())));
        assertThat(graph(), is(empty()));
    }

    @Test
    @DisplayName("Settings saved by one repository are read back by another one")
    public void saveAndFindAcrossInstances() {
        // GIVEN:
        final Settings settings = SettingsFixtures.settings();
        settings.setPing(SettingsPing
                .builder()
                .enabled(true)
                .endpoints(List.of("https://home.fairdatapoint.org", "https://example.com/index"))
                .build()
        );
        this.settingsRepository.save(settings);

        // WHEN: another repository reads the same store, as another instance of the application would
        final SettingsRepository other =
                new SettingsRepository(new SystemGraphStore(this.repository), new SettingsRdfMapper());

        // THEN:
        assertThat(other.find().orElseThrow(), is(equalTo(settings)));
    }

    /** The statements of the settings graph. */
    private Model graph() {
        final Model model = new LinkedHashModel();
        try (var conn = this.repository.getConnection()) {
            conn.getStatements(null, null, null, FDPRI.SETTINGS_GRAPH).forEach(model::add);
        }
        return model;
    }

    /** Every statement of the store, whichever graph it is in. */
    private Model everything() {
        final Model model = new LinkedHashModel();
        try (var conn = this.repository.getConnection()) {
            conn.getStatements(null, null, null).forEach(model::add);
        }
        return model;
    }

}
