import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  getAllChecklists,
  type Checklist,
} from "../services/api/checklistService";

export default function ChecklistListPage() {
  const [checklists, setChecklists] = useState<Checklist[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getAllChecklists()
      .then((data) => setChecklists(data))
      .catch((err) => {
        console.error(err);
        setError("Fehler beim Laden der Checklists");
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Lade Checklists...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>Checklists</h1>
      <ul>
        {checklists.map((c) => (
          <li key={c.id}>
            <Link to={`/checklists/${c.id}`}>
              <strong>{c.name}</strong> – {c.plantName}
            </Link>
            <br />
            <small>{c.recommendations}</small>
          </li>
        ))}
      </ul>
    </div>
  );
}
