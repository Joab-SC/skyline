package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    //Tests assign seat

    @Test
    void shouldGetLegSeatById() {
        Leg leg = createLegWithSeats();
        LegSeat firstSeat = leg.getSeats().get(0);

        assertEquals(Optional.of(firstSeat), leg.getLegSeatById(firstSeat.getId()));
    }

    @Test
    void shouldReturnEmptyWhenLegSeatDoesNotExist() {
        Leg leg = createLegWithSeats();

        assertEquals(Optional.empty(), leg.getLegSeatById("non-existent-seat"));
    }

    @Test
    void shouldAssignSeatToPassenger() {
        // Arrange
        Leg leg = createLegWithSeats();
        LegSeat seat = leg.getSeats().get(0);

        // Act
        leg.assignSeat(seat.getId(), "1001");

        // Assert
        assertEquals(Optional.of("1001"), seat.getPassengerId());
        assertEquals(SeatStatus.OCCUPIED, seat.getSeatStatus());
        assertTrue(leg.hasSeatAssigned("1001"));
    }

    @Test
    void shouldThrowExceptionWhenAssigningNonExistentSeat() {
        Leg leg = createLegWithSeats();

        assertThrows(NoSuchElementException.class, () -> leg.assignSeat("non-existent-seat", "1001"));
        assertFalse(leg.hasSeatAssigned("1001"));
    }

    @Test
    void shouldThrowExceptionWhenSeatIsOccupiedByAnotherPassenger() {
        Leg leg = createLegWithSeats();
        LegSeat seat = leg.getSeats().get(0);
        leg.assignSeat(seat.getId(), "1001");

        assertThrows(DomainRuleException.class, () -> leg.assignSeat(seat.getId(), "2002"));
        assertEquals(Optional.of("1001"), seat.getPassengerId());
        assertFalse(leg.hasSeatAssigned("2002"));
    }

    @Test
    void shouldChangeSeatAndReleasePreviousOne() {
        // Arrange
        Leg leg = createLegWithSeats();
        LegSeat oldSeat = leg.getSeats().get(0);
        LegSeat newSeat = leg.getSeats().get(1);
        leg.assignSeat(oldSeat.getId(), "1001");

        // Act
        leg.assignSeat(newSeat.getId(), "1001");

        // Assert
        assertEquals(Optional.of("1001"), newSeat.getPassengerId());
        assertEquals(SeatStatus.OCCUPIED, newSeat.getSeatStatus());
        assertEquals(Optional.empty(), oldSeat.getPassengerId());
        assertEquals(SeatStatus.AVAILABLE, oldSeat.getSeatStatus());
    }

    @Test
    void shouldKeepPreviousSeatWhenNewSeatIsOccupied() {
        Leg leg = createLegWithSeats();
        LegSeat ownSeat = leg.getSeats().get(0);
        LegSeat takenSeat = leg.getSeats().get(1);
        leg.assignSeat(ownSeat.getId(), "1001");
        leg.assignSeat(takenSeat.getId(), "2002");

        assertThrows(DomainRuleException.class, () -> leg.assignSeat(takenSeat.getId(), "1001"));
        assertEquals(Optional.of("1001"), ownSeat.getPassengerId());
        assertEquals(Optional.of("2002"), takenSeat.getPassengerId());
    }

    @Test
    void shouldDoNothingWhenPassengerChoosesTheSameSeat() {
        Leg leg = createLegWithSeats();
        LegSeat seat = leg.getSeats().get(0);
        leg.assignSeat(seat.getId(), "1001");

        assertDoesNotThrow(() -> leg.assignSeat(seat.getId(), "1001"));
        assertEquals(Optional.of("1001"), seat.getPassengerId());
        assertEquals(SeatStatus.OCCUPIED, seat.getSeatStatus());
    }

    @Test
    void passengerHasNoSeatAssignedByDefault() {
        Leg leg = createLegWithSeats();

        assertFalse(leg.hasSeatAssigned("1001"));
    }

    private Leg createLegWithSeats() {
        List<Seat> aircraftSeats = List.of(new Seat("001"), new Seat("002"), new Seat("003"));
        return Leg.createLeg(Airport.EL_EDEN, Airport.EL_DORADO,
                LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0), "001", "AV0A123", aircraftSeats, 2000, 3000);
    }
}
