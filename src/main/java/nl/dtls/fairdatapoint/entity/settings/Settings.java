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
package nl.dtls.fairdatapoint.entity.settings;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import nl.dtls.fairdatapoint.entity.base.BaseEntityCustomUUID;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity(name = "Settings")
@Table(name = "settings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
public class Settings extends BaseEntityCustomUUID {

    @Column(name = "app_title")
    private String appTitle;

    @Column(name = "app_subtitle")
    private String appSubtitle;

    @NotNull
    @Column(name = "ping_enabled", nullable = false)
    private Boolean pingEnabled;

    @NotNull
    @Type(ListArrayType.class)
    @Column(name = "ping_endpoints", columnDefinition = "text[]", nullable = false)
    private List<String> pingEndpoints;

    @NotNull
    @Column(name = "autocomplete_search_ns", nullable = false)
    private Boolean autocompleteSearchNamespace;

    @OrderBy("orderPriority")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettingsAutocompleteSource> autocompleteSources;

    @OrderBy("orderPriority")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettingsMetric> metrics;

    @OrderBy("orderPriority")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "settings", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SettingsSearchFilter> searchFilters;
}
