package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.PassengerData;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import com.uniquindio.skyline.domain.valueObject.TripType;
import com.uniquindio.skyline.infrastructure.persistence.FlightRepositoryInMemory;
import com.uniquindio.skyline.infrastructure.persistence.LegRepositoryInMemory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingSeatsReleaserTest {

    private static final LocalDate DAY = LocalDate.of(2026, 11, 5);

    private final LegRepository legRepository = new LegRepositoryInMemory();
    private final FlightRepository flightRepository = new FlightRepositoryInMemory();
    private final BookingSeatsReleaser releaser = new BookingSeatsReleaser(flightRepository, legRepository);

    @Test
    void shouldRestoreSeatsAndReleaseAssignedSeatsOnOneWayBooking() {
        // Arrange
        Leg leg = saveLeg(Airport.EL_EDEN, Airport.EL_DORADO, DAY.atTime(10, 0));
        Flight flight = saveFlight(leg);
        Booking booking = bookingWithTwoPassengers(flight, TripType.ONE_WAY);
        takeSeats(leg);

        // Act
        releaser.releaseSeats(booking);

        // Assert
        assertEquals(4, leg.getAvailableSeats());
        assertFalse(leg.hasSeatAssigned("1001"));
        assertFalse(leg.hasSeatAssigned("1002"));
        assertTrue(leg.getSeats().stream().allMatch(seat -> seat.getSeatStatus() == SeatStatus.AVAILABLE));
    }

    @Test
    void shouldReleaseEveryLegOfOutboundAndReturnFlights() {
        // Arrange
        Leg outboundLeg1 = saveLeg(Airport.EL_EDEN, Airport.EL_DORADO, DAY.atTime(10, 0));
        Leg outboundLeg2 = saveLeg(Airport.EL_DORADO, Airport.JOSE_MARIA_CORDOVA, DAY.atTime(14, 0));
        Leg returnLeg = saveLeg(Airport.JOSE_MARIA_CORDOVA, Airport.EL_EDEN, DAY.plusDays(3).atTime(10, 0));
        Flight outboundFlight = saveFlight(outboundLeg1, outboundLeg2);
        Flight returnFlight = saveFlight(returnLeg);

        Booking booking = bookingWithTwoPassengers(outboundFlight, TripType.ROUND_TRIP);
        booking.addReturnFlight(returnFlight.getId(), returnFlight.getPrice());
        List<Leg> legs = List.of(outboundLeg1, outboundLeg2, returnLeg);
        legs.forEach(this::takeSeats);

        // Act
        releaser.releaseSeats(booking);

        // Assert
        for (Leg leg : legs) {
            assertEquals(4, leg.getAvailableSeats());
            assertFalse(leg.hasSeatAssigned("1001"));
            assertFalse(leg.hasSeatAssigned("1002"));
        }
    }

    @Test
    void shouldThrowExceptionWhenFlightDoesNotExist() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, "missing-flight", TripType.ONE_WAY);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> releaser.releaseSeats(booking));
    }

    @Test
    void shouldThrowExceptionWhenLegDoesNotExist() {
        // Arrange
        Leg leg = createLeg(Airport.EL_EDEN, Airport.EL_DORADO, DAY.atTime(10, 0)); // not saved
        Flight flight = saveFlight(leg);
        Booking booking = bookingWithTwoPassengers(flight, TripType.ONE_WAY);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> releaser.releaseSeats(booking));
    }

    @Test
    void shouldNotChangeAnyLegWhenOneLegCannotBeRestored() {
        // Arrange
        Leg leg1 = saveLeg(Airport.EL_EDEN, Airport.EL_DORADO, DAY.atTime(10, 0));
        Leg leg2 = saveLeg(Airport.EL_DORADO, Airport.JOSE_MARIA_CORDOVA, DAY.atTime(14, 0));
        Flight flight = saveFlight(leg1, leg2);
        Booking booking = bookingWithTwoPassengers(flight, TripType.ONE_WAY);
        takeSeats(leg1); // leg2 is still full, so it cannot get seats back

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> releaser.releaseSeats(booking));
        assertEquals(2, leg1.getAvailableSeats());
        assertTrue(leg1.hasSeatAssigned("1001"));
    }

    private Leg createLeg(Airport origin, Airport destination, LocalDateTime departure) {
        return Leg.createLeg(origin, destination, departure, departure.plusHours(2), "aircraft-1", "AV",
                List.of(new Seat("001"), new Seat("002"), new Seat("003"), new Seat("004")), 2000, 3000);
    }

    private Leg saveLeg(Airport origin, Airport destination, LocalDateTime departure) {
        Leg leg = createLeg(origin, destination, departure);
        legRepository.save(leg);
        return leg;
    }

    private Flight saveFlight(Leg... legs) {
        Flight flight = Flight.createFlight(Arrays.stream(legs).map(Leg::toSummary).toList());
        flightRepository.save(flight);
        return flight;
    }

    private Booking bookingWithTwoPassengers(Flight outboundFlight, TripType tripType) {
        Booking booking = Booking.createPendingBooking(2, outboundFlight.getPrice(), outboundFlight.getId(), tripType);
        booking.addPassengers(List.of(passengerData("1001"), passengerData("1002")));
        return booking;
    }

    // Simulates what the booking took: the seat count and one seat per passenger.
    private void takeSeats(Leg leg) {
        leg.reduceAvailableSeats(2);
        leg.assignSeat(leg.getSeats().get(0).getId(), "1001");
        leg.assignSeat(leg.getSeats().get(1).getId(), "1002");
    }

    private PassengerData passengerData(String idDocument) {
        return new PassengerData(idDocument, DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);
    }
}