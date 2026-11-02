package com.uniquindio.skyline.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Loads the whole application: the request goes through the real SecurityConfig, the BCrypt encoder
// and the H2 database. Each test uses a new email, because the context (and the database) is shared.
@SpringBootTest
@AutoConfigureMockMvc
class RegisterEndpointTest {

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String LOGIN_URL = "/api/auth/login";
    private static final String PASSWORD = "secret123";

    @Autowired
    private MockMvc mockMvc;

    private String newEmail() {
        return "customer-" + UUID.randomUUID() + "@mail.com";
    }

    private String registerJson(String email, String password) {
        return """
                {"email": "%s", "password": "%s"}
                """.formatted(email, password);
    }

    @Test
    void shouldRegisterACustomerWithoutToken() throws Exception {
        // Arrange
        String email = newEmail();

        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson(email, PASSWORD)))
                .andExpect(status().isCreated())                   // 201
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldLoginWithTheRegisteredUser() throws Exception {
        // Arrange
        String email = newEmail();
        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson(email, PASSWORD)));

        // Act & Assert
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "%s", "password": "%s"}
                                """.formatted(email, PASSWORD)))
                .andExpect(status().isOk())                        // 200
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.roles[0]").value("CUSTOMER"));
    }

    @Test
    void shouldAnswer409WhenTheEmailIsAlreadyRegistered() throws Exception {
        // Arrange: customer1@skyline.com is one of the initial users.
        String email = "customer1@skyline.com";

        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson(email, PASSWORD)))
                .andExpect(status().isConflict())                  // 409
                .andExpect(jsonPath("$.message").value("The email is already registered"));
    }

    @Test
    void shouldAnswer400WhenThePasswordIsTooShort() throws Exception {
        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson(newEmail(), "123")))
                .andExpect(status().isBadRequest())                // 400
                .andExpect(jsonPath("$.errors.password").exists());
    }
}
