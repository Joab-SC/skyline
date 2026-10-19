package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlightTest {

    private static final LocalDateTime T0 = LocalDateTime.of(2026, 11, 10, 0, 0);
    private static final List<Seat> SEATS = List.of(new Seat("A1"), new Seat("A2"));

    private Airport armenia;
    private Airport bogota;
    private Airport medellin;
    private Airport cali;
    private Airport cartagena;
    private Airport miami;


    private Leg firstLeg;
    private Flight flight;

    @BeforeEach
    void setUp() {
        armenia = Airport.EL_EDEN;
        bogota = Airport.EL_DORADO;
        medellin = Airport.JOSE_MARIA_CORDOVA;
        cali = Airport.ALFONSO_BONILLA_ARAGON;
        cartagena = Airport.RAFAEL_NUNEZ;
        miami = Airport.MIAMI_INTERNATIONAL;

        firstLeg = buildLeg(armenia, bogota, 1, 2, "AIR1", 100000);
        flight = Flight.createFlight(firstLeg.getId(), firstLeg.toSummary());
    }



    @Test
    void createFlightWithNullSummaryIsNotAccepted() {
        assertThrows(DomainRuleException.class, () -> Flight.createFlight("L1", null));
    }



    @Test
    void addLegWithDifferentAirlineIsNotAccepted() {
        Leg next = buildLeg(bogota, miami, 4, 5, "AIR2", 150000);
        assertThrows(DomainRuleException.class, () -> flight.addLeg(next.getId(), next.toSummary()));
    }

    @Test
    void addLegWithOriginDifferentFromCurrentDestinationIsNotAccepted() {
        Leg next = buildLeg(medellin, miami, 4, 5, "AIR1", 150000);
        assertThrows(DomainRuleException.class, () -> flight.addLeg(next.getId(), next.toSummary()));
    }


    @Test
    void addLegDepartingBeforeCurrentArrivalIsNotAccepted() {
        Leg next = buildLeg(bogota, miami, 1, 3, "AIR1", 150000);
        assertThrows(DomainRuleException.class, () -> flight.addLeg(next.getId(), next.toSummary()));
    }

    @Test
    void addLegWithInvalidIdIsNotAccepted() {
        Leg nextOne = buildLeg(bogota, miami, 4, 5, "AIR1", 150000);
        assertThrows(DomainRuleException.class, () -> flight.addLeg(null, nextOne.toSummary()));
        Leg nextTwo = buildLeg(bogota, miami, 4, 5, "AIR1", 150000);
        assertThrows(DomainRuleException.class, () -> flight.addLeg("   ", nextTwo.toSummary()));
    }


    @Test
    void addLegBeyondMaximumLegsThrows() {

        Leg second = buildLeg(bogota, medellin, 3, 4, "AIR1", 100000);
        Leg third = buildLeg(medellin, cali, 5, 6, "AIR1", 100000);
        Leg fourth = buildLeg(cali, cartagena, 7, 8, "AIR1", 100000);
        flight.addLeg(second.getId(), second.toSummary());
        flight.addLeg(third.getId(), third.toSummary());
        flight.addLeg(fourth.getId(), fourth.toSummary());

        Leg fifth = buildLeg(cartagena, miami, 9, 10, "AIR1", 100000);

        assertThrows(DomainRuleException.class, () -> flight.addLeg(fifth.getId(), fifth.toSummary()));
    }



    @Test
    void addValidLegUpdatesFlightState() {
        Leg next = buildLeg(bogota, miami, 4, 6, "AIR1", 150000);

        flight.addLeg(next.getId(), next.toSummary());

        assertEquals(250000, flight.getPrice(), 0.001);
        assertEquals(next.getArrivalTime(), flight.getArrivalTime());
        assertEquals(miami, flight.getDestinationAirport());
        assertEquals(List.of(firstLeg.getId(), next.getId()), flight.getIdLegs());
        assertEquals(1, flight.getLayovers().size());
    }


    @Test
    void isNewLegValidWithValidLegIsTrue() {
        Leg next = buildLeg(bogota, miami, 4, 5, "AIR1", 150000);
        assertTrue(Flight.isNewLegValid(firstLeg.toSummary(), next.toSummary()));
    }

    @Test
    void isNewLegValidWithDifferentAirlineIsFalse() {
        Leg next = buildLeg(bogota, miami, 4, 5, "AIR2", 150000);
        assertFalse(Flight.isNewLegValid(firstLeg.toSummary(), next.toSummary()));
    }

    @Test
    void isNewLegValidWithDifferentOriginAirportIsFalse() {
        Leg next = buildLeg(medellin, miami, 4, 5, "AIR1", 150000);
        assertFalse(Flight.isNewLegValid(firstLeg.toSummary(), next.toSummary()));
    }

    @Test
    void isNewLegValidWithDepartureNotAfterArrivalIsFalse() {
        Leg next = buildLeg(bogota, miami, 2, 3, "AIR1", 150000);
        assertFalse(Flight.isNewLegValid(firstLeg.toSummary(), next.toSummary()));
    }


    @Test
    void canAddMoreLegsBelowLimitIsTrue() {
        assertTrue(Flight.canAddMoreLegs(3));
    }

    @Test
    void canAddMoreLegsAtLimitIsFalse() {
        assertFalse(Flight.canAddMoreLegs(4));
    }



    private Leg buildLeg(Airport origin, Airport destination, int departureHour, int arrivalHour,
                    String airlineId, double price) {
        return Leg.createLeg(origin, destination, T0.plusHours(departureHour), T0.plusHours(arrivalHour),
                "AIRCRAFT-1", airlineId, SEATS, 20000, price);
    }
}