import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api/httpClient";

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

  if (loading) return <p>Lädt Checkliste...</p>;
  if (error) return <p>{error}</p>;
  if (!checklist) return <p>Keine Daten gefunden.</p>;

  return (
    <div>
      <h1>{checklist.name}</h1>
      <p>
        <strong>Standort:</strong> {checklist.plantName}
      </p>
      {checklist.recommendations && (
        <p>
          <strong>Empfehlungen:</strong>
          <br />
          {checklist.recommendations}
        </p>
      )}

      <hr />

      <h2>Prüfschritte ({steps.length})</h2>
      {steps.length === 0 ? (
        <p>Für diese Checkliste sind noch keine Schritte vorhanden.</p>
      ) : (
        <ol>
          {steps.map((step) => (
            <li key={step.id} style={{ marginBottom: "1rem" }}>
              <strong>{step.description}</strong>
              {step.requirement && (
                <p
                  style={{
                    margin: "0.25rem 0",
                    fontSize: "0.9rem",
                    color: "#666",
                  }}
                >
                  Anforderung: {step.requirement}
                </p>
              )}
            </li>
          ))}
        </ol>
      )}

      <hr />
      <button
        onClick={() => navigate(-1)}
        style={{
          padding: "0.5rem 1rem",
          backgroundColor: "#6c757d",
          color: "white",
          border: "none",
          borderRadius: "4px",
          cursor: "pointer",
        }}
      >
        Zurück
      </button>
    </div>
  );
}
