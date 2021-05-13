package nl.dtls.fairdatapoint.api.dto.reset;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResetDTO {

    private boolean user;

    private boolean metadata;

    private boolean resourceDefinition;
}
