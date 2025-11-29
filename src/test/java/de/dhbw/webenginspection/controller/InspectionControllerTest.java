package de.dhbw.webenginspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.webenginspection.dto.InspectionCreateRequest;
import de.dhbw.webenginspection.entity.Checklist;
import de.dhbw.webenginspection.entity.User;
import de.dhbw.webenginspection.entity.UserRole;
import de.dhbw.webenginspection.repository.ChecklistRepository;
import de.dhbw.webenginspection.repository.UserRepository;
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
class InspectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChecklistRepository checklistRepository;

    @Autowired
    private UserRepository userRepository;

    private Checklist testChecklist;
    private User testInspector;

    @BeforeEach
    void setUp() {
        // Test-User erstellen
        testInspector = new User("test_inspector", "Test Inspector", "hashedPassword123", UserRole.INSPECTOR);
        testInspector = userRepository.save(testInspector);

        // Test-Checklist erstellen
        testChecklist = new Checklist();
        testChecklist.setName("Test Checklist");
        testChecklist.setPlantName("Test Plant");
        testChecklist.setRecommendations("Test Recommendations");
        testChecklist = checklistRepository.save(testChecklist);
    }

    @Test
    void createInspection_withInvalidBody_returnsBadRequestAndErrorResponse() throws Exception {
        // Body absichtlich unvollständig/ungültig
        InspectionCreateRequest request = new InspectionCreateRequest();
        // nichts setzen → checklistId null, title/plantName leer, assignedInspectorId null

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void createInspection_withValidBody_returnsCreated() throws Exception {
        // Gültigen Request bauen
        InspectionCreateRequest request = new InspectionCreateRequest();
        request.setChecklistId(testChecklist.getId());
        request.setTitle("Test Inspection");
        request.setPlantName("Test Plant");
        request.setPlannedDate(LocalDateTime.now().plusDays(1));
        request.setAssignedInspectorId(testInspector.getId());
        request.setGeneralComment("This is a test inspection");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Test Inspection"))
                .andExpect(jsonPath("$.plantName").value("Test Plant"))
                .andExpect(jsonPath("$.status").value("PLANNED"))
                .andExpect(jsonPath("$.assignedInspector.id").value(testInspector.getId()));
    }

    @Test
    void createInspection_withNonExistentChecklist_returnsBadRequest() throws Exception {
        InspectionCreateRequest request = new InspectionCreateRequest();
        request.setChecklistId(99999L); // nicht existierende ID
        request.setTitle("Test Inspection");
        request.setPlantName("Test Plant");
        request.setPlannedDate(LocalDateTime.now().plusDays(1));
        request.setAssignedInspectorId(testInspector.getId());

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createInspection_withNonExistentInspector_returnsBadRequest() throws Exception {
        InspectionCreateRequest request = new InspectionCreateRequest();
        request.setChecklistId(testChecklist.getId());
        request.setTitle("Test Inspection");
        request.setPlantName("Test Plant");
        request.setPlannedDate(LocalDateTime.now().plusDays(1));
        request.setAssignedInspectorId(99999L); // nicht existierender User

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getInspectionById_withValidId_returnsOk() throws Exception {
        // Erst eine Inspection erstellen
        InspectionCreateRequest request = new InspectionCreateRequest();
        request.setChecklistId(testChecklist.getId());
        request.setTitle("Test Inspection");
        request.setPlantName("Test Plant");
        request.setPlannedDate(LocalDateTime.now().plusDays(1));
        request.setAssignedInspectorId(testInspector.getId());

        String json = objectMapper.writeValueAsString(request);

        String createResponse = mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long inspectionId = objectMapper.readTree(createResponse).get("id").asLong();

        // Dann abrufen
        mockMvc.perform(get("/api/inspections/" + inspectionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(inspectionId))
                .andExpect(jsonPath("$.title").value("Test Inspection"));
    }

    @Test
    void getInspectionById_withInvalidId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/inspections/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getInspectionsByUser_returnsAllInspectionsForUser() throws Exception {
        // Mehrere Inspektionen für einen User erstellen
        for (int i = 0; i < 3; i++) {
            InspectionCreateRequest request = new InspectionCreateRequest();
            request.setChecklistId(testChecklist.getId());
            request.setTitle("Test Inspection " + i);
            request.setPlantName("Test Plant");
            request.setPlannedDate(LocalDateTime.now().plusDays(i));
            request.setAssignedInspectorId(testInspector.getId());

            String json = objectMapper.writeValueAsString(request);
            mockMvc.perform(post("/api/inspections")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }

        // Alle Inspektionen des Users abrufen
        mockMvc.perform(get("/api/inspections/by-user/" + testInspector.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void getInspectionsByUser_withNoInspections_returnsEmptyList() throws Exception {
        // Neuen User ohne Inspektionen erstellen
        User otherUser = new User("other_user", "Other User", "hashedPassword123", UserRole.INSPECTOR);
        otherUser = userRepository.save(otherUser);

        mockMvc.perform(get("/api/inspections/by-user/" + otherUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllInspections_returnsEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/inspections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAllInspections_returnsMultipleInspections() throws Exception {
        // 2 Inspektionen erstellen
        for (int i = 0; i < 2; i++) {
            InspectionCreateRequest request = new InspectionCreateRequest();
            request.setChecklistId(testChecklist.getId());
            request.setTitle("Inspection " + i);
            request.setPlantName("Test Plant");
            request.setPlannedDate(LocalDateTime.now().plusDays(i));
            request.setAssignedInspectorId(testInspector.getId());

            String json = objectMapper.writeValueAsString(request);
            mockMvc.perform(post("/api/inspections")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/inspections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateStatus_withValidStatus_returnsUpdatedInspection() throws Exception {
        // Inspection erstellen
        InspectionCreateRequest request = new InspectionCreateRequest();
        request.setChecklistId(testChecklist.getId());
        request.setTitle("Test Inspection");
        request.setPlantName("Test Plant");
        request.setPlannedDate(LocalDateTime.now().plusDays(1));
        request.setAssignedInspectorId(testInspector.getId());

        String json = objectMapper.writeValueAsString(request);

        String createResponse = mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long inspectionId = objectMapper.readTree(createResponse).get("id").asLong();

        // Status aktualisieren
        String newStatus = "\"IN_PROGRESS\"";

        mockMvc.perform(patch("/api/inspections/" + inspectionId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStatus))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateStatus_withInvalidStatus_returnsBadRequest() throws Exception {
        // Inspection erstellen
        InspectionCreateRequest request = new InspectionCreateRequest();
        request.setChecklistId(testChecklist.getId());
        request.setTitle("Test Inspection");
        request.setPlantName("Test Plant");
        request.setPlannedDate(LocalDateTime.now().plusDays(1));
        request.setAssignedInspectorId(testInspector.getId());

        String json = objectMapper.writeValueAsString(request);

        String createResponse = mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long inspectionId = objectMapper.readTree(createResponse).get("id").asLong();

        // Status mit ungültigem Wert aktualisieren
        String invalidStatus = "\"INVALID_STATUS\"";

        mockMvc.perform(patch("/api/inspections/" + inspectionId + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidStatus))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_withNonExistentInspection_returnsNotFound() throws Exception {
        String newStatus = "\"IN_PROGRESS\"";

        mockMvc.perform(patch("/api/inspections/99999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newStatus))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteInspection_withValidId_returnsNoContent() throws Exception {
        // Inspection erstellen
        InspectionCreateRequest request = new InspectionCreateRequest();
        request.setChecklistId(testChecklist.getId());
        request.setTitle("Test Inspection");
        request.setPlantName("Test Plant");
        request.setPlannedDate(LocalDateTime.now().plusDays(1));
        request.setAssignedInspectorId(testInspector.getId());

        String json = objectMapper.writeValueAsString(request);

        String createResponse = mockMvc.perform(post("/api/inspections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long inspectionId = objectMapper.readTree(createResponse).get("id").asLong();

        // Löschen
        mockMvc.perform(delete("/api/inspections/" + inspectionId))
                .andExpect(status().isNoContent());

        // Verifizieren, dass gelöscht
        mockMvc.perform(get("/api/inspections/" + inspectionId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteInspection_withNonExistentId_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/inspections/99999"))
                .andExpect(status().isNotFound());
    }
}