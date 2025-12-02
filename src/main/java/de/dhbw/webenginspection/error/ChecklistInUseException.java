// d:\Software-Projekte\dhbw\webeng-inspection\src\main\java\de\dhbw\webenginspection\error\ChecklistInUseException.java

package de.dhbw.webenginspection.error;

/**
 * Exception, die ausgelöst wird, wenn eine Checklist gelöscht werden soll, die
 * noch von aktiven Inspections verwendet wird.
 */
public class ChecklistInUseException extends RuntimeException {

    private final int inspectionCount;

    /**
     * Erstellt eine neue ChecklistInUseException.
     *
     * @param inspectionCount die Anzahl abhängiger Inspections
     */
    public ChecklistInUseException(int inspectionCount) {
        super("Checklist can not be deleted. There are " + inspectionCount + " active inspection"
                + (inspectionCount != 1 ? "s" : "") + " based on it.");
        this.inspectionCount = inspectionCount;
    }

    /**
     * Gibt die Anzahl der abhängigen Inspections zurück.
     *
     * @return die Anzahl abhängiger Inspections
     */
    public int getInspectionCount() {
        return inspectionCount;
    }
}