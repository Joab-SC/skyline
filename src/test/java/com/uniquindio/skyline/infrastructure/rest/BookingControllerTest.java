package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.usecase.SelectOutboundFlightUseCase;
import com.uniquindio.skyline.application.usecase.SelectReturnFlightUseCase;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.TripType;
import com.uniquindio.skyline.infrastructure.rest.mapper.BookingMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    private static final String OUTBOUND_JSON = """
            {
            "outboundFlightId": "flight-1", "numPassengers": 2, "tripType": "ROUND_TRIP"
            }
            """;

    private static final String RETURN_JSON = """
            {
            "returnFlightId": "flight-2"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SelectOutboundFlightUseCase selectOutboundFlightUseCase;

    @MockitoBean
    private SelectReturnFlightUseCase selectReturnFlightUseCase;

    @MockitoBean
    private BookingMapper mapper;

    // ---------- POST /api/bookings ----------

    @Test
    void shouldAnswer201WhenOutboundFlightIsSelected() throws Exception {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 200, "flight-1", TripType.ROUND_TRIP);
        when(selectOutboundFlightUseCase.execute(anyInt(), any(), any())).thenReturn(booking);

        // Act & Assert
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OUTBOUND_JSON))
                .andExpect(status().isCreated())           // 201
                .andExpect(header().exists("Location"));
    }

    @Test
    void shouldAnswer404WhenOutboundFlightIsNotInSearchResults() throws Exception {
        // Arrange
        when(selectOutboundFlightUseCase.execute(anyInt(), any(), any()))
                .thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OUTBOUND_JSON))
                .andExpect(status().isNotFound());         // 404
    }

    @Test
    void shouldAnswer400WhenTripTypeIsMissing() throws Exception {
        // Arrange
        String requestJson = """
                {
                "outboundFlightId": "flight-1", "numPassengers": 2
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400 by @Valid

        verify(selectOutboundFlightUseCase, never()).execute(anyInt(), any(), any());
    }

    // ---------- PUT /api/bookings/{id}/return-flight ----------

    @Test
    void shouldAnswer200WhenReturnFlightIsSelected() throws Exception {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 200, "flight-1", TripType.ROUND_TRIP);
        when(selectReturnFlightUseCase.execute(eq("booking-1"), any())).thenReturn(booking);

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/return-flight")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RETURN_JSON))
                .andExpect(status().isOk());               // 200
    }

    @Test
    void shouldAnswer404WhenBookingDoesNotExist() throws Exception {
        // Arrange
        when(selectReturnFlightUseCase.execute(any(), any())).thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/return-flight")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RETURN_JSON))
                .andExpect(status().isNotFound());         // 404
    }

    @Test
    void shouldAnswer400WhenBookingIsOneWay() throws Exception {
        // Arrange
        when(selectReturnFlightUseCase.execute(any(), any()))
                .thenThrow(new DomainRuleException("A one way booking cannot have a return flight"));

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/return-flight")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(RETURN_JSON))
                .andExpect(status().isBadRequest());       // 400
    }

    @Test
    void shouldAnswer400WhenReturnFlightIdIsBlank() throws Exception {
        // Arrange
        String requestJson = """
                {
                "returnFlightId": ""
                }
                """;

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/return-flight")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400 by @Valid

        verify(selectReturnFlightUseCase, never()).execute(any(), any());
    }
}