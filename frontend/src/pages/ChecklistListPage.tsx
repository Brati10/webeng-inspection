import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../services/api/httpClient";
import { useAuth } from "../context/useAuth";

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

      // Checklist ist in Gebrauch (409 Conflict)
      if (err.response?.status === 409) {
        // Zahl aus der Fehlermeldung extrahieren
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

  if (loading) return <p>Lädt Checklisten...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>Checklisten</h1>

      {user?.role === "ADMIN" && (
        <Link to="/checklists/create">
          <button
            style={{
              marginBottom: "1rem",
              padding: "0.5rem 1rem",
              backgroundColor: "#28a745",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
            }}
          >
            Neue Checklist
          </button>
        </Link>
      )}

      {checklists.length === 0 ? (
        <p>Keine Checklisten vorhanden.</p>
      ) : (
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr
              style={{
                backgroundColor: "#f5f5f5",
                borderBottom: "2px solid #ddd",
              }}
            >
              <th style={{ padding: "0.5rem", textAlign: "left" }}>Name</th>
              <th style={{ padding: "0.5rem", textAlign: "left" }}>Standort</th>
              <th style={{ padding: "0.5rem", textAlign: "left" }}>Aktion</th>
            </tr>
          </thead>
          <tbody>
            {checklists.map((checklist) => (
              <tr key={checklist.id} style={{ borderBottom: "1px solid #ddd" }}>
                <td style={{ padding: "0.5rem" }}>{checklist.name}</td>
                <td style={{ padding: "0.5rem" }}>{checklist.plantName}</td>
                <td style={{ padding: "0.5rem" }}>
                  <Link
                    to={`/checklists/${checklist.id}`}
                    style={{ color: "#007bff", marginRight: "1rem" }}
                  >
                    Anzeigen
                  </Link>
                  {user?.role === "ADMIN" && (
                    <button
                      onClick={() => handleDelete(checklist.id, checklist.name)}
                      disabled={deleting === checklist.id}
                      style={{
                        padding: "0.25rem 0.75rem",
                        backgroundColor:
                          deleting === checklist.id ? "#ccc" : "#dc3545",
                        color: "white",
                        border: "none",
                        borderRadius: "4px",
                        cursor:
                          deleting === checklist.id ? "not-allowed" : "pointer",
                      }}
                    >
                      {deleting === checklist.id ? "Löschen..." : "Löschen"}
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
