import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import {
  getChecklistById,
  getChecklistSteps,
  type Checklist,
  type ChecklistStep,
} from "../services/api/checklistService";

export default function ChecklistDetailPage() {
  const { checklistId } = useParams();
  const [checklist, setChecklist] = useState<Checklist | null>(null);
  const [steps, setSteps] = useState<ChecklistStep[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Param prüfen
    if (!checklistId) {
      setError("Keine Checklist-ID in der URL.");
      setLoading(false);
      return;
    }

    const id = Number(checklistId);
    if (Number.isNaN(id)) {
      setError("Ungültige Checklist-ID.");
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    Promise.all([getChecklistById(id), getChecklistSteps(id)])
      .then(([checklistData, stepsData]) => {
        setChecklist(checklistData);
        setSteps([...stepsData].sort((a, b) => a.orderIndex - b.orderIndex));
      })
      .catch((err) => {
        console.error(err);
        setError("Fehler beim Laden der Checklist.");
      })
      .finally(() => setLoading(false));
  }, [checklistId]);

  if (loading) return <p>Lade Checklist...</p>;
  if (error) return <p>{error}</p>;
  if (!checklist) return <p>Keine Daten gefunden.</p>;

  return (
    <div>
      <h1>Checklist: {checklist.name}</h1>
      <p>
        <strong>Standort:</strong> {checklist.plantName}
      </p>
      <p>
        <strong>Empfehlungen:</strong>
        <br />
        {checklist.recommendations}
      </p>

      <hr />

      <h2>Steps</h2>
      {steps.length === 0 ? (
        <p>Für diese Checklist sind noch keine Steps vorhanden.</p>
      ) : (
        <ol>
          {steps.map((step) => (
            <li key={step.id}>
              <div>
                <strong>
                  {step.orderIndex}. {step.description}
                </strong>
              </div>
              <div>
                <small>{step.requirement}</small>
              </div>
            </li>
          ))}
        </ol>
      )}

      <hr />
      <p>
        <Link to="/checklists">Zurück zur Übersicht</Link>
      </p>
    </div>
  );
}
