import api from "./httpClient";

/**
 * Entspricht der Checklist-Entity im Backend.
 * Felder basieren auf den Tests:
 * - id
 * - name
 * - plantName
 * - recommendations
 */
export interface Checklist {
  id: number;
  name: string;
  plantName: string;
  recommendations: string;
  // steps werden oft separat geladen:
  steps?: ChecklistStep[];
}

/**
 * Entspricht ChecklistStep-Entity im Backend.
 * Felder aus ChecklistStepControllerTest:
 * - description
 * - requirement
 * - orderIndex
 */
export interface ChecklistStep {
  id: number;
  description: string;
  requirement: string;
  orderIndex: number;
  // ggf. später: checklistId oder checklist?: Checklist
}

/**
 * Holt alle Checklists: GET /api/checklists
 */
export async function getAllChecklists(): Promise<Checklist[]> {
  const response = await api.get<Checklist[]>("/checklists");
  return response.data;
}

/**
 * Holt eine einzelne Checklist:
 * GET /api/checklists/{id}
 */
export async function getChecklistById(id: number): Promise<Checklist> {
  const response = await api.get<Checklist>(`/checklists/${id}`);
  return response.data;
}

/**
 * Holt alle Steps zu einer Checklist:
 * GET /api/checklists/{checklistId}/steps
 */
export async function getChecklistSteps(
  checklistId: number
): Promise<ChecklistStep[]> {
  const response = await api.get<ChecklistStep[]>(
    `/checklists/${checklistId}/steps`
  );
  return response.data;
}
