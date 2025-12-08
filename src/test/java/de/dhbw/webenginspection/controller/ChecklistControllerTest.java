package de.dhbw.webenginspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.webenginspection.entity.Checklist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChecklistControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @WithMockUser(roles = "ADMIN")
        void testCreateChecklist() throws Exception {
                Checklist c = new Checklist();
                c.setName("C1");
                c.setPlantName("P1");
                c.setRecommendations("R1");
                mockMvc.perform(post("/api/checklists").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(c))).andExpect(status().isCreated());
        }

        @Test
        @WithMockUser(roles = "INSPECTOR")
        void testGetAll() throws Exception {
                mockMvc.perform(get("/api/checklists")).andExpect(status().isOk());
        }
}