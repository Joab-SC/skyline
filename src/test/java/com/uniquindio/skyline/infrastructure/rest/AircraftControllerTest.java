package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.response.AircraftResponse;
import com.uniquindio.skyline.application.usecase.CreateAircraftUseCase;
import com.uniquindio.skyline.application.usecase.ListAirlineAircraftsUseCase;
import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import com.uniquindio.skyline.infrastructure.rest.mapper.AircraftMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AircraftController.class)
public class AircraftControllerTest {

    private static final JwtAuthenticationToken AIRLINE_USER = JwtTestTokens.airlineUser("air-1");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAircraftUseCase createAircraftUseCase;

    @MockitoBean
    private ListAirlineAircraftsUseCase listAirlineAircraftsUseCase;

    @MockitoBean
    private AircraftMapper mapper;

    @Test
    void shouldAnswerCreateAircraftWithValidData() throws Exception {
        // Arrange
        Aircraft aircraft = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
        when(createAircraftUseCase.execute(any(), any())).thenReturn(aircraft);

        String requestJson = """
                {
                "aircraftModel": "EMBRAER_E175"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/aircrafts")
                        .principal(AIRLINE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())           // 201
                .andExpect(header().exists("Location"));

        verify(createAircraftUseCase).execute(AircraftModel.EMBRAER_E175, "air-1");  // airline of the token
    }

    @Test
    void shouldIgnoreAirlineSentInBodyAndUseAirlineOfToken() throws Exception {
        // Arrange
        Aircraft aircraft = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
        when(createAircraftUseCase.execute(any(), any())).thenReturn(aircraft);

        String requestJson = """
                {
                "airlineId": "another-airline", "aircraftModel": "EMBRAER_E175"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/aircrafts")
                        .principal(AIRLINE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());          // 201

        verify(createAircraftUseCase).execute(AircraftModel.EMBRAER_E175, "air-1");
    }

    @Test
    void shouldAnswer404WhenAirlineDoesNotExist() throws Exception {
        // Arrange
        when(createAircraftUseCase.execute(any(), any())).thenThrow(new NoSuchElementException());

        String requestJson = """
                {
                "aircraftModel": "EMBRAER_E175"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/aircrafts")
                        .principal(AIRLINE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());         // 404
    }

    @Test
    void shouldAnswer400WhenAircraftModelIsMissing() throws Exception {
        // Arrange
        String requestJson = """
                {
                
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/aircrafts")
                        .principal(AIRLINE_USER)
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
                "aircraftModel": "BOEING_747"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/aircrafts")
                        .principal(AIRLINE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400, JSON cannot be read

        verify(createAircraftUseCase, never()).execute(any(), any());
    }

    @Test
    void shouldAnswerAircraftsOfTheAirlineInTheToken() throws Exception {
        // Arrange
        Aircraft aircraft = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
        when(listAirlineAircraftsUseCase.execute("air-1")).thenReturn(List.of(aircraft));
        when(mapper.toAircraftResponse(aircraft))
                .thenReturn(new AircraftResponse(aircraft.getId(), "EMBRAER_E175", List.of("1A")));

        // Act & Assert
        mockMvc.perform(get("/api/aircrafts").principal(AIRLINE_USER))
                .andExpect(status().isOk())                // 200
                .andExpect(jsonPath("$[0].id").value(aircraft.getId()))
                .andExpect(jsonPath("$[0].aircraftModel").value("EMBRAER_E175"));

        verify(listAirlineAircraftsUseCase).execute("air-1");  // airline of the token
    }

    @Test
    void shouldAnswer404WhenListingAndAirlineDoesNotExist() throws Exception {
        // Arrange
        when(listAirlineAircraftsUseCase.execute(any())).thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(get("/api/aircrafts").principal(AIRLINE_USER))
                .andExpect(status().isNotFound());         // 404
    }
}
