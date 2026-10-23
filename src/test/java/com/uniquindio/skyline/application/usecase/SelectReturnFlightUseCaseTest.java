package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.FlightSearchCache;
import com.uniquindio.skyline.domain.service.OutboundAndReturnFlightsValidator;
import com.uniquindio.skyline.domain.service.SeatsAvailableReducer;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectReturnFlightUseCaseTest {

    private static final String ID_OUTBOUND_FLIGHT = "outbound-1";
    private static final String ID_RETURN_FLIGHT = "return-1";

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private FlightSearchCache flightSearchCache;

    @Mock
    private OutboundAndReturnFlightsValidator outboundAndReturnFlightsValidator;

    @Mock
    private SeatsAvailableReducer seatsAvailableReducer;

    @InjectMocks
    private SelectReturnFlightUseCase useCase;

    @Test
    void shouldSelectReturnFlightSuccessfully() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, ID_OUTBOUND_FLIGHT, TripType.ROUND_TRIP);
        Flight outboundFlight = createFlight(Airport.EL_EDEN, Airport.EL_DORADO, 500.0);
        Flight returnFlight = createFlight(Airport.EL_DORADO, Airport.EL_EDEN, 300.0);
        givenBookingAndFlights(booking, outboundFlight, returnFlight);

        // Act
        Booking result = useCase.execute(booking.getId(), ID_RETURN_FLIGHT);

        // Assert
        assertEquals(booking, result);
        assertEquals(Optional.of(returnFlight.getId()), result.getIdReturnFlight());
        assertEquals(1600.0, result.getPrice(), 0.001); // 500 x 2 + 300 x 2

        verify(outboundAndReturnFlightsValidator)
                .validateOutboundAndReturnFlightCompatibility(outboundFlight, returnFlight);
        verify(seatsAvailableReducer).reduceAvailableSeats(returnFlight, 2);
        verify(flightRepository).save(returnFlight);
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowExceptionWhenBookingDoesNotExist() {
        // Arrange
        when(bookingRepository.findById("booking-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                NoSuchElementException.class,
                () -> useCase.execute("booking-1", ID_RETURN_FLIGHT)
        );

        verifyNoInteractions(flightSearchCache, flightRepository,
                outboundAndReturnFlightsValidator, seatsAvailableReducer);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightIsNotInSearchCache() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, ID_OUTBOUND_FLIGHT, TripType.ROUND_TRIP);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(flightSearchCache.findById(ID_RETURN_FLIGHT)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                NoSuchElementException.class,
                () -> useCase.execute(booking.getId(), ID_RETURN_FLIGHT)
        );

        assertTrue(booking.getIdReturnFlight().isEmpty());
        assertEquals(1000.0, booking.getPrice(), 0.001);
        verifyNoInteractions(flightRepository, outboundAndReturnFlightsValidator, seatsAvailableReducer);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenOutboundFlightDoesNotExist() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, ID_OUTBOUND_FLIGHT, TripType.ROUND_TRIP);
        Flight returnFlight = createFlight(Airport.EL_DORADO, Airport.EL_EDEN, 300.0);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(flightSearchCache.findById(ID_RETURN_FLIGHT)).thenReturn(Optional.of(returnFlight));
        when(flightRepository.findById(ID_OUTBOUND_FLIGHT)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                NoSuchElementException.class,
                () -> useCase.execute(booking.getId(), ID_RETURN_FLIGHT)
        );

        assertTrue(booking.getIdReturnFlight().isEmpty());
        verifyNoInteractions(outboundAndReturnFlightsValidator, seatsAvailableReducer);
        verify(flightRepository, never()).save(any(Flight.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldNotReduceSeatsWhenBookingIsOneWay() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, ID_OUTBOUND_FLIGHT, TripType.ONE_WAY);
        Flight outboundFlight = createFlight(Airport.EL_EDEN, Airport.EL_DORADO, 500.0);
        Flight returnFlight = createFlight(Airport.EL_DORADO, Airport.EL_EDEN, 300.0);
        givenBookingAndFlights(booking, outboundFlight, returnFlight);

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> useCase.execute(booking.getId(), ID_RETURN_FLIGHT)
        );

        // Seats must not be lost when the booking cannot take a return flight
        verify(seatsAvailableReducer, never()).reduceAvailableSeats(any(Flight.class), anyInt());
        verify(flightRepository, never()).save(any(Flight.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldNotReduceSeatsWhenAirportValidationFails() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, ID_OUTBOUND_FLIGHT, TripType.ROUND_TRIP);
        Flight outboundFlight = createFlight(Airport.EL_EDEN, Airport.EL_DORADO, 500.0);
        Flight returnFlight = createFlight(Airport.JOSE_MARIA_CORDOVA, Airport.EL_EDEN, 300.0);
        givenBookingAndFlights(booking, outboundFlight, returnFlight);

        doThrow(new DomainRuleException("Return flight must depart from the outbound destination airport"))
                .when(outboundAndReturnFlightsValidator)
                .validateOutboundAndReturnFlightCompatibility(outboundFlight, returnFlight);

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> useCase.execute(booking.getId(), ID_RETURN_FLIGHT)
        );

        assertTrue(booking.getIdReturnFlight().isEmpty());
        assertEquals(1000.0, booking.getPrice(), 0.001);
        verify(seatsAvailableReducer, never()).reduceAvailableSeats(any(Flight.class), anyInt());
        verify(flightRepository, never()).save(any(Flight.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    private void givenBookingAndFlights(Booking booking, Flight outboundFlight, Flight returnFlight) {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(flightSearchCache.findById(ID_RETURN_FLIGHT)).thenReturn(Optional.of(returnFlight));
        when(flightRepository.findById(ID_OUTBOUND_FLIGHT)).thenReturn(Optional.of(outboundFlight));
    }

    private Flight createFlight(Airport origin, Airport destination, double price) {
        Leg leg = Leg.createLeg(origin, destination, LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0), "AIR-001", "airline-1",
                List.of(new Seat("001")), 100, price);
        return Flight.createFlight(List.of(leg.toSummary()));
    }
}
