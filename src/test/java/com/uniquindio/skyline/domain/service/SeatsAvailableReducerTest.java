
package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
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
        Leg leg = createLeg();
        legRepository.save(leg);
        Flight flight = Flight.createFlight(List.of(leg.toSummary()));

        SeatsAvailableReducer reducer =
                new SeatsAvailableReducer(legRepository);

        // Act
        reducer.reduceAvailableSeats(flight, 2);

        // Assert
        assertEquals(2, leg.getAvailableSeats());
    }

    @Test
    void shouldThrowExceptionWhenLegDoesNotExist() {
        // Arrange
        LegRepository legRepository = new LegRepositoryInMemory();
        Leg leg = createLeg(); // not saved in the repository
        Flight flight = Flight.createFlight(List.of(leg.toSummary()));

        SeatsAvailableReducer reducer =
                new SeatsAvailableReducer(legRepository);

        // Act and Assert
        assertThrows(
                DomainRuleException.class,
                () -> reducer.reduceAvailableSeats(flight, 2)
        );
    }

    @Test
    void shouldThrowExceptionWhenThereAreNotEnoughSeats() {
        // Arrange
        LegRepository legRepository = new LegRepositoryInMemory();
        Leg leg = createLeg();
        legRepository.save(leg);
        Flight flight = Flight.createFlight(List.of(leg.toSummary()));

        SeatsAvailableReducer reducer =
                new SeatsAvailableReducer(legRepository);

        // Act and Assert
        assertThrows(
                DomainRuleException.class,
                () -> reducer.reduceAvailableSeats(flight, 5)
        );
        assertEquals(4, leg.getAvailableSeats());
    }

    private Leg createLeg() {
        return Leg.createLeg(
                Airport.EL_EDEN,
                Airport.EL_DORADO,
                LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0),
                "aircraft-1",
                "AV",
                List.of(new Seat("001"), new Seat("002"), new Seat("003"), new Seat("004")),
                2000,
                3000
        );
    }
}
