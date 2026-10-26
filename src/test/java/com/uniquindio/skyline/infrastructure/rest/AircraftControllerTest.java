package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.usecase.CreateAircraftUseCase;
import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import com.uniquindio.skyline.infrastructure.rest.mapper.AircraftMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AircraftController.class)
public class AircraftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAircraftUseCase createAircraftUseCase;

    @MockitoBean
    private AircraftMapper mapper;

    @Test
    void shouldAnswerCreateAircraftWithValidData() throws Exception {
        // Arrange
        Aircraft aircraft = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
        when(createAircraftUseCase.execute(any(), any())).thenReturn(aircraft);

        String requestJson = """
                {
                "airlineId": "air-1", "aircraftModel": "EMBRAER_E175"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())           // 201
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldAnswer404WhenAirlineDoesNotExist() throws Exception {
        // Arrange
        when(createAircraftUseCase.execute(any(), any())).thenThrow(new NoSuchElementException());

        String requestJson = """
                {
                "airlineId": "air-1", "aircraftModel": "EMBRAER_E175"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());         // 404
    }

    @Test
    void shouldAnswer400WhenAircraftModelIsMissing() throws Exception {
        // Arrange
        String requestJson = """
                {
                "airlineId": "air-1"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400 by @Valid

        verify(createAircraftUseCase, never()).execute(any(), any());
    }

    @Test
    void shouldAnswer400WhenAircraftModelDoesNotExist() throws Exception {
        // Arrange
        String requestJson = """
                {
                "airlineId": "air-1", "aircraftModel": "BOEING_747"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/aircrafts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400, JSON cannot be read

        verify(createAircraftUseCase, never()).execute(any(), any());
    }
}
