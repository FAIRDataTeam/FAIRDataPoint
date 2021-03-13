package nl.dtls.fairdatapoint.entity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
@AllArgsConstructor
public class ShapeImportException extends RuntimeException {

    private final String from;

    private final String message;

}
