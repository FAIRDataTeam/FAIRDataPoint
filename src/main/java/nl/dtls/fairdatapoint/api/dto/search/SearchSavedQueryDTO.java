package nl.dtls.fairdatapoint.api.dto.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import nl.dtls.fairdatapoint.api.dto.user.UserDTO;
import nl.dtls.fairdatapoint.entity.search.SearchSavedQueryType;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SearchSavedQueryDTO {

    @NotBlank
    private String uuid;

    @NotBlank
    private String name;

    @NotNull
    private String description;

    @NotNull
    private SearchSavedQueryType type;

    @Valid
    @JsonInclude
    private UserDTO user;

    @NotNull
    private Instant createdAt;

    @NotNull
    private Instant updatedAt;

    @Valid
    @NotNull
    private SearchQueryVariablesDTO variables;
}
