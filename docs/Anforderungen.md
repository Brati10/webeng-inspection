## Programmentwurf Web Engineering

Im Verlauf des Studiums haben Sie die Technologien HTML, CSS und Javascript kennen gelernt und
haben auch Backend-Technologien thematisiert. Im Rahmen des Programmentwurfs erstellen Sie eine
responsive Web-Anwendung zur Durchführung von Inspektionen.
Mitarbeiter sollen mithilfe der Anwendung Inspektionen auf Basis von digitalen Checklisten durchführen
und Ergebnisse erfassen.

### 1.1 Anwendung zur Verwaltung von Inspektionen

Die Web-Anwendung unterstütz ein Unternehmen bei der Inspektion von Industrie-Anlagen. Mit Hilfe
der Anwendung kann ein Mitarbeiter eine Inspektion durchführen.

- Funktionalitäten: Benutzeroberfläche
  - Dashboard
    - Übersicht über geplante, laufende und abgeschlossene Inspektionen
    - Anzeige wichtiger Kennzahlen (z. B. Anzahl offener Inspektionen)
  - Inspektionen
    - Neue Inspektion anlegen (Anlage, Datum, verantwortlicher Mitarbeiter)
    - Inspektion starten, fortsetzen und abschließen
    - Statusanzeige (geplant / in Bearbeitung / abgeschlossen)
  - Checklisten
    - Auswahl oder Erstellung einer Checkliste
    - Durchführung einer Inspektion anhand der Prüfpunkte
    - Eingabe von Prüfergebnissen (erfüllt / nicht erfüllt / n. a.)
    - Hinzufügen von Kommentaren
    - Upload von Fotos
  - Ergebnisanzeige und Berichterstellung
    - Zusammenfassung der Ergebnisse
    - Export als PDF oder Druckansicht
- Technische Rahmenbedingungen
  - Das Projekt soll mit dem Spring-Framework realisiert werden
  - Für das Frontend kann ein beliebiges Framework nach Absprache verwendet werden.
  - Das Backend soll auf der Basis von Spring realisiert werden.
