package nl.dtls.fairdatapoint.api.dto.search;

import lombok.*;
import nl.dtls.fairdatapoint.api.validator.ValidIri;
import nl.dtls.fairdatapoint.entity.search.SearchFilterType;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
public class SearchFilterDTO {

    @NotNull
    private SearchFilterType type;

    @NotNull
    private String label;

    @ValidIri
    private String predicate;

    @NotNull
    private List<@Valid SearchFilterItemDTO> values;

    private boolean queryFromRecords;
}
