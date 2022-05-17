package nl.dtls.fairdatapoint.entity.settings;

import lombok.*;
import nl.dtls.fairdatapoint.entity.search.SearchFilterType;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class SettingsSearchFilter {

    private SearchFilterType type;

    private String label;

    private String predicate;

    private List<SettingsSearchFilterItem> presetValues;

    private boolean queryFromRecords;
}
