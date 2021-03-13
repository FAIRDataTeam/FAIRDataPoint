package nl.dtls.fairdatapoint.api.dto.shape;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShapeRemoteDTO {
    private String from;

    private String uuid;

    private String name;

    private String definition;
}
