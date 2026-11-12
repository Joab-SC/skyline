package com.uniquindio.skyline.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Loads the whole application, so the requests go through the real SecurityConfig.
// GET /api/airlines/top (airline ranking) and POST /api/airlines/{id}/admins are only for ADMIN,
// POST /api/airlines/sellers only for AIRLINE_ADMIN,
// and GET /api/bookings/mine only for CUSTOMER.
@SpringBootTest
@AutoConfigureMockMvc
class SecurityEndpointTest {

    private static final String TOP_AIRLINES_URL = "/api/airlines/top";
    private static final String MY_BOOKINGS_URL = "/api/bookings/mine";
    // The airline does not exist: an ADMIN gets past security and the use case answers 404.
    private static final String REGISTER_ADMIN_URL = "/api/airlines/no-airline/admins";
    private static final String REGISTER_SELLER_URL = "/api/airlines/sellers";
    private static final String REGISTER_ADMIN_JSON = """
            {
            "email": "admin@airline.com", "password": "Secret123",
            "idDocument": "1001", "documentType": "CITIZENSHIP_ID",
            "firstName": "Laura", "lastName1": "Duarte",
            "birthDate": "2000-05-10", "gender": "FEMALE"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void airportsShouldBePublic() throws Exception {
        // Act & Assert: a guest needs the airports to search flights.
        mockMvc.perform(get("/api/airports"))
                .andExpect(status().isOk())                // 200 without a token
                .andExpect(jsonPath("$[0].code").exists());
    }

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

    // ---------- POST /api/airlines/{id}/admins: only ADMIN ----------

    @Test
    void registerAirlineAdminShouldAnswer401WhenThereIsNoToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(REGISTER_ADMIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_ADMIN_JSON))
                .andExpect(status().isUnauthorized());     // 401
    }

    @Test
    void registerAirlineAdminShouldAnswer403WhenRoleIsNotAdmin() throws Exception {
        // Arrange
        SimpleGrantedAuthority airline = new SimpleGrantedAuthority("AIRLINE_ADMIN");

        // Act & Assert
        mockMvc.perform(post(REGISTER_ADMIN_URL)
                        .with(jwt().authorities(airline))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_ADMIN_JSON))
                .andExpect(status().isForbidden());        // 403
    }

    @Test
    void registerAirlineAdminShouldLetAnAdminThrough() throws Exception {
        // Arrange
        SimpleGrantedAuthority admin = new SimpleGrantedAuthority("ADMIN");

        // Act & Assert
        mockMvc.perform(post(REGISTER_ADMIN_URL)
                        .with(jwt().authorities(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(REGISTER_ADMIN_JSON))
                .andExpect(status().isNotFound());         // 404: security passed, the airline does not exist
    }

    // ---------- POST /api/airlines/sellers: only AIRLINE_ADMIN ----------

    // A new email each time, because the context (and the database) is shared.
    private String sellerJson() {
        return """
                {
                "email": "seller-%s@airline.com", "password": "Secret123",
                "idDocument": "1001", "documentType": "CITIZENSHIP_ID",
                "firstName": "Laura", "lastName1": "Duarte",
                "birthDate": "2000-05-10", "gender": "FEMALE"
                }
                """.formatted(UUID.randomUUID());
    }

    @Test
    void registerSellerShouldAnswer401WhenThereIsNoToken() throws Exception {
        // Act & Assert
        mockMvc.perform(post(REGISTER_SELLER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sellerJson()))
                .andExpect(status().isUnauthorized());     // 401
    }

    @Test
    void registerSellerShouldAnswer403WhenRoleIsNotAirlineAdmin() throws Exception {
        // Arrange
        SimpleGrantedAuthority admin = new SimpleGrantedAuthority("ADMIN");

        // Act & Assert
        mockMvc.perform(post(REGISTER_SELLER_URL)
                        .with(jwt().authorities(admin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sellerJson()))
                .andExpect(status().isForbidden());        // 403
    }

    @Test
    void registerSellerShouldAnswer201WhenRoleIsAirlineAdmin() throws Exception {
        // Arrange: airline-latam is one of the initial airlines.
        SimpleGrantedAuthority airlineAdmin = new SimpleGrantedAuthority("AIRLINE_ADMIN");

        // Act & Assert
        mockMvc.perform(post(REGISTER_SELLER_URL)
                        .with(jwt().jwt(token -> token.claim(JwtTokenProvider.AIRLINE_ID_CLAIM, "airline-latam"))
                                .authorities(airlineAdmin))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sellerJson()))
                .andExpect(status().isCreated())           // 201
                .andExpect(jsonPath("$.role").value("AIRLINE_SELLER"))
                .andExpect(jsonPath("$.airlineId").value("airline-latam"));
    }
}
