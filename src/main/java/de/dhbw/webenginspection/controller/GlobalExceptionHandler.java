package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.error.ChecklistInUseException;
import de.dhbw.webenginspection.error.ErrorResponse;
import de.dhbw.webenginspection.error.ErrorResponse.FieldValidationError;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Zentrale Fehlerbehandlung für alle REST-Controller der Anwendung. Wandelt
 * ausgelöste Exceptions in konsistente {@link ErrorResponse}-Objekte um und
 * sorgt damit für ein einheitliches Fehler-Response-Format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /**
         * Behandelt Validierungsfehler, die durch
         * {@link jakarta.validation.Valid} ausgelöst werden (z.&nbsp;B.
         * fehlende oder ungültige Felder im Request-Body).
         *
         * @param ex die ausgelöste {@link MethodArgumentNotValidException}
         * @param request das aktuelle {@link HttpServletRequest}
         * @return eine Response mit HTTP-Status {@code 400 Bad Request} und
         * einer {@link ErrorResponse}, die die einzelnen Feldfehler enthält
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                log.warn("Validation failed for request {}: {} field errors", request.getRequestURI(),
                                ex.getBindingResult().getFieldErrorCount());

                HttpStatus status = HttpStatus.BAD_REQUEST;

                ErrorResponse error = new ErrorResponse(status.value(), status.getReasonPhrase(), "Validation failed",
                                request.getRequestURI());

                List<FieldValidationError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                                .map(fe -> new FieldValidationError(fe.getField(), fe.getDefaultMessage())).toList();

                error.setFieldErrors(fieldErrors);

                return ResponseEntity.status(status).body(error);
        }

        /**
         * Behandelt {@link IllegalArgumentException}, die typischerweise bei
         * fachlichen Fehlern oder fehlenden Entitäten verwendet wird.
         * 
         * Differenzierung: - Enthält "not found" → {@code 404 Not Found} -
         * Sonst → {@code 400 Bad Request}
         *
         * @param ex die ausgelöste {@link IllegalArgumentException}
         * @param request das aktuelle {@link HttpServletRequest}
         * @return eine Response mit HTTP-Status 404 oder 400 und einer
         * {@link ErrorResponse}, die die Fehlermeldung enthält
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                        HttpServletRequest request) {

                String message = ex.getMessage();
                log.warn("Illegal argument at {}: {}", request.getRequestURI(), message);

                // Differenziere zwischen "not found" (404) und anderen Fehlern
                // (400)
                HttpStatus status = message != null && message.toLowerCase().contains("not found")
                                ? HttpStatus.NOT_FOUND
                                : HttpStatus.BAD_REQUEST;

                ErrorResponse error = new ErrorResponse(status.value(), status.getReasonPhrase(), message,
                                request.getRequestURI());

                return ResponseEntity.status(status).body(error);
        }

        /**
         * Behandelt {@link ChecklistInUseException}, wenn eine Checklist
         * gelöscht werden soll, die noch von aktiven Inspections verwendet
         * wird.
         *
         * @param ex die ausgelöste {@link ChecklistInUseException}
         * @param request das aktuelle {@link HttpServletRequest}
         * @return eine Response mit HTTP-Status {@code 409 Conflict} und einer
         * {@link ErrorResponse}, die die Anzahl abhängiger Inspections enthält
         */
        @ExceptionHandler(ChecklistInUseException.class)
        public ResponseEntity<ErrorResponse> handleChecklistInUse(ChecklistInUseException ex,
                        HttpServletRequest request) {

                log.warn("Attempt to delete checklist with {} dependent inspections at {}", ex.getInspectionCount(),
                                request.getRequestURI());

                HttpStatus status = HttpStatus.CONFLICT;

                ErrorResponse error = new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage(),
                                request.getRequestURI());

                return ResponseEntity.status(status).body(error);
        }

        /**
         * Behandelt Fehler beim Einlesen oder Parsen des HTTP-Request-Bodys,
         * z.&nbsp;B. ungültiges JSON oder ein falsches Datumsformat. Nutzt,
         * falls möglich, die genaueste Ursache, um eine aussagekräftige
         * Fehlermeldung zu erzeugen.
         *
         * @param ex die ausgelöste {@link HttpMessageNotReadableException}
         * @param request das aktuelle {@link HttpServletRequest}
         * @return eine Response mit HTTP-Status {@code 400 Bad Request} und
         * einer {@link ErrorResponse} mit einer Beschreibung des
         * Parsing-Problems
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                        HttpServletRequest request) {

                log.warn("Malformed JSON request at {}: {}", request.getRequestURI(), ex.getMessage());

                HttpStatus status = HttpStatus.BAD_REQUEST;

                String message = "Invalid request body";
                Throwable mostSpecificCause = ex.getMostSpecificCause();

                if (mostSpecificCause instanceof InvalidFormatException ife) {
                        String fieldName = ife.getPath().isEmpty() ? "unknown" : ife.getPath().get(0).getFieldName();

                        message = "Invalid value '" + ife.getValue() + "' for field '" + fieldName
                                        + "'. Please use a valid format.";
                } else if (mostSpecificCause instanceof DateTimeParseException dtpe) {
                        message = "Invalid date/time format: '" + dtpe.getParsedString()
                                        + "'. Please use a valid format (e.g. 2025-01-15T10:00:00).";
                } else if (mostSpecificCause != null && mostSpecificCause.getMessage() != null) {
                        // Fallback: konkrete Fehlermeldung übernehmen
                        message = mostSpecificCause.getMessage();
                }

                ErrorResponse error = new ErrorResponse(status.value(), status.getReasonPhrase(), message,
                                request.getRequestURI());

                return ResponseEntity.status(status).body(error);
        }

        /**
         * Fängt alle nicht speziell behandelten Exceptions ab und verhindert
         * so, dass interne Fehler unkontrolliert nach außen durchgereicht
         * werden.
         *
         * @param ex die aufgetretene {@link Exception}
         * @param request das aktuelle {@link HttpServletRequest}
         * @return eine Response mit HTTP-Status
         * {@code 500 Internal Server Error} und einer generischen
         * {@link ErrorResponse}
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {

                log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

                ErrorResponse error = new ErrorResponse(status.value(), status.getReasonPhrase(),
                                "Unexpected error occurred", request.getRequestURI());

                return ResponseEntity.status(status).body(error);
        }
}