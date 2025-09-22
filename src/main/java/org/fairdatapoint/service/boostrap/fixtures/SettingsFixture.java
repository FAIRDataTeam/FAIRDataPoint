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
package org.fairdatapoint.service.boostrap.fixtures;

import lombok.Data;
import org.fairdatapoint.api.dto.search.SearchFilterDTO;
import org.fairdatapoint.api.dto.settings.SettingsAutocompleteSourceDTO;
import org.fairdatapoint.api.dto.settings.SettingsMetricDTO;

import java.util.ArrayList;
import java.util.List;

@Data
public class SettingsFixture {
    private String appTitle = "FAIR Data Point";
    private String appSubtitle = "Metadata for Machines";
    private Boolean pingEnabled = true;
    private List<String> pingEndpoints = new ArrayList<>();
    private Boolean autocompleteSearchNamespace = true;
    private List<SettingsAutocompleteSourceDTO> autocompleteSources = new ArrayList<>();
    private List<SettingsMetricDTO> metrics = new ArrayList<>();
    private List<SearchFilterDTO> searchFilters = new ArrayList<>();
}
