package nl.dtls.fairdatapoint.entity.settings;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class SettingsSearchFilterItem {

    private String value;

    private String label;
}
