import { useEffect, useState } from "react";
import api from "../services/api/httpClient";
import { useAuth } from "../context/useAuth";
import "./DashboardPage.css";

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
          response = await api.get("/inspections");
        } else {
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

  if (loading) return <p className="text-muted">Lädt Dashboard...</p>;
  if (error) return <div className="alert alert-danger">{error}</div>;

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <h1>Dashboard</h1>
        <p className="subtitle">
          Willkommen, {user?.displayName}! ({user?.role})
        </p>
      </div>

      <section className="dashboard-section">
        <h2>Kennzahlen</h2>
        <div className="stats-grid">
          <div className="stat-card stat-planned">
            <div className="stat-number">{planned}</div>
            <div className="stat-label">Geplant</div>
          </div>
          <div className="stat-card stat-inprogress">
            <div className="stat-number">{inProgress}</div>
            <div className="stat-label">In Bearbeitung</div>
          </div>
          <div className="stat-card stat-completed">
            <div className="stat-number">{completed}</div>
            <div className="stat-label">Abgeschlossen</div>
          </div>
        </div>
      </section>

      <section className="dashboard-section">
        <h2>Inspektionen</h2>
        {inspections.length === 0 ? (
          <p className="text-muted">Keine Inspektionen vorhanden.</p>
        ) : (
          <div className="table-wrapper">
            <table className="inspections-table">
              <thead>
                <tr>
                  <th>Titel</th>
                  <th>Anlage</th>
                  <th>Status</th>
                  <th>Inspector</th>
                  <th>Aktion</th>
                </tr>
              </thead>
              <tbody>
                {inspections.map((inspection) => (
                  <tr key={inspection.id}>
                    <td className="cell-title">{inspection.title}</td>
                    <td>{inspection.plantName}</td>
                    <td>
                      <span
                        className={`badge badge-${inspection.status.toLowerCase()}`}
                      >
                        {inspection.status === "PLANNED"
                          ? "Geplant"
                          : inspection.status === "IN_PROGRESS"
                          ? "In Bearbeitung"
                          : "Abgeschlossen"}
                      </span>
                    </td>
                    <td>{inspection.assignedInspector?.displayName}</td>
                    <td>
                      <a
                        href={`/inspections/${inspection.id}`}
                        className="link-action"
                      >
                        Anzeigen
                      </a>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </div>
  );
}
