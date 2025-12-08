import "./InspectionReportModal.css";

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

interface InspectionReportModalProps {
  inspection: Inspection | null;
  steps: InspectionStep[];
  isOpen: boolean;
  onClose: () => void;
}

const BACKEND_URL = "http://localhost:8080";

export default function InspectionReportModal({
  inspection,
  steps,
  isOpen,
  onClose,
}: InspectionReportModalProps) {
  if (!isOpen || !inspection) return null;

  const getStatusLabel = (status: string) => {
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

  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case "PASSED":
        return "status-badge-passed";
      case "FAILED":
        return "status-badge-failed";
      case "NOT_APPLICABLE":
        return "status-badge-na";
      default:
        return "";
    }
  };

  const passedCount = steps.filter((s) => s.status === "PASSED").length;
  const failedCount = steps.filter((s) => s.status === "FAILED").length;
  const naCount = steps.filter((s) => s.status === "NOT_APPLICABLE").length;
  const totalCount = steps.length;

  const handlePrint = () => {
    // Get the report content
    const reportElement = document.getElementById("inspection-report");
    if (!reportElement) return;

    // Build the complete HTML
    const htmlContent = `
    <!DOCTYPE html>
    <html>
      <head>
        <meta charset="UTF-8">
        <title>Inspektionsbericht</title>
        <style>
          * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
          }
          
          body {
            font-family: Arial, sans-serif;
            font-size: 11pt;
            line-height: 1.4;
            color: #000;
            padding: 1cm;
          }
          
          @page {
            margin: 1cm;
            size: A4;
          }
          
          /* Remove default print headers and footers */
          @page {
            @bottom-left {
              content: "";
            }
            @bottom-right {
              content: "";
            }
            @top-left {
              content: "";
            }
            @top-right {
              content: "";
            }
          }
          
          h2 {
            font-size: 18pt;
            margin-bottom: 0.8cm;
            border-bottom: 2px solid #000;
            padding-bottom: 0.3cm;
          }
          
          h3 {
            font-size: 12pt;
            margin: 1cm 0 0.5cm 0;
            border-bottom: 2px solid #4a7ba7;
            padding-bottom: 0.3cm;
            color: #1e3a5f;
          }
          
          .report-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 0.5cm;
            margin-bottom: 0.8cm;
          }
          
          .report-item {
            break-inside: avoid;
          }
          
          .report-label {
            font-weight: bold;
            font-size: 9pt;
            margin-bottom: 2pt;
            color: #1e3a5f;
          }
          
          .report-value {
            font-size: 10pt;
            color: #333;
          }
          
          .report-summary {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            gap: 0.4cm;
            margin-bottom: 0.8cm;
          }
          
          .summary-item {
            border: 2px solid #4a7ba7;
            padding: 0.4cm;
            text-align: center;
            break-inside: avoid;
            background-color: #f0f5fa;
          }
          
          .summary-total {
            border-color: #4a7ba7;
            background-color: #f0f5fa;
          }
          
          .summary-passed {
            border-color: #27ae60;
            background-color: #f0fdf4;
          }
          
          .summary-failed {
            border-color: #e74c3c;
            background-color: #fef2f2;
          }
          
          .summary-na {
            border-color: #95a5a6;
            background-color: #f9fafb;
          }
          
          .summary-number {
            font-size: 14pt;
            font-weight: bold;
            margin-bottom: 2pt;
            color: #1e3a5f;
          }
          
          .summary-label {
            font-size: 8pt;
            font-weight: bold;
            color: #333;
          }
          
          .report-percentage {
            display: flex;
            justify-content: space-between;
            padding: 0.4cm;
            background: #e8f0f8;
            border: 2px solid #4a7ba7;
            margin-bottom: 0.8cm;
            font-weight: bold;
            color: #1e3a5f;
            break-inside: avoid;
          }
          
          .report-steps {
            margin-top: 0.8cm;
          }
          
          .report-step {
            border-left: 4px solid #4a7ba7;
            padding: 0.5cm;
            margin-bottom: 0.6cm;
            break-inside: avoid;
            page-break-inside: avoid;
            background-color: #fafbfc;
            border: 1px solid #e0e7ff;
            border-left: 4px solid #4a7ba7;
          }
          
          .report-step-header {
            margin-bottom: 0.3cm;
            font-size: 10pt;
          }
          
          .step-number {
            font-weight: bold;
            margin-right: 3pt;
            color: #1e3a5f;
          }
          
          .step-description {
            margin-right: 5pt;
            color: #333;
          }
          
          .step-status {
            display: inline-block;
            border: 1px solid #000;
            padding: 3pt 6pt;
            font-size: 8pt;
            font-weight: bold;
            border-radius: 3px;
          }
          
          .status-badge-passed {
            background-color: #d1fae5;
            color: #065f46;
            border-color: #10b981;
          }
          
          .status-badge-failed {
            background-color: #fee2e2;
            color: #7f1d1d;
            border-color: #ef4444;
          }
          
          .status-badge-na {
            background-color: #f3f4f6;
            color: #374151;
            border-color: #9ca3af;
          }
          
          .report-step-comment {
            margin-top: 0.3cm;
            padding-top: 0.3cm;
            border-top: 1px solid #e0e7ff;
            font-size: 9pt;
          }
          
          .comment-label {
            font-weight: bold;
            margin-bottom: 2pt;
            display: block;
            color: #1e3a5f;
          }
          
          .report-step-comment p {
            margin: 0;
            color: #555;
          }
          
          .report-step-photo {
            margin-top: 0.3cm;
            padding-top: 0.3cm;
            border-top: 1px solid #e0e7ff;
          }
          
          .photo-label {
            font-weight: bold;
            font-size: 9pt;
            margin-bottom: 3pt;
            display: block;
            color: #1e3a5f;
          }
          
          .report-step-photo img {
            max-width: 100%;
            height: auto;
            max-height: 10cm;
            border: 1px solid #d0d5e0;
            display: block;
          }
          
          .report-general-comment {
            padding: 0.5cm;
            border-left: 4px solid #4a7ba7;
            background: #f0f5fa;
            break-inside: avoid;
            page-break-inside: avoid;
            font-size: 10pt;
            margin: 0.8cm 0;
            color: #1e3a5f;
            border: 1px solid #d0e1f0;
            border-left: 4px solid #4a7ba7;
          }
          
          .report-footer {
            text-align: center;
            font-size: 8pt;
            margin-top: 1cm;
            padding-top: 0.5cm;
            break-inside: avoid;
            page-break-inside: avoid;
            color: #666;
          }
          
          section {
            break-inside: avoid;
            page-break-inside: avoid;
          }
        </style>
      </head>
      <body>
        <h2>Inspektionsbericht</h2>
        ${reportElement.innerHTML}
      </body>
    </html>
  `;

    // Create and append iframe
    const iframe = document.createElement("iframe");
    iframe.style.display = "none";
    document.body.appendChild(iframe);

    // Write content to iframe
    const iframeDoc = iframe.contentDocument || iframe.contentWindow?.document;
    if (!iframeDoc) return;

    iframeDoc.open();
    iframeDoc.write(htmlContent);
    iframeDoc.close();

    // Print after content is loaded
    iframe.onload = () => {
      iframe.contentWindow?.print();
      // Clean up after printing
      setTimeout(() => {
        document.body.removeChild(iframe);
      }, 100);
    };
  };

  const handleClose = () => {
    onClose();
  };

  return (
    <>
      {/* Overlay */}
      <div className="modal-overlay" onClick={handleClose} />

      {/* Modal */}
      <div className="modal-container">
        <div className="modal-header">
          <h2>Inspektionsbericht</h2>
          <button
            className="modal-close"
            onClick={handleClose}
            title="Schließen"
          >
            ✕
          </button>
        </div>

        {/* Report Content */}
        <div className="modal-content" id="inspection-report">
          {/* Report Header */}
          <section className="report-section">
            <h3>Inspektionsdetails</h3>
            <div className="report-grid">
              <div className="report-item">
                <span className="report-label">Titel:</span>
                <span className="report-value">{inspection.title}</span>
              </div>
              <div className="report-item">
                <span className="report-label">Anlage:</span>
                <span className="report-value">{inspection.plantName}</span>
              </div>
              <div className="report-item">
                <span className="report-label">Status:</span>
                <span className="report-value">Abgeschlossen</span>
              </div>
              <div className="report-item">
                <span className="report-label">Datum:</span>
                <span className="report-value">
                  {new Date().toLocaleDateString("de-DE")}
                </span>
              </div>
            </div>
          </section>

          {/* Summary Statistics */}
          <section className="report-section">
            <h3>Zusammenfassung</h3>
            <div className="report-summary">
              <div className="summary-item summary-total">
                <span className="summary-number">{totalCount}</span>
                <span className="summary-label">Gesamtpunkte</span>
              </div>
              <div className="summary-item summary-passed">
                <span className="summary-number">{passedCount}</span>
                <span className="summary-label">Erfüllt</span>
              </div>
              <div className="summary-item summary-failed">
                <span className="summary-number">{failedCount}</span>
                <span className="summary-label">Nicht erfüllt</span>
              </div>
              <div className="summary-item summary-na">
                <span className="summary-number">{naCount}</span>
                <span className="summary-label">N.A.</span>
              </div>
            </div>
            {totalCount > 0 && (
              <div className="report-percentage">
                <span>Erfüllungsquote:</span>
                <strong>
                  {Math.round(((passedCount + naCount) / totalCount) * 100)}%
                </strong>
              </div>
            )}
          </section>

          {/* Steps/Prüfpunkte */}
          <section className="report-section">
            <h3>Prüfpunkte ({totalCount})</h3>
            <div className="report-steps">
              {steps.map((step, index) => (
                <div key={step.id} className="report-step">
                  <div className="report-step-header">
                    <span className="step-number">{index + 1}.</span>
                    <span className="step-description">
                      {step.checklistStep?.description}
                    </span>
                    <span
                      className={`step-status ${getStatusBadgeClass(
                        step.status
                      )}`}
                    >
                      {getStatusLabel(step.status)}
                    </span>
                  </div>

                  {step.comment && (
                    <div className="report-step-comment">
                      <span className="comment-label">Kommentar:</span>
                      <p>{step.comment}</p>
                    </div>
                  )}

                  {step.photoPath && (
                    <div className="report-step-photo">
                      <span className="photo-label">Foto:</span>
                      <img
                        src={`${BACKEND_URL}/api/files/${step.photoPath}`}
                        alt="Step foto"
                      />
                    </div>
                  )}
                </div>
              ))}
            </div>
          </section>

          {/* General Comment */}
          {inspection.generalComment && (
            <section className="report-section">
              <h3>Allgemeine Bemerkungen</h3>
              <p className="report-general-comment">
                {inspection.generalComment}
              </p>
            </section>
          )}

          {/* Footer */}
          <section className="report-section report-footer">
            <p>Bericht erstellt am: {new Date().toLocaleString("de-DE")}</p>
          </section>
        </div>

        {/* Modal Footer with Actions */}
        <div className="modal-footer">
          <button onClick={handlePrint} className="btn-primary">
            Drucken / PDF speichern
          </button>
          <button onClick={handleClose} className="btn-secondary">
            Schließen
          </button>
        </div>
      </div>
    </>
  );
}
