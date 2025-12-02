import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../services/api/httpClient";
import { useAuth } from "../context/useAuth";

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

  // Nur ADMIN darf diese Page sehen
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
    return <p>Zugriff verweigert</p>;
  }

  if (loading) return <p>Lädt Inspektionen...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>Inspektionen (Admin-Bereich)</h1>
      <Link to="/inspections/create">
        <button
          style={{
            padding: "0.5rem 1rem",
            marginBottom: "1rem",
            backgroundColor: "#28a745",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: "pointer",
          }}
        >
          Neue Inspection
        </button>
      </Link>

      {inspections.length === 0 ? (
        <p>Keine Inspektionen vorhanden.</p>
      ) : (
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr
              style={{
                backgroundColor: "#f5f5f5",
                borderBottom: "2px solid #ddd",
              }}
            >
              <th style={{ padding: "0.5rem", textAlign: "left" }}>Titel</th>
              <th style={{ padding: "0.5rem", textAlign: "left" }}>Anlage</th>
              <th style={{ padding: "0.5rem", textAlign: "left" }}>Status</th>
              <th style={{ padding: "0.5rem", textAlign: "left" }}>
                Inspector
              </th>
              <th style={{ padding: "0.5rem", textAlign: "left" }}>Aktion</th>
            </tr>
          </thead>
          <tbody>
            {inspections.map((inspection) => (
              <tr
                key={inspection.id}
                style={{ borderBottom: "1px solid #ddd" }}
              >
                <td style={{ padding: "0.5rem" }}>{inspection.title}</td>
                <td style={{ padding: "0.5rem" }}>{inspection.plantName}</td>
                <td style={{ padding: "0.5rem" }}>
                  <span
                    style={{
                      padding: "0.25rem 0.5rem",
                      backgroundColor:
                        inspection.status === "COMPLETED"
                          ? "#d4edda"
                          : inspection.status === "IN_PROGRESS"
                          ? "#fff3cd"
                          : "#e7e7e7",
                      borderRadius: "4px",
                      fontSize: "0.9rem",
                    }}
                  >
                    {inspection.status}
                  </span>
                </td>
                <td style={{ padding: "0.5rem" }}>
                  {inspection.assignedInspector?.displayName}
                </td>
                <td style={{ padding: "0.5rem" }}>
                  <Link
                    to={`/inspections/${inspection.id}`}
                    style={{ color: "#007bff" }}
                  >
                    Bearbeiten
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
