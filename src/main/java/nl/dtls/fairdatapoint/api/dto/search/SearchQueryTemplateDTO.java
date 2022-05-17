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
public class SearchQueryTemplateDTO {

    @NotNull
    private String template;

}
