package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.response.AirlineResponse;
import com.uniquindio.skyline.application.dto.response.ExtraServiceResponse;
import com.uniquindio.skyline.application.dto.response.TopAirlineResponse;
import com.uniquindio.skyline.application.usecase.CreateAirlineUseCase;
import com.uniquindio.skyline.application.usecase.GetTopAirlinesUseCase;
import com.uniquindio.skyline.application.usecase.ListAirlineExtraServicesUseCase;
import com.uniquindio.skyline.application.usecase.ListAirlinesUseCase;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineAircraftCount;
import com.uniquindio.skyline.infrastructure.rest.mapper.AirlineMapper;
import com.uniquindio.skyline.infrastructure.rest.mapper.ExtraServiceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AirlineController.class)
public class AirlineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAirlineUseCase createAirlineUseCase;

    @MockitoBean
    private ListAirlineExtraServicesUseCase listAirlineExtraServicesUseCase;

    @MockitoBean
    private GetTopAirlinesUseCase getTopAirlinesUseCase;

    @MockitoBean
    private ListAirlinesUseCase listAirlinesUseCase;

    @MockitoBean
    private AirlineMapper mapper;

    @MockitoBean
    private ExtraServiceMapper extraServiceMapper;

    @Test
    void shouldAnswerExtraServicesOfTheAirline() throws Exception {
        // Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 100);
        ExtraService meal = airline.createExtraService("Meal", "Hot meal", 20, 40);
        when(listAirlineExtraServicesUseCase.execute("airline-1")).thenReturn(List.of(meal));
        when(extraServiceMapper.toExtraServiceResponse(meal))
                .thenReturn(new ExtraServiceResponse(meal.getId(), "Meal", "Hot meal", 20, 40));

        // Act & Assert
        mockMvc.perform(get("/api/airlines/airline-1/extra-services"))
                .andExpect(status().isOk())                // 200
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(meal.getId()))
                .andExpect(jsonPath("$[0].nameService").value("Meal"));
    }

    @Test
    void shouldAnswerEmptyListWhenAirlineHasNoExtraServices() throws Exception {
        // Arrange
        when(listAirlineExtraServicesUseCase.execute("airline-1")).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/airlines/airline-1/extra-services"))
                .andExpect(status().isOk())                // 200
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldAnswer404WhenAirlineDoesNotExist() throws Exception {
        // Arrange
        when(listAirlineExtraServicesUseCase.execute("airline-1")).thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(get("/api/airlines/airline-1/extra-services"))
                .andExpect(status().isNotFound());         // 404
    }

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
    void shouldAnswer422WhenBusinessRuleIsBroken() throws Exception {
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
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.message").value("The seat price cannot be negative"));      // 422
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists());  // 400 by @Valid

        verify(createAirlineUseCase, never()).execute(any(), anyInt(), anyDouble());
    }

    @Test
    void shouldAnswerTopAirlinesWithDefaultLimit() throws Exception {
        // Arrange
        AirlineAircraftCount avianca = new AirlineAircraftCount("airline-1", "Avianca", 3L);
        when(getTopAirlinesUseCase.execute(5)).thenReturn(List.of(avianca));
        when(mapper.toTopAirlineResponse(avianca))
                .thenReturn(new TopAirlineResponse("airline-1", "Avianca", 3L));

        // Act & Assert
        mockMvc.perform(get("/api/airlines/top"))
                .andExpect(status().isOk())                // 200
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].airlineId").value("airline-1"))
                .andExpect(jsonPath("$[0].aircraftCount").value(3));
    }

    @Test
    void shouldAnswerTopAirlinesWithGivenLimit() throws Exception {
        // Arrange
        when(getTopAirlinesUseCase.execute(3)).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/airlines/top").param("limit", "3"))
                .andExpect(status().isOk())                // 200
                .andExpect(jsonPath("$.length()").value(0));

        verify(getTopAirlinesUseCase).execute(3);
    }

    @Test
    void shouldAnswer400WhenLimitIsOutOfRange() throws Exception {
        // Arrange
        when(getTopAirlinesUseCase.execute(0))
                .thenThrow(new IllegalArgumentException("The limit must be between 1 and 20"));

        // Act & Assert
        mockMvc.perform(get("/api/airlines/top").param("limit", "0"))
                .andExpect(status().isBadRequest());       // 400
    }

    @Test
    void shouldAnswerEveryAirline() throws Exception {
        // Arrange
        Airline avianca = Airline.createAirline("Avianca", 2, 50000);
        when(listAirlinesUseCase.execute()).thenReturn(List.of(avianca));
        when(mapper.toAirlineResponse(avianca))
                .thenReturn(new AirlineResponse(avianca.getId(), "Avianca", 2, 50000));

        // Act & Assert
        mockMvc.perform(get("/api/airlines"))
                .andExpect(status().isOk())                // 200
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(avianca.getId()))
                .andExpect(jsonPath("$[0].name").value("Avianca"));
    }

    @Test
    void shouldAnswerEmptyListWhenThereAreNoAirlines() throws Exception {
        // Arrange
        when(listAirlinesUseCase.execute()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/airlines"))
                .andExpect(status().isOk())                // 200
                .andExpect(jsonPath("$.length()").value(0));
    }
}