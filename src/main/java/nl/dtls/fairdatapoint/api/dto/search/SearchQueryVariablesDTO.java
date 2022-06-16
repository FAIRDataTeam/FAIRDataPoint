package nl.dtls.fairdatapoint.api.dto.search;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SearchQueryVariablesDTO {

    @NotNull
    private String prefixes;

    @NotNull
    private String graphPattern;

    @NotNull
    private String ordering;
}
