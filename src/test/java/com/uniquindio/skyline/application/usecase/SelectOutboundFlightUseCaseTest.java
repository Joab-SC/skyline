package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.service.SeatsAvailableReducer;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectOutboundFlightUseCaseTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private SeatsAvailableReducer seatsAvailableReducer;

    @Mock
    private Flight flight;

    @InjectMocks
    private SelectOutboundFlightUseCase selectOutboundFlightUseCase;

    @Test
    void shouldCreatePendingBookingAndSaveIt() {
        // Arrange
        int numPassengers = 2;
        double price = 500.0;
        String idOutboundFlight = "flight-1";
        TripType tripType = TripType.ONE_WAY;

        when(flightRepository.findById(idOutboundFlight)).thenReturn(Optional.of(flight));

        // Act
        Booking booking = selectOutboundFlightUseCase.execute(
                numPassengers, price, idOutboundFlight, tripType
        );

        // Assert
        assertNotNull(booking);
        assertEquals(numPassengers, booking.getNumPassengers());
        assertEquals(price, booking.getPrice());
        assertEquals(idOutboundFlight, booking.getIdOutboundFlight());
        assertEquals(tripType, booking.getTripType());
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());

        verify(seatsAvailableReducer).reduceAvailableSeats(idOutboundFlight, numPassengers);
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldGenerateUniqueBookingIds() {
        // Arrange
        String idOutboundFlight = "flight-1";
        when(flightRepository.findById(idOutboundFlight)).thenReturn(Optional.of(flight));

        // Act
        Booking firstBooking = selectOutboundFlightUseCase.execute(1, 300.0, idOutboundFlight, TripType.ONE_WAY);
        Booking secondBooking = selectOutboundFlightUseCase.execute(1, 300.0, idOutboundFlight, TripType.ONE_WAY);

        // Assert
        assertNotEquals(firstBooking.getId(), secondBooking.getId());
        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    void shouldNotSaveBookingNorReduceSeatsWhenCreationFails() {
        // Arrange
        String idOutboundFlight = "flight-1";
        when(flightRepository.findById(idOutboundFlight)).thenReturn(Optional.of(flight));

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> selectOutboundFlightUseCase.execute(0, 500.0, idOutboundFlight, TripType.ONE_WAY)
        );

        verify(seatsAvailableReducer, never()).reduceAvailableSeats(anyString(), anyInt());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowWhenOutboundFlightDoesNotExist() {
        // Arrange
        when(flightRepository.findById("missing")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> selectOutboundFlightUseCase.execute(1, 500.0, "missing", TripType.ONE_WAY)
        );

        verifyNoInteractions(seatsAvailableReducer, bookingRepository);
    }

    @Test
    void shouldReduceSeatsBeforeSavingBooking() {
        // Arrange
        when(flightRepository.findById("flight-1")).thenReturn(Optional.of(flight));

        // Act
        Booking booking = selectOutboundFlightUseCase.execute(2, 500.0, "flight-1", TripType.ONE_WAY);

        // Assert
        InOrder inOrder = inOrder(seatsAvailableReducer, bookingRepository);
        inOrder.verify(seatsAvailableReducer).reduceAvailableSeats("flight-1", 2);
        inOrder.verify(bookingRepository).save(booking);
    }
}