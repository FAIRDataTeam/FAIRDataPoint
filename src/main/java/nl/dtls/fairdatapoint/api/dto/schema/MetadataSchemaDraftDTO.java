package nl.dtls.fairdatapoint.api.dto.schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
}
