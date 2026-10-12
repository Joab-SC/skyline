package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.City;
import com.uniquindio.skyline.domain.valueObject.Flight;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.infrastructure.persistence.LegRepositoryInMemory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FlightSearcherTest {

    private static final LocalDate DAY = LocalDate.of(2026, 11, 10);
    private static final int MAX_LEGS = 5;

    private final List<Seat> seats = List.of(new Seat("A1"), new Seat("A2"));

    private LegRepositoryInMemory legRepository;
    private FlightSearcher flightSearcher;

    private Airport armenia;
    private Airport bogota;
    private Airport medellin;
    private Airport cali;
    private Airport cartagena;
    private Airport barranquilla;
    private Airport miami;
    private Airport madrid;

    @BeforeEach
    void setUp() {
        legRepository = new LegRepositoryInMemory();
        flightSearcher = new FlightSearcher(legRepository);

        armenia = new Airport("El Eden", "AXM", City.ARMENIA);
        bogota = new Airport("El Dorado", "BOG", City.BOGOTA);
        medellin = new Airport("Jose Maria Cordova", "MDE", City.MEDELLIN);
        cali = new Airport("Alfonso Bonilla Aragon", "CLO", City.CALI);
        cartagena = new Airport("Rafael Nunez", "CTG", City.CARTAGENA);
        barranquilla = new Airport("Ernesto Cortissoz", "BAQ", City.BARRANQUILLA);
        miami = new Airport("Miami International", "MIA", City.MIAMI);
        madrid = new Airport("Adolfo Suarez Madrid-Barajas", "MAD", City.MADRID);
    }

    @Test
    void withNoPathsReturnsEmptyList() {
        saveLeg(armenia, bogota, 6, 8, "AIR1");
        saveLeg(medellin, madrid, 10, 20, "AIR1");

        List<Flight> flights = flightSearcher.searchFlights(armenia, madrid, DAY);

        assertTrue(flights.isEmpty());
    }

    @Test
    void resultsAreSortedByFewestLayoversFirst() {
        // Legs are deliberately saved from the longest path to the shortest
        saveLeg(cartagena, miami, 8, 9, "AIR1");
        saveLeg(bogota, cartagena, 4, 5, "AIR1");
        saveLeg(bogota, miami, 5, 6, "AIR1");
        saveLeg(armenia, bogota, 1, 2, "AIR1");
        saveLeg(armenia, miami, 6, 8, "AIR1");

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY);
        for (int i = 1; i<flights.size(); i++) {
            Flight previous = flights.get(i-1);
            Flight current = flights.get(i);
            assertTrue(current.getLayovers().size() >= previous.getLayovers().size());
        }
    }

    @Test
    void pathWithMoreThanFiveLegsReturnsEmptyList() {
        saveLeg(armenia, bogota, 0, 1, "AIR1");
        saveLeg(bogota, medellin, 3, 4, "AIR1");
        saveLeg(medellin, cali, 6, 7, "AIR1");
        saveLeg(cali, cartagena, 9, 10, "AIR1");
        saveLeg(cartagena, barranquilla, 12, 13, "AIR1");
        saveLeg(barranquilla, miami, 15, 16, "AIR1");

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY);

        assertTrue(flights.isEmpty());
    }

    @Test
    void pathMixingAirlinesReturnsEmptyList() {
        saveLeg(armenia, bogota, 6, 8, "AIR1");
        saveLeg(bogota, miami, 12, 14, "AIR2");

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY);

        assertTrue(flights.isEmpty());
    }

    @Test
    void returnsAllValidPossibleFlights() {
        // AIR1
        saveLeg(armenia, bogota, 1, 2, "AIR1");
        saveLeg(bogota, miami, 5, 6, "AIR1");
        saveLeg(armenia, miami, 7, 9, "AIR1");
        saveLeg(bogota, cartagena, 4, 5, "AIR1");
        saveLeg(cartagena, miami, 8, 9, "AIR1");
        // AIR2
        saveLeg(armenia, bogota, 2, 3, "AIR2");
        saveLeg(bogota, miami, 7, 8, "AIR2");

        // Noise that should NOT produce flights
        saveLeg(bogota, miami, 1, 3, "AIR1");        // Overlaps with the first AIR1 leg
        saveLeg(armenia, medellin, 3, 4, "AIR1");    // Does not reach Miami
        saveLeg(armenia, miami, 1, 6, 8, "AIR1");    // Takes place on another day

        // Expected flights (Armenia -> Miami, on DAY):
        // 1. AIR1: ARM -> MIA                  (direct)
        // 2. AIR1: ARM -> BOG -> MIA           (1 layover)
        // 3. AIR1: ARM -> BOG -> CTG -> MIA    (2 layovers)
        // 4. AIR2: ARM -> BOG -> MIA           (1 layover)
        int expectedFlights = 4;

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY);

        assertEquals(expectedFlights, flights.size());
    }

    @Test
    void overlappingLegsReturnEmptyList() {
        saveLeg(armenia, bogota, 6, 8, "AIR1");
        saveLeg(bogota, miami, 7, 9, "AIR1");

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY);

        assertTrue(flights.isEmpty());
    }

    @Test
    void departureDateOfResultsMatchesRequestedDate() {
        saveLeg(armenia, miami, 0, 6, 8, "AIR1");
        saveLeg(armenia, miami, 1, 6, 8, "AIR1");
        saveLeg(armenia, bogota, 2, 6, 8, "AIR1");
        saveLeg(bogota, miami, 2, 10, 12, "AIR1");

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY);

        for (Flight flight : flights) {
            assertEquals(DAY, flight.getDepartureTime().toLocalDate());
        }
    }

    @Test
    void originAndDestinationOfResultsMatchRequestedAirports() {
        saveLeg(armenia, miami, 6, 8, "AIR1");
        saveLeg(armenia, bogota, 1, 2, "AIR1");
        saveLeg(bogota, miami, 5, 6, "AIR1");
        saveLeg(armenia, medellin, 3, 4, "AIR1");
        saveLeg(medellin, cali, 8, 9, "AIR1");
        saveLeg(bogota, cartagena, 4, 5, "AIR1");

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY);

        for (Flight flight : flights) {
            assertEquals(armenia, flight.getOriginAirport());
            assertEquals(miami, flight.getDestinationAirport());
        }
    }

    @Test
    void allResultsHaveAtMostFiveLegs() {
        saveLeg(armenia, bogota, 0, 1, "AIR1");
        saveLeg(bogota, medellin, 3, 4, "AIR1");
        saveLeg(medellin, cali, 6, 7, "AIR1");
        saveLeg(cali, cartagena, 9, 10, "AIR1");
        saveLeg(cartagena, miami, 12, 13, "AIR1");
        saveLeg(cartagena, barranquilla, 12, 13, "AIR1");
        saveLeg(barranquilla, miami, 15, 16, "AIR1");

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY);

        for (Flight flight : flights) {
            assertTrue(flight.getIdLegs().size() <= MAX_LEGS);
        }
    }

    private Leg saveLeg(Airport origin, Airport destination,
                        int departureHour, int arrivalHour, String airlineId) {
        return saveLeg(origin, destination, 0, departureHour, arrivalHour, airlineId);
    }

    private Leg saveLeg(Airport origin, Airport destination, int dayOffset,
                        int departureHour, int arrivalHour, String airlineId) {
        LocalDateTime departure = DAY.plusDays(dayOffset).atTime(departureHour, 0);
        LocalDateTime arrival = DAY.plusDays(dayOffset).atTime(arrivalHour, 0);

        Leg leg = Leg.createLeg(origin, destination, departure, arrival,
                "AIRCRAFT-1", airlineId, seats, 20000, 150000);
        legRepository.save(leg);
        return leg;
    }
}