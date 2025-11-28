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
- [ ] Feld „verantwortlicher Mitarbeiter“ in `Inspection` modelliert (z. B. Relation auf `User`)
- [ ] Felder für geplantes / tatsächliches Datum (z. B. `plannedDate`, `startedAt`, `finishedAt`)
- [ ] Status-Werte decken die Anforderungen ab (`GEPLANT` / `IN_BEARBEITUNG` / `ABGESCHLOSSEN`)

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
- [ ] Step-Status deckt Werte „erfüllt / nicht erfüllt / n. a.“ ab (Mapping auf `StepStatus`-Enum)
- [ ] Kommentarfeld pro Step in der API nutzbar
- [ ] Feld zum Hinterlegen eines Foto-Pfads oder Upload-Mechanismus (z. B. `photoPath`)

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

### 1.8 User & Authentifizierung

- [ ] Entity `User` angelegt (z. B. Felder: `id`, `username`, `passwordHash`, `displayName`, `role`)
- [ ] (Optional) Enum `UserRole` angelegt (z. B. `ADMIN`, `INSPECTOR`)
- [ ] Relation `User` ↔ `Inspection` modelliert (z. B. `assignedInspector`)
- [ ] `UserRepository` angelegt
- [ ] `UserService` implementiert (Verwaltung von Benutzern, Passwort-Hashing, Suche nach Username)
- [ ] `AuthController` implementiert (z. B. `/api/auth/login`, `/api/auth/me`)
- [ ] Spring Security Grundkonfiguration eingerichtet
- [ ] Passwort-Hashing mit BCrypt konfiguriert
- [ ] Login-Endpunkt implementiert (`/api/auth/login`, gibt Token oder Session-Info zurück)
- [ ] Endpoint zum Laden der Inspektionen des eingeloggten Users (z. B. `GET /api/me/inspections`)
- [ ] Security-Regeln definiert (z. B. nur eingeloggter User sieht seine Inspektionen, Admin sieht alle)

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
- [x] Detailansicht für eine Checklist inkl. Steps
- [x] Detailansicht für eine Inspection inkl. Steps
- [ ] Anzeige der zugehörigen Inspections bei einer Checklist
- [ ] Formulare für Erstellen/Bearbeiten (mind. für einen Typ: Checklist oder Inspection oder Step)

- [ ] Dashboard-Seite

  - [ ] Anzeige geplanter Inspektionen
  - [ ] Anzeige laufender Inspektionen
  - [ ] Anzeige abgeschlossener Inspektionen
  - [ ] Anzeige wichtiger Kennzahlen (z. B. Anzahl offener Inspektionen)

- [ ] Ergebnisansicht / Berichtseite für eine Inspection
  - [ ] Zusammenfassung der Ergebnisse (z. B. Anzahl erfüllter/nicht erfüllter/n. a. Steps)
  - [ ] Anzeige aller Kommentare und Fotos
  - [ ] Druckansicht / PDF-Export (z. B. über Browser-Print)

### 2.4 UX / Feinschliff

- [ ] Grundlegendes Styling (Layout, Navigation)
- [ ] Sinnvolle Fehlermeldungen im UI
- [ ] Loading-Indikatoren

### 2.5 Authentifizierung & User-Flow (Frontend)

- [ ] `authService` im Frontend (z. B. `login()`, `logout()`, `getCurrentUser()`)
- [ ] Login-Seite (Formular für Benutzername/Passwort)
- [ ] Speicherung der Auth-Info (z. B. Token) im `localStorage` oder `sessionStorage`
- [ ] Globaler Auth-Context oder Hook (z. B. `AuthContext`, `useAuth()`)
- [ ] Geschützte Routen (Redirect auf Login, wenn nicht eingeloggt)
- [ ] Anzeige des eingeloggten Benutzers in der Navbar
- [ ] Logout-Funktion (Button in der Navbar)

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

### 3.6 Auth-Flow

- [ ] Requests für Auth-Endpunkte angelegt (z. B. `/api/auth/login`, `/api/auth/me`)
- [ ] Happy-Path Login getestet (korrekte Logindaten → gültige Antwort)
- [ ] Negativfälle getestet (falsche Logindaten, gesperrter User etc.)
- [ ] (Optional) Requests für User-bezogene Endpoints (z. B. `/api/me/inspections`)

## 4. Planung & Architektur

### 4.1 Struktur & Layer

- [x] Package-Struktur definiert (`entity`, `repository`, `service`, `controller`, `dto`)
- [x] Trennung von Domänenschicht (Entities/Services) und Web-Schicht (Controller)
- [ ] Kurze Text-Doku zur Architektur (1–2 Absätze, z. B. in `docs/ARCHITECTURE.md`)

### 4.2 Designentscheidungen dokumentieren

- [ ] Entscheidung: Warum aktuell (noch) direkte Rückgabe von Entities (und wie ein DTO-Layer aussehen könnte)
- [ ] Erklärung der wichtigsten Relationen (Checklist ↔ Inspection ↔ ChecklistStep/InspectionStep)
- [ ] Entscheidung: Wie Authentifizierung umgesetzt wird (Spring Security, Token/Session, Rollenmodell)
- [ ] Hinweis: Welche Endpoints geschützt sind und warum

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
- [ ] Hinweis: Wie das Frontend gestartet wird
- [ ] Beschreibung: Wie man sich einloggt (Standard-User/Passwort, Rollen)

### 6.2 Für die Prüfung

- [ ] Kurze Anleitung „Wie prüfe ich das Projekt?“ (z. B. Schritte: Starten → Dashboard → Inspektion durchführen)
- [ ] Liste der wichtigsten Endpoints mit kurzer Erklärung
- [ ] Kurze Notiz, welche Teile besonders prüfungsrelevant sind (z. B. Relationen, Error-Handling, Validation, Auth)
- [ ] Hinweis, wie Auth / User-System im Zusammenspiel von Backend & Frontend funktioniert
