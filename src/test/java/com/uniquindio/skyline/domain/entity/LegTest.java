package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.PassengerBookingId;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class LegTest {

    private static final PassengerBookingId PASSENGER_1001 = new PassengerBookingId("booking-1", "1001");
    private static final PassengerBookingId PASSENGER_2002 = new PassengerBookingId("booking-1", "2002");

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
        leg.assignSeat(seat.getId(), PASSENGER_1001);

        // Assert
        assertEquals(Optional.of(PASSENGER_1001), seat.getPassengerBookingId());
        assertEquals(SeatStatus.OCCUPIED, seat.getSeatStatus());
        assertTrue(leg.hasSeatAssigned(PASSENGER_1001));
    }

    @Test
    void shouldThrowExceptionWhenAssigningNonExistentSeat() {
        Leg leg = createLegWithSeats();

        assertThrows(NoSuchElementException.class, () -> leg.assignSeat("non-existent-seat", PASSENGER_1001));
        assertFalse(leg.hasSeatAssigned(PASSENGER_1001));
    }

    @Test
    void shouldThrowExceptionWhenSeatIsOccupiedByAnotherPassenger() {
        Leg leg = createLegWithSeats();
        LegSeat seat = leg.getSeats().get(0);
        leg.assignSeat(seat.getId(), PASSENGER_1001);

        assertThrows(DomainRuleException.class, () -> leg.assignSeat(seat.getId(), PASSENGER_2002));
        assertEquals(Optional.of(PASSENGER_1001), seat.getPassengerBookingId());
        assertFalse(leg.hasSeatAssigned(PASSENGER_2002));
    }

    @Test
    void shouldChangeSeatAndReleasePreviousOne() {
        // Arrange
        Leg leg = createLegWithSeats();
        LegSeat oldSeat = leg.getSeats().get(0);
        LegSeat newSeat = leg.getSeats().get(1);
        leg.assignSeat(oldSeat.getId(), PASSENGER_1001);

        // Act
        leg.assignSeat(newSeat.getId(), PASSENGER_1001);

        // Assert
        assertEquals(Optional.of(PASSENGER_1001), newSeat.getPassengerBookingId());
        assertEquals(SeatStatus.OCCUPIED, newSeat.getSeatStatus());
        assertEquals(Optional.empty(), oldSeat.getPassengerBookingId());
        assertEquals(SeatStatus.AVAILABLE, oldSeat.getSeatStatus());
    }

    @Test
    void shouldKeepPreviousSeatWhenNewSeatIsOccupied() {
        Leg leg = createLegWithSeats();
        LegSeat ownSeat = leg.getSeats().get(0);
        LegSeat takenSeat = leg.getSeats().get(1);
        leg.assignSeat(ownSeat.getId(), PASSENGER_1001);
        leg.assignSeat(takenSeat.getId(), PASSENGER_2002);

        assertThrows(DomainRuleException.class, () -> leg.assignSeat(takenSeat.getId(), PASSENGER_1001));
        assertEquals(Optional.of(PASSENGER_1001), ownSeat.getPassengerBookingId());
        assertEquals(Optional.of(PASSENGER_2002), takenSeat.getPassengerBookingId());
    }

    @Test
    void shouldDoNothingWhenPassengerChoosesTheSameSeat() {
        Leg leg = createLegWithSeats();
        LegSeat seat = leg.getSeats().get(0);
        leg.assignSeat(seat.getId(), PASSENGER_1001);

        assertDoesNotThrow(() -> leg.assignSeat(seat.getId(), PASSENGER_1001));
        assertEquals(Optional.of(PASSENGER_1001), seat.getPassengerBookingId());
        assertEquals(SeatStatus.OCCUPIED, seat.getSeatStatus());
    }

    @Test
    void passengerHasNoSeatAssignedByDefault() {
        Leg leg = createLegWithSeats();

        assertFalse(leg.hasSeatAssigned(PASSENGER_1001));
    }

    @Test
    void samePassengerInAnotherBookingDoesNotReleaseTheFirstSeat() {
        // Arrange
        Leg leg = createLegWithSeats();
        LegSeat firstBookingSeat = leg.getSeats().get(0);
        LegSeat secondBookingSeat = leg.getSeats().get(1);
        PassengerBookingId sameDocumentOtherBooking = new PassengerBookingId("booking-2", "1001");
        leg.assignSeat(firstBookingSeat.getId(), PASSENGER_1001);

        // Act
        leg.assignSeat(secondBookingSeat.getId(), sameDocumentOtherBooking);

        // Assert
        assertEquals(Optional.of(PASSENGER_1001), firstBookingSeat.getPassengerBookingId());
        assertEquals(SeatStatus.OCCUPIED, firstBookingSeat.getSeatStatus());
        assertEquals(Optional.of(sameDocumentOtherBooking), secondBookingSeat.getPassengerBookingId());
    }

    @Test
    void releasingAPassengerDoesNotReleaseTheSameDocumentInAnotherBooking() {
        // Arrange
        Leg leg = createLegWithSeats();
        LegSeat firstBookingSeat = leg.getSeats().get(0);
        LegSeat secondBookingSeat = leg.getSeats().get(1);
        PassengerBookingId sameDocumentOtherBooking = new PassengerBookingId("booking-2", "1001");
        leg.assignSeat(firstBookingSeat.getId(), PASSENGER_1001);
        leg.assignSeat(secondBookingSeat.getId(), sameDocumentOtherBooking);

        // Act
        leg.releaseSeatOfPassenger(sameDocumentOtherBooking);

        // Assert
        assertEquals(SeatStatus.OCCUPIED, firstBookingSeat.getSeatStatus());
        assertEquals(SeatStatus.AVAILABLE, secondBookingSeat.getSeatStatus());
    }

    private Leg createLegWithSeats() {
        List<Seat> aircraftSeats = List.of(new Seat("001"), new Seat("002"), new Seat("003"));
        return Leg.createLeg(Airport.EL_EDEN, Airport.EL_DORADO,
                LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0), "001", "AV0A123", aircraftSeats, 2000, 3000);
    }
}
