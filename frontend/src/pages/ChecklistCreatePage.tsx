import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api/httpClient";

interface ChecklistStep {
  description: string;
  requirement: string;
  orderIndex: number;
}

interface ChecklistFormData {
  name: string;
  plantName: string;
  recommendations: string;
  steps: ChecklistStep[];
}

export default function ChecklistCreatePage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<ChecklistFormData>({
    name: "",
    plantName: "",
    recommendations: "",
    steps: [],
  });
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const addStep = () => {
    setFormData((prev) => ({
      ...prev,
      steps: [
        ...prev.steps,
        {
          description: "",
          requirement: "",
          orderIndex: prev.steps.length,
        },
      ],
    }));
  };

  const removeStep = (index: number) => {
    setFormData((prev) => ({
      ...prev,
      steps: prev.steps.filter((_, i) => i !== index),
    }));
  };

  const updateStep = (index: number, field: string, value: string) => {
    setFormData((prev) => ({
      ...prev,
      steps: prev.steps.map((step, i) =>
        i === index ? { ...step, [field]: value } : step
      ),
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);

    try {
      // 1. Checklist erstellen
      const checklistRes = await api.post("/checklists", {
        name: formData.name,
        plantName: formData.plantName,
        recommendations: formData.recommendations,
        steps: [],
      });

      const checklistId = checklistRes.data.id;

      // 2. Steps hinzufügen (wenn vorhanden)
      if (formData.steps.length > 0) {
        for (const step of formData.steps) {
          await api.post(`/checklists/${checklistId}/steps`, step);
        }
      }

      // Erfolg - zur Detail-Seite navigieren
      navigate(`/checklists/${checklistId}`);
    } catch (err: any) {
      console.error("Error creating checklist:", err);
      setError(
        err.response?.data?.message || "Fehler beim Erstellen der Checkliste"
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div style={{ maxWidth: "800px" }}>
      <h1>Neue Checkliste erstellen</h1>

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
        {/* Basis-Informationen */}
        <fieldset
          style={{
            marginBottom: "2rem",
            padding: "1rem",
            border: "1px solid #ddd",
            borderRadius: "4px",
          }}
        >
          <legend style={{ fontWeight: "bold" }}>Basis-Informationen</legend>

          <div style={{ marginBottom: "1rem" }}>
            <label htmlFor="name">Name der Checkliste:</label>
            <br />
            <input
              id="name"
              type="text"
              name="name"
              value={formData.name}
              onChange={handleInputChange}
              style={{ width: "100%", padding: "0.5rem", marginTop: "0.25rem" }}
              required
            />
          </div>

          <div style={{ marginBottom: "1rem" }}>
            <label htmlFor="plantName">Standort/Anlage:</label>
            <br />
            <input
              id="plantName"
              type="text"
              name="plantName"
              value={formData.plantName}
              onChange={handleInputChange}
              style={{ width: "100%", padding: "0.5rem", marginTop: "0.25rem" }}
            />
          </div>

          <div style={{ marginBottom: "1rem" }}>
            <label htmlFor="recommendations">Empfehlungen:</label>
            <br />
            <textarea
              id="recommendations"
              name="recommendations"
              value={formData.recommendations}
              onChange={handleInputChange}
              style={{
                width: "100%",
                padding: "0.5rem",
                marginTop: "0.25rem",
                minHeight: "100px",
              }}
            />
          </div>
        </fieldset>

        {/* Prüfschritte */}
        <fieldset
          style={{
            marginBottom: "2rem",
            padding: "1rem",
            border: "1px solid #ddd",
            borderRadius: "4px",
          }}
        >
          <legend style={{ fontWeight: "bold" }}>Prüfschritte</legend>

          {formData.steps.length === 0 ? (
            <p style={{ color: "#999" }}>Keine Schritte hinzugefügt</p>
          ) : (
            <div>
              {formData.steps.map((step, index) => (
                <div
                  key={index}
                  style={{
                    padding: "1rem",
                    marginBottom: "1rem",
                    backgroundColor: "#f9f9f9",
                    border: "1px solid #eee",
                    borderRadius: "4px",
                  }}
                >
                  <div style={{ marginBottom: "0.5rem", fontWeight: "bold" }}>
                    Schritt {index + 1}
                  </div>

                  <div style={{ marginBottom: "0.5rem" }}>
                    <label>Beschreibung:</label>
                    <br />
                    <input
                      type="text"
                      value={step.description}
                      onChange={(e) =>
                        updateStep(index, "description", e.target.value)
                      }
                      style={{
                        width: "100%",
                        padding: "0.5rem",
                        marginTop: "0.25rem",
                      }}
                      required
                    />
                  </div>

                  <div style={{ marginBottom: "0.5rem" }}>
                    <label>Anforderung:</label>
                    <br />
                    <textarea
                      value={step.requirement}
                      onChange={(e) =>
                        updateStep(index, "requirement", e.target.value)
                      }
                      style={{
                        width: "100%",
                        padding: "0.5rem",
                        marginTop: "0.25rem",
                        minHeight: "60px",
                      }}
                    />
                  </div>

                  <button
                    type="button"
                    onClick={() => removeStep(index)}
                    style={{
                      padding: "0.25rem 0.75rem",
                      backgroundColor: "#dc3545",
                      color: "white",
                      border: "none",
                      borderRadius: "4px",
                      cursor: "pointer",
                      fontSize: "0.9rem",
                    }}
                  >
                    Schritt entfernen
                  </button>
                </div>
              ))}
            </div>
          )}

          <button
            type="button"
            onClick={addStep}
            style={{
              padding: "0.5rem 1rem",
              backgroundColor: "#17a2b8",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
            }}
          >
            + Schritt hinzufügen
          </button>
        </fieldset>

        {/* Submit Buttons */}
        <div style={{ display: "flex", gap: "1rem" }}>
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
            {isSubmitting ? "Wird erstellt..." : "Checkliste erstellen"}
          </button>

          <button
            type="button"
            onClick={() => navigate("/checklists")}
            style={{
              padding: "0.75rem 1.5rem",
              backgroundColor: "#6c757d",
              color: "white",
              border: "none",
              borderRadius: "4px",
              cursor: "pointer",
            }}
          >
            Abbrechen
          </button>
        </div>
      </form>
    </div>
  );
}
