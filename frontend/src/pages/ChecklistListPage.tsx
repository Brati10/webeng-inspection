import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../services/api/httpClient";
import { useAuth } from "../context/useAuth";
import "./ChecklistListPage.css";

interface Checklist {
  id: number;
  name: string;
  plantName: string;
  recommendations: string;
}

export default function ChecklistListPage() {
  const { user } = useAuth();
  const [checklists, setChecklists] = useState<Checklist[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deleting, setDeleting] = useState<number | null>(null);

  useEffect(() => {
    const fetchChecklists = async () => {
      try {
        setError(null);
        const response = await api.get("/checklists");
        setChecklists(response.data);
      } catch (err) {
        console.error(err);
        setError("Fehler beim Laden der Checklisten");
      } finally {
        setLoading(false);
      }
    };

    fetchChecklists();
  }, []);

  const handleDelete = async (id: number, name: string) => {
    if (!window.confirm(`Checklist "${name}" wirklich löschen?`)) {
      return;
    }

    setDeleting(id);
    try {
      await api.delete(`/checklists/${id}`);
      setChecklists(checklists.filter((c) => c.id !== id));
    } catch (err: any) {
      console.error(err);

      if (err.response?.status === 409) {
        const message = err.response.data.message || "";
        const match = message.match(/\d+/);
        const count = match ? parseInt(match[0], 10) : 0;

        setError(
          `Checklist kann nicht gelöscht werden: ${count} Inspection${
            count !== 1 ? "en" : ""
          } basiert${count !== 1 ? "en" : ""} auf dieser Checklist.`
        );
      } else {
        setError("Fehler beim Löschen der Checklist");
      }
    } finally {
      setDeleting(null);
    }
  };

  if (loading) return <p className="text-muted">Lädt Checklisten...</p>;
  if (error) return <div className="alert alert-danger">{error}</div>;

  return (
    <div className="checklist-list">
      <div className="page-header">
        <h1>Checklisten</h1>
        {user?.role === "ADMIN" && (
          <Link to="/checklists/create" className="btn-primary">
            + Neue Checklist
          </Link>
        )}
      </div>

      {checklists.length === 0 ? (
        <div className="empty-state">
          <p>Keine Checklisten vorhanden.</p>
        </div>
      ) : (
        <div className="checklist-grid">
          {checklists.map((checklist) => (
            <div key={checklist.id} className="checklist-card card">
              <div className="card-header">
                <h3>{checklist.name}</h3>
              </div>
              <div className="card-body">
                <div className="info-group">
                  <span className="info-label">Standort:</span>
                  <span className="info-value">{checklist.plantName}</span>
                </div>
                {checklist.recommendations && (
                  <div className="info-group">
                    <span className="info-label">Empfehlungen:</span>
                    <p className="info-text">{checklist.recommendations}</p>
                  </div>
                )}
              </div>
              <div className="card-footer">
                <Link
                  to={`/checklists/${checklist.id}`}
                  className="btn-primary btn-sm"
                >
                  Anzeigen
                </Link>
                {user?.role === "ADMIN" && (
                  <button
                    onClick={() => handleDelete(checklist.id, checklist.name)}
                    disabled={deleting === checklist.id}
                    className="btn-danger btn-sm"
                  >
                    {deleting === checklist.id ? "Löschen..." : "Löschen"}
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
