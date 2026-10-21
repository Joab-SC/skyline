package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import org.junit.jupiter.api.Test;

import java.security.Security;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LegSeatTest {

    @Test
    public void LegSeatIsCreatedAvailable() {
        // Checks the initial seat status.
        LegSeat legSeat = LegSeat.createLegSeat(UUID.randomUUID().toString(), "A32");
        assertEquals(SeatStatus.AVAILABLE, legSeat.getSeatStatus());
    }

    @Test
    public void LegSeatIsCreatedWthEmptyPassengerId() {
        // Checks that no passenger is assigned.
        LegSeat legSeat = LegSeat.createLegSeat(UUID.randomUUID().toString(), "B28");
        assertEquals(Optional.empty(), legSeat.getPassengerId());
    }

    @Test
    public void twoLegSeatsWithTheSameIdAreEqual() {
        // Checks equality by leg seat id.
        LegSeat legSeatOriginal = LegSeat.createLegSeat("001", "A1");
        LegSeat legSeatDuplicated = LegSeat.createLegSeat("001", "B2");

        assertEquals(legSeatOriginal, legSeatDuplicated);
    }

    //Tests assign and release leg seat

    @Test
    public void shouldAssignPassengerAndMarkSeatAsOccupied() {
        // Arrange
        LegSeat legSeat = LegSeat.createLegSeat("001", "A1");

        // Act
        legSeat.assignPassengerToLegSeat("1001");

        // Assert
        assertEquals(Optional.of("1001"), legSeat.getPassengerId());
        assertEquals(SeatStatus.OCCUPIED, legSeat.getSeatStatus());
    }

    @Test
    public void shouldThrowExceptionWhenPassengerIdIsNull() {
        LegSeat legSeat = LegSeat.createLegSeat("001", "A1");

        assertThrows(DomainRuleException.class, () -> legSeat.assignPassengerToLegSeat(null));
        assertEquals(SeatStatus.AVAILABLE, legSeat.getSeatStatus());
    }

    @Test
    public void shouldThrowExceptionWhenPassengerIdIsBlank() {
        LegSeat legSeat = LegSeat.createLegSeat("001", "A1");

        assertThrows(DomainRuleException.class, () -> legSeat.assignPassengerToLegSeat("  "));
        assertEquals(SeatStatus.AVAILABLE, legSeat.getSeatStatus());
    }

    @Test
    public void shouldThrowExceptionWhenSeatIsAlreadyOccupied() {
        LegSeat legSeat = LegSeat.createLegSeat("001", "A1");
        legSeat.assignPassengerToLegSeat("1001");

        assertThrows(DomainRuleException.class, () -> legSeat.assignPassengerToLegSeat("2002"));
        assertEquals(Optional.of("1001"), legSeat.getPassengerId());
    }

    @Test
    public void shouldReleaseSeat() {
        // Arrange
        LegSeat legSeat = LegSeat.createLegSeat("001", "A1");
        legSeat.assignPassengerToLegSeat("1001");

        // Act
        legSeat.releaseSeat();

        // Assert
        assertEquals(Optional.empty(), legSeat.getPassengerId());
        assertEquals(SeatStatus.AVAILABLE, legSeat.getSeatStatus());
    }
}
