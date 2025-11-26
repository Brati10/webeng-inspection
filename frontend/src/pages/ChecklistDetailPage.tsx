import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import {
  getChecklistById,
  type Checklist,
} from "../services/api/checklistService";

export default function ChecklistDetailPage() {
  const { checklistId } = useParams();
  const [checklist, setChecklist] = useState<Checklist | null>(null);
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

    getChecklistById(id)
      .then((data) => setChecklist(data))
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

      {/* Hier werden später Inspections / Steps ergänzt */}
      <hr />
      <p>
        <Link to="/checklists">Zurück zur Übersicht</Link>
      </p>
    </div>
  );
}
