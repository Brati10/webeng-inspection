package de.dhbw.webenginspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.repository.ChecklistRepository;
import de.dhbw.webenginspection.repository.ChecklistStepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ChecklistStepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChecklistRepository checklistRepository;

    @Autowired
    private ChecklistStepRepository checklistStepRepository;

    private Checklist testChecklist;
    private ChecklistStep testStep;

    @BeforeEach
    void setUp() {
        // Test-Checklist erstellen
        testChecklist = new Checklist();
        testChecklist.setName("Test Checklist");
        testChecklist.setPlantName("Test Plant");
        testChecklist.setRecommendations("Test Recommendations");
        testChecklist = checklistRepository.save(testChecklist);

        // Test-Step vorbereiten (wird nicht gespeichert, nur für Request-Body)
        testStep = new ChecklistStep();
        testStep.setDescription("Fenster überprüfen");
        testStep.setRequirement("Alle Fenster müssen geschlossen sein.");
        testStep.setOrderIndex(1);
    }

    @Test
    void createChecklistStep_withValidData_returnsCreated() throws Exception {
        String json = objectMapper.writeValueAsString(testStep);

        mockMvc.perform(post("/api/checklists/{checklistId}/steps", testChecklist.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.description").value("Fenster überprüfen"))
                .andExpect(jsonPath("$.requirement").value("Alle Fenster müssen geschlossen sein."))
                .andExpect(jsonPath("$.orderIndex").value(1));
    }

    @Test
    void createChecklistStep_withInvalidData_returnsBadRequest() throws Exception {
        // Step mit leerer Beschreibung
        ChecklistStep invalidStep = new ChecklistStep();
        invalidStep.setDescription(""); // leer
        invalidStep.setRequirement("Requirement");
        invalidStep.setOrderIndex(1);

        String json = objectMapper.writeValueAsString(invalidStep);

        mockMvc.perform(post("/api/checklists/{checklistId}/steps", testChecklist.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createChecklistStep_withNonExistentChecklist_returnsNotFound() throws Exception {
        String json = objectMapper.writeValueAsString(testStep);

        mockMvc.perform(post("/api/checklists/{checklistId}/steps", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStepsForChecklist_returnsListOfSteps() throws Exception {
        // Step speichern
        testStep.setChecklist(testChecklist);
        checklistStepRepository.save(testStep);

        mockMvc.perform(get("/api/checklists/{checklistId}/steps", testChecklist.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Fenster überprüfen"))
                .andExpect(jsonPath("$[0].orderIndex").value(1));
    }

    @Test
    void getStepsForChecklist_returnsEmptyList() throws Exception {
        // Checklist ohne Steps
        mockMvc.perform(get("/api/checklists/{checklistId}/steps", testChecklist.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getStepsForChecklist_returnsOrderedByOrderIndex() throws Exception {
        // Mehrere Steps mit unterschiedlichen Indizes
        for (int i = 3; i >= 1; i--) {
            ChecklistStep step = new ChecklistStep();
            step.setDescription("Step " + i);
            step.setRequirement("Requirement " + i);
            step.setOrderIndex(i);
            step.setChecklist(testChecklist);
            checklistStepRepository.save(step);
        }

        mockMvc.perform(get("/api/checklists/{checklistId}/steps", testChecklist.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].orderIndex").value(1))
                .andExpect(jsonPath("$[1].orderIndex").value(2))
                .andExpect(jsonPath("$[2].orderIndex").value(3));
    }

    @Test
    void getStepById_withValidId_returnsOk() throws Exception {
        // Step speichern
        testStep.setChecklist(testChecklist);
        ChecklistStep savedStep = checklistStepRepository.save(testStep);

        mockMvc.perform(get("/api/checklist-steps/{id}", savedStep.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedStep.getId()))
                .andExpect(jsonPath("$.description").value("Fenster überprüfen"));
    }

    @Test
    void getStepById_withInvalidId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/checklist-steps/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStep_withValidData_returnsUpdatedStep() throws Exception {
        // Step speichern
        testStep.setChecklist(testChecklist);
        ChecklistStep savedStep = checklistStepRepository.save(testStep);

        // Update
        ChecklistStep updatedStep = new ChecklistStep();
        updatedStep.setDescription("Türen überprüfen");
        updatedStep.setRequirement("Alle Türen müssen geschlossen sein.");
        updatedStep.setOrderIndex(2);

        String json = objectMapper.writeValueAsString(updatedStep);

        mockMvc.perform(put("/api/checklist-steps/{id}", savedStep.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Türen überprüfen"))
                .andExpect(jsonPath("$.requirement").value("Alle Türen müssen geschlossen sein."))
                .andExpect(jsonPath("$.orderIndex").value(2));
    }

    @Test
    void updateStep_withNonExistentId_returnsNotFound() throws Exception {
        ChecklistStep updatedStep = new ChecklistStep();
        updatedStep.setDescription("Updated Description");
        updatedStep.setRequirement("Updated Requirement");
        updatedStep.setOrderIndex(1);

        String json = objectMapper.writeValueAsString(updatedStep);

        mockMvc.perform(put("/api/checklist-steps/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStep_withValidId_returnsNoContent() throws Exception {
        // Step speichern
        testStep.setChecklist(testChecklist);
        ChecklistStep savedStep = checklistStepRepository.save(testStep);

        // Löschen
        mockMvc.perform(delete("/api/checklist-steps/{id}", savedStep.getId()))
                .andExpect(status().isNoContent());

        // Verifizieren, dass gelöscht
        mockMvc.perform(get("/api/checklist-steps/{id}", savedStep.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteStep_withNonExistentId_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/checklist-steps/99999"))
                .andExpect(status().isNotFound());
    }
}