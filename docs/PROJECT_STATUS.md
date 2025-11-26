# Projektstatus – webeng-inspection

> Stand: bitte bei Änderung von dir aktualisieren

## 1. Backend – Spring Boot

### 1.1 Projektgrundlage

- [x] Spring-Boot-Projekt angelegt (Gradle, Java)
- [x] Anwendung startet ohne Fehler
- [x] Package-Struktur festgelegt (`controller`, `service`, `repository`, `entity`, `dto`)

### 1.2 Domänenmodell & Persistenz

- [x] Entity `Checklist` angelegt
- [x] Entity `Inspection` angelegt
- [x] Entity `ChecklistStep` angelegt
- [x] Entity `InspectionStep` angelegt
- [x] Enum `StepStatus` angelegt
- [x] Relationen `Checklist` ↔ `Inspection` modelliert
- [x] Relationen `Checklist` ↔ `ChecklistStep` modelliert
- [x] Relationen `Inspection` ↔ `InspectionStep` modelliert
- [x] Relation `InspectionStep` ↔ `ChecklistStep` modelliert
- [x] JPA-Repositories für `Checklist`, `Inspection`, `ChecklistStep`, `InspectionStep` angelegt

### 1.3 Use-Case: Checklist

- [x] `ChecklistService` implementiert
- [x] `ChecklistController` implementiert
- [x] Basis-CRUD-Endpunkte für Checklists vorhanden:
  - `GET /api/checklists`
  - `GET /api/checklists/{id}`
  - `POST /api/checklists`
  - `PUT /api/checklists/{id}`
  - `DELETE /api/checklists/{id}`
- [x] Logik zum Aktualisieren der Steps innerhalb einer Checklist im Service implementiert

### 1.4 Use-Case: Inspection

- [x] DTO `InspectionCreateRequest` angelegt
- [x] `InspectionService` implementiert
- [x] `InspectionController` implementiert
- [x] Endpunkte für Inspections vorhanden:
  - `GET /api/inspections`
  - `GET /api/inspections/{id}`
  - `POST /api/inspections` (Erzeugen inkl. zugehöriger `InspectionStep`s aus `ChecklistStep`s)
  - `DELETE /api/inspections/{id}`
  - `PATCH /api/inspections/{id}/status` (Status-Update)
- [x] Automatisches Anlegen der `InspectionStep`s beim Erzeugen einer Inspection umgesetzt

### 1.5 Use-Case: Steps

#### 1.5.1 ChecklistSteps (Vorlagen-Schritte)

- [x] `ChecklistStepService` implementiert
- [x] `ChecklistStepController` implementiert
- [x] Endpunkte für ChecklistSteps (lesen/anlegen/ändern/löschen) vorhanden
- [x] ChecklistStep-Endpunkte in Bruno angelegt und getestet (Happy Path)

#### 1.5.2 InspectionSteps (konkrete Schritte einer Inspection)

- [x] `InspectionStepService` implementiert
- [x] `InspectionStepController` implementiert
- [x] Endpunkte für InspectionSteps (lesen/anlegen/ändern/löschen, Status/Kommentar setzen) vorhanden
- [x] InspectionStep-Endpunkte in Bruno angelegt und getestet (Happy Path)

### 1.6 Querschnittsthemen Backend

- [x] Globales Error-Handling mit `@ControllerAdvice`
- [x] Einheitliches Fehler-Response-Format (z. B. Error-DTO)
- [x] Bean Validation auf Request-Bodies (z. B. `@NotNull`, `@Size`, …)
- [x] Sinnvolles Logging eingerichtet (z. B. in Services/Controllern)
- [x] Basis-Tests mit MockMvc für Checklist
- [x] Basis-Tests mit MockMvc für Inspection
- [x] Basis-Tests mit MockMvc für Steps
- [ ] (Optional) Swagger / OpenAPI-Doku

### 1.7 Codequalität & Dokumentation

- [x] JavaDoc für Services ergänzt (Checklist, Inspection, ChecklistStep, InspectionStep)
- [x] JavaDoc für Controller ergänzt (Checklist, Inspection, ChecklistStep, InspectionStep)
- [x] JavaDoc für Entities ergänzt (Checklist, Inspection, ChecklistStep, InspectionStep, StepStatus)
- [x] JavaDoc für DTOs ergänzt (InspectionCreateRequest)
- [x] JavaDoc für Fehlerklassen ergänzt (ErrorResponse, GlobalExceptionHandler)

## 2. Frontend – React (Vite)

### 2.1 Grundsetup

- [x] React/Vite-Projekt erstellt
- [x] Projektstruktur (Pages/Components/Services) definiert
- [x] Basis-Routing eingerichtet

### 2.2 API-Anbindung

- [x] API-Service-Layer zum Backend (Fetch/Axios)
- [ ] Fehler- und Loading-Handling im API-Layer
- [x] Environment-/Config für Backend-URL

### 2.3 Views / Seiten

- [x] Übersichtsliste für Checklists
- [x] Detailansicht für eine Checklist inkl. zugehöriger Inspections
- [ ] Detailansicht für eine Inspection inkl. Steps
- [ ] Formulare für Erstellen/Bearbeiten (mind. für einen Typ: Checklist oder Inspection oder Step)

### 2.4 UX / Feinschliff

- [ ] Grundlegendes Styling (Layout, Navigation)
- [ ] Sinnvolle Fehlermeldungen im UI
- [ ] Loading-Indikatoren

## 3. API-Tests – Bruno

### 3.1 Grundstruktur

- [x] Bruno-Collection für Backend angelegt (`api-tests/bruno/webeng-inspection`)
- [x] Environment `local` mit Base-URL konfiguriert

### 3.2 Checklist-Flow

- [x] Requests für Checklist-Endpunkte angelegt:
  - Create checklist
  - Get all checklists
  - Get checklist by id
  - Update checklist
  - Delete checklist
- [x] Happy-Path getestet (z. B. „Checklist erstellen → abrufen → ändern → löschen“)
- [ ] Negativfälle getestet (z. B. 404 bei unbekannter ID, Validierungsfehler)

### 3.3 Inspection-Flow

- [x] Requests für Inspection-Endpunkte angelegt:
  - Create inspection
  - Get all inspections
  - Get inspection by id
  - Update inspection status
  - Delete inspection
- [x] Happy-Path getestet (Inspection zu existierender Checklist anlegen, abrufen, löschen)
- [ ] Negativfälle getestet (Checklist nicht vorhanden, ungültige Daten)

### 3.4 Step-Flow

- [ ] Requests für ChecklistStep-Endpunkte angelegt
- [ ] Requests für InspectionStep-Endpunkte angelegt
- [ ] Happy-Path getestet (Steps zu Checklist/Inspection anlegen, abrufen, ändern, löschen)
- [ ] Negativfälle getestet

### 3.5 End-to-End-Szenarien

- [x] E2E: Checklist → Inspection → Steps komplett durchgespielt
- [x] E2E-Szenario dokumentiert (kurze Beschreibung in Bruno oder README)

## 4. Planung & Architektur

### 4.1 Struktur & Layer

- [x] Package-Struktur definiert (`entity`, `repository`, `service`, `controller`, `dto`)
- [x] Trennung von Domänenschicht (Entities/Services) und Web-Schicht (Controller)
- [ ] Kurze Text-Doku zur Architektur (1–2 Absätze, z. B. in `docs/ARCHITECTURE.md`)

### 4.2 Designentscheidungen dokumentieren

- [ ] Entscheidung: Warum aktuell direkte Rückgabe von Entities (noch keine vollständige DTO-Schicht)
- [ ] Hinweis: Wie ein DTO-Layer aussehen _könnte_ (für spätere Erweiterung)
- [ ] Erklärung der wichtigsten Relationen (Checklist ↔ Inspection ↔ ChecklistStep/InspectionStep)

## 5. Fehleranalyse & Bugs

### 5.1 Bug-Liste

- [ ] Datei `docs/BUGS.md` angelegt
- [ ] Aktuell bekannte Bugs eingetragen (mit Repro-Schritten, erwartetes Verhalten, aktuelles Verhalten)
- [ ] Für jeden Bug entschieden: „Fixen“ oder „bewusst offen lassen“

### 5.2 Technische Schulden

- [ ] Stellen markiert, an denen du „Quick & Dirty“-Lösungen verwendest (z. B. Kommentare `// TODO`)
- [ ] Liste dieser Stellen zentral gesammelt (z. B. in `docs/TECH_DEBT.md`)

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
