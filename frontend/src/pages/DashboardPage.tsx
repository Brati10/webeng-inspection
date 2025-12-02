import { useEffect, useState } from "react";
import api from "../services/api/httpClient";
import { useAuth } from "../context/useAuth";

interface Inspection {
  id: number;
  title: string;
  status: "PLANNED" | "IN_PROGRESS" | "COMPLETED";
  plantName: string;
  assignedInspector: { id: number; displayName: string };
}

export default function DashboardPage() {
  const { user } = useAuth();
  const [inspections, setInspections] = useState<Inspection[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchInspections = async () => {
      try {
        setError(null);
        let response;

        if (user?.role === "ADMIN") {
          // Admin sieht alle Inspektionen
          response = await api.get("/inspections");
        } else {
          // Inspector sieht nur seine Inspektionen
          response = await api.get(`/inspections/by-user/${user?.id}`);
        }

        setInspections(response.data);
      } catch (err) {
        console.error(err);
        setError("Fehler beim Laden der Inspektionen");
      } finally {
        setLoading(false);
      }
    };

    fetchInspections();
  }, [user?.id, user?.role]);

  const planned = inspections.filter((i) => i.status === "PLANNED").length;
  const inProgress = inspections.filter(
    (i) => i.status === "IN_PROGRESS"
  ).length;
  const completed = inspections.filter((i) => i.status === "COMPLETED").length;

  if (loading) return <p>Lädt Dashboard...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>Dashboard</h1>

      <div style={{ marginBottom: "2rem" }}>
        <h2>Kennzahlen</h2>
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "repeat(3, 1fr)",
            gap: "1rem",
          }}
        >
          <div style={{ border: "1px solid #ddd", padding: "1rem" }}>
            <p style={{ fontSize: "2rem", margin: "0 0 0.5rem 0" }}>
              {planned}
            </p>
            <p style={{ margin: "0", color: "#666" }}>Geplant</p>
          </div>
          <div style={{ border: "1px solid #ddd", padding: "1rem" }}>
            <p style={{ fontSize: "2rem", margin: "0 0 0.5rem 0" }}>
              {inProgress}
            </p>
            <p style={{ margin: "0", color: "#666" }}>In Bearbeitung</p>
          </div>
          <div style={{ border: "1px solid #ddd", padding: "1rem" }}>
            <p style={{ fontSize: "2rem", margin: "0 0 0.5rem 0" }}>
              {completed}
            </p>
            <p style={{ margin: "0", color: "#666" }}>Abgeschlossen</p>
          </div>
        </div>
      </div>

      <h2>Inspektionen</h2>
      {inspections.length === 0 ? (
        <p>Keine Inspektionen vorhanden.</p>
      ) : (
        <table
          style={{
            width: "100%",
            borderCollapse: "collapse",
            marginBottom: "2rem",
          }}
        >
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
                  <a
                    href={`/inspections/${inspection.id}`}
                    style={{ color: "#007bff" }}
                  >
                    Anzeigen
                  </a>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
