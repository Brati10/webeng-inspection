import { Routes, Route, Navigate } from "react-router-dom";
import ChecklistListPage from "./pages/ChecklistListPage";
import ChecklistDetailPage from "./pages/ChecklistDetailPage";
import InspectionDetailPage from "./pages/InspectionDetailPage";
import Navbar from "./components/layout/Navbar";

function App() {
  return (
    <div>
      <Navbar />
      <main style={{ padding: "1rem" }}>
        <Routes>
          {/* Standard: / -> /checklists */}
          <Route path="/" element={<Navigate to="/checklists" replace />} />

          {/* Übersicht der Checklists */}
          <Route path="/checklists" element={<ChecklistListPage />} />

          {/* Detail einer Checklist */}
          <Route
            path="/checklists/:checklistId"
            element={<ChecklistDetailPage />}
          />

          {/* Detail einer Inspection */}
          <Route
            path="/inspections/:inspectionId"
            element={<InspectionDetailPage />}
          />
        </Routes>
      </main>
    </div>
  );
}

export default App;
