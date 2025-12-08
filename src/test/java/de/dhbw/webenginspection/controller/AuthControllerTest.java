package de.dhbw.webenginspection.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dhbw.webenginspection.dto.LoginRequest;
import de.dhbw.webenginspection.entity.UserRole;
import de.dhbw.webenginspection.service.UserService;
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
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    void testLoginValid() throws Exception {
        userService.createUser("u" + System.nanoTime(), "U", "p", UserRole.INSPECTOR);
        LoginRequest req = new LoginRequest();
        req.setUsername("u" + System.nanoTime());
        req.setPassword("p");
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "INSPECTOR")
    void testGetMeNoParam() throws Exception {
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isBadRequest());
    }
}