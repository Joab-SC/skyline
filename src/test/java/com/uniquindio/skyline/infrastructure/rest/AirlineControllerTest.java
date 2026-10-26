package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.usecase.CreateAirlineUseCase;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.infrastructure.rest.mapper.AirlineMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AirlineController.class)
public class AirlineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAirlineUseCase createAirlineUseCase;

    @MockitoBean
    private AirlineMapper mapper;

    @Test
    void shouldAnswerCreateAirlineWithValidData() throws Exception {
        // Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 100);
        when(createAirlineUseCase.execute(any(), anyInt(), anyDouble())).thenReturn(airline);

        String requestJson = """
                {
                "name": "Mega airline", "maximumLuggageAmount": 10, "seatPrice": 100
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/airlines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())           // 201
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldAnswer400WhenBusinessRuleIsBroken() throws Exception {
        // Arrange
        when(createAirlineUseCase.execute(any(), anyInt(), anyDouble()))
                .thenThrow(new DomainRuleException("The seat price cannot be negative"));

        String requestJson = """
                {
                "name": "Mega airline", "maximumLuggageAmount": 10, "seatPrice": 100
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/airlines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400
    }

    @Test
    void shouldAnswer400WhenNameIsBlank() throws Exception {
        // Arrange
        String requestJson = """
                {
                "name": "", "maximumLuggageAmount": 10, "seatPrice": 100
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/airlines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400 by @Valid

        verify(createAirlineUseCase, never()).execute(any(), anyInt(), anyDouble());
    }
}