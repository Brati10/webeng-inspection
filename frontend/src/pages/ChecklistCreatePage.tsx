import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api/httpClient";
import "./ChecklistCreatePage.css";

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
      const checklistRes = await api.post("/checklists", {
        name: formData.name,
        plantName: formData.plantName,
        recommendations: formData.recommendations,
        steps: [],
      });

      const checklistId = checklistRes.data.id;

      if (formData.steps.length > 0) {
        for (const step of formData.steps) {
          await api.post(`/checklists/${checklistId}/steps`, step);
        }
      }

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
    <div className="checklist-create">
      <div className="detail-header">
        <h1>Neue Checkliste erstellen</h1>
        <button
          onClick={() => navigate("/checklists")}
          className="btn-back"
          title="Abbrechen"
        >
          ✕
        </button>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={handleSubmit} className="create-form">
        {/* ===== SECTION 1: BASIS-INFORMATIONEN ===== */}
        <section className="form-section">
          <h2>Basis-Informationen</h2>
          <div className="section-content">
            <div className="form-group">
              <label htmlFor="name">Name der Checkliste *</label>
              <input
                id="name"
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                placeholder="z.B. Sicherheitsprüfung Lager"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="plantName">Standort/Anlage</label>
              <input
                id="plantName"
                type="text"
                name="plantName"
                value={formData.plantName}
                onChange={handleInputChange}
                placeholder="z.B. Lagerbereich A"
              />
            </div>

            <div className="form-group">
              <label htmlFor="recommendations">Empfehlungen</label>
              <textarea
                id="recommendations"
                name="recommendations"
                value={formData.recommendations}
                onChange={handleInputChange}
                placeholder="Zusätzliche Hinweise und Empfehlungen..."
              />
            </div>
          </div>
        </section>

        {/* ===== SECTION 2: PRÜFSCHRITTE ===== */}
        <section className="form-section">
          <div className="section-header">
            <h2>Prüfschritte</h2>
            <span className="step-count">{formData.steps.length} Schritte</span>
          </div>

          {formData.steps.length === 0 ? (
            <div className="empty-steps">
              <p>Noch keine Prüfschritte hinzugefügt</p>
            </div>
          ) : (
            <div className="steps-container">
              {formData.steps.map((step, index) => (
                <div key={index} className="step-form-card">
                  <div className="step-form-header">
                    <span className="step-number">{index + 1}</span>
                    <button
                      type="button"
                      onClick={() => removeStep(index)}
                      className="btn-remove"
                      title="Schritt entfernen"
                    >
                      ✕
                    </button>
                  </div>

                  <div className="step-form-content">
                    <div className="form-group">
                      <label htmlFor={`description-${index}`}>
                        Beschreibung *
                      </label>
                      <input
                        id={`description-${index}`}
                        type="text"
                        value={step.description}
                        onChange={(e) =>
                          updateStep(index, "description", e.target.value)
                        }
                        placeholder="Beschreibung des Prüfschrittes"
                        required
                      />
                    </div>

                    <div className="form-group">
                      <label htmlFor={`requirement-${index}`}>
                        Anforderung
                      </label>
                      <textarea
                        id={`requirement-${index}`}
                        value={step.requirement}
                        onChange={(e) =>
                          updateStep(index, "requirement", e.target.value)
                        }
                        placeholder="Welche Anforderungen müssen erfüllt sein?"
                      />
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}

          <button type="button" onClick={addStep} className="btn-accent">
            + Schritt hinzufügen
          </button>
        </section>

        {/* ===== FORM ACTIONS ===== */}
        <div className="form-actions">
          <button
            type="submit"
            disabled={isSubmitting || !formData.name}
            className="btn-primary btn-lg"
          >
            {isSubmitting ? "Wird erstellt..." : "Checkliste erstellen"}
          </button>
          <button
            type="button"
            onClick={() => navigate("/checklists")}
            className="btn-secondary btn-lg"
          >
            Abbrechen
          </button>
        </div>
      </form>
    </div>
  );
}
