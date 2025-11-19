package de.dhbw.webenginspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.webenginspection.entity.Checklist;
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
class ChecklistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createChecklist_withValidBody_returnsCreated() throws Exception {
        Checklist checklist = new Checklist();
        checklist.setName("Checklist for ChecklistController test");
        checklist.setPlantName("Checklist plantname for ChecklistController test");
        checklist.setRecommendations("Checklist recommendations for ChecklistController test");

        String json = objectMapper.writeValueAsString(checklist);

        mockMvc.perform(post("/api/checklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Checklist for ChecklistController test"))
                .andExpect(jsonPath("$.plantName").value("Checklist plantname for ChecklistController test"))
                .andExpect(jsonPath("$.recommendations").value("Checklist recommendations for ChecklistController test"));
    }

    @Test
    void getChecklist_withUnknownId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/checklists/{id}", 999999L))
                .andExpect(status().isNotFound());
    }
}

