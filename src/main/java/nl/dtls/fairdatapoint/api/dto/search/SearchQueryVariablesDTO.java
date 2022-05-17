package nl.dtls.fairdatapoint.api.dto.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SearchQueryVariablesDTO {

    @NotNull
    private String prefixes;

    @NotNull
    private String graphPattern;

    @NotNull
    private String ordering;
}
