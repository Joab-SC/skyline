package com.uniquindio.skyline.domain.valueObject;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AirportTest {

    @Test
    void getByAcronymWithValidCodeReturnsMatchingAirport() {
        Optional<Airport> airport = Airport.getByAcronym("AXM");

        assertEquals(Optional.of(Airport.EL_EDEN), airport);
    }
}