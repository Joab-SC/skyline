package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.City;
import com.uniquindio.skyline.domain.valueObject.Seat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;

public class LegTest {

    @Test
    void legIsCreatedWithSameSizeOfAircraftSeats() {
        List<Seat> aircraftSeats = List.of(new Seat("001"), new Seat("002"), new Seat("003"));
        Leg leg = Leg.createLeg(new Airport("El Eden", "AXM", City.ARMENIA), new Airport("El Dorado", "EDR", City.BOGOTA),
                LocalDateTime.of(2025, 11, 5, 10, 10, 10),
                LocalDateTime.of(2025, 11, 6, 10, 10, 10), "001", aircraftSeats, 2000, 3000);
        assertEquals(leg.getSeats().size(), aircraftSeats.size());
    }

    @Test
    public void legThrowsExceptionWhenArrivalTimeIsBeforeDepartureTime() {
        // Checks that arrival time must be later than departure time.
        List<Seat> aircraftSeats = List.of(
                new Seat("001"),
                new Seat("002")
        );

        assertThrows(DomainRuleException.class, () -> {
            Leg.createLeg(
                    new Airport("El Eden", "AXM", City.ARMENIA),
                    new Airport("El Dorado", "EDR", City.BOGOTA),
                    LocalDateTime.of(2025, 11, 5, 15, 10),
                    LocalDateTime.of(2025, 11, 5, 12, 10),
                    "001",
                    aircraftSeats,
                    2000,
                    3000
            );
        });
    }

    @Test
    public void legThrowsExceptionWhenOriginAndDestinationAirportsAreTheSame() {
        // Checks that origin and destination airports cannot be the same.
        Airport airport = new Airport("El Eden", "AXM", City.ARMENIA);

        List<Seat> aircraftSeats = List.of(
                new Seat("001"),
                new Seat("002")
        );

        assertThrows(DomainRuleException.class, () -> {
            Leg.createLeg(
                    airport,
                    airport,
                    LocalDateTime.of(2025, 11, 5, 10, 10),
                    LocalDateTime.of(2025, 11, 5, 12, 10),
                    "001",
                    aircraftSeats,
                    2000,
                    3000
            );
        });
    }
}
