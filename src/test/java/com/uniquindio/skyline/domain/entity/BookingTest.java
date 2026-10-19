package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

    @Test
    void bookingIsCreatedWithPendingStatus() {
        Booking booking = Booking.createPendingBooking(
                2,
                500000,
                "flight001",
                TripType.ONE_WAY
        );

        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
    }

    @Test
    void bookingIsCreatedWithCorrectData() {
        Booking booking = Booking.createPendingBooking(
                2,
                500000,
                "flight001",
                TripType.ONE_WAY
        );

        assertEquals(2, booking.getNumPassengers());
        assertEquals(500000, booking.getPrice());
        assertEquals("flight001", booking.getIdOutboundFlight());
        assertEquals(Optional.empty(), booking.getIdReturnFlight());
        assertEquals(TripType.ONE_WAY, booking.getTripType());
        assertTrue(booking.getPassengerList().isEmpty());
        assertTrue(booking.getExtraServices().isEmpty());
        assertNotNull(booking.getId());
    }

    @Test
    void bookingThrowsExceptionWhenNumberOfPassengersIsZero() {
        assertThrows(DomainRuleException.class, () ->
                Booking.createPendingBooking(
                        0,
                        500000,
                        "flight001",
                        TripType.ONE_WAY
                )
        );
    }

    @Test
    void bookingThrowsExceptionWhenNumberOfPassengersIsNegative() {
        assertThrows(DomainRuleException.class, () ->
                Booking.createPendingBooking(
                        -1,
                        500000,
                        "flight001",
                        TripType.ONE_WAY
                )
        );
    }

    @Test
    void bookingThrowsExceptionWhenOutboundFlightIdIsNull() {
        assertThrows(DomainRuleException.class, () ->
                Booking.createPendingBooking(
                        2,
                        500000,
                        null,
                        TripType.ONE_WAY
                )
        );
    }

    @Test
    void bookingThrowsExceptionWhenOutboundFlightIdIsBlank() {
        assertThrows(DomainRuleException.class, () ->
                Booking.createPendingBooking(
                        2,
                        500000,
                        "   ",
                        TripType.ONE_WAY
                )
        );
    }

    @Test
    void bookingThrowsExceptionWhenTripTypeIsNull() {
        assertThrows(DomainRuleException.class, () ->
                Booking.createPendingBooking(
                        2,
                        500000,
                        "flight001",
                        null
                )
        );
    }

    @Test
    void twoBookingsHaveDifferentIds() {
        Booking firstBooking = Booking.createPendingBooking(
                2,
                500000,
                "flight001",
                TripType.ONE_WAY
        );

        Booking secondBooking = Booking.createPendingBooking(
                2,
                500000,
                "flight001",
                TripType.ONE_WAY
        );

        assertNotEquals(firstBooking.getId(), secondBooking.getId());
        assertNotEquals(firstBooking, secondBooking);
    }

    //Tests add return flight


    @Test
    void shouldAddReturnFlightAndUpdatePrice() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2,
                500.0,
                "flight-outbound",
                TripType.ROUND_TRIP
        );

        // Act
        booking.addReturnFlight("flight-return", 300.0);

        // Assert
        assertEquals(
                Optional.of("flight-return"),
                booking.getIdReturnFlight()
        );
        assertEquals(800.0, booking.getPrice(), 0.001);
    }

    @Test
    void shouldThrowExceptionWhenAddingReturnFlightToOneWayTrip() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2,
                500.0,
                "flight-outbound",
                TripType.ONE_WAY
        );

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> booking.addReturnFlight("flight-return", 300.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightIdIsNull() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2,
                500.0,
                "flight-outbound",
                TripType.ROUND_TRIP
        );

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> booking.addReturnFlight(null, 300.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightIdIsBlank() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2,
                500.0,
                "flight-outbound",
                TripType.ROUND_TRIP
        );

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> booking.addReturnFlight("   ", 300.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightPriceIsZero() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2,
                500.0,
                "flight-outbound",
                TripType.ROUND_TRIP
        );

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> booking.addReturnFlight("flight-return", 0.0)
        );
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightPriceIsNegative() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2,
                500.0,
                "flight-outbound",
                TripType.ROUND_TRIP
        );

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> booking.addReturnFlight("flight-return", -100.0)
        );
    }

}
