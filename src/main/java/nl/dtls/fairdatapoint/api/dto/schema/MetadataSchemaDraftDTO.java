package nl.dtls.fairdatapoint.api.dto.schema;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MetadataSchemaDraftDTO {

    @NotNull
    @NotBlank
    private String uuid;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    private String description;

    private boolean abstractSchema;

    @NotNull
    private String definition;

    @NotNull
    private List<String> extendsSchemaUuids;

    private String suggestedResourceName;

    private String suggestedUrlPrefix;
}
