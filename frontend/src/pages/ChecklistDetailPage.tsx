import { useParams } from "react-router-dom";

export default function ChecklistDetailPage() {
  const { checklistId } = useParams();

  return (
    <div>
      <h1>Checklist Detail</h1>
      <p>Checklist ID: {checklistId}</p>
      <p>
        Hier kommen später Infos zur Checklist und die zugehörigen Inspections
        hin.
      </p>
    </div>
  );
}
