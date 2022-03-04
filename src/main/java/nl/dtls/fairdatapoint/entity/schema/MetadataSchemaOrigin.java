package nl.dtls.fairdatapoint.entity.schema;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MetadataSchemaOrigin {

    private String uri;

    private String fdpUri;

    private String uuid;

}
