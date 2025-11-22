package de.dhbw.webenginspection.controller;

import de.dhbw.webenginspection.entity.*;
import de.dhbw.webenginspection.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    private InspectionStep createInspectionStepFixture() {
        Checklist checklist = new Checklist();
        checklist.setName("Checklist for InspectionStep test");
        checklist.setPlantName("Checklist plantname for InspectionStep test");
        checklist.setRecommendations("Checklist recommendations for InspectionStep test");
        Checklist savedChecklist = checklistRepository.save(checklist);

        ChecklistStep checklistStep = new ChecklistStep();
        checklistStep.setDescription("Türen prüfen");
        checklistStep.setRequirement("Alle Außentüren abgeschlossen.");
        checklistStep.setOrderIndex(1);
        checklistStep.setChecklist(savedChecklist);
        ChecklistStep savedChecklistStep = checklistStepRepository.save(checklistStep);

        Inspection inspection = new Inspection();
        inspection.setTitle("Test Inspection");
        inspection.setPlantName("Test Plant");
        inspection.setInspectionDate(LocalDateTime.now());
        inspection.setGeneralComment("Created in test");
        inspection.setChecklist(savedChecklist);
        Inspection savedInspection = inspectionRepository.save(inspection);

        InspectionStep inspectionStep = new InspectionStep();
        inspectionStep.setStatus(StepStatus.NOT_APPLICABLE);
        inspectionStep.setComment("Initial");
        inspectionStep.setChecklistStep(savedChecklistStep);
        inspectionStep.setInspection(savedInspection);

        return inspectionStepRepository.save(inspectionStep);
    }

    @Test
    void updateStatus_withValidStatus_returnsUpdatedStep() throws Exception {
        // Arrange
        InspectionStep step = createInspectionStepFixture();

        // Act + Assert
        mockMvc.perform(patch("/api/inspection-steps/{id}/status", step.getId())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("PASSED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(step.getId()))
                .andExpect(jsonPath("$.status").value("PASSED"));
    }

    @Test
    void updateStatus_withInvalidStatus_returnsBadRequest() throws Exception {
        // Arrange
        InspectionStep step = createInspectionStepFixture();

        // Act + Assert
        mockMvc.perform(patch("/api/inspection-steps/{id}/status", step.getId())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("FOO"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStepsForInspection_returnsSteps() throws Exception {
        // Arrange
        InspectionStep step = createInspectionStepFixture();
        Long inspectionId = step.getInspection().getId();

        // Act + Assert
        mockMvc.perform(get("/api/inspections/{inspectionId}/steps", inspectionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].status").value("NOT_APPLICABLE"));
    }
}

