package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Seat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;

public class LegTest {

    @Test
    void legIsCreatedWithSameSizeOfAircraftSeats() {
        List<Seat> aircraftSeats = List.of(new Seat("001"), new Seat("002"), new Seat("003"));
        Leg leg = Leg.createLeg(Airport.EL_EDEN, Airport.EL_DORADO,
                LocalDateTime.of(2025, 11, 5, 10, 10, 10),
                LocalDateTime.of(2025, 11, 6, 10, 10, 10), "001", "AV0A123", aircraftSeats, 2000, 3000);
        assertEquals(leg.getSeats().size(), aircraftSeats.size());
    }

    @Test
    public void legThrowsExceptionWhenArrivalTimeIsBeforeDepartureTime() {
        List<Seat> aircraftSeats = List.of(
                new Seat("001"),
                new Seat("002")
        );

        assertThrows(DomainRuleException.class, () -> {
            Leg.createLeg(
                    Airport.EL_EDEN,
                    Airport.EL_DORADO,
                    LocalDateTime.of(2025, 11, 5, 15, 10),
                    LocalDateTime.of(2025, 11, 5, 12, 10),
                    "001",
                    "AV0A123",
                    aircraftSeats,
                    2000,
                    3000
            );
        });
    }

    @Test
    public void legThrowsExceptionWhenOriginAndDestinationAirportsAreTheSame() {
        Airport airport = Airport.EL_EDEN;

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
                    "AV0A123",
                    aircraftSeats,
                    2000,
                    3000
            );
        });
    }
}