package nl.dtls.fairdatapoint.api.dto.schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.dtls.fairdatapoint.api.validator.ValidSemVer;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MetadataSchemaPublishDTO {

    @NotBlank
    @ValidSemVer
    protected String version;

    @NotBlank
    protected String description;

    private boolean published;
}

