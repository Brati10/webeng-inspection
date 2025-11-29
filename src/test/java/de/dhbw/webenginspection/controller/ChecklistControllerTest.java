package de.dhbw.webenginspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.webenginspection.entity.Checklist;
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
class ChecklistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Checklist testChecklist;

    @BeforeEach
    void setUp() {
        // Test-Checklist vorbereiten (wird nicht gespeichert, nur für Request-Body)
        testChecklist = new Checklist();
        testChecklist.setName("Test Checklist");
        testChecklist.setPlantName("Test Plant");
        testChecklist.setRecommendations("Test Recommendations");
    }

    @Test
    void createChecklist_withValidBody_returnsCreated() throws Exception {
        String json = objectMapper.writeValueAsString(testChecklist);

        mockMvc.perform(post("/api/checklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Test Checklist"))
                .andExpect(jsonPath("$.plantName").value("Test Plant"))
                .andExpect(jsonPath("$.recommendations").value("Test Recommendations"));
    }

    @Test
    void createChecklist_withInvalidBody_returnsBadRequest() throws Exception {
        // Checklist mit leeren erforderlichen Feldern
        Checklist invalidChecklist = new Checklist();
        invalidChecklist.setName(""); // leer
        invalidChecklist.setPlantName("Test Plant");
        invalidChecklist.setRecommendations("Test Recommendations");

        String json = objectMapper.writeValueAsString(invalidChecklist);

        mockMvc.perform(post("/api/checklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllChecklists_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/checklists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllChecklists_returnsMultipleChecklists() throws Exception {
        // 2 Checklisten erstellen
        for (int i = 0; i < 2; i++) {
            Checklist checklist = new Checklist();
            checklist.setName("Checklist " + i);
            checklist.setPlantName("Plant " + i);
            checklist.setRecommendations("Recommendations " + i);

            String json = objectMapper.writeValueAsString(checklist);
            mockMvc.perform(post("/api/checklists")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/checklists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getChecklistById_withValidId_returnsOk() throws Exception {
        // Erst eine Checklist erstellen
        String json = objectMapper.writeValueAsString(testChecklist);

        String createResponse = mockMvc.perform(post("/api/checklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long checklistId = objectMapper.readTree(createResponse).get("id").asLong();

        // Dann abrufen
        mockMvc.perform(get("/api/checklists/" + checklistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(checklistId))
                .andExpect(jsonPath("$.name").value("Test Checklist"));
    }

    @Test
    void getChecklistById_withUnknownId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/checklists/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateChecklist_withValidData_returnsUpdatedChecklist() throws Exception {
        // Erst eine Checklist erstellen
        String json = objectMapper.writeValueAsString(testChecklist);

        String createResponse = mockMvc.perform(post("/api/checklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long checklistId = objectMapper.readTree(createResponse).get("id").asLong();

        // Update
        Checklist updatedChecklist = new Checklist();
        updatedChecklist.setName("Updated Checklist");
        updatedChecklist.setPlantName("Updated Plant");
        updatedChecklist.setRecommendations("Updated Recommendations");

        String updateJson = objectMapper.writeValueAsString(updatedChecklist);

        mockMvc.perform(put("/api/checklists/" + checklistId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Checklist"))
                .andExpect(jsonPath("$.plantName").value("Updated Plant"))
                .andExpect(jsonPath("$.recommendations").value("Updated Recommendations"));
    }

    @Test
    void updateChecklist_withNonExistentId_returnsNotFound() throws Exception {
        Checklist updatedChecklist = new Checklist();
        updatedChecklist.setName("Updated Checklist");
        updatedChecklist.setPlantName("Updated Plant");
        updatedChecklist.setRecommendations("Updated Recommendations");

        String json = objectMapper.writeValueAsString(updatedChecklist);

        mockMvc.perform(put("/api/checklists/99999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteChecklist_withValidId_returnsNoContent() throws Exception {
        // Erst eine Checklist erstellen
        String json = objectMapper.writeValueAsString(testChecklist);

        String createResponse = mockMvc.perform(post("/api/checklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long checklistId = objectMapper.readTree(createResponse).get("id").asLong();

        // Löschen
        mockMvc.perform(delete("/api/checklists/" + checklistId))
                .andExpect(status().isNoContent());

        // Verifizieren, dass gelöscht
        mockMvc.perform(get("/api/checklists/" + checklistId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteChecklist_withNonExistentId_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/checklists/99999"))
                .andExpect(status().isNotFound());
    }
}