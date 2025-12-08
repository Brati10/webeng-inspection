import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api/httpClient";
import "./InspectionDetailPage.css";

interface InspectionStep {
  id: number;
  status: "PASSED" | "FAILED" | "NOT_APPLICABLE";
  comment: string;
  photoPath?: string;
  checklistStep: { id: number; description: string };
}

interface Inspection {
  id: number;
  title: string;
  status: "PLANNED" | "IN_PROGRESS" | "COMPLETED";
  plantName: string;
  generalComment: string;
}

const BACKEND_URL = "http://localhost:8080";

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
  const [uploadingPhotoStepId, setUploadingPhotoStepId] = useState<
    number | null
  >(null);
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
        const inspectionRes = await api.get(`/inspections/${inspectionId}`);
        setInspection(inspectionRes.data);

        let stepsData: InspectionStep[] = [];
        try {
          const stepsRes = await api.get(`/inspections/${inspectionId}/steps`);
          stepsData = stepsRes.data;
        } catch (stepErr) {
          console.warn("Failed to fetch steps:", stepErr);
        }

        setSteps(stepsData);
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

  const uploadPhoto = async (stepId: number, file: File) => {
    setUploadingPhotoStepId(stepId);
    try {
      const formData = new FormData();
      formData.append("file", file);

      const response = await api.post(
        `/inspection-steps/${stepId}/photo`,
        formData
      );

      setSteps(
        steps.map((s) =>
          s.id === stepId ? { ...s, photoPath: response.data.photoPath } : s
        )
      );
      alert("Foto erfolgreich hochgeladen!");
    } catch (err: any) {
      console.error("Error uploading photo:", err);
      alert(err.response?.data?.message || "Fehler beim Foto-Upload");
    } finally {
      setUploadingPhotoStepId(null);
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

  if (loading) return <p className="text-muted">Lädt Inspection...</p>;
  if (error) return <div className="alert alert-danger">{error}</div>;
  if (!inspection) return <p className="text-muted">Keine Daten gefunden</p>;

  return (
    <div className="inspection-detail">
      <div className="detail-header">
        <h1>{inspection.title}</h1>
        <button
          onClick={() => navigate(-1)}
          className="btn-back"
          title="Zurück"
        >
          ←
        </button>
      </div>

      <section className="detail-section">
        <div className="info-card">
          <div className="info-row">
            <span className="info-label">Anlage:</span>
            <span className="info-value">{inspection.plantName}</span>
          </div>
          <div className="info-row">
            <span className="info-label">Status:</span>
            <select
              value={inspection.status}
              onChange={(e) => updateInspectionStatus(e.target.value)}
              disabled={isUpdating}
              className="status-select"
            >
              <option value="PLANNED">Geplant</option>
              <option value="IN_PROGRESS">In Bearbeitung</option>
              <option value="COMPLETED">Abgeschlossen</option>
            </select>
          </div>
        </div>
      </section>

      <section className="detail-section">
        <h2>Prüfschritte ({steps.length})</h2>
        {steps.length === 0 ? (
          <p className="text-muted">Keine Steps vorhanden</p>
        ) : (
          <div className="steps-list">
            {steps.map((step) => (
              <div key={step.id} className="step-detail-card">
                <div className="step-header">
                  <h3>{step.checklistStep?.description}</h3>
                  <select
                    value={step.status}
                    onChange={(e) => updateStepStatus(step.id, e.target.value)}
                    disabled={isUpdating}
                    className={`status-badge status-${step.status.toLowerCase()}`}
                  >
                    <option value="PASSED">Erfüllt</option>
                    <option value="FAILED">Nicht erfüllt</option>
                    <option value="NOT_APPLICABLE">N.A.</option>
                  </select>
                </div>

                <div className="step-content">
                  <div className="comment-section">
                    <label htmlFor={`comment-${step.id}`}>Kommentar:</label>
                    <textarea
                      id={`comment-${step.id}`}
                      value={editingComments.get(step.id) || ""}
                      onChange={(e) => {
                        setEditingComments(
                          new Map(editingComments).set(step.id, e.target.value)
                        );
                      }}
                      className="comment-textarea"
                      placeholder="Notizen zu diesem Schritt..."
                    />
                    <button
                      onClick={() => updateStepComment(step.id)}
                      disabled={savingCommentStepId === step.id}
                      className="btn-primary btn-sm"
                    >
                      {savingCommentStepId === step.id
                        ? "Speichert..."
                        : "Kommentar speichern"}
                    </button>
                  </div>

                  <div className="photo-section">
                    <label htmlFor={`photo-${step.id}`}>Foto hochladen:</label>
                    <div className="photo-upload">
                      <input
                        id={`photo-${step.id}`}
                        type="file"
                        accept="image/*"
                        onChange={(e) => {
                          const file = e.target.files?.[0];
                          if (file) {
                            uploadPhoto(step.id, file);
                          }
                        }}
                        disabled={uploadingPhotoStepId === step.id}
                      />
                      {uploadingPhotoStepId === step.id && (
                        <p className="uploading">Foto wird hochgeladen...</p>
                      )}
                    </div>

                    {step.photoPath && (
                      <div className="photo-display">
                        <img
                          src={`${BACKEND_URL}/api/files/${step.photoPath}`}
                          alt="Step photo"
                        />
                      </div>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      <div className="action-buttons">
        <button onClick={() => navigate(-1)} className="btn-secondary btn-lg">
          Zurück
        </button>
      </div>
    </div>
  );
}
