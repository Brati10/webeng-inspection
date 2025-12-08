import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api/httpClient";
import { useAuth } from "../context/useAuth";
import "./InspectionListPage.css";

interface Inspection {
  id: number;
  title: string;
  status: "PLANNED" | "IN_PROGRESS" | "COMPLETED";
  plantName: string;
  assignedInspector: { id: number; displayName: string };
}

export default function InspectionListPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [inspections, setInspections] = useState<Inspection[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (user?.role !== "ADMIN") {
      navigate("/", { replace: true });
      return;
    }
  }, [user?.role, navigate]);

  useEffect(() => {
    const fetchInspections = async () => {
      try {
        setError(null);
        const response = await api.get("/inspections");
        setInspections(response.data);
      } catch (err) {
        console.error(err);
        setError("Fehler beim Laden der Inspektionen");
      } finally {
        setLoading(false);
      }
    };

    if (user?.role === "ADMIN") {
      fetchInspections();
    }
  }, [user?.role]);

  if (!user || user.role !== "ADMIN") {
    return <p className="text-muted">Zugriff verweigert</p>;
  }

  if (loading) return <p className="text-muted">Lädt Inspektionen...</p>;
  if (error) return <div className="alert alert-danger">{error}</div>;

  return (
    <div className="inspection-list">
      <div className="page-header">
        <h1>Inspektionen (Admin-Bereich)</h1>
        <Link to="/inspections/create" className="btn-primary">
          + Neue Inspektion
        </Link>
      </div>

      {inspections.length === 0 ? (
        <div className="empty-state">
          <p>Keine Inspektionen vorhanden.</p>
        </div>
      ) : (
        <div className="inspection-grid">
          {inspections.map((inspection) => (
            <div key={inspection.id} className="inspection-card card">
              <div className="card-header">
                <h3>{inspection.title}</h3>
                <span
                  className={`badge badge-${inspection.status.toLowerCase()}`}
                >
                  {inspection.status === "PLANNED"
                    ? "Geplant"
                    : inspection.status === "IN_PROGRESS"
                    ? "In Bearbeitung"
                    : "Abgeschlossen"}
                </span>
              </div>
              <div className="card-body">
                <div className="info-group">
                  <span className="info-label">Anlage:</span>
                  <span className="info-value">{inspection.plantName}</span>
                </div>
                <div className="info-group">
                  <span className="info-label">Inspektor:</span>
                  <span className="info-value">
                    {inspection.assignedInspector?.displayName}
                  </span>
                </div>
              </div>
              <div className="card-footer">
                <Link
                  to={`/inspections/${inspection.id}`}
                  className="btn-primary btn-sm"
                >
                  Bearbeiten
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
