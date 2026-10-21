package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.FlightSearchCache;
import com.uniquindio.skyline.domain.service.SeatsAvailableReducer;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectOutboundFlightUseCaseTest {

    private static final String ID_OUTBOUND_FLIGHT = "flight-1";

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightSearchCache flightSearchCache;

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
        TripType tripType = TripType.ONE_WAY;
        givenFlightInCache();
        when(flight.getPrice()).thenReturn(500.0);

        // Act
        Booking booking = selectOutboundFlightUseCase.execute(
                numPassengers, ID_OUTBOUND_FLIGHT, tripType
        );

        // Assert
        assertNotNull(booking);
        assertEquals(numPassengers, booking.getNumPassengers());
        assertEquals(1000, booking.getPrice());
        assertEquals(ID_OUTBOUND_FLIGHT, booking.getIdOutboundFlight());
        assertEquals(tripType, booking.getTripType());
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());

        verify(seatsAvailableReducer).reduceAvailableSeats(flight, numPassengers);
        verify(flightRepository).save(flight);
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldGenerateUniqueBookingIds() {
        // Arrange
        givenFlightInCache();

        // Act
        Booking firstBooking = selectOutboundFlightUseCase.execute(1,  ID_OUTBOUND_FLIGHT, TripType.ONE_WAY);
        Booking secondBooking = selectOutboundFlightUseCase.execute(1,  ID_OUTBOUND_FLIGHT, TripType.ONE_WAY);

        // Assert
        assertNotEquals(firstBooking.getId(), secondBooking.getId());
        verify(bookingRepository, times(2)).save(any(Booking.class));
    }

    @Test
    void shouldNotSaveBookingNorReduceSeatsWhenCreationFails() {
        // Arrange
        givenFlightInCache();

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> selectOutboundFlightUseCase.execute(0,  ID_OUTBOUND_FLIGHT, TripType.ONE_WAY)
        );

        verifyNoInteractions(seatsAvailableReducer, flightRepository, bookingRepository);
    }

    @Test
    void shouldThrowWhenOutboundFlightIsNotInSearchCache() {
        // Arrange
        when(flightSearchCache.findById("missing")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                NoSuchElementException.class,
                () -> selectOutboundFlightUseCase.execute(1,  "missing", TripType.ONE_WAY)
        );

        verifyNoInteractions(seatsAvailableReducer, flightRepository, bookingRepository);
    }

    @Test
    void shouldNotSaveAnythingWhenThereAreNotEnoughSeats() {
        // Arrange
        givenFlightInCache();
        doThrow(new DomainRuleException("There's not enough available seats for the num of passengers"))
                .when(seatsAvailableReducer).reduceAvailableSeats(flight, 2);

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> selectOutboundFlightUseCase.execute(2,  ID_OUTBOUND_FLIGHT, TripType.ONE_WAY));

        verifyNoInteractions(flightRepository, bookingRepository);
    }

    @Test
    void shouldReduceSeatsBeforeSavingFlightAndBooking() {
        // Arrange
        givenFlightInCache();

        // Act
        Booking booking = selectOutboundFlightUseCase.execute(2,  ID_OUTBOUND_FLIGHT, TripType.ONE_WAY);

        // Assert
        InOrder inOrder = inOrder(seatsAvailableReducer, flightRepository, bookingRepository);
        inOrder.verify(seatsAvailableReducer).reduceAvailableSeats(flight, 2);
        inOrder.verify(flightRepository).save(flight);
        inOrder.verify(bookingRepository).save(booking);
    }

    private void givenFlightInCache() {
        when(flightSearchCache.findById(ID_OUTBOUND_FLIGHT)).thenReturn(Optional.of(flight));
        when(flight.getId()).thenReturn(ID_OUTBOUND_FLIGHT);
    }
}
