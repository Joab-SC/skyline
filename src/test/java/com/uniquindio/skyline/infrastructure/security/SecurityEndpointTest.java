package com.uniquindio.skyline.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Loads the whole application, so the requests go through the real SecurityConfig.
// GET /api/airlines/top (airline ranking) is only for ADMIN, and GET /api/bookings/mine only for CUSTOMER.
@SpringBootTest
@AutoConfigureMockMvc
class SecurityEndpointTest {

    private static final String TOP_AIRLINES_URL = "/api/airlines/top";
    private static final String MY_BOOKINGS_URL = "/api/bookings/mine";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAnswer401WhenThereIsNoToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(TOP_AIRLINES_URL))
                .andExpect(status().isUnauthorized())      // 401
                .andExpect(header().string("WWW-Authenticate", "Bearer"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("A valid token is required"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldAnswer401WhenTheTokenIsInvalid() throws Exception {
        // Act & Assert
        mockMvc.perform(get(TOP_AIRLINES_URL).header("Authorization", "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized())      // 401
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("A valid token is required"));
    }

    @Test
    void shouldAnswer403WhenRoleIsNotAdmin() throws Exception {
        // Arrange
        SimpleGrantedAuthority customer = new SimpleGrantedAuthority("CUSTOMER");

        // Act & Assert
        mockMvc.perform(get(TOP_AIRLINES_URL).with(jwt().authorities(customer)))
                .andExpect(status().isForbidden())         // 403
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("You do not have permission for this action"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldAnswer200WhenRoleIsAdmin() throws Exception {
        // Arrange
        SimpleGrantedAuthority admin = new SimpleGrantedAuthority("ADMIN");

        // Act & Assert
        mockMvc.perform(get(TOP_AIRLINES_URL).with(jwt().authorities(admin)))
                .andExpect(status().isOk());               // 200
    }

    // ---------- GET /api/bookings/mine: only CUSTOMER ----------

    @Test
    void myBookingsShouldAnswer401WhenThereIsNoToken() throws Exception {
        // Act & Assert
        mockMvc.perform(get(MY_BOOKINGS_URL))
                .andExpect(status().isUnauthorized());     // 401
    }

    @Test
    void myBookingsShouldAnswer403WhenRoleIsNotCustomer() throws Exception {
        // Arrange
        SimpleGrantedAuthority airline = new SimpleGrantedAuthority("AIRLINE_ADMIN");

        // Act & Assert
        mockMvc.perform(get(MY_BOOKINGS_URL).with(jwt().authorities(airline)))
                .andExpect(status().isForbidden());        // 403
    }

    @Test
    void myBookingsShouldAnswer200WhenRoleIsCustomer() throws Exception {
        // Arrange
        SimpleGrantedAuthority customer = new SimpleGrantedAuthority("CUSTOMER");

        // Act & Assert
        mockMvc.perform(get(MY_BOOKINGS_URL)
                        .with(jwt().jwt(token -> token.claim(JwtTokenProvider.USER_ID_CLAIM, "customer1"))
                                .authorities(customer)))
                .andExpect(status().isOk());               // 200
    }
}
