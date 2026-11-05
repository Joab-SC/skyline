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
    // Meets the strength rule of User: 8+ characters, an uppercase letter and a number.
    private static final String PASSWORD = "Secret123";

    @Autowired
    private MockMvc mockMvc;

    private String newEmail() {
        return "customer-" + UUID.randomUUID() + "@mail.com";
    }

    private String registerJson(String email, String password) {
        return """
                {
                  "email": "%s",
                  "password": "%s",
                  "idDocument": "1094000000",
                  "documentType": "CITIZENSHIP_ID",
                  "firstName": "Ana",
                  "secondName": "Maria",
                  "lastName1": "Lopez",
                  "lastName2": "Diaz",
                  "birthDate": "1995-05-20",
                  "gender": "FEMALE"
                }
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

    // The strength rule lives in the domain (User.validatePasswordStrength), so it is a broken rule: 422.
    @Test
    void shouldAnswer422WhenThePasswordIsTooShort() throws Exception {
        // Act & Assert
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson(newEmail(), "Ab1")))
                .andExpect(status().isUnprocessableContent())      // 422
                .andExpect(jsonPath("$.message").value("The password must have at least 8 characters"));
    }
}
