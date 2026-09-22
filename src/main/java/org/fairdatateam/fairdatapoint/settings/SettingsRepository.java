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

import lombok.RequiredArgsConstructor;
import org.fairdatateam.fairdatapoint.rdf.system.SystemGraphStore;
import org.fairdatateam.fairdatapoint.rdf.vocabulary.FDPRI;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * The settings of this installation, kept in the settings sub-graph of the system graph.
 *
 * <p>The graph holds the settings as a whole: saving replaces its entire content, so that the
 * settings in the triple store are always the ones that were last saved, without leftovers from
 * a previous version of them. The MongoDB to 2.0 importer copies the 1.x settings document into
 * this graph; until it runs, an upgraded installation falls back on the defaults. {@link
 * SettingsRdfMapper} describes the shape of the RDF.
 */
@Component
@RequiredArgsConstructor
public class SettingsRepository {

    private final SystemGraphStore systemGraphStore;

    private final SettingsRdfMapper mapper;

    /**
     * The settings of this installation.
     *
     * @return the settings, or empty if none were saved yet; the caller falls back on the defaults
     */
    public Optional<Settings> find() {
        return mapper.fromModel(systemGraphStore.dump(FDPRI.SETTINGS_GRAPH));
    }

    /**
     * Saves the settings, replacing the ones that were saved before.
     *
     * @param settings the settings to save
     */
    public void save(Settings settings) {
        systemGraphStore.replace(FDPRI.SETTINGS_GRAPH, mapper.toModel(settings));
    }

    /** Removes the settings; the installation falls back on the defaults until it saves again. */
    public void delete() {
        systemGraphStore.clear(FDPRI.SETTINGS_GRAPH);
    }

}
