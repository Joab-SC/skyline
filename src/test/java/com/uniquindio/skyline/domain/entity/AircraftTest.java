package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import com.uniquindio.skyline.domain.valueObject.Seat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AircraftTest {

    @Test
    void aircraftMustIncludeModel() {
        assertThrows(DomainRuleException.class, ()-> Aircraft.createAircraft(null));
    }

    @Test
    void aircraftNumberSeatsMustBeEqualOfTheModel() {
        Aircraft aircraft = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
        assertEquals(AircraftModel.EMBRAER_E175.getMaxSeats(), aircraft.getSeats().size());
    }

    @Test
    void aircraftSeatsMustHaveUniqueCodes() {
        Aircraft aircraft = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);

        long numberOfUniqueCodes = aircraft.getSeats()
                .stream()
                .map(Seat::code)
                .distinct()
                .count();

        assertEquals(aircraft.getSeats().size(), numberOfUniqueCodes);
    }

}
