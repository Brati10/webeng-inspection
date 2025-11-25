import { useParams } from "react-router-dom";

export default function InspectionDetailPage() {
  const { inspectionId } = useParams();

  return (
    <div>
      <h1>Inspection Detail</h1>
      <p>Inspection ID: {inspectionId}</p>
      <p>Hier kommen später die Steps der Inspection hin.</p>
    </div>
  );
}
