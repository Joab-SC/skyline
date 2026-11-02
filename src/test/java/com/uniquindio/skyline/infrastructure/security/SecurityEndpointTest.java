package com.uniquindio.skyline.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Loads the whole application, so the requests go through the real SecurityConfig.
// GET /api/airlines/top (airline ranking) is only for ADMIN.
@SpringBootTest
@AutoConfigureMockMvc
class SecurityEndpointTest {

    private static final String TOP_AIRLINES_URL = "/api/airlines/top";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAnswer401WhenThereIsNoToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(TOP_AIRLINES_URL))
                .andExpect(status().isUnauthorized());     // 401
    }

    @Test
    void shouldAnswer403WhenRoleIsNotAdmin() throws Exception {
        // Arrange
        SimpleGrantedAuthority customer = new SimpleGrantedAuthority("CUSTOMER");

        // Act & Assert
        mockMvc.perform(get(TOP_AIRLINES_URL).with(jwt().authorities(customer)))
                .andExpect(status().isForbidden());        // 403
    }

    @Test
    void shouldAnswer200WhenRoleIsAdmin() throws Exception {
        // Arrange
        SimpleGrantedAuthority admin = new SimpleGrantedAuthority("ADMIN");

        // Act & Assert
        mockMvc.perform(get(TOP_AIRLINES_URL).with(jwt().authorities(admin)))
                .andExpect(status().isOk());               // 200
    }
}
