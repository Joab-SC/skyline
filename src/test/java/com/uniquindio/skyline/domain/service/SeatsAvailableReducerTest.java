
package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.City;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.infrastructure.persistence.FlightRepositoryInMemory;
import com.uniquindio.skyline.infrastructure.persistence.LegRepositoryInMemory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SeatsAvailableReducerTest {

    @Test
    void shouldReduceAvailableSeatsForFlightLegs() {
        // Arrange
        LegRepository legRepository = new LegRepositoryInMemory();
        FlightRepository flightRepository = new FlightRepositoryInMemory();

        Airport origin = Airport.EL_EDEN;
        Airport destination = Airport.EL_DORADO;

        List<Seat> aircraftSeats = List.of(
                new Seat("001"),
                new Seat("002"),
                new Seat("003"),
                new Seat("004")
        );
        Leg leg = Leg.createLeg(
                origin,
                destination,
                LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0),
                "aircraft-1",
                "AV",
                aircraftSeats,
                2000,
                3000
        );

        legRepository.save(leg);

        Flight flight = Flight.createFlight(leg.getId(), leg.toSummary());


        flightRepository.save(flight);

        SeatsAvailableReducer reducer =
                new SeatsAvailableReducer(legRepository, flightRepository);

        // Act
        reducer.reduceAvailableSeats(flight.getId(), 2);

        // Assert
        assertEquals(2, leg.getAvailableSeats());
    }

    @Test
    void shouldThrowExceptionWhenFlightDoesNotExist() {
        // Arrange
        LegRepository legRepository = new LegRepositoryInMemory();
        FlightRepository flightRepository = new FlightRepositoryInMemory();

        SeatsAvailableReducer reducer =
                new SeatsAvailableReducer(legRepository, flightRepository);

        // Act and Assert
        assertThrows(
                DomainRuleException.class,
                () -> reducer.reduceAvailableSeats("non-existent-flight", 2)
        );
    }

    @Test
    void shouldThrowExceptionWhenLegDoesNotExist() {
        // Arrange
        LegRepository legRepository = new LegRepositoryInMemory();
        FlightRepository flightRepository = new FlightRepositoryInMemory();

        Airport origin = Airport.EL_EDEN;
        Airport destination = Airport.EL_DORADO;

        Leg leg = Leg.createLeg(
                origin,
                destination,
                LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0),
                "aircraft-1",
                "AV",
                List.of(new Seat("001"), new Seat("002"), new Seat("003"), new Seat("004")),
                2000,
                3000
        );
        Flight flight = Flight.createFlight(leg.getId(), leg.toSummary());

        flightRepository.save(flight);

        SeatsAvailableReducer reducer =
                new SeatsAvailableReducer(legRepository, flightRepository);

        // Act and Assert
        assertThrows(
                DomainRuleException.class,
                () -> reducer.reduceAvailableSeats(flight.getId(), 2)
        );
    }
}
