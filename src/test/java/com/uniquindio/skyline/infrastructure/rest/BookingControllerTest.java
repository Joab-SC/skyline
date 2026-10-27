package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.usecase.*;
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

import static org.mockito.ArgumentMatchers.*;
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

    private static final String PASSENGERS_JSON = """
        {
        "passengers": [
            { "idDocument": "1094", "documentType": "CITIZENSHIP_ID", "firstName": "Laura",
              "lastName1": "Duarte", "birthDate": "2000-05-10", "gender": "FEMALE" }
        ],
        "holderIdDocument": "1094", "email": "laura@mail.com",
        "phoneNumber": "3001234567", "phonePrefix": "COLOMBIA"
        }
        """;

    private static final String SEAT_JSON = """
        {
        "legId": "leg-1", "legSeatId": "seat-1", "passengerId": "1094"
        }
        """;

    private static final String LUGGAGE_JSON = """
        {
        "passengerId": "1094", "quantity": 2
        }
        """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SelectOutboundFlightUseCase selectOutboundFlightUseCase;

    @MockitoBean
    private SelectReturnFlightUseCase selectReturnFlightUseCase;

    @MockitoBean
    private AddPassengerUseCase addPassengerUseCase;

    @MockitoBean
    private SelectLegSeatUseCase selectLegSeatUseCase;

    @MockitoBean
    private BookingMapper mapper;

    @MockitoBean
    private AddLuggageUseCase addLuggageUseCase;

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

    // ---------- PUT /api/bookings/{id}/passengers ----------

    @Test
    void shouldAnswer200WhenPassengersAreAdded() throws Exception {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 200, "flight-1", TripType.ONE_WAY);
        when(addPassengerUseCase.execute(eq("booking-1"), anyList(), any(), any(), any(), any()))
                .thenReturn(booking);

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PASSENGERS_JSON))
                .andExpect(status().isOk());               // 200
    }

    @Test
    void shouldAnswer404WhenBookingForPassengersDoesNotExist() throws Exception {
        // Arrange
        when(addPassengerUseCase.execute(any(), anyList(), any(), any(), any(), any()))
                .thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PASSENGERS_JSON))
                .andExpect(status().isNotFound());         // 404
    }

    @Test
    void shouldAnswer400WhenHolderIsUnderage() throws Exception {
        // Arrange
        when(addPassengerUseCase.execute(any(), anyList(), any(), any(), any(), any()))
                .thenThrow(new DomainRuleException("The holder must be at least 18 years old"));

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PASSENGERS_JSON))
                .andExpect(status().isBadRequest());       // 400
    }

    @Test
    void shouldAnswer400WhenPassengerListIsEmpty() throws Exception {
        // Arrange
        String requestJson = """
            {
            "passengers": [],
            "holderIdDocument": "1094", "email": "laura@mail.com",
            "phoneNumber": "3001234567", "phonePrefix": "COLOMBIA"
            }
            """;

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400 by @NotEmpty

        verify(addPassengerUseCase, never()).execute(any(), anyList(), any(), any(), any(), any());
    }

    @Test
    void shouldAnswer400WhenPassengerHasNoFirstName() throws Exception {
        // Arrange
        String requestJson = """
            {
            "passengers": [
                { "idDocument": "1094", "documentType": "CITIZENSHIP_ID",
                  "lastName1": "Duarte", "birthDate": "2000-05-10", "gender": "FEMALE" }
            ],
            "holderIdDocument": "1094", "email": "laura@mail.com",
            "phoneNumber": "3001234567", "phonePrefix": "COLOMBIA"
            }
            """;

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400 by @Valid on the list

        verify(addPassengerUseCase, never()).execute(any(), anyList(), any(), any(), any(), any());
    }

    @Test
    void shouldAnswer400WhenEmailIsInvalid() throws Exception {
        // Arrange
        String requestJson = """
            {
            "passengers": [
                { "idDocument": "1094", "documentType": "CITIZENSHIP_ID", "firstName": "Laura",
                  "lastName1": "Duarte", "birthDate": "2000-05-10", "gender": "FEMALE" }
            ],
            "holderIdDocument": "1094", "email": "not-an-email",
            "phoneNumber": "3001234567", "phonePrefix": "COLOMBIA"
            }
            """;

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400 by @Email

        verify(addPassengerUseCase, never()).execute(any(), anyList(), any(), any(), any(), any());
    }

    // ---------- PUT /api/bookings/{id}/seats ----------

    @Test
    void shouldAnswer200WhenSeatIsSelected() throws Exception {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 200, "flight-1", TripType.ONE_WAY);
        when(selectLegSeatUseCase.execute("booking-1", "seat-1", "1094", "leg-1")).thenReturn(booking);
        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SEAT_JSON))
                .andExpect(status().isOk());               // 200
    }

    @Test
    void shouldAnswer404WhenLegOrPassengerDoesNotExist() throws Exception {
        // Arrange
        when(selectLegSeatUseCase.execute(any(), any(), any(), any()))
                .thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SEAT_JSON))
                .andExpect(status().isNotFound());         // 404
    }

    @Test
    void shouldAnswer400WhenSeatIsAlreadyOccupied() throws Exception {
        // Arrange
        when(selectLegSeatUseCase.execute(any(), any(), any(), any()))
                .thenThrow(new DomainRuleException("The seat is already occupied"));

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SEAT_JSON))
                .andExpect(status().isBadRequest());       // 400
    }

    @Test
    void shouldAnswer400WhenLegSeatIdIsBlank() throws Exception {
        // Arrange
        String requestJson = """
            {
            "legId": "leg-1", "legSeatId": "", "passengerId": "1094"
            }
            """;

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/seats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400 by @Valid

        verify(selectLegSeatUseCase, never()).execute(any(), any(), any(), any());
    }

    // ---------- PUT /api/bookings/{id}/luggage ----------

    @Test
    void shouldAnswer200WhenLuggageIsAdded() throws Exception {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 200, "flight-1", TripType.ONE_WAY);
        when(addLuggageUseCase.execute("1094", "booking-1", 2)).thenReturn(booking);

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/luggage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LUGGAGE_JSON))
                .andExpect(status().isOk());               // 200
    }

    @Test
    void shouldAnswer404WhenBookingForLuggageDoesNotExist() throws Exception {
        // Arrange
        when(addLuggageUseCase.execute(any(), any(), anyInt())).thenThrow(new NoSuchElementException());

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/luggage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LUGGAGE_JSON))
                .andExpect(status().isNotFound());         // 404
    }

    @Test
    void shouldAnswer400WhenLuggageExceedsAirlineMaximum() throws Exception {
        // Arrange
        when(addLuggageUseCase.execute(any(), any(), anyInt()))
                .thenThrow(new DomainRuleException("The luggage quantity cannot exceed the maximum quantity"));

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/luggage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(LUGGAGE_JSON))
                .andExpect(status().isBadRequest());       // 400
    }

    @Test
    void shouldAnswer400WhenLuggageQuantityIsZero() throws Exception {
        // Arrange
        String requestJson = """
            {
            "passengerId": "1094", "quantity": 0
            }
            """;

        // Act & Assert
        mockMvc.perform(put("/api/bookings/booking-1/luggage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());       // 400 by @Positive

        verify(addLuggageUseCase, never()).execute(any(), any(), anyInt());
    }
}