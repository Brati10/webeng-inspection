package de.dhbw.webenginspection.controller;

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

@RestControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {

                log.warn("Validation failed for request {}: {} field errors",
                                request.getRequestURI(),
                                ex.getBindingResult().getFieldErrorCount());

                HttpStatus status = HttpStatus.BAD_REQUEST;

                ErrorResponse error = new ErrorResponse(
                                status.value(),
                                status.getReasonPhrase(),
                                "Validation failed",
                                request.getRequestURI());

                List<FieldValidationError> fieldErrors = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(fe -> new FieldValidationError(fe.getField(), fe.getDefaultMessage()))
                                .toList();

                error.setFieldErrors(fieldErrors);

                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(
                        IllegalArgumentException ex,
                        HttpServletRequest request) {

                log.warn("Illegal argument at {}: {}", request.getRequestURI(), ex.getMessage());

                HttpStatus status = HttpStatus.BAD_REQUEST;

                ErrorResponse error = new ErrorResponse(
                                status.value(),
                                status.getReasonPhrase(),
                                ex.getMessage(),
                                request.getRequestURI());

                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
                        HttpMessageNotReadableException ex,
                        HttpServletRequest request) {

                log.warn("Malformed JSON request at {}: {}", request.getRequestURI(), ex.getMessage());
                
                HttpStatus status = HttpStatus.BAD_REQUEST;

                String message = "Invalid request body";
                Throwable mostSpecificCause = ex.getMostSpecificCause();

                if (mostSpecificCause instanceof InvalidFormatException ife) {
                        String fieldName = ife.getPath().isEmpty()
                                        ? "unknown"
                                        : ife.getPath().get(0).getFieldName();

                        message = "Invalid value '" + ife.getValue() + "' for field '" + fieldName +
                                        "'. Please use a valid format.";
                } else if (mostSpecificCause instanceof DateTimeParseException dtpe) {
                        message = "Invalid date/time format: '" + dtpe.getParsedString() +
                                        "'. Please use a valid format (e.g. 2025-01-15T10:00:00).";
                } else if (mostSpecificCause != null && mostSpecificCause.getMessage() != null) {
                        // Fallback: konkrete Fehlermeldung übernehmen
                        message = mostSpecificCause.getMessage();
                }

                ErrorResponse error = new ErrorResponse(
                                status.value(),
                                status.getReasonPhrase(),
                                message,
                                request.getRequestURI());

                return ResponseEntity.status(status).body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex,
                        HttpServletRequest request) {

                log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

                ErrorResponse error = new ErrorResponse(
                                status.value(),
                                status.getReasonPhrase(),
                                "Unexpected error occurred",
                                request.getRequestURI());

                return ResponseEntity.status(status).body(error);
        }
}
