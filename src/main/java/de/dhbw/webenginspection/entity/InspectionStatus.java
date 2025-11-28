package de.dhbw.webenginspection.entity;

/**
 * Status einer Inspektion im Lebenszyklus.
 */
public enum InspectionStatus {
    /**
     * Die Inspektion ist geplant, aber noch nicht gestartet.
     */
    PLANNED,

    /**
     * Die Inspektion wurde gestartet, ist aber noch nicht abgeschlossen.
     */
    IN_PROGRESS,

    /**
     * Die Inspektion wurde abgeschlossen.
     */
    COMPLETED
}
