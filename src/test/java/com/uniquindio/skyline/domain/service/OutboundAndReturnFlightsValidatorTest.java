
package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Seat;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OutboundAndReturnFlightsValidatorTest {

    private final OutboundAndReturnFlightsValidator validator = new OutboundAndReturnFlightsValidator();

    @Test
    void shouldValidateWhenOutboundDestinationEqualsReturnOrigin() {
        // Arrange
        Flight outboundFlight = createFlight(Airport.EL_EDEN, Airport.EL_DORADO);
        Flight returnFlight = createFlight(Airport.EL_DORADO, Airport.EL_EDEN);

        // Act & Assert
        assertDoesNotThrow(
                () -> validator.validateOutboundAirportEqualsReturnFlight(outboundFlight, returnFlight)
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightDepartsFromDifferentAirport() {
        // Arrange
        Flight outboundFlight = createFlight(Airport.EL_EDEN, Airport.EL_DORADO);
        Flight returnFlight = createFlight(Airport.JOSE_MARIA_CORDOVA, Airport.EL_EDEN);

        // Act & Assert
        DomainRuleException exception = assertThrows(
                DomainRuleException.class,
                () -> validator.validateOutboundAirportEqualsReturnFlight(outboundFlight, returnFlight)
        );
        assertEquals("Return flight must depart from the outbound destination airport", exception.getMessage());
    }

    private Flight createFlight(Airport origin, Airport destination) {
        Leg leg = Leg.createLeg(origin, destination, LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0), "AIR-001", "airline-1",
                List.of(new Seat("001")), 100, 1000);
        return Flight.createFlight(leg.getId(), leg.toSummary());
    }
}
