package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.LuggageDetail;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.domain.valueObject.TripType;
import com.uniquindio.skyline.infrastructure.persistence.AirlineRepositoryInMemory;
import com.uniquindio.skyline.infrastructure.persistence.FlightRepositoryInMemory;
import com.uniquindio.skyline.infrastructure.persistence.LegRepositoryInMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LuggageDetailCreatorTest {

    private static final LocalDate DAY = LocalDate.of(2026, 12, 1);
    private final List<Seat> seats = List.of(new Seat("1A"), new Seat("1B"));

    private FlightRepositoryInMemory flightRepository;
    private LegRepositoryInMemory legRepository;
    private AirlineRepositoryInMemory airlineRepository;
    private LuggageDetailCreator luggageDetailCreator;
    private Airline airline;

    @BeforeEach
    void setUp() {
        flightRepository = new FlightRepositoryInMemory();
        legRepository = new LegRepositoryInMemory();
        airlineRepository = new AirlineRepositoryInMemory();
        luggageDetailCreator = new LuggageDetailCreator(flightRepository, legRepository, airlineRepository);

        airline = Airline.createAirline("Skyline Air", 3, 0);
        airlineRepository.save(airline);
    }

    private Leg createLeg(Airport origin, Airport destination, int departureHour, int arrivalHour,
                          String airlineId, double luggagePrice) {
        return Leg.createLeg(origin, destination, DAY.atTime(departureHour, 0), DAY.atTime(arrivalHour, 0),
                "aircraft-1", airlineId, seats, luggagePrice, 100.0);
    }

    private Leg saveLeg(Airport origin, Airport destination, int departureHour, int arrivalHour, double luggagePrice) {
        Leg leg = createLeg(origin, destination, departureHour, arrivalHour, airline.getId(), luggagePrice);
        legRepository.save(leg);
        return leg;
    }

    private Flight saveFlight(Leg firstLeg, Leg... nextLegs) {
        List<Leg.LegSummary> legs = new ArrayList<>();
        legs.add(firstLeg.toSummary());
        for (Leg leg : nextLegs) {
            legs.add(leg.toSummary());
        }
        Flight flight = Flight.createFlight(legs);
        flightRepository.save(flight);
        return flight;
    }

    @Test
    void shouldCreateDetailForEveryLegOfOneWayBooking() {
        // Arrange
        Leg firstLeg = saveLeg(Airport.EL_EDEN, Airport.EL_DORADO, 6, 7, 30.0);
        Leg secondLeg = saveLeg(Airport.EL_DORADO, Airport.JOSE_MARIA_CORDOVA, 9, 10, 20.0);
        Flight flight = saveFlight(firstLeg, secondLeg);
        Booking booking = Booking.createPendingBooking(1, 500.0, flight.getId(), TripType.ONE_WAY);

        // Act
        LuggageDetail detail = luggageDetailCreator.createDetail(booking, 2);

        // Assert
        assertEquals(List.of(firstLeg.getLuggage().getId(), secondLeg.getLuggage().getId()), detail.luggageIds());
        assertEquals(2, detail.quantity());
        assertEquals(100.0, detail.subTotal(), 0.001);
        assertEquals(3, detail.maxQuantity());
    }

    @Test
    void shouldIncludeReturnFlightLegsInRoundTripBooking() {
        // Arrange
        Leg outboundLeg = saveLeg(Airport.EL_EDEN, Airport.EL_DORADO, 6, 7, 30.0);
        Leg returnLeg = saveLeg(Airport.EL_DORADO, Airport.EL_EDEN, 18, 19, 20.0);
        Flight outboundFlight = saveFlight(outboundLeg);
        Flight returnFlight = saveFlight(returnLeg);
        Booking booking = Booking.createPendingBooking(1, 500.0, outboundFlight.getId(), TripType.ROUND_TRIP);
        booking.addReturnFlight(returnFlight.getId(), returnFlight.getPrice());

        // Act
        LuggageDetail detail = luggageDetailCreator.createDetail(booking, 1);

        // Assert
        assertEquals(List.of(outboundLeg.getLuggage().getId(), returnLeg.getLuggage().getId()), detail.luggageIds());
        assertEquals(50.0, detail.subTotal(), 0.001);
    }




    @Test
    void shouldThrowExceptionWhenFlightIsNotFound() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 500.0, "missing-flight", TripType.ONE_WAY);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> luggageDetailCreator.createDetail(booking, 1));
    }

    @Test
    void shouldThrowExceptionWhenLegIsNotFound() {
        // Arrange
        Leg unsavedLeg = createLeg(Airport.EL_EDEN, Airport.EL_DORADO, 6, 7, airline.getId(), 30.0);
        Flight flight = saveFlight(unsavedLeg);
        Booking booking = Booking.createPendingBooking(1, 500.0, flight.getId(), TripType.ONE_WAY);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> luggageDetailCreator.createDetail(booking, 1));
    }

    @Test
    void shouldThrowExceptionWhenAirlineIsNotFound() {
        // Arrange
        Leg leg = createLeg(Airport.EL_EDEN, Airport.EL_DORADO, 6, 7, "missing-airline", 30.0);
        legRepository.save(leg);
        Flight flight = saveFlight(leg);
        Booking booking = Booking.createPendingBooking(1, 500.0, flight.getId(), TripType.ONE_WAY);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> luggageDetailCreator.createDetail(booking, 1));
    }
}
