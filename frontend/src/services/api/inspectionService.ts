import api from "./httpClient";
import type { Checklist, ChecklistStep } from "./checklistService";

// Status-Beispiele: laut Tests u. a. "NOT_APPLICABLE"
export type InspectionStepStatus =
  | "PASSED"
  | "FAILED"
  | "NOT_APPLICABLE"
  | string;

export interface Inspection {
  id: number;
  title: string;
  plantName: string;
  generalComment?: string | null;
  status?: string | null;
  startedAt?: string | null;
  finishedAt?: string | null;
  checklist?: Checklist;
}

export interface InspectionStep {
  id: number;
  status: InspectionStepStatus;
  comment?: string | null;
  photoPath?: string | null;
  checklistStep?: ChecklistStep;
}

/**
 * GET /api/inspections/{id}
 */
export async function getInspectionById(id: number): Promise<Inspection> {
  const response = await api.get<Inspection>(`/inspections/${id}`);
  return response.data;
}

/**
 * GET /api/inspections/{inspectionId}/steps
 */
export async function getInspectionSteps(
  inspectionId: number
): Promise<InspectionStep[]> {
  const response = await api.get<InspectionStep[]>(
    `/inspections/${inspectionId}/steps`
  );
  return response.data;
}
