package nl.dtls.fairdatapoint.api.dto.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaOrigin;
import nl.dtls.fairdatapoint.entity.schema.MetadataSchemaType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MetadataSchemaVersionDTO {

    @NotNull
    @NotBlank
    private String uuid;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String version;

    @NotNull
    @NotBlank
    private String name;

    private boolean published;

    private boolean abstractSchema;

    private boolean latest;

    @NotNull
    private MetadataSchemaType type;

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private MetadataSchemaOrigin origin;

    @NotNull
    private String definition;

    @NotNull
    private String description;

    @NotNull
    private Set<String> targetClasses;

    @NotNull
    private List<String> extendsSchemaUuids;

    private String suggestedResourceName;

    private String suggestedUrlPrefix;
}
