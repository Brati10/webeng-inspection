import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api/httpClient";
import "./InspectionCreatePage.css";

interface Checklist {
  id: number;
  name: string;
  plantName: string;
}

interface User {
  id: number;
  displayName: string;
}

export default function InspectionCreatePage() {
  const navigate = useNavigate();
  const [checklists, setChecklists] = useState<Checklist[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    title: "",
    checklistId: "",
    assignedInspectorId: "",
    plantName: "",
    plannedDate: "",
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [checklistsRes, usersRes] = await Promise.all([
          api.get("/checklists"),
          api.get("/users"),
        ]);
        setChecklists(checklistsRes.data);
        setUsers(usersRes.data);
      } catch (err) {
        console.error(err);
        setError("Fehler beim Laden der Daten");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      const payload = {
        title: formData.title,
        checklistId: Number(formData.checklistId),
        assignedInspectorId: Number(formData.assignedInspectorId),
        plantName: formData.plantName,
        plannedDate: formData.plannedDate || null,
      };

      const response = await api.post("/inspections", payload);
      navigate(`/inspections/${response.data.id}`);
    } catch (err) {
      console.error(err);
      setError("Fehler beim Erstellen der Inspection");
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) return <p className="text-muted">Lädt Formulardaten...</p>;

  return (
    <div className="inspection-create">
      <div className="detail-header">
        <h1>Neue Inspection anlegen</h1>
        <button
          onClick={() => navigate("/inspections")}
          className="btn-back"
          title="Abbrechen"
        >
          ✕
        </button>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={handleSubmit} className="create-form">
        <section className="form-section">
          <h2>Inspection-Details</h2>
          <div className="section-content">
            <div className="form-group">
              <label htmlFor="title">Titel *</label>
              <input
                id="title"
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                placeholder="z.B. Sicherheitsprüfung Mai 2025"
                required
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="checklistId">Checkliste *</label>
                <select
                  id="checklistId"
                  name="checklistId"
                  value={formData.checklistId}
                  onChange={handleChange}
                  required
                >
                  <option value="">-- Checkliste wählen --</option>
                  {checklists.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="assignedInspectorId">
                  Verantwortlicher Inspector *
                </label>
                <select
                  id="assignedInspectorId"
                  name="assignedInspectorId"
                  value={formData.assignedInspectorId}
                  onChange={handleChange}
                  required
                >
                  <option value="">-- Inspector wählen --</option>
                  {users.map((u) => (
                    <option key={u.id} value={u.id}>
                      {u.displayName}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="plantName">Anlage</label>
              <input
                id="plantName"
                type="text"
                name="plantName"
                value={formData.plantName}
                onChange={handleChange}
                placeholder="z.B. Lagerbereich A"
              />
            </div>

            <div className="form-group">
              <label htmlFor="plannedDate">Geplantes Datum</label>
              <input
                id="plannedDate"
                type="datetime-local"
                name="plannedDate"
                value={formData.plannedDate}
                onChange={handleChange}
              />
            </div>
          </div>
        </section>

        <div className="form-actions">
          <button
            type="submit"
            disabled={isSubmitting || !formData.title || !formData.checklistId}
            className="btn-primary btn-lg"
          >
            {isSubmitting ? "Wird erstellt..." : "Inspektion erstellen"}
          </button>
          <button
            type="button"
            onClick={() => navigate("/inspections")}
            className="btn-secondary btn-lg"
          >
            Abbrechen
          </button>
        </div>
      </form>
    </div>
  );
}
