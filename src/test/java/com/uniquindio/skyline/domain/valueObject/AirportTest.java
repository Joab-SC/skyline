package com.uniquindio.skyline.domain.valueObject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AirportTest {

    @Test
    void getByAcronymWithValidCodeReturnsMatchingAirport() {
        Airport airport = Airport.getByAcronym("AXM");

        assertEquals(Airport.EL_EDEN, airport);
    }
}