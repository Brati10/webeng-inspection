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

type StatusFilter = "PLANNED" | "IN_PROGRESS" | "COMPLETED";

export default function DashboardPage() {
  const { user } = useAuth();
  const [inspections, setInspections] = useState<Inspection[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedStatus, setSelectedStatus] = useState<StatusFilter>("PLANNED");

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

  const filteredInspections = inspections.filter(
    (i) => i.status === selectedStatus
  );

  const getStatusLabel = (status: StatusFilter) => {
    switch (status) {
      case "PLANNED":
        return "Geplant";
      case "IN_PROGRESS":
        return "In Bearbeitung";
      case "COMPLETED":
        return "Abgeschlossen";
    }
  };

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
          <div
            className={`stat-card stat-planned ${
              selectedStatus === "PLANNED" ? "stat-active" : ""
            }`}
            onClick={() => setSelectedStatus("PLANNED")}
            role="button"
            tabIndex={0}
            onKeyDown={(e) => {
              if (e.key === "Enter" || e.key === " ") {
                setSelectedStatus("PLANNED");
              }
            }}
          >
            <div className="stat-number">{planned}</div>
            <div className="stat-label">Geplant</div>
          </div>
          <div
            className={`stat-card stat-inprogress ${
              selectedStatus === "IN_PROGRESS" ? "stat-active" : ""
            }`}
            onClick={() => setSelectedStatus("IN_PROGRESS")}
            role="button"
            tabIndex={0}
            onKeyDown={(e) => {
              if (e.key === "Enter" || e.key === " ") {
                setSelectedStatus("IN_PROGRESS");
              }
            }}
          >
            <div className="stat-number">{inProgress}</div>
            <div className="stat-label">In Bearbeitung</div>
          </div>
          <div
            className={`stat-card stat-completed ${
              selectedStatus === "COMPLETED" ? "stat-active" : ""
            }`}
            onClick={() => setSelectedStatus("COMPLETED")}
            role="button"
            tabIndex={0}
            onKeyDown={(e) => {
              if (e.key === "Enter" || e.key === " ") {
                setSelectedStatus("COMPLETED");
              }
            }}
          >
            <div className="stat-number">{completed}</div>
            <div className="stat-label">Abgeschlossen</div>
          </div>
        </div>
      </section>

      <section className="dashboard-section">
        {filteredInspections.length === 0 ? (
          <p className="text-muted">
            Keine Inspektionen im Status "{getStatusLabel(selectedStatus)}".
          </p>
        ) : (
          <div className="table-wrapper">
            <table className="inspections-table">
              <thead>
                <tr>
                  <th>Titel</th>
                  <th>Anlage</th>
                  <th>Inspektor</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {filteredInspections.map((inspection) => (
                  <tr key={inspection.id}>
                    <td className="cell-title">{inspection.title}</td>
                    <td>{inspection.plantName}</td>
                    <td>{inspection.assignedInspector?.displayName}</td>
                    <td>
                      <a
                        href={`/inspections/${inspection.id}`}
                        className="btn-primary btn-sm"
                      >
                        Bearbeiten
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
