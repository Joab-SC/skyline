package com.uniquindio.skyline.integration;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CreateExtraServiceIntegrationTest {

    // Wingo has no extra services in data.sql
    private static final String AIRLINE_ID = "airline-wingo";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AirlineRepository airlineRepository;

    @Test
    void shouldCreateExtraServiceAndSaveItInTheAirline() throws Exception {
        // Arrange
        String requestJson = """
                {
                  "airlineId": "airline-wingo",
                  "nameService": "Pet in cabin",
                  "description": "Small pet travels with the passenger",
                  "nationalPrice": 80000,
                  "internationalPrice": 150000
                }
                """;

        // Act
        mockMvc.perform(post("/api/extra_services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nameService").value("Pet in cabin"))
                .andExpect(jsonPath("$.nationalPrice").value(80000.0))
                .andExpect(jsonPath("$.internationalPrice").value(150000.0));

        Airline airline = airlineRepository.findById(AIRLINE_ID).orElseThrow();
        List<ExtraService> extraServices = airline.getExtraServices();
        assertEquals(1, extraServices.size());
        assertEquals("Pet in cabin", extraServices.getFirst().getNameService());
        assertEquals("Small pet travels with the passenger", extraServices.getFirst().getDescription().orElse(null));
        assertEquals(80000, extraServices.getFirst().getNationalPrice());
        assertEquals(150000, extraServices.getFirst().getInternationalPrice());
    }
}
