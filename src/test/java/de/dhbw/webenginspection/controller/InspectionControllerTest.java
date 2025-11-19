package de.dhbw.webenginspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.webenginspection.dto.InspectionCreateRequest;
import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.repository.ChecklistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InspectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChecklistRepository checklistRepository;

    @Test
    void createInspection_withInvalidBody_returnsBadRequestAndErrorResponse() throws Exception {
        // Body absichtlich unvollständig/ungültig: verletzt @NotNull/@NotBlank
        InspectionCreateRequest request = new InspectionCreateRequest();
        // nichts setzen → checklistId null, title/plantName leer

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                // optional noch ein paar JSON-Felder prüfen:
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void createInspection_withValidBody_returnsCreated() throws Exception {
        // 1. Test-Checklist in die DB legen, damit checklistId gültig ist
        Checklist checklist = new Checklist();
        checklist.setName("Test Checklist");
        checklist.setPlantName("Test Checklist PlantName");
        checklist.setRecommendations("Test Checklist Recommendations");
        Checklist savedChecklist = checklistRepository.save(checklist);

        // 2. Gültigen Request bauen
        InspectionCreateRequest request = new InspectionCreateRequest();
        request.setChecklistId(savedChecklist.getId());
        request.setTitle("MockMvc Test Inspection");
        request.setPlantName("Test Plant");
        request.setInspectionDate(LocalDateTime.now());
        request.setGeneralComment("Created via MockMvc test");

        String json = objectMapper.writeValueAsString(request);

        // 3. Request ausführen und 201 erwarten
        mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("MockMvc Test Inspection"))
                .andExpect(jsonPath("$.checklist.id").value(savedChecklist.getId()));
    }
}
