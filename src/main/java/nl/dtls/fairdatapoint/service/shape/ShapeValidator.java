package nl.dtls.fairdatapoint.service.shape;

import nl.dtls.fairdatapoint.api.dto.shape.ShapeChangeDTO;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.util.RdfIOUtil;
import org.springframework.stereotype.Service;

@Service
public class ShapeValidator {

    public void validate(ShapeChangeDTO reqDto) {
        // Try to parse SHACL definition
        try {
            RdfIOUtil.read(reqDto.getDefinition(), "");
        } catch (ValidationException e) {
            throw new ValidationException("Unable to read SHACL definition");
        }
    }

}
