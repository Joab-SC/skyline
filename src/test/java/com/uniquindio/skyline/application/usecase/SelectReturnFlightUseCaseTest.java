
package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.service.OutboundAndReturnFlightsValidator;
import com.uniquindio.skyline.domain.service.SeatsAvailableReducer;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.City;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectReturnFlightUseCaseTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private OutboundAndReturnFlightsValidator outboundAndReturnFlightsValidator;

    @Mock
    private SeatsAvailableReducer seatsAvailableReducer;

    @InjectMocks
    private SelectReturnFlightUseCase useCase;

    @Test
    void shouldSelectReturnFlightSuccessfully() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2, 500.0, "outbound-1", TripType.ROUND_TRIP
        );

        Flight returnFlight = createFlight(300.0);

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        when(flightRepository.findById("return-1"))
                .thenReturn(Optional.of(returnFlight));

        // Act
        Booking result = useCase.execute(booking.getId(), "return-1");

        // Assert
        assertNotNull(result);
        assertEquals(booking, result);
        assertEquals(1600.0, result.getPrice(), 0.001);

        verify(bookingRepository).findById(booking.getId());
        verify(flightRepository).findById("return-1");
        verify(bookingRepository, never()).save(any(Booking.class));

        verify(outboundAndReturnFlightsValidator)
                .validateOutboundAirportEqualsReturnFlight(booking.getId());

        verify(seatsAvailableReducer)
                .reduceAvailableSeats("return-1", 2);
    }

    @Test
    void shouldThrowExceptionWhenBookingDoesNotExist() {
        // Arrange
        when(bookingRepository.findById("booking-1"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> useCase.execute("booking-1", "return-1")
        );

        verify(flightRepository, never()).findById(anyString());
        verifyNoInteractions(outboundAndReturnFlightsValidator);
        verifyNoInteractions(seatsAvailableReducer);
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightDoesNotExist() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2, 500.0, "outbound-1", TripType.ROUND_TRIP
        );

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        when(flightRepository.findById("return-1"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> useCase.execute(booking.getId(), "return-1")
        );

        assertTrue(booking.getIdReturnFlight().isEmpty());
        assertEquals(1000.0, booking.getPrice(), 0.001);

        verifyNoInteractions(outboundAndReturnFlightsValidator);
        verifyNoInteractions(seatsAvailableReducer);
    }

    @Test
    void shouldNotContinueWhenBookingCannotAddReturnFlight() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2, 500.0, "outbound-1", TripType.ONE_WAY
        );

        Flight returnFlight = createFlight(300.0);

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        when(flightRepository.findById("return-1"))
                .thenReturn(Optional.of(returnFlight));

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> useCase.execute(booking.getId(), "return-1")
        );

        verifyNoInteractions(outboundAndReturnFlightsValidator);
        verifyNoInteractions(seatsAvailableReducer);
    }

    @Test
    void shouldNotReduceSeatsWhenValidationFails() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2, 500.0, "outbound-1", TripType.ROUND_TRIP
        );

        Flight returnFlight = createFlight(300.0);

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(booking));

        when(flightRepository.findById("return-1"))
                .thenReturn(Optional.of(returnFlight));

        doThrow(new DomainRuleException(
                "Return flight must depart from the outbound destination airport"
        )).when(outboundAndReturnFlightsValidator)
                .validateOutboundAirportEqualsReturnFlight(booking.getId());

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> useCase.execute(booking.getId(), "return-1")
        );

        verify(outboundAndReturnFlightsValidator)
                .validateOutboundAirportEqualsReturnFlight(booking.getId());

        verify(seatsAvailableReducer, never())
                .reduceAvailableSeats(anyString(), anyInt());
    }

    private Flight createFlight(double price) {
        Airport origin = Airport.EL_EDEN;
        Airport destination = Airport.EL_DORADO;
        Leg leg = Leg.createLeg(origin, destination, LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0), "AIR-001", "airline-1",
                List.of(new Seat("001")), 100, price);
        return Flight.createFlight(leg.getId(), leg.toSummary());
    }
}
