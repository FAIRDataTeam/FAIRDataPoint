package nl.dtls.fairdatapoint.api.dto.search;

import lombok.*;
import nl.dtls.fairdatapoint.api.validator.ValidIri;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class SearchFilterItemDTO {

    @ValidIri
    private String value;

    private String label = null;

    private boolean preset = true;

}
