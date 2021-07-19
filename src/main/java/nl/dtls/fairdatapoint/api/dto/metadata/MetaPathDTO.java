package nl.dtls.fairdatapoint.api.dto.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class MetaPathDTO {

    @NotNull
    private String resourceDefinitionUuid;

    @NotNull
    private String title;

    @JsonInclude
    private String parent = null;
}
