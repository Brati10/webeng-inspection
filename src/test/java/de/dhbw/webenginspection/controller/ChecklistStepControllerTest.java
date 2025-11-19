package de.dhbw.webenginspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.entity.ChecklistStep;
import de.dhbw.webenginspection.repository.ChecklistRepository;
import de.dhbw.webenginspection.repository.ChecklistStepRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void createChecklistStep_withValidData_returnsCreated() throws Exception {
        // Arrange: Checklist anlegen
        Checklist checklist = new Checklist();
        checklist.setName("Test checklist for ChecklistSteps test");
        checklist.setPlantName("Test checklist plantname for ChecklistSteps test");
        checklist.setRecommendations("Test checklist recommendations for ChecklistSteps test");
        Checklist savedChecklist = checklistRepository.save(checklist);

        // Step-Daten vorbereiten (ohne checklist, wird im Service gesetzt)
        ChecklistStep step = new ChecklistStep();
        step.setDescription("Fenster überprüfen");
        step.setRequirement("Alle Fenster müssen geschlossen sein.");
        step.setOrderIndex(1);

        String json = objectMapper.writeValueAsString(step);

        // Act + Assert
        mockMvc.perform(post("/api/checklists/{checklistId}/steps", savedChecklist.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Fenster überprüfen"))
                .andExpect(jsonPath("$.orderIndex").value(1));
    }

    @Test
    void getStepsForChecklist_returnsListOfSteps() throws Exception {
        // Arrange: Checklist + Step per Repository anlegen
        Checklist checklist = new Checklist();
        checklist.setName("Test Checklist");
        checklist.setPlantName("Test Checklist Plantname");
        checklist.setRecommendations("Test Checklist Recommendations");
        Checklist savedChecklist = checklistRepository.save(checklist);

        ChecklistStep step = new ChecklistStep();
        step.setDescription("Fenster überprüfen");
        step.setRequirement("Alle Fenster müssen geschlossen sein.");
        step.setOrderIndex(1);
        step.setChecklist(savedChecklist);
        checklistStepRepository.save(step);

        // Act + Assert
        mockMvc.perform(get("/api/checklists/{checklistId}/steps", savedChecklist.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Fenster überprüfen"))
                .andExpect(jsonPath("$[0].orderIndex").value(1));
    }
}

