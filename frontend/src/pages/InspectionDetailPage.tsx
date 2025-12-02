import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api/httpClient";

interface InspectionStep {
  id: number;
  status: "PASSED" | "FAILED" | "NOT_APPLICABLE";
  comment: string;
  checklistStep: { id: number; description: string };
}

interface Inspection {
  id: number;
  title: string;
  status: "PLANNED" | "IN_PROGRESS" | "COMPLETED";
  plantName: string;
  generalComment: string;
}

export default function InspectionDetailPage() {
  const { inspectionId } = useParams();
  const navigate = useNavigate();
  const [inspection, setInspection] = useState<Inspection | null>(null);
  const [steps, setSteps] = useState<InspectionStep[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isUpdating, setIsUpdating] = useState(false);
  const [savingCommentStepId, setSavingCommentStepId] = useState<number | null>(
    null
  );
  const [editingComments, setEditingComments] = useState<Map<number, string>>(
    new Map()
  );

  useEffect(() => {
    const fetchData = async () => {
      if (!inspectionId) {
        setError("Keine Inspection-ID vorhanden");
        setLoading(false);
        return;
      }

      try {
        // Inspection laden
        const inspectionRes = await api.get(`/inspections/${inspectionId}`);
        setInspection(inspectionRes.data);

        // Steps versuchen zu laden - mit verschiedenen Endpoints
        let stepsData: InspectionStep[] = [];
        try {
          const stepsRes = await api.get(
            `/inspection-steps/inspection/${inspectionId}`
          );
          stepsData = stepsRes.data;
        } catch (stepErr) {
          console.warn("First steps endpoint failed, trying alternative...");
          try {
            const stepsRes = await api.get(
              `/inspections/${inspectionId}/steps`
            );
            stepsData = stepsRes.data;
          } catch (stepErr2) {
            console.warn(
              "Both steps endpoints failed, continuing without steps"
            );
          }
        }

        setSteps(stepsData);
        // Initialisiere editingComments mit bestehenden Kommentaren
        const commentsMap = new Map<number, string>();
        stepsData.forEach((step) => {
          commentsMap.set(step.id, step.comment || "");
        });
        setEditingComments(commentsMap);
      } catch (err) {
        console.error("Error details:", err);
        setError("Fehler beim Laden der Inspection");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [inspectionId]);

  const updateStepStatus = async (stepId: number, newStatus: string) => {
    setIsUpdating(true);
    try {
      await api.patch(`/inspection-steps/${stepId}/status`, newStatus, {
        headers: {
          "Content-Type": "text/plain",
        },
      });
      setSteps(
        steps.map((s) =>
          s.id === stepId ? { ...s, status: newStatus as any } : s
        )
      );
    } catch (err: any) {
      console.error(
        "Error updating step status:",
        err.response?.data || err.message
      );
      alert("Fehler beim Aktualisieren des Steps");
    } finally {
      setIsUpdating(false);
    }
  };

  const updateStepComment = async (stepId: number) => {
    setSavingCommentStepId(stepId);
    try {
      const newComment = editingComments.get(stepId) || "";
      await api.patch(`/inspection-steps/${stepId}/comment`, newComment, {
        headers: {
          "Content-Type": "text/plain",
        },
      });
      setSteps(
        steps.map((s) => (s.id === stepId ? { ...s, comment: newComment } : s))
      );
    } catch (err: any) {
      console.error(
        "Error updating step comment:",
        err.response?.data || err.message
      );
      alert("Fehler beim Aktualisieren des Kommentars");
    } finally {
      setSavingCommentStepId(null);
    }
  };

  const updateInspectionStatus = async (newStatus: string) => {
    setIsUpdating(true);
    try {
      await api.patch(`/inspections/${inspectionId}/status`, newStatus, {
        headers: {
          "Content-Type": "text/plain",
        },
      });
      setInspection(
        inspection ? { ...inspection, status: newStatus as any } : null
      );
    } catch (err: any) {
      console.error(
        "Error updating inspection status:",
        err.response?.data || err.message
      );
      alert("Fehler beim Aktualisieren des Status");
    } finally {
      setIsUpdating(false);
    }
  };

  if (loading) return <p>Lädt Inspection...</p>;
  if (error) return <p>{error}</p>;
  if (!inspection) return <p>Keine Daten gefunden</p>;

  return (
    <div>
      <h1>{inspection.title}</h1>
      <p>
        <strong>Anlage:</strong> {inspection.plantName}
      </p>
      <p>
        <strong>Status:</strong>{" "}
        <select
          value={inspection.status}
          onChange={(e) => updateInspectionStatus(e.target.value)}
          disabled={isUpdating}
        >
          <option value="PLANNED">Geplant</option>
          <option value="IN_PROGRESS">In Bearbeitung</option>
          <option value="COMPLETED">Abgeschlossen</option>
        </select>
      </p>

      <hr />

      <h2>Prüfschritte</h2>
      {steps.length === 0 ? (
        <p>Keine Steps vorhanden</p>
      ) : (
        <div>
          {steps.map((step) => (
            <div
              key={step.id}
              style={{
                padding: "1rem",
                marginBottom: "1rem",
                border: "1px solid #ddd",
                borderRadius: "4px",
              }}
            >
              <p>
                <strong>{step.checklistStep?.description}</strong>
              </p>
              <p>
                <strong>Status:</strong>{" "}
                <select
                  value={step.status}
                  onChange={(e) => updateStepStatus(step.id, e.target.value)}
                  disabled={isUpdating}
                >
                  <option value="PASSED">Erfüllt</option>
                  <option value="FAILED">Nicht erfüllt</option>
                  <option value="NOT_APPLICABLE">N.A.</option>
                </select>
              </p>
              <p>
                <strong>Kommentar:</strong>
                <br />
                <textarea
                  value={editingComments.get(step.id) || ""}
                  onChange={(e) => {
                    setEditingComments(
                      new Map(editingComments).set(step.id, e.target.value)
                    );
                  }}
                  style={{
                    width: "100%",
                    padding: "0.5rem",
                    marginTop: "0.25rem",
                    minHeight: "80px",
                  }}
                />
                <button
                  onClick={() => updateStepComment(step.id)}
                  disabled={savingCommentStepId === step.id}
                  style={{
                    marginTop: "0.5rem",
                    padding: "0.5rem 1rem",
                    backgroundColor:
                      savingCommentStepId === step.id ? "#ccc" : "#007bff",
                    color: "white",
                    border: "none",
                    borderRadius: "4px",
                    cursor:
                      savingCommentStepId === step.id
                        ? "not-allowed"
                        : "pointer",
                  }}
                >
                  {savingCommentStepId === step.id
                    ? "Speichert..."
                    : "Kommentar speichern"}
                </button>
              </p>
            </div>
          ))}
        </div>
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
