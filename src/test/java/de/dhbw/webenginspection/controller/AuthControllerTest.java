package de.dhbw.webenginspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.webenginspection.dto.LoginRequest;
import de.dhbw.webenginspection.entity.User;
import de.dhbw.webenginspection.entity.UserRole;
import de.dhbw.webenginspection.service.UserService;
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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private User testUser;
    private static final String TEST_PASSWORD = "testPassword123";

    @BeforeEach
    void setUp() {
        // Test-User erstellen mit echter Passwort-Hashing
        testUser = userService.createUser("test_user", "Test User", TEST_PASSWORD, UserRole.INSPECTOR);
    }

    @Test
    void login_withValidCredentials_returnsOkAndUserData() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("test_user");
        request.setPassword(TEST_PASSWORD);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value("test_user"))
                .andExpect(jsonPath("$.displayName").value("Test User"))
                .andExpect(jsonPath("$.role").value("INSPECTOR"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist()); // Sicherheit!
    }

    @Test
    void login_withInvalidUsername_returnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("non_existent_user");
        request.setPassword(TEST_PASSWORD);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void login_withInvalidPassword_returnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("test_user");
        request.setPassword("wrongPassword");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void login_withMissingUsername_returnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername(""); // leer
        request.setPassword(TEST_PASSWORD);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void login_withMissingPassword_returnsBadRequest() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("test_user");
        request.setPassword(""); // leer

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors").isArray());
    }

    @Test
    void getMe_withValidUserId_returnsUserData() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .param("userId", String.valueOf(testUser.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.username").value("test_user"))
                .andExpect(jsonPath("$.displayName").value("Test User"))
                .andExpect(jsonPath("$.role").value("INSPECTOR"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist()); // Sicherheit!
    }

    @Test
    void getMe_withInvalidUserId_returnsNotFound() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .param("userId", "99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMe_withMissingUserId_returnsBadRequest() throws Exception {
        // userId Parameter fehlt
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_differentRoles_returnsCorrectRole() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin_user");
        request.setPassword(TEST_PASSWORD);

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void login_multipleUsers_returnsCorrectUser() throws Exception {
        // Zweiter User
        User secondUser = userService.createUser("second_user", "Second User", "anotherPassword", UserRole.INSPECTOR);

        LoginRequest request = new LoginRequest();
        request.setUsername("second_user");
        request.setPassword("anotherPassword");

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(secondUser.getId()))
                .andExpect(jsonPath("$.username").value("second_user"))
                .andExpect(jsonPath("$.displayName").value("Second User"));
    }
}