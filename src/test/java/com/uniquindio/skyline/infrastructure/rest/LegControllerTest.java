package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.usecase.CreateLegUseCase;
import com.uniquindio.skyline.application.usecase.GetLegSeatMapUseCase;
import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.infrastructure.rest.mapper.LegMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LegController.class)
public class LegControllerTest {

    private static final String VALID_JSON = """
            {
            "originAirport": "EL_DORADO", "destinationAirport": "EL_EDEN",
            "departureTime": "2026-12-01T08:00:00", "arrivalTime": "2026-12-01T09:00:00",
            "airlineId": "air-1", "aircraftId": "craft-1",
            "luggagePrice": 50, "price": 200
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateLegUseCase createLegUseCase;

    @MockitoBean
    private GetLegSeatMapUseCase getLegSeatMapUseCase;

    @MockitoBean
    private LegMapper mapper;

    @Test
    void shouldAnswerCreateLegWithValidData() throws Exception {
        // Arrange
        Aircraft aircraft = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
        Leg leg = Leg.createLeg(Airport.EL_DORADO, Airport.EL_EDEN,
                LocalDateTime.of(2026, 12, 1, 8, 0), LocalDateTime.of(2026, 12, 1, 9, 0),
                aircraft.getId(), "air-1", aircraft.getSeats(), 50, 200);
        when(createLegUseCase.execute(any(), any(), any(), any(), any(), any(), anyDouble(), anyDouble()))
                .thenReturn(leg);

        // Act & Assert
        mockMvc.perform(post("/api/legs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isCreated())           // 201
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldAnswer404WhenAirlineOrAircraftDoesNotExist() throws Exception {
        // Arrange
        when(createLegUseCase.execute(any(), any(), any(), any(), any(), any(), anyDouble(), anyDouble()))
                .thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(post("/api/legs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isNotFound());         // 404
    }

    @Test
    void shouldAnswer400WhenAircraftHasOverlappingLeg() throws Exception {
        // Arrange
        when(createLegUseCase.execute(any(), any(), any(), any(), any(), any(), anyDouble(), anyDouble()))
                .thenThrow(new DomainRuleException("The aircraft already has a leg in that time range"));

        // Act & Assert
        mockMvc.perform(post("/api/legs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isBadRequest());       // 400
    }

    @Test
    void shouldAnswer400WhenOriginAirportIsMissing() throws Exception {
        // Arrange
        String requestJson = """
                {
                "destinationAirport": "EL_EDEN",
                "departureTime": "2026-12-01T08:00:00", "arrivalTime": "2026-12-01T09:00:00",
                "airlineId": "air-1", "aircraftId": "craft-1",
                "luggagePrice": 50, "price": 200
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/legs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400 by @Valid

        verify(createLegUseCase, never())
                .execute(any(), any(), any(), any(), any(), any(), anyDouble(), anyDouble());
    }

    // ---------- GET /api/legs/{id}/seats ----------

    @Test
    void shouldAnswer200WithSeatMap() throws Exception {
        // Arrange
        Aircraft aircraft = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
        Leg leg = Leg.createLeg(Airport.EL_DORADO, Airport.EL_EDEN,
                LocalDateTime.of(2026, 12, 1, 8, 0), LocalDateTime.of(2026, 12, 1, 9, 0),
                aircraft.getId(), "air-1", aircraft.getSeats(), 50, 200);
        when(getLegSeatMapUseCase.execute("leg-1")).thenReturn(leg);

        // Act & Assert
        mockMvc.perform(get("/api/legs/leg-1/seats"))
                .andExpect(status().isOk());               // 200
    }

    @Test
    void shouldAnswer404WhenLegForSeatMapDoesNotExist() throws Exception {
        // Arrange
        when(getLegSeatMapUseCase.execute("leg-1")).thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(get("/api/legs/leg-1/seats"))
                .andExpect(status().isNotFound());         // 404
    }
}