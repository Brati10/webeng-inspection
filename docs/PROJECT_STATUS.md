# Projektstatus – webeng-inspection

> Stand: bitte bei Änderung von dir aktualisieren


## 1. Backend – Spring Boot

### 1.1 Projektgrundlage
- [x] Spring-Boot-Projekt angelegt (Gradle/Maven)
- [x] Anwendung startet ohne Fehler
- [x] Package-Struktur festgelegt

### 1.2 Domänenmodell & Persistenz
- [x] Entity `Checklist` angelegt
- [x] Entity `Inspection` angelegt
- [x] Entity `Step` angelegt
- [x] Relationen zwischen `Checklist`, `Inspection` und `Step` modelliert
- [x] JPA-Repositories für alle benötigten Entities angelegt

### 1.3 Use-Case: Checklist
- [x] `ChecklistService` implementiert
- [x] `ChecklistController` implementiert
- [x] Basis-CRUD-Endpunkte für Checklists vorhanden
- [x] Checklist-Endpunkte in Bruno anlegst und getestet (Happy Path)

### 1.4 Use-Case: Inspection
- [x] `InspectionService` implementiert
- [x] `InspectionController` implementiert
- [x] Endpunkte für Inspections (lesen/anlegen/ändern/löschen) vorhanden
- [x] Inspection-Endpunkte in Bruno angelegt und getestet (Happy Path)

### 1.5 Use-Case: Step
- [ ] `StepService` implementiert
- [ ] `StepController` implementiert
- [ ] Endpunkte für Steps (lesen/anlegen/ändern/löschen) vorhanden
- [ ] Step-Endpunkte in Bruno angelegt und getestet (Happy Path)

### 1.6 Querschnittsthemen Backend
- [ ] Globales Error-Handling mit `@ControllerAdvice`
- [ ] Einheitliches Fehler-Response-Format (z. B. Error-DTO)
- [ ] Bean Validation auf Request-Bodies (z. B. `@NotNull`, `@Size`, …)
- [ ] Sinnvolles Logging eingerichtet
- [ ] Basis-Tests mit MockMvc für zentrale Endpoints
- [ ] (Optional) Swagger / OpenAPI-Doku


## 2. Frontend – React (Vite)

> Hier kannst du Häkchen setzen, sobald du den jeweiligen Schritt erledigt hast.

### 2.1 Grundsetup
- [ ] React/Vite-Projekt erstellt
- [ ] Projektstruktur (Pages/Components/Services) definiert
- [ ] Basis-Routing eingerichtet

### 2.2 API-Anbindung
- [ ] API-Service-Layer zum Backend (Fetch/Axios)
- [ ] Fehler- und Loading-Handling im API-Layer
- [ ] Environment-/Config für Backend-URL

### 2.3 Views / Seiten
- [ ] Übersichtsliste für Checklists
- [ ] Detailansicht für eine Checklist inkl. zugehöriger Inspections
- [ ] Detailansicht für eine Inspection inkl. Steps
- [ ] Formulare für Erstellen/Bearbeiten (mind. für einen Typ: Checklist oder Inspection oder Step)

### 2.4 UX / Feinschliff
- [ ] Grundlegendes Styling (Layout, Navigation)
- [ ] Sinnvolle Fehlermeldungen im UI
- [ ] Loading-Indikatoren


## 3. API-Tests – Bruno

### 3.1 Grundstruktur
- [x] Bruno-Collection für Backend angelegt
- [ ] Environments / Base-URL sauber konfiguriert
- [ ] Kurze Beschreibung/Notizen in der Collection ergänzt

### 3.2 Checklist-Flow
- [x] Requests für Checklist-Endpunkte (GET/POST/PUT/DELETE)
- [x] Happy-Path getestet (z. B. „Checklist erstellen → abrufen → ändern → löschen“)
- [ ] Negativfälle getestet (z. B. 404 bei unbekannter ID, Validierungsfehler)

### 3.3 Inspection-Flow
- [x] Requests für Inspection-Endpunkte
- [x] Happy-Path getestet (Inspection zu existierender Checklist anlegen)
- [ ] Negativfälle getestet (Checklist nicht vorhanden, ungültige Daten)

### 3.4 Step-Flow
- [ ] Requests für Step-Endpunkte
- [ ] Happy-Path getestet (Step zu Inspection anlegen, abrufen, ändern, löschen)
- [ ] Negativfälle getestet

### 3.5 End-to-End-Szenarien
- [ ] E2E: Checklist → Inspection → Steps komplett durchgespielt
- [ ] E2E-Szenario dokumentiert (kurze Beschreibung in Bruno oder README)


## 4. Planung & Architektur

### 4.1 Struktur & Layer
- [x] Package-Struktur definiert (z. B. `entity`, `repository`, `service`, `controller`)
- [x] Trennung von Domänenschicht (Entities/Services) und Web-Schicht (Controller)
- [ ] Kurze Text-Doku zur Architektur (1–2 Absätze, z. B. in `ARCHITECTURE.md`)

### 4.2 Designentscheidungen dokumentieren
- [ ] Entscheidung: Warum aktuell direkte Rückgabe von Entities (noch keine DTOs)
- [ ] Hinweis: Wie ein DTO-Layer aussehen *könnte* (für spätere Erweiterung)
- [ ] Erklärung der wichtigsten Relationen (Checklist ↔ Inspection ↔ Step)


## 5. Fehleranalyse & Bugs

> Dieser Bereich ist als laufende Liste gedacht.

### 5.1 Bug-Liste
- [ ] Datei `BUGS.md` angelegt
- [ ] Aktuell bekannte Bugs eingetragen (mit:
      Repro-Schritte, erwartetes Verhalten, aktuelles Verhalten)
- [ ] Für jeden Bug entschieden: „Fixen“ oder „bewusst offen lassen“

### 5.2 Technische Schulden
- [ ] Stellen markiert, an denen du „Quick & Dirty“-Lösungen verwendest (z. B. Kommentare `// TODO`)
- [ ] Liste dieser Stellen zentral gesammelt (z. B. in `TECH_DEBT.md`)


## 6. Projektorga & Dokumentation

### 6.1 README & Setup
- [ ] README mit Kurzbeschreibung des Projekts
- [ ] Anleitung: Wie Backend lokal gestartet wird
- [ ] Hinweis: Wie Bruno-Collection verwendet wird
- [ ] (Optional) Hinweis: Frontend-Start (falls vorhanden)

### 6.2 Für die Prüfung
- [ ] Kurze Anleitung „Wie prüfe ich das Projekt?“ (z. B. Schritte: Starten → bestimmte Requests / Screens)
- [ ] Liste der wichtigsten Endpoints mit kurzer Erklärung
- [ ] Kurze Notiz, welche Teile besonders prüfungsrelevant sind (z. B. Relationen, Error-Handling, Validation)
