package nl.dtls.fairdatapoint.api.dto.search;

import lombok.*;
import nl.dtls.fairdatapoint.entity.search.SearchSavedQueryType;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SearchSavedQueryChangeDTO {

    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    private SearchSavedQueryType type;

    @Valid
    @NotNull
    private SearchQueryVariablesDTO variables;
}
