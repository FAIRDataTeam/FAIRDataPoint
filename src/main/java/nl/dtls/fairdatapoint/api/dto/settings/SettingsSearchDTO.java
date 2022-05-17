package nl.dtls.fairdatapoint.api.dto.settings;

import lombok.*;
import nl.dtls.fairdatapoint.api.dto.search.SearchFilterDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class SettingsSearchDTO {

    @NotNull
    private List<@Valid SearchFilterDTO> filters;
}
