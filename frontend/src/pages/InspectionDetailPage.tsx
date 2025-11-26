import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import {
  getInspectionById,
  getInspectionSteps,
  type Inspection,
  type InspectionStep,
} from "../services/api/inspectionService";

export default function InspectionDetailPage() {
  const { inspectionId } = useParams();
  const [inspection, setInspection] = useState<Inspection | null>(null);
  const [steps, setSteps] = useState<InspectionStep[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!inspectionId) {
      setError("Keine Inspection-ID in der URL.");
      setLoading(false);
      return;
    }

    const id = Number(inspectionId);
    if (Number.isNaN(id)) {
      setError("Ungültige Inspection-ID.");
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    Promise.all([getInspectionById(id), getInspectionSteps(id)])
      .then(([inspectionData, stepsData]) => {
        setInspection(inspectionData);
        setSteps(stepsData);
      })
      .catch((err) => {
        console.error(err);
        setError("Fehler beim Laden der Inspection.");
      })
      .finally(() => setLoading(false));
  }, [inspectionId]);

  if (loading) return <p>Lade Inspection...</p>;
  if (error) return <p>{error}</p>;
  if (!inspection) return <p>Keine Daten gefunden.</p>;

  return (
    <div>
      <h1>Inspection: {inspection.title}</h1>

      {inspection.status && (
        <p>
          <strong>Status:</strong> {inspection.status}
        </p>
      )}

      {inspection.startedAt && (
        <p>
          <strong>Beginn:</strong> {inspection.startedAt}
        </p>
      )}

      {inspection.finishedAt && (
        <p>
          <strong>Ende:</strong> {inspection.finishedAt}
        </p>
      )}

      {inspection.generalComment && (
        <p>
          <strong>Allgemeiner Kommentar:</strong>
          <br />
          {inspection.generalComment}
        </p>
      )}

      {inspection.checklist && (
        <p>
          Zugehörige Checklist:{" "}
          <Link to={`/checklists/${inspection.checklist.id}`}>
            {inspection.checklist.name}
          </Link>
        </p>
      )}

      <hr />

      <h2>Steps</h2>
      {steps.length === 0 ? (
        <p>Für diese Inspection sind noch keine Steps vorhanden.</p>
      ) : (
        <ol>
          {steps.map((step) => (
            <li key={step.id}>
              <div>
                <strong>Status:</strong> {step.status}
              </div>
              {step.checklistStep && (
                <div>
                  <strong>Schritt:</strong> {step.checklistStep.description}
                </div>
              )}
              {step.comment && (
                <div>
                  <strong>Kommentar:</strong> {step.comment}
                </div>
              )}
            </li>
          ))}
        </ol>
      )}

      <hr />
      <p>
        <Link to="/checklists">Zurück zur Checklist-Übersicht</Link>
      </p>
    </div>
  );
}
