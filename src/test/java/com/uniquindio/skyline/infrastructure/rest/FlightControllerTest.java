package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.usecase.SearchFlightsUseCase;
import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.infrastructure.rest.mapper.FlightMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlightController.class)
public class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchFlightsUseCase searchFlightsUseCase;

    @MockitoBean
    private FlightMapper mapper;

    @Test
    void shouldAnswer200WithFoundFlights() throws Exception {
        // Arrange
        Aircraft aircraft = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
        Leg leg = Leg.createLeg(Airport.EL_DORADO, Airport.EL_EDEN,
                LocalDateTime.of(2026, 12, 1, 8, 0), LocalDateTime.of(2026, 12, 1, 9, 0),
                aircraft.getId(), "air-1", aircraft.getSeats(), 50, 200);
        Flight flight = Flight.createFlight(List.of(leg.toSummary()));
        when(searchFlightsUseCase.execute(any(), any(), any(), anyInt())).thenReturn(List.of(flight));

        // Act & Assert
        mockMvc.perform(get("/api/flights")
                        .param("origin", "BOG")
                        .param("destination", "AXM")
                        .param("departureDay", "2026-12-01")
                        .param("passengers", "2"))
                .andExpect(status().isOk());               // 200
    }

    @Test
    void shouldAnswer200WhenNoFlightsAreFound() throws Exception {
        // Arrange
        when(searchFlightsUseCase.execute(any(), any(), any(), anyInt())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/flights")
                        .param("origin", "BOG")
                        .param("destination", "AXM")
                        .param("departureDay", "2026-12-01")
                        .param("passengers", "2"))
                .andExpect(status().isOk());               // 200 with []
    }

    @Test
    void shouldAnswer404WhenAirportCodeDoesNotExist() throws Exception {
        // Arrange
        when(searchFlightsUseCase.execute(any(), any(), any(), anyInt()))
                .thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(get("/api/flights")
                        .param("origin", "XXX")
                        .param("destination", "AXM")
                        .param("departureDay", "2026-12-01")
                        .param("passengers", "2"))
                .andExpect(status().isNotFound());         // 404
    }

    @Test
    void shouldAnswer400WhenPassengersIsZero() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/flights")
                        .param("origin", "BOG")
                        .param("destination", "AXM")
                        .param("departureDay", "2026-12-01")
                        .param("passengers", "0"))
                .andExpect(status().isBadRequest());       // 400 by @Valid

        verify(searchFlightsUseCase, never()).execute(any(), any(), any(), anyInt());
    }

    @Test
    void shouldAnswer400WhenOriginIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/flights")
                        .param("destination", "AXM")
                        .param("departureDay", "2026-12-01")
                        .param("passengers", "2"))
                .andExpect(status().isBadRequest());       // 400 by @Valid

        verify(searchFlightsUseCase, never()).execute(any(), any(), any(), anyInt());
    }
}