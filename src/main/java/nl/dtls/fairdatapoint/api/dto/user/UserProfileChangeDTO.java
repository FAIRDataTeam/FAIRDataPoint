package nl.dtls.fairdatapoint.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserProfileChangeDTO {

    @NotBlank
    protected String firstName;

    @NotBlank
    protected String lastName;

    @NotBlank
    protected String email;


}
