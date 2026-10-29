package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.PassengerData;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.domain.valueObject.TripType;
import com.uniquindio.skyline.infrastructure.persistence.BookingRepositoryInMemory;
import com.uniquindio.skyline.infrastructure.persistence.FlightRepositoryInMemory;
import com.uniquindio.skyline.infrastructure.persistence.LegRepositoryInMemory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingExpirationValidatorTest {

    private final BookingRepository bookingRepository = new BookingRepositoryInMemory();
    private final LegRepository legRepository = new LegRepositoryInMemory();
    private final FlightRepository flightRepository = new FlightRepositoryInMemory();
    private final BookingExpirationValidator validator = new BookingExpirationValidator(
            bookingRepository, new BookingSeatsReleaser(flightRepository, legRepository));

    @Test
    void shouldFindOnlyPendingBookingsWithPassedDeadline() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Booking expired = booking("booking-1", BookingStatus.PENDING, now.minusMinutes(1), "flight-1");
        Booking onTime = Booking.createPendingBooking(1, 500.0, "flight-1", TripType.ONE_WAY);
        Booking cancelled = booking("booking-3", BookingStatus.CANCELLED, now.minusMinutes(1), "flight-1");
        List.of(expired, onTime, cancelled).forEach(bookingRepository::save);

        // Act
        List<Booking> result = validator.findExpiredPendingBookings(now);

        // Assert
        assertEquals(List.of(expired), result);
    }

    @Test
    void shouldExpireBookingAndGiveSeatsBack() {
        // Arrange
        Leg leg = saveLegWithSeatTakenBy("1001");
        Flight flight = saveFlight(leg);
        Booking booking = booking("booking-1", BookingStatus.PENDING, LocalDateTime.now().minusMinutes(1), flight.getId());

        // Act
        validator.expire(booking);

        // Assert
        assertEquals(BookingStatus.EXPIRED, booking.getBookingStatus());
        assertEquals(3, leg.getAvailableSeats());
        assertFalse(leg.hasSeatAssigned("1001"));
        assertEquals(BookingStatus.EXPIRED,
                bookingRepository.findById("booking-1").orElseThrow().getBookingStatus());
    }

    @Test
    void shouldNotExpireBookingBeforeDeadline() {
        // Arrange
        Leg leg = saveLegWithSeatTakenBy("1001");
        Flight flight = saveFlight(leg);
        Booking booking = Booking.createPendingBooking(1, 500.0, flight.getId(), TripType.ONE_WAY);
        booking.addPassengers(List.of(new PassengerData("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE)));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> validator.expire(booking));
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
        assertEquals(2, leg.getAvailableSeats());
        assertTrue(leg.hasSeatAssigned("1001"));
        assertTrue(bookingRepository.findById(booking.getId()).isEmpty());
    }

    // Only for bookings whose payment deadline already passed: createPendingBooking always sets
    // the deadline 20 minutes ahead, so this is the only way to get one without waiting.
    private Booking booking(String id, BookingStatus status, LocalDateTime deadline, String idOutboundFlight) {
        Passenger passenger = Passenger.createPassenger("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);
        return Booking.reconstruct(id, 1, 500.0, new ArrayList<>(List.of(passenger)), idOutboundFlight, null,
                TripType.ONE_WAY, new ArrayList<>(), status, deadline, new ArrayList<>());
    }

    private Leg saveLegWithSeatTakenBy(String idPassenger) {
        Leg leg = Leg.createLeg(Airport.EL_EDEN, Airport.EL_DORADO,
                LocalDateTime.of(2026, 11, 5, 10, 0), LocalDateTime.of(2026, 11, 5, 12, 0),
                "aircraft-1", "AV", List.of(new Seat("001"), new Seat("002"), new Seat("003")), 2000, 3000);
        leg.reduceAvailableSeats(1);
        leg.assignSeat(leg.getSeats().get(0).getId(), idPassenger);
        legRepository.save(leg);
        return leg;
    }

    private Flight saveFlight(Leg leg) {
        Flight flight = Flight.createFlight(List.of(leg.toSummary()));
        flightRepository.save(flight);
        return flight;
    }
}