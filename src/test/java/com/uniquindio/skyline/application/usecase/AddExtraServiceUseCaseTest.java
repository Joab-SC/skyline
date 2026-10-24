package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.DetailExtraService;
import com.uniquindio.skyline.domain.valueObject.FlightScope;
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
class AddExtraServiceUseCaseTest {

    private static final String AIRLINE_ID = "airline-1";
    private static final String SERVICE_ID = "service-1";

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private Airline airline;

    @Mock
    private ExtraService extraService;

    @InjectMocks
    private AddExtraServiceUseCase useCase;

    @Test
    void shouldAddExtraServiceWithOutboundFlightPriceAndSaveBooking() {
        // Arrange
        Flight outboundFlight = createInternationalFlight();
        Booking booking = createBooking(outboundFlight.getId());

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.of(airline));
        when(airline.getExtraService(SERVICE_ID)).thenReturn(Optional.of(extraService));
        when(flightRepository.findById(outboundFlight.getId())).thenReturn(Optional.of(outboundFlight));
        when(extraService.getId()).thenReturn(SERVICE_ID);
        when(extraService.getPrice(FlightScope.INTERNATIONAL)).thenReturn(80.0);

        // Act
        Booking result = useCase.execute(booking.getId(), AIRLINE_ID, SERVICE_ID, 2);

        // Assert
        assertEquals(List.of(new DetailExtraService(SERVICE_ID, 2, 160.0)), result.getDetailExtraServices());
        assertEquals(660.0, result.getPrice(), 0.001);
        verify(extraService).getPrice(FlightScope.INTERNATIONAL);
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldUseNationalPriceWhenOutboundFlightIsNational() {
        // Arrange
        Flight outboundFlight = createNationalFlight();
        Booking booking = createBooking(outboundFlight.getId());

        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.of(airline));
        when(airline.getExtraService(SERVICE_ID)).thenReturn(Optional.of(extraService));
        when(flightRepository.findById(outboundFlight.getId())).thenReturn(Optional.of(outboundFlight));
        when(extraService.getId()).thenReturn(SERVICE_ID);
        when(extraService.getPrice(FlightScope.NATIONAL)).thenReturn(30.0);

        // Act
        Booking result = useCase.execute(booking.getId(), AIRLINE_ID, SERVICE_ID, 2);

        // Assert
        assertEquals(List.of(new DetailExtraService(SERVICE_ID, 2, 60.0)), result.getDetailExtraServices());
        assertEquals(560.0, result.getPrice(), 0.001);
        verify(extraService).getPrice(FlightScope.NATIONAL);
        verify(extraService, never()).getPrice(FlightScope.INTERNATIONAL);
        verify(bookingRepository).save(booking);
    }

    @Test
    void shouldThrowExceptionWhenBookingDoesNotExist() {
        // Arrange
        when(bookingRepository.findById("missing")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
                () -> useCase.execute("missing", AIRLINE_ID, SERVICE_ID, 2));

        verifyNoInteractions(airlineRepository, flightRepository);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenAirlineDoesNotExist() {
        // Arrange
        Booking booking = createBooking("flight-1");
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
                () -> useCase.execute(booking.getId(), AIRLINE_ID, SERVICE_ID, 2));

        verifyNoInteractions(flightRepository);
        verify(bookingRepository, never()).save(any(Booking.class));
        assertTrue(booking.getDetailExtraServices().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenExtraServiceIsNotInAirline() {
        // Arrange
        Booking booking = createBooking("flight-1");
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.of(airline));
        when(airline.getExtraService(SERVICE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
                () -> useCase.execute(booking.getId(), AIRLINE_ID, SERVICE_ID, 2));

        verifyNoInteractions(flightRepository);
        verify(bookingRepository, never()).save(any(Booking.class));
        assertTrue(booking.getDetailExtraServices().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenOutboundFlightDoesNotExist() {
        // Arrange
        Booking booking = createBooking("flight-1");
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.of(airline));
        when(airline.getExtraService(SERVICE_ID)).thenReturn(Optional.of(extraService));
        when(flightRepository.findById("flight-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
                () -> useCase.execute(booking.getId(), AIRLINE_ID, SERVICE_ID, 2));

        verify(extraService, never()).getPrice(any());
        verify(bookingRepository, never()).save(any(Booking.class));
        assertTrue(booking.getDetailExtraServices().isEmpty());
        assertEquals(500.0, booking.getPrice(), 0.001);
    }

    private Booking createBooking(String idOutboundFlight) {
        return Booking.createPendingBooking(1, 500.0, idOutboundFlight, TripType.ONE_WAY);
    }

    // Bogotá -> Miami: airports in different countries, so the flight is international.
    private Flight createInternationalFlight() {
        return createFlight(Airport.EL_DORADO, Airport.MIAMI_INTERNATIONAL);
    }

    // Armenia -> Bogotá: both airports in Colombia, so the flight is national.
    private Flight createNationalFlight() {
        return createFlight(Airport.EL_EDEN, Airport.EL_DORADO);
    }

    private Flight createFlight(Airport origin, Airport destination) {
        Leg leg = Leg.createLeg(origin, destination,
                LocalDateTime.of(2026, 11, 5, 10, 0), LocalDateTime.of(2026, 11, 5, 14, 0),
                "AIR-001", AIRLINE_ID, List.of(new Seat("001")), 100, 500);
        return Flight.createFlight(List.of(leg.toSummary()));
    }
}
