package uk.gov.justice.digital.delius.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import uk.gov.justice.digital.delius.controller.UnauthorisedException;
import uk.gov.justice.digital.delius.controller.NotFoundException;

@RestControllerAdvice(basePackages = { "uk.gov.justice.digital.delius.controller.secure" } )
@Slf4j
public class SecureControllerAdvice {

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<byte[]> handleException(final RestClientResponseException e) {
        log.error("Unexpected exception", e);
        return ResponseEntity
                .status(e.getRawStatusCode())
                .body(e.getResponseBodyAsByteArray());
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleException(final RestClientException e) {
        log.error("Unexpected exception", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .developerMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(final AccessDeniedException e) {
        log.debug("Forbidden (403) returned", e);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .build());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(final NotFoundException e) {
        log.debug("Not Found (404) returned", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .developerMessage(e.getMessage())
                        .build());
    }

    @ExceptionHandler(UnauthorisedException.class)
    public ResponseEntity<ErrorResponse> handleException(final UnauthorisedException e) {
        log.debug("Unauthorised (401) returned", e);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse
                        .builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .developerMessage(e.getMessage())
                        .build());
    }

}