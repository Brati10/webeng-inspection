import { Routes, Route, Navigate } from "react-router-dom";
import Navbar from "./components/layout/Navbar";
import ProtectedRoute from "./components/ProtectedRoute";

// Pages
import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import ChecklistListPage from "./pages/ChecklistListPage";
import ChecklistDetailPage from "./pages/ChecklistDetailPage";
import InspectionListPage from "./pages/InspectionListPage";
import InspectionCreatePage from "./pages/InspectionCreatePage";
import InspectionDetailPage from "./pages/InspectionDetailPage";
import ChecklistCreatePage from "./pages/ChecklistCreatePage";

function App() {
  return (
    <div>
      <Navbar />
      <main style={{ padding: "1rem" }}>
        <Routes>
          {/* ===== PUBLIC ROUTES ===== */}
          <Route path="/login" element={<LoginPage />} />

          {/* ===== PROTECTED ROUTES (für alle) ===== */}
          <Route
            path="/"
            element={
              <ProtectedRoute>
                <DashboardPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/checklists"
            element={
              <ProtectedRoute>
                <ChecklistListPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/checklists/:checklistId"
            element={
              <ProtectedRoute>
                <ChecklistDetailPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/checklists/create"
            element={
              <ProtectedRoute>
                <ChecklistCreatePage />
              </ProtectedRoute>
            }
          />

          {/* ===== INSPECTION ROUTES (für ADMIN) ===== */}
          <Route
            path="/inspections"
            element={
              <ProtectedRoute>
                <InspectionListPage />
              </ProtectedRoute>
            }
          />

          <Route
            path="/inspections/create"
            element={
              <ProtectedRoute>
                <InspectionCreatePage />
              </ProtectedRoute>
            }
          />

          {/* ===== DETAIL ROUTES (für beide) ===== */}
          <Route
            path="/inspections/:inspectionId"
            element={
              <ProtectedRoute>
                <InspectionDetailPage />
              </ProtectedRoute>
            }
          />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
