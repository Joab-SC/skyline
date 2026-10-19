
package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.City;
import com.uniquindio.skyline.domain.valueObject.TripType;
import com.uniquindio.skyline.infrastructure.persistence.BookingRepositoryInMemory;
import com.uniquindio.skyline.infrastructure.persistence.FlightRepositoryInMemory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutboundAndReturnFlightsValidatorTest {

    @Test
    void shouldValidateWhenOutboundDestinationEqualsReturnOrigin() {
        // Arrange
        BookingRepository bookingRepository = new BookingRepositoryInMemory();
        FlightRepository flightRepository = new FlightRepositoryInMemory();

        Airport armenia = new Airport("El Eden", "AXM", City.ARMENIA);
        Airport bogota = new Airport("El Dorado", "BOG", City.BOGOTA);

        Flight outboundFlight = createFlight(
                "outbound-1", armenia, bogota
        );

        Flight returnFlight = createFlight(
                "return-1", bogota, armenia
        );

        Booking booking = Booking.createPendingBooking(
                2, 500.0, "outbound-1", TripType.ROUND_TRIP
        );
        booking.addReturnFlight("return-1", 300.0);

        flightRepository.save(outboundFlight);
        flightRepository.save(returnFlight);
        bookingRepository.save(booking);

        OutboundAndReturnFlightsValidator validator =
                new OutboundAndReturnFlightsValidator(
                        bookingRepository, flightRepository
                );

        // Act & Assert
        assertDoesNotThrow(
                () -> validator.validateOutboundAirportEqualsReturnFlight(
                        booking.getId()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenBookingDoesNotExist() {
        // Arrange
        BookingRepository bookingRepository = new BookingRepositoryInMemory();
        FlightRepository flightRepository = new FlightRepositoryInMemory();

        OutboundAndReturnFlightsValidator validator =
                new OutboundAndReturnFlightsValidator(
                        bookingRepository, flightRepository
                );

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> validator.validateOutboundAirportEqualsReturnFlight(
                        "non-existent-booking"
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenOutboundFlightDoesNotExist() {
        // Arrange
        BookingRepository bookingRepository = new BookingRepositoryInMemory();
        FlightRepository flightRepository = new FlightRepositoryInMemory();

        Booking booking = Booking.createPendingBooking(
                2, 500.0, "non-existent-flight", TripType.ROUND_TRIP
        );
        booking.addReturnFlight("return-1", 300.0);
        bookingRepository.save(booking);

        OutboundAndReturnFlightsValidator validator =
                new OutboundAndReturnFlightsValidator(
                        bookingRepository, flightRepository
                );

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> validator.validateOutboundAirportEqualsReturnFlight(
                        booking.getId()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenBookingHasNoReturnFlight() {
        // Arrange
        BookingRepository bookingRepository = new BookingRepositoryInMemory();
        FlightRepository flightRepository = new FlightRepositoryInMemory();

        Airport armenia = new Airport("El Eden", "AXM", City.ARMENIA);
        Airport bogota = new Airport("El Dorado", "BOG", City.BOGOTA);

        Flight outboundFlight = createFlight(
                "outbound-1", armenia, bogota
        );

        Booking booking = Booking.createPendingBooking(
                2, 500.0, "outbound-1", TripType.ROUND_TRIP
        );

        flightRepository.save(outboundFlight);
        bookingRepository.save(booking);

        OutboundAndReturnFlightsValidator validator =
                new OutboundAndReturnFlightsValidator(
                        bookingRepository, flightRepository
                );

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> validator.validateOutboundAirportEqualsReturnFlight(
                        booking.getId()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightDoesNotExist() {
        // Arrange
        BookingRepository bookingRepository = new BookingRepositoryInMemory();
        FlightRepository flightRepository = new FlightRepositoryInMemory();

        Airport armenia = new Airport("El Eden", "AXM", City.ARMENIA);
        Airport bogota = new Airport("El Dorado", "BOG", City.BOGOTA);

        Flight outboundFlight = createFlight(
                "outbound-1", armenia, bogota
        );

        Booking booking = Booking.createPendingBooking(
                2, 500.0, "outbound-1", TripType.ROUND_TRIP
        );
        booking.addReturnFlight("non-existent-return", 300.0);

        flightRepository.save(outboundFlight);
        bookingRepository.save(booking);

        OutboundAndReturnFlightsValidator validator =
                new OutboundAndReturnFlightsValidator(
                        bookingRepository, flightRepository
                );

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> validator.validateOutboundAirportEqualsReturnFlight(
                        booking.getId()
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightDepartsFromDifferentAirport() {
        // Arrange
        BookingRepository bookingRepository = new BookingRepositoryInMemory();
        FlightRepository flightRepository = new FlightRepositoryInMemory();

        Airport armenia = new Airport("El Eden", "AXM", City.ARMENIA);
        Airport bogota = new Airport("El Dorado", "BOG", City.BOGOTA);
        Airport medellin = new Airport("Jose Maria Cordova", "MDE", City.MEDELLIN);

        Flight outboundFlight = createFlight(
                "outbound-1", armenia, bogota
        );

        Flight returnFlight = createFlight(
                "return-1", medellin, armenia
        );

        Booking booking = Booking.createPendingBooking(
                2, 500.0, "outbound-1", TripType.ROUND_TRIP
        );
        booking.addReturnFlight("return-1", 300.0);

        flightRepository.save(outboundFlight);
        flightRepository.save(returnFlight);
        bookingRepository.save(booking);

        OutboundAndReturnFlightsValidator validator =
                new OutboundAndReturnFlightsValidator(
                        bookingRepository, flightRepository
                );

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> validator.validateOutboundAirportEqualsReturnFlight(
                        booking.getId()
                )
        );
    }

    private Flight createFlight(
            String id,
            Airport origin,
            Airport destination
    ) {
        return Flight.createFlightBasic(
                id,
                "airline-1",
                300.0,
                List.of(),
                origin,
                destination,
                LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0)
        );
    }
}
