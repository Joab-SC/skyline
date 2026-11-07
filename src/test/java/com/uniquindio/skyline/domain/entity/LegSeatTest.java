package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.PassengerBookingId;
import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LegSeatTest {

    private static final PassengerBookingId PASSENGER_1001 = new PassengerBookingId("booking-1", "1001");
    private static final PassengerBookingId PASSENGER_2002 = new PassengerBookingId("booking-1", "2002");

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
        assertEquals(Optional.empty(), legSeat.getPassengerBookingId());
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
        legSeat.assignPassengerToLegSeat(PASSENGER_1001);

        // Assert
        assertEquals(Optional.of(PASSENGER_1001), legSeat.getPassengerBookingId());
        assertEquals(SeatStatus.OCCUPIED, legSeat.getSeatStatus());
    }

    @Test
    public void seatIsOccupiedOnlyByTheSamePassengerOfTheSameBooking() {
        LegSeat legSeat = LegSeat.createLegSeat("001", "A1");
        legSeat.assignPassengerToLegSeat(PASSENGER_1001);

        assertTrue(legSeat.isOccupiedBy(new PassengerBookingId("booking-1", "1001")));
        assertFalse(legSeat.isOccupiedBy(new PassengerBookingId("booking-2", "1001")));
    }

    @Test
    public void reconstructedFreeSeatHasNoPassenger() {
        LegSeat legSeat = LegSeat.reconstruct("001", "A1", SeatStatus.AVAILABLE, null, null);

        assertEquals(Optional.empty(), legSeat.getPassengerBookingId());
    }

    @Test
    public void reconstructedOccupiedSeatKeepsBookingAndPassenger() {
        LegSeat legSeat = LegSeat.reconstruct("001", "A1", SeatStatus.OCCUPIED, "1001", "booking-1");

        assertEquals(Optional.of(PASSENGER_1001), legSeat.getPassengerBookingId());
    }

    @Test
    public void shouldThrowExceptionWhenPassengerIdIsNull() {
        LegSeat legSeat = LegSeat.createLegSeat("001", "A1");

        assertThrows(DomainRuleException.class, () -> legSeat.assignPassengerToLegSeat(null));
        assertEquals(SeatStatus.AVAILABLE, legSeat.getSeatStatus());
    }


    @Test
    public void shouldThrowExceptionWhenSeatIsAlreadyOccupied() {
        LegSeat legSeat = LegSeat.createLegSeat("001", "A1");
        legSeat.assignPassengerToLegSeat(PASSENGER_1001);

        assertThrows(DomainRuleException.class, () -> legSeat.assignPassengerToLegSeat(PASSENGER_2002));
        assertEquals(Optional.of(PASSENGER_1001), legSeat.getPassengerBookingId());
    }

    @Test
    public void shouldReleaseSeat() {
        // Arrange
        LegSeat legSeat = LegSeat.createLegSeat("001", "A1");
        legSeat.assignPassengerToLegSeat(PASSENGER_1001);

        // Act
        legSeat.releaseSeat();

        // Assert
        assertEquals(Optional.empty(), legSeat.getPassengerBookingId());
        assertEquals(SeatStatus.AVAILABLE, legSeat.getSeatStatus());
    }
}
