package de.dhbw.webenginspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.webenginspection.dto.InspectionCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InspectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createInspection_withInvalidBody_returnsBadRequest() throws Exception {
        InspectionCreateRequest request = new InspectionCreateRequest();
        // nichts setzen → verletzt @NotNull/@NotBlank

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createInspection_withValidBody_returnsCreated() throws Exception {
        InspectionCreateRequest request = new InspectionCreateRequest();
        request.setChecklistId(1L); // ggf. vorher per SQL/H2-Init sicherstellen
        request.setTitle("MockMvc Test Inspection");
        request.setPlantName("Test Plant");
        request.setInspectionDate(LocalDateTime.now());
        request.setGeneralComment("Created via MockMvc test");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }
}
