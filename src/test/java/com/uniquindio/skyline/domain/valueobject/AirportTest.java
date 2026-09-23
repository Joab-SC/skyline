package com.uniquindio.skyline.domain.valueobject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.City;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AirportTest {

    @Test
    void TwoAirportsWIthSameDataAreEqual() {
        Airport airport = new Airport("El Eden", "AXM", City.ARMENIA);
        Airport duplicateAirport = new Airport("El Eden", "AXM", City.ARMENIA);

        assertEquals(airport, duplicateAirport);
    }

    @Test
    void AirportWithInvalidDataThrowsException() {

        assertThrows(DomainRuleException.class, () -> {
            new Airport("El Eden", "AXM", null);
        });
        assertThrows(DomainRuleException.class, () -> {
            new Airport("El dorado", " ", City.BOGOTA);
        });
        assertThrows(DomainRuleException.class, () -> {
            new Airport(null, "LAX", City.LOS_ANGELES);
        });
    }
}
