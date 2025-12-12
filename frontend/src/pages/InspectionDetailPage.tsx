import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api/httpClient";
import InspectionReportModal from "../components/InspectionReportModal";
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
  const [showReport, setShowReport] = useState(false);

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

  const getStatusLabel = () => {
    switch (inspection?.status) {
      case "PLANNED":
        return "Geplant";
      case "IN_PROGRESS":
        return "In Bearbeitung";
      case "COMPLETED":
        return "Abgeschlossen";
      default:
        return inspection?.status;
    }
  };

  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case "PASSED":
        return "badge-passed";
      case "FAILED":
        return "badge-failed";
      case "NOT_APPLICABLE":
        return "badge-na";
      default:
        return "";
    }
  };

  const getStepStatusLabel = (status: string) => {
    switch (status) {
      case "PASSED":
        return "Erfüllt";
      case "FAILED":
        return "Nicht erfüllt";
      case "NOT_APPLICABLE":
        return "N.A.";
      default:
        return status;
    }
  };

  const getStatusActions = () => {
    switch (inspection?.status) {
      case "PLANNED":
        return [
          { label: "Beginnen", status: "IN_PROGRESS", variant: "primary" },
          { label: "Abbrechen", status: "PLANNED", variant: "secondary" },
        ];
      case "IN_PROGRESS":
        return [
          { label: "Abschließen", status: "COMPLETED", variant: "success" },
          { label: "Zurücksetzen", status: "PLANNED", variant: "secondary" },
        ];
      case "COMPLETED":
        return [
          { label: "Wieder öffnen", status: "IN_PROGRESS", variant: "primary" },
        ];
      default:
        return [];
    }
  };

  const isInProgress = inspection?.status === "IN_PROGRESS";

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
      alert("Fehler beim Aktualisieren des Status");
    } finally {
      setIsUpdating(false);
    }
  };

  const updateStepComment = async (stepId: number) => {
    setSavingCommentStepId(stepId);
    try {
      const comment = editingComments.get(stepId) || "";
      await api.patch(`/inspection-steps/${stepId}/comment`, comment, {
        headers: {
          "Content-Type": "text/plain",
        },
      });

      setSteps(
        steps.map((s) => (s.id === stepId ? { ...s, comment: comment } : s))
      );
      alert("Kommentar erfolgreich gespeichert!");
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

  const statusActions = getStatusActions();

  return (
    <>
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

            {statusActions.length > 0 && (
              <div className="status-row">
                <span
                  className={`badge badge-${inspection.status.toLowerCase()}`}
                >
                  {getStatusLabel()}
                </span>
                <div className="status-actions">
                  {statusActions.map((action) => (
                    <button
                      key={action.status}
                      onClick={() => updateInspectionStatus(action.status)}
                      disabled={isUpdating}
                      className={`btn-${action.variant} btn-sm`}
                    >
                      {action.label}
                    </button>
                  ))}
                </div>
              </div>
            )}
          </div>

          {inspection.status === "COMPLETED" && (
            <button
              onClick={() => setShowReport(true)}
              className="btn-primary btn-lg"
            >
              📄 Bericht anzeigen
            </button>
          )}
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

                    {/* Status Badge/Dropdown basierend auf Inspection Status */}
                    {isInProgress ? (
                      <select
                        value={step.status}
                        onChange={(e) =>
                          updateStepStatus(step.id, e.target.value)
                        }
                        disabled={isUpdating}
                        className={`status-dropdown status-${step.status.toLowerCase()}`}
                      >
                        <option value="PASSED">Erfüllt</option>
                        <option value="FAILED">Nicht erfüllt</option>
                        <option value="NOT_APPLICABLE">N.A.</option>
                      </select>
                    ) : (
                      <span
                        className={`status-badge ${getStatusBadgeClass(
                          step.status
                        )}`}
                      >
                        {getStepStatusLabel(step.status)}
                      </span>
                    )}
                  </div>

                  <div className="step-content">
                    {/* Kommentar Sektion */}
                    <div className="comment-section">
                      <label htmlFor={`comment-${step.id}`}>Kommentar:</label>

                      {isInProgress ? (
                        <>
                          <textarea
                            id={`comment-${step.id}`}
                            value={editingComments.get(step.id) || ""}
                            onChange={(e) => {
                              setEditingComments(
                                new Map(editingComments).set(
                                  step.id,
                                  e.target.value
                                )
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
                        </>
                      ) : (
                        <div className="comment-readonly">
                          {step.comment ? (
                            <p>{step.comment}</p>
                          ) : (
                            <p className="text-muted">
                              Kein Kommentar vorhanden
                            </p>
                          )}
                        </div>
                      )}
                    </div>

                    {/* Foto Sektion */}
                    <div className="photo-section">
                      <label>Foto:</label>

                      {/* Foto Upload nur in In Bearbeitung */}
                      {isInProgress && (
                        <div className="photo-upload">
                          <label>Foto hochladen:</label>
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
                            <p className="uploading">
                              Foto wird hochgeladen...
                            </p>
                          )}
                        </div>
                      )}

                      {/* Foto Anzeige - immer sichtbar wenn vorhanden */}
                      {step.photoPath && (
                        <div className="photo-display">
                          <img
                            src={`${BACKEND_URL}/api/files/${step.photoPath}`}
                            alt="Step photo"
                          />
                        </div>
                      )}

                      {!step.photoPath && !isInProgress && (
                        <p className="text-muted">Kein Foto vorhanden</p>
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

      {/* Report Modal */}
      <InspectionReportModal
        inspection={inspection}
        steps={steps}
        isOpen={showReport}
        onClose={() => setShowReport(false)}
      />
    </>
  );
}
