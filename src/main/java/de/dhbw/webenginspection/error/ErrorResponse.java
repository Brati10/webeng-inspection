package de.dhbw.webenginspection.error;

import java.time.LocalDateTime;
import java.util.List;

import de.dhbw.webenginspection.controller.GlobalExceptionHandler;

/**
 * Standardisiertes Fehler-Response-Objekt für REST-Endpunkte. Wird vom
 * {@link GlobalExceptionHandler} erzeugt und enthält Metadaten über den Fehler,
 * darunter Statuscode, Fehlermeldung, Request-Pfad sowie optionale Feldfehler
 * für Validierungsfehler.
 */
public class ErrorResponse {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

    private List<FieldValidationError> fieldErrors;

    public ErrorResponse() {
    }

    /**
     * Erstellt ein neues Fehler-Response-Objekt mit Zeitstempel.
     *
     * @param status der HTTP-Statuscode des Fehlers
     * @param error die standardisierte Fehlerspezifikation (z. B. "Bad
     * Request")
     * @param message eine detaillierte Fehlermeldung
     * @param path der angefragte HTTP-Pfad, an dem der Fehler aufgetreten ist
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<FieldValidationError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldValidationError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    /**
     * Repräsentiert einen einzelnen Validierungsfehler eines Request-Feldes.
     * Wird verwendet, um detaillierte Fehlerangaben bei ungültigen Eingaben (z.
     * B. durch Bean Validation) bereitzustellen.
     */
    public static class FieldValidationError {

        private String field;

        private String message;

        public FieldValidationError() {
        }

        /**
         * Erstellt ein Objekt zur Beschreibung eines Validierungsfehlers.
         *
         * @param field der Name des Feldes, das ungültig war
         * @param message die Fehlermeldung zu diesem Feld
         */
        public FieldValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }
}
