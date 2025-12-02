import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api/httpClient";

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

  if (loading) return <p>Lädt Formulardaten...</p>;

  return (
    <div style={{ maxWidth: "600px" }}>
      <h1>Neue Inspection anlegen</h1>

      {error && (
        <div
          style={{
            padding: "0.5rem",
            marginBottom: "1rem",
            backgroundColor: "#fee",
            color: "#c00",
            borderRadius: "4px",
          }}
        >
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: "1rem" }}>
          <label htmlFor="title">Titel:</label>
          <br />
          <input
            id="title"
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            style={{ width: "100%", padding: "0.5rem", marginTop: "0.25rem" }}
            required
          />
        </div>

        <div style={{ marginBottom: "1rem" }}>
          <label htmlFor="checklistId">Checkliste:</label>
          <br />
          <select
            id="checklistId"
            name="checklistId"
            value={formData.checklistId}
            onChange={handleChange}
            style={{ width: "100%", padding: "0.5rem", marginTop: "0.25rem" }}
            required
          >
            <option value="">-- Wähle eine Checkliste --</option>
            {checklists.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </div>

        <div style={{ marginBottom: "1rem" }}>
          <label htmlFor="assignedInspectorId">
            Verantwortlicher Inspector:
          </label>
          <br />
          <select
            id="assignedInspectorId"
            name="assignedInspectorId"
            value={formData.assignedInspectorId}
            onChange={handleChange}
            style={{ width: "100%", padding: "0.5rem", marginTop: "0.25rem" }}
            required
          >
            <option value="">-- Wähle einen Inspector --</option>
            {users.map((u) => (
              <option key={u.id} value={u.id}>
                {u.displayName}
              </option>
            ))}
          </select>
        </div>

        <div style={{ marginBottom: "1rem" }}>
          <label htmlFor="plantName">Anlage:</label>
          <br />
          <input
            id="plantName"
            type="text"
            name="plantName"
            value={formData.plantName}
            onChange={handleChange}
            style={{ width: "100%", padding: "0.5rem", marginTop: "0.25rem" }}
          />
        </div>

        <div style={{ marginBottom: "1rem" }}>
          <label htmlFor="plannedDate">Geplantes Datum:</label>
          <br />
          <input
            id="plannedDate"
            type="datetime-local"
            name="plannedDate"
            value={formData.plannedDate}
            onChange={handleChange}
            style={{ width: "100%", padding: "0.5rem", marginTop: "0.25rem" }}
          />
        </div>

        <button
          type="submit"
          disabled={isSubmitting}
          style={{
            padding: "0.75rem 1.5rem",
            backgroundColor: "#007bff",
            color: "white",
            border: "none",
            borderRadius: "4px",
            cursor: isSubmitting ? "not-allowed" : "pointer",
            opacity: isSubmitting ? 0.6 : 1,
          }}
        >
          {isSubmitting ? "Wird erstellt..." : "Erstellen"}
        </button>
      </form>
    </div>
  );
}
