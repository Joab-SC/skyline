package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.entity.Flight;
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
    private static final int MAX_LEGS = 4;
    private static final int PASSENGERS = 2;

    private final List<Seat> seats2 = List.of(new Seat("A1"), new Seat("A2"));
    private final List<Seat> seats3 = List.of(new Seat("A1"), new Seat("A2"), new Seat("A3"));
    private final List<Seat> seats4 = List.of(new Seat("A1"), new Seat("A2"), new Seat("A3"), new Seat("A4"));

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

        armenia = Airport.EL_EDEN;
        bogota = Airport.EL_DORADO;
        medellin = Airport.JOSE_MARIA_CORDOVA;
        cali = Airport.ALFONSO_BONILLA_ARAGON;
        cartagena = Airport.RAFAEL_NUNEZ;
        barranquilla = Airport.ERNESTO_CORTISSOZ;
        miami = Airport.MIAMI_INTERNATIONAL;
        madrid = Airport.ADOLFO_SUAREZ_MADRID;
    }

    @Test
    void withNoPathsReturnsEmptyList() {
        saveLeg(armenia, bogota, 6, 8, "AIR1", seats2);
        saveLeg(medellin, madrid, 10, 20, "AIR1", seats2);

        List<Flight> flights = flightSearcher.searchFlights(armenia, madrid, DAY, PASSENGERS);

        assertTrue(flights.isEmpty());
    }

    @Test
    void resultsAreSortedByFewestLayoversFirst() {
        saveLeg(cartagena, miami, 8, 9, "AIR1", seats2);
        saveLeg(bogota, cartagena, 4, 5, "AIR1", seats2);
        saveLeg(bogota, miami, 5, 6, "AIR1", seats2);
        saveLeg(armenia, bogota, 1, 2, "AIR1", seats2);
        saveLeg(armenia, miami, 6, 8, "AIR1", seats2);

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY, PASSENGERS);
        for (int i = 1; i < flights.size(); i++) {
            Flight previous = flights.get(i - 1);
            Flight current = flights.get(i);
            assertTrue(current.getLayovers().size() >= previous.getLayovers().size());
        }
    }

    @Test
    void pathWithMoreThanFourLegsReturnsEmptyList() {
        saveLeg(armenia, bogota, 0, 1, "AIR1", seats2);
        saveLeg(bogota, medellin, 3, 4, "AIR1", seats2);
        saveLeg(medellin, cali, 6, 7, "AIR1", seats2);
        saveLeg(cali, cartagena, 9, 10, "AIR1", seats2);
        saveLeg(cartagena, miami, 12, 13, "AIR1", seats2);


        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY, PASSENGERS);

        assertTrue(flights.isEmpty());
    }

    @Test
    void pathMixingAirlinesReturnsEmptyList() {
        saveLeg(armenia, bogota, 6, 8, "AIR1", seats2);
        saveLeg(bogota, miami, 12, 14, "AIR2", seats2);

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY, PASSENGERS);

        assertTrue(flights.isEmpty());
    }

    @Test
    void returnsAllValidPossibleFlights() {
        saveLeg(armenia, miami, 7, 9, "AIR1", seats2);
        saveLeg(armenia, bogota, 1, 2, "AIR1", seats2);
        saveLeg(bogota, miami, 5, 6, "AIR1", seats2);
        saveLeg(bogota, cartagena, 4, 5, "AIR1", seats2);
        saveLeg(cartagena, miami, 8, 9, "AIR1", seats2);

        saveLeg(armenia, bogota, 2, 3, "AIR2", seats2);
        saveLeg(bogota, miami, 7, 8, "AIR2", seats2);

        int expectedFlights = 4;

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY, PASSENGERS);

        assertEquals(expectedFlights, flights.size());
    }

    @Test
    void overlappingLegsReturnEmptyList() {
        saveLeg(armenia, bogota, 6, 8, "AIR1", seats2);
        saveLeg(bogota, miami, 7, 9, "AIR1", seats2);

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY, PASSENGERS);

        assertTrue(flights.isEmpty());
    }

    @Test
    void departureDateOfResultsMatchesRequestedDate() {
        saveLeg(armenia, miami, 0, 6, 8, "AIR1", seats2);
        saveLeg(armenia, miami, 1, 6, 8, "AIR1", seats2);
        saveLeg(armenia, bogota, 2, 6, 8, "AIR1", seats2);
        saveLeg(bogota, miami, 2, 10, 12, "AIR1", seats2);

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY, PASSENGERS);

        for (Flight flight : flights) {
            assertEquals(DAY, flight.getDepartureTime().toLocalDate());
        }
    }

    @Test
    void originAndDestinationOfResultsMatchRequestedAirports() {
        saveLeg(armenia, miami, 6, 8, "AIR1", seats2);
        saveLeg(armenia, bogota, 1, 2, "AIR1", seats2);
        saveLeg(bogota, miami, 5, 6, "AIR1", seats2);
        saveLeg(armenia, medellin, 3, 4, "AIR1", seats2);
        saveLeg(medellin, cali, 8, 9, "AIR1", seats2);
        saveLeg(bogota, cartagena, 4, 5, "AIR1", seats2);

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY, PASSENGERS);

        for (Flight flight : flights) {
            assertEquals(armenia, flight.getOriginAirport());
            assertEquals(miami, flight.getDestinationAirport());
        }
    }

    @Test
    void allResultsHaveAtMostFiveLegs() {
        saveLeg(armenia, bogota, 0, 1, "AIR1", seats2);
        saveLeg(bogota, medellin, 3, 4, "AIR1", seats2);
        saveLeg(medellin, cali, 6, 7, "AIR1", seats2);
        saveLeg(cali, cartagena, 9, 10, "AIR1", seats2);
        saveLeg(cartagena, miami, 12, 13, "AIR1", seats2);
        saveLeg(cartagena, barranquilla, 12, 13, "AIR1", seats2);
        saveLeg(barranquilla, miami, 15, 16, "AIR1", seats2);

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY, PASSENGERS);

        for (Flight flight : flights) {
            assertTrue(flight.getIdLegs().size() <= MAX_LEGS);
        }
    }

    @Test
    void onlyFormsFlightsWithEnoughSeatsForAllPassengers() {
        int passengers = 4;

        saveLeg(armenia, miami, 6, 8, "AIR1", seats4);

        saveLeg(armenia, bogota, 1, 2, "AIR1", seats4);
        saveLeg(bogota, miami, 5, 6, "AIR1", seats4);

        saveLeg(armenia, miami, 10, 12, "AIR1", seats3);
        saveLeg(armenia, miami, 13, 15, "AIR1", seats2);
        saveLeg(armenia, medellin, 1, 2, "AIR1", seats4);
        saveLeg(medellin, miami, 5, 6, "AIR1", seats3);

        List<Flight> flights = flightSearcher.searchFlights(armenia, miami, DAY, passengers);

        assertEquals(2, flights.size());
    }

    private Leg saveLeg(Airport origin, Airport destination,
                        int departureHour, int arrivalHour, String airlineId, List<Seat> seats) {
        return saveLeg(origin, destination, 0, departureHour, arrivalHour, airlineId, seats);
    }

    private Leg saveLeg(Airport origin, Airport destination, int dayOffset,
                        int departureHour, int arrivalHour, String airlineId, List<Seat> seats) {
        LocalDateTime departure = DAY.plusDays(dayOffset).atTime(departureHour, 0);
        LocalDateTime arrival = DAY.plusDays(dayOffset).atTime(arrivalHour, 0);

        Leg leg = Leg.createLeg(origin, destination, departure, arrival,
                "AIRCRAFT-1", airlineId, seats, 20000, 150000);
        legRepository.save(leg);
        return leg;
    }
}