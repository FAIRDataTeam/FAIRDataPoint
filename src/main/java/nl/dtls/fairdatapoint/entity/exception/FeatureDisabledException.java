package nl.dtls.fairdatapoint.entity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FeatureDisabledException extends RuntimeException {

    public FeatureDisabledException(String message) {
        super(message);
    }

}
