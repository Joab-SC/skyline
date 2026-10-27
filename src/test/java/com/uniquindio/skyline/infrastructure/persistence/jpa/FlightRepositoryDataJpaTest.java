package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.entity.Leg.LegSummary;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.FlightScope;
import com.uniquindio.skyline.domain.valueObject.Layover;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.FlightPersistenceMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({FlightRepositoryJpa.class, FlightPersistenceMapper.class})
class FlightRepositoryDataJpaTest {

    private static final String AIRLINE_ID = "airline-avianca";
    private static final String AIRCRAFT_ID = "aircraft-a320-01";
    private static final LocalDateTime DAY = LocalDateTime.of(2026, 12, 10, 0, 0);

    @Autowired
    private FlightRepository flightRepository;


    private LegSummary createLeg(Airport origin, Airport destination,
                                 LocalDateTime departure, LocalDateTime arrival, double price) {
        return Leg.createLeg(origin, destination, departure, arrival, AIRCRAFT_ID, AIRLINE_ID,
                List.of(new Seat("1A")), 80000, price).toSummary();
    }

    // Flight BOG -> MDE -> CLO -> CTG with three legs and two layovers (MDE 2h, CLO 1h30).
    private Flight createFlight() {
        return Flight.createFlight(List.of(
                createLeg(Airport.EL_DORADO, Airport.JOSE_MARIA_CORDOVA,
                        DAY.withHour(6), DAY.withHour(7), 200000),
                createLeg(Airport.JOSE_MARIA_CORDOVA, Airport.ALFONSO_BONILLA_ARAGON,
                        DAY.withHour(9), DAY.withHour(10), 200000),
                createLeg(Airport.ALFONSO_BONILLA_ARAGON, Airport.RAFAEL_NUNEZ,
                        DAY.withHour(11).withMinute(30), DAY.withHour(13), 250000)));
    }

    // Flight BOG -> MDE -> CLO -> CTG -> BAQ with four legs and three layovers of different durations.
    private Flight createFlightWithFourLegs() {
        return Flight.createFlight(List.of(
                createLeg(Airport.EL_DORADO, Airport.JOSE_MARIA_CORDOVA,
                        DAY.withHour(6), DAY.withHour(7), 200000),
                createLeg(Airport.JOSE_MARIA_CORDOVA, Airport.ALFONSO_BONILLA_ARAGON,
                        DAY.withHour(9), DAY.withHour(10), 200000),
                createLeg(Airport.ALFONSO_BONILLA_ARAGON, Airport.RAFAEL_NUNEZ,
                        DAY.withHour(11).withMinute(30), DAY.withHour(13), 250000),
                createLeg(Airport.RAFAEL_NUNEZ, Airport.ERNESTO_CORTISSOZ,
                        DAY.withHour(16), DAY.withHour(17), 150000)));
    }


    private Flight saveAndRecover(Flight flight) {
        flightRepository.save(flight);
        return flightRepository.findById(flight.getId()).orElseThrow();
    }

    @Test
    void shouldSaveAndRecoverFlightWithItsData() {
        // Arrange
        Flight flight = createFlight();

        // Act
        Flight recovered = saveAndRecover(flight);

        // Assert
        assertEquals(flight.getId(), recovered.getId());
        assertEquals(AIRLINE_ID, recovered.getAirlineId());
        assertEquals(650000, recovered.getPrice());
        assertEquals(Airport.EL_DORADO, recovered.getOriginAirport());
        assertEquals(Airport.RAFAEL_NUNEZ, recovered.getDestinationAirport());
        assertEquals(DAY.withHour(6), recovered.getDepartureTime());
        assertEquals(DAY.withHour(13), recovered.getArrivalTime());
        assertEquals(FlightScope.NATIONAL, recovered.getFlightScope());
    }


    @Test
    void shouldKeepTheSameLegsAndLayoversInOrder() {
        // Arrange
        Flight flight = createFlightWithFourLegs();

        // Act
        Flight recovered = saveAndRecover(flight);

        // Assert
        assertEquals(flight.getIdLegs(), recovered.getIdLegs());
        assertEquals(flight.getLayovers(), recovered.getLayovers());
    }

    @Test
    void shouldReturnEmptyWhenFlightDoesNotExist() {
        // Arrange
        flightRepository.save(createFlight());

        // Act
        Optional<Flight> result = flightRepository.findById("flight-does-not-exist");

        // Assert
        assertTrue(result.isEmpty());
    }
}
