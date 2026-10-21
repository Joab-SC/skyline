
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

    private static final String AIRLINE_ID = "airline-1";
    private static final LocalDateTime OUTBOUND_DEPARTURE = LocalDateTime.of(2026, 11, 5, 10, 0);
    private static final LocalDateTime RETURN_DEPARTURE = LocalDateTime.of(2026, 11, 10, 10, 0);

    private final OutboundAndReturnFlightsValidator validator = new OutboundAndReturnFlightsValidator();

    @Test
    void shouldValidateWhenFlightsAreCompatible() {
        // Arrange
        Flight outboundFlight = createFlight(Airport.EL_EDEN, Airport.EL_DORADO, AIRLINE_ID, OUTBOUND_DEPARTURE);
        Flight returnFlight = createFlight(Airport.EL_DORADO, Airport.EL_EDEN, AIRLINE_ID, RETURN_DEPARTURE);

        // Act & Assert
        assertDoesNotThrow(
                () -> validator.validateOutboundAndReturnFlightCompatibility(outboundFlight, returnFlight)
        );
    }

    @Test
    void shouldThrowExceptionWhenFlightsAreFromDifferentAirlines() {
        // Arrange
        Flight outboundFlight = createFlight(Airport.EL_EDEN, Airport.EL_DORADO, AIRLINE_ID, OUTBOUND_DEPARTURE);
        Flight returnFlight = createFlight(Airport.EL_DORADO, Airport.EL_EDEN, "airline-2", RETURN_DEPARTURE);

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> validator.validateOutboundAndReturnFlightCompatibility(outboundFlight, returnFlight)
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightDepartsFromDifferentAirport() {
        // Arrange
        Flight outboundFlight = createFlight(Airport.EL_EDEN, Airport.EL_DORADO, AIRLINE_ID, OUTBOUND_DEPARTURE);
        Flight returnFlight = createFlight(Airport.JOSE_MARIA_CORDOVA, Airport.EL_EDEN, AIRLINE_ID, RETURN_DEPARTURE);

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> validator.validateOutboundAndReturnFlightCompatibility(outboundFlight, returnFlight)
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightArrivesAtDifferentAirport() {
        // Arrange
        Flight outboundFlight = createFlight(Airport.EL_EDEN, Airport.EL_DORADO, AIRLINE_ID, OUTBOUND_DEPARTURE);
        Flight returnFlight = createFlight(Airport.EL_DORADO, Airport.JOSE_MARIA_CORDOVA, AIRLINE_ID, RETURN_DEPARTURE);

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> validator.validateOutboundAndReturnFlightCompatibility(outboundFlight, returnFlight)
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightDepartsBeforeOutboundArrives() {
        // Arrange: outbound arrives at 12:00, return departs at 11:00 the same day
        Flight outboundFlight = createFlight(Airport.EL_EDEN, Airport.EL_DORADO, AIRLINE_ID, OUTBOUND_DEPARTURE);
        Flight returnFlight = createFlight(Airport.EL_DORADO, Airport.EL_EDEN, AIRLINE_ID, OUTBOUND_DEPARTURE.plusHours(1));

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> validator.validateOutboundAndReturnFlightCompatibility(outboundFlight, returnFlight)
        );
    }


    // Each flight has a single 2-hour leg.
    private Flight createFlight(Airport origin, Airport destination, String airlineId, LocalDateTime departure) {
        Leg leg = Leg.createLeg(origin, destination, departure, departure.plusHours(2), "AIR-001", airlineId,
                List.of(new Seat("001")), 100, 1000);
        return Flight.createFlight(leg.getId(), leg.toSummary());
    }
}
