# Inspektionsverwaltung

Web-Engineering Abschlussprojekt TIF25A | DHBW Lörrach
Conrad Ahlborn, Nathaniel Ainsworth, Lucas Sedelmayr

---

## Voraussetzungen

- **Java 21** (JDK)
- **Node.js 24.x** (oder aktuell)

---

## Installation & Start

### Backend starten

```bash
# Im Projekt-Root
./gradlew bootRun

# Windows:
gradlew.bat bootRun
```

Backend läuft auf: **http://localhost:8080**

### Frontend starten

```bash
# Neues Terminal
cd frontend

# Beim ersten Mal: Dependencies installieren
npm install

# Dev-Server starten
npm run dev
```

Frontend läuft auf: **http://localhost:5173**

---

## Login

| Benutzer       | Passwort       | Rolle     |
| -------------- | -------------- | --------- |
| `admin`        | `admin123`     | Admin     |
| `inspector`    | `inspector123` | Inspector |
| `thomas.weber` | `inspector123` | Inspector |

---

## Datenbank

- **H2 Database** (file-based, persistent)
- Daten werden in `./data/inspectiondb.mv.db` gespeichert
- Hochgeladene Fotos: `./uploads/photos/`
