package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.entity.*;
import de.dhbw.webenginspection.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InspectionStepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChecklistRepository checklistRepository;

    @Autowired
    private ChecklistStepRepository checklistStepRepository;

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private InspectionStepRepository inspectionStepRepository;

    @Autowired
    private UserRepository userRepository;

    private Checklist testChecklist;
    private ChecklistStep testChecklistStep;
    private Inspection testInspection;
    private InspectionStep testInspectionStep;
    private User testInspector;

    @BeforeEach
    void setUp() {
        // User erstellen
        testInspector = new User("test_inspector", "Test Inspector", "hashedPassword123", UserRole.INSPECTOR);
        testInspector = userRepository.save(testInspector);

        // Checklist erstellen
        testChecklist = new Checklist();
        testChecklist.setName("Checklist for InspectionStep test");
        testChecklist.setPlantName("Checklist plantname for InspectionStep test");
        testChecklist.setRecommendations("Checklist recommendations for InspectionStep test");
        testChecklist = checklistRepository.save(testChecklist);

        // ChecklistStep erstellen
        testChecklistStep = new ChecklistStep();
        testChecklistStep.setDescription("Türen prüfen");
        testChecklistStep.setRequirement("Alle Außentüren abgeschlossen.");
        testChecklistStep.setOrderIndex(1);
        testChecklistStep.setChecklist(testChecklist);
        testChecklistStep = checklistStepRepository.save(testChecklistStep);

        // Inspection erstellen (mit assignedInspector!)
        testInspection = new Inspection();
        testInspection.setTitle("Test Inspection");
        testInspection.setPlantName("Test Plant");
        testInspection.setPlannedDate(LocalDateTime.now().plusDays(1));
        testInspection.setStatus(InspectionStatus.PLANNED);
        testInspection.setChecklist(testChecklist);
        testInspection.setAssignedInspector(testInspector);
        testInspection = inspectionRepository.save(testInspection);

        // InspectionStep erstellen
        testInspectionStep = new InspectionStep();
        testInspectionStep.setChecklistStep(testChecklistStep);
        testInspectionStep.setInspection(testInspection);
        testInspectionStep.setStatus(StepStatus.NOT_APPLICABLE);
        testInspectionStep.setComment(null);
        testInspectionStep.setPhotoPath(null);
        testInspectionStep = inspectionStepRepository.save(testInspectionStep);
    }

    @Test
    void getStepsForInspection_returnsSteps() throws Exception {
        mockMvc.perform(get("/api/inspections/" + testInspection.getId() + "/steps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(testInspectionStep.getId()))
                .andExpect(jsonPath("$[0].status").value("NOT_APPLICABLE"));
    }

    @Test
    void getStepsForInspectionByStatus_withValidStatus_returnsFilteredSteps() throws Exception {
        mockMvc.perform(get("/api/inspections/" + testInspection.getId() + "/steps/status/NOT_APPLICABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("NOT_APPLICABLE"));
    }

    @Test
    void getStepsForInspectionByStatus_withInvalidStatus_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/inspections/" + testInspection.getId() + "/steps/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStepById_withValidId_returnsOk() throws Exception {
        mockMvc.perform(get("/api/inspection-steps/" + testInspectionStep.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testInspectionStep.getId()))
                .andExpect(jsonPath("$.status").value("NOT_APPLICABLE"));
    }

    @Test
    void getStepById_withInvalidId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/inspection-steps/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_withValidStatus_returnsUpdatedStep() throws Exception {
        String newStatus = "\"CONFORM\"";

        mockMvc.perform(patch("/api/inspection-steps/" + testInspectionStep.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testInspectionStep.getId()))
                .andExpect(jsonPath("$.status").value("CONFORM"));
    }

    @Test
    void updateStatus_withInvalidStatus_returnsBadRequest() throws Exception {
        String invalidStatus = "\"INVALID_STATUS\"";

        mockMvc.perform(patch("/api/inspection-steps/" + testInspectionStep.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidStatus))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateComment_withValidComment_returnsUpdatedStep() throws Exception {
        String newComment = "\"Dies ist ein neuer Kommentar\"";

        mockMvc.perform(patch("/api/inspection-steps/" + testInspectionStep.getId() + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newComment))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment").value("Dies ist ein neuer Kommentar"));
    }

    @Test
    void deleteStep_withValidId_returnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/inspection-steps/" + testInspectionStep.getId()))
                .andExpect(status().isNoContent());

        // Verifizieren, dass der Step gelöscht wurde
        mockMvc.perform(get("/api/inspection-steps/" + testInspectionStep.getId()))
                .andExpect(status().isNotFound());
    }
}