import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api/httpClient";
import "./ChecklistDetailPage.css";

interface ChecklistStep {
  id: number;
  description: string;
  requirement: string;
  orderIndex: number;
}

interface Checklist {
  id: number;
  name: string;
  plantName: string;
  recommendations: string;
}

export default function ChecklistDetailPage() {
  const { checklistId } = useParams();
  const navigate = useNavigate();
  const [checklist, setChecklist] = useState<Checklist | null>(null);
  const [steps, setSteps] = useState<ChecklistStep[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      if (!checklistId) {
        setError("Keine Checklist-ID vorhanden");
        setLoading(false);
        return;
      }

      try {
        const [checklistRes, stepsRes] = await Promise.all([
          api.get(`/checklists/${checklistId}`),
          api.get(`/checklists/${checklistId}/steps`),
        ]);

        setChecklist(checklistRes.data);
        const sortedSteps = stepsRes.data.sort(
          (a: ChecklistStep, b: ChecklistStep) => a.orderIndex - b.orderIndex
        );
        setSteps(sortedSteps);
      } catch (err) {
        console.error("Error loading checklist:", err);
        setError("Fehler beim Laden der Checkliste");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [checklistId]);

  if (loading) return <p className="text-muted">Lädt Checkliste...</p>;
  if (error) return <div className="alert alert-danger">{error}</div>;
  if (!checklist) return <p className="text-muted">Keine Daten gefunden.</p>;

  return (
    <div className="checklist-detail">
      <div className="detail-header">
        <h1>{checklist.name}</h1>
        <button
          onClick={() => navigate(-1)}
          className="btn-back"
          title="Zurück zur vorherigen Seite"
        >
          ←
        </button>
      </div>

      <section className="detail-section">
        <div className="info-card">
          <div className="info-row">
            <span className="info-label">Standort/Anlage:</span>
            <span className="info-value">{checklist.plantName}</span>
          </div>
          {checklist.recommendations && (
            <div className="info-row">
              <span className="info-label">Empfehlungen:</span>
              <p className="info-text">{checklist.recommendations}</p>
            </div>
          )}
        </div>
      </section>

      <section className="detail-section">
        <h2>Prüfschritte ({steps.length})</h2>
        {steps.length === 0 ? (
          <p className="text-muted">
            Für diese Checkliste sind noch keine Schritte vorhanden.
          </p>
        ) : (
          <div className="steps-list">
            {steps.map((step, index) => (
              <div key={step.id} className="step-card card">
                <div className="step-number">{index + 1}</div>
                <div className="step-content">
                  <h3>{step.description}</h3>
                  {step.requirement && (
                    <div className="requirement">
                      <span className="req-label">Anforderung:</span>
                      <p>{step.requirement}</p>
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
