/**
 * The MIT License
 * Copyright © 2017 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.api.controller.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.error.ErrorDTO;
import nl.dtls.fairdatapoint.entity.exception.*;
import nl.dtls.fairdatapoint.entity.index.exception.AbstractIndexException;
import nl.dtls.fairdatapoint.service.metadata.exception.MetadataServiceException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.StringWriter;
import java.util.Map;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.RdfIOUtil.getWriterConfig;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ErrorDTO.class)
        )
    )
    public ErrorDTO handleBadRequest(Exception exception) {
        log.warn(exception.getMessage());
        log.debug("Handling bad request (ValidationException)", exception);
        return new ErrorDTO(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(RdfValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public Model handleBadRequest(RdfValidationException exception) {
        final Model validationReportModel = exception.getModel();
        log.debug("Handling bad request (RdfValidationException)", exception);

        // Log number of errors
        final IRI validationResultIri = i("http://www.w3.org/ns/shacl#ValidationResult");
        final int errorsCount = validationReportModel.filter(
                null, null, validationResultIri
        ).size();
        log.warn(format("Number of error: %s", errorsCount));

        // Log validation errors
        final StringWriter serialized = new StringWriter();
        Rio.write(validationReportModel, serialized, RDFFormat.TURTLE, getWriterConfig());
        log.warn(serialized.toString());

        return validationReportModel;
    }

    @ExceptionHandler(MalformedQueryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ErrorDTO handleInvalidQuery(MalformedQueryException exception) {
        return handleInvalidSparqlQuery(exception);
    }

    @ExceptionHandler(QueryEvaluationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ApiResponse(
            responseCode = "400",
            description = "Bad request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ErrorDTO handleInvalidQuery(QueryEvaluationException exception) {
        return handleInvalidSparqlQuery(exception);
    }

    @ExceptionHandler({BadCredentialsException.class, UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ErrorDTO handleUnauthorized(Exception exception) {
        log.error(exception.getMessage());
        log.debug("Handling unauthorized", exception);
        return new ErrorDTO(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler({ForbiddenException.class, AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    @ApiResponse(
            responseCode = "403",
            description = "Forbidden",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ErrorDTO handleForbidden(Exception exception) {
        log.error(exception.getMessage());
        log.debug("Handling forbidden", exception);
        return new ErrorDTO(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    @ApiResponse(
            responseCode = "404",
            description = "Resource Not Found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ErrorDTO handleResourceNotFound(ResourceNotFoundException exception) {
        log.error(exception.getMessage());
        log.debug("Handling resource not found", exception);
        return new ErrorDTO(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(MetadataServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ApiResponse(
            responseCode = "500",
            description = "Internal Server Error",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorDTO.class)
            )
    )
    public ErrorDTO handleInternalServerError(Exception exception) {
        log.error(exception.getMessage());
        log.debug("Handling internal server error (MetadataServiceException)", exception);
        return new ErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }

    @ExceptionHandler(AbstractIndexException.class)
    public ResponseEntity<ErrorDTO> handleIndexException(AbstractIndexException exception) {
        log.debug("Handling index exception", exception);
        return new ResponseEntity<>(exception.getErrorDTO(), exception.getStatus());
    }

    private ErrorDTO handleInvalidSparqlQuery(Exception exception) {
        final String message = "Invalid SPARQL query";
        log.error(message);
        log.debug("Handling invalid query ({})", exception.getClass().getName(), exception);
        final Map<String, String> details = Map.of(
                "sparql", exception.getMessage(),
                "exception", exception.getClass().getName()
        );
        return new ErrorDTO(HttpStatus.BAD_REQUEST, message, details);
    }

}
