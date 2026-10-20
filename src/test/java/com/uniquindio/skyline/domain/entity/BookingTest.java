package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.PassengerData;
import com.uniquindio.skyline.domain.valueObject.PhonePrefix;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        assertEquals(1000000, booking.getPrice());
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
        assertEquals(1600.0, booking.getPrice(), 0.001);
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

    //Tests add passengers

    @Test
    void shouldAddPassengersToBooking() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, "flight001", TripType.ONE_WAY);

        // Act
        booking.addPassengers(List.of(passengerData("1001"), passengerData("1002")));

        // Assert
        assertEquals(2, booking.getPassengerList().size());
        assertEquals("1001", booking.getPassengerList().get(0).getPerson().getIdDocument());
        assertEquals("1002", booking.getPassengerList().get(1).getPerson().getIdDocument());
        assertFalse(booking.getPassengerList().get(0).isHolder());
    }

    @Test
    void shouldThrowExceptionWhenPassengerListIsNull() {
        Booking booking = Booking.createPendingBooking(2, 500.0, "flight001", TripType.ONE_WAY);

        assertThrows(DomainRuleException.class, () -> booking.addPassengers(null));
    }

    @Test
    void shouldThrowExceptionWhenPassengerListContainsNull() {
        Booking booking = Booking.createPendingBooking(2, 500.0, "flight001", TripType.ONE_WAY);
        List<PassengerData> passengers = new ArrayList<>();
        passengers.add(passengerData("1001"));
        passengers.add(null);

        assertThrows(DomainRuleException.class, () -> booking.addPassengers(passengers));
        assertTrue(booking.getPassengerList().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenAddingPassengersToCancelledBooking() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);
        booking.cancelBooking();

        assertThrows(DomainRuleException.class,
                () -> booking.addPassengers(List.of(passengerData("1001"))));
        assertTrue(booking.getPassengerList().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenPassengersWereAlreadyAdded() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);
        booking.addPassengers(List.of(passengerData("1001")));

        assertThrows(DomainRuleException.class,
                () -> booking.addPassengers(List.of(passengerData("1002"))));
        assertEquals(1, booking.getPassengerList().size());
    }

    @Test
    void shouldThrowExceptionWhenListSizeIsDifferentFromNumPassengers() {
        Booking booking = Booking.createPendingBooking(2, 500.0, "flight001", TripType.ONE_WAY);

        assertThrows(DomainRuleException.class,
                () -> booking.addPassengers(List.of(passengerData("1001"))));
        assertTrue(booking.getPassengerList().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenPassengersHaveSameDocument() {
        Booking booking = Booking.createPendingBooking(2, 500.0, "flight001", TripType.ONE_WAY);

        assertThrows(DomainRuleException.class,
                () -> booking.addPassengers(List.of(passengerData("1001"), passengerData("1001"))));
        assertTrue(booking.getPassengerList().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenPassengerDataIsInvalid() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);
        PassengerData invalid = new PassengerData("1001", DocumentType.CITIZENSHIP_ID, "  ", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);

        assertThrows(DomainRuleException.class, () -> booking.addPassengers(List.of(invalid)));
    }

    //Tests chose holder

    @Test
    void shouldChooseHolderAmongPassengers() {
        // Arrange
        Booking booking = Booking.createPendingBooking(2, 500.0, "flight001", TripType.ONE_WAY);
        booking.addPassengers(List.of(passengerData("1001"), passengerData("1002")));

        // Act
        booking.choseHolder("1002", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA);

        // Assert
        Passenger first = booking.getPassengerList().get(0);
        Passenger second = booking.getPassengerList().get(1);
        assertFalse(first.isHolder());
        assertTrue(second.isHolder());
        assertEquals(Optional.of("laura@mail.com"), second.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenHolderDocumentIsBlank() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);
        booking.addPassengers(List.of(passengerData("1001")));

        assertThrows(DomainRuleException.class,
                () -> booking.choseHolder("  ", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA));
    }

    @Test
    void shouldThrowExceptionWhenChoosingHolderWithoutPassengers() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);

        assertThrows(DomainRuleException.class,
                () -> booking.choseHolder("1001", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA));
    }

    @Test
    void shouldThrowExceptionWhenBookingAlreadyHasHolder() {
        Booking booking = Booking.createPendingBooking(2, 500.0, "flight001", TripType.ONE_WAY);
        booking.addPassengers(List.of(passengerData("1001"), passengerData("1002")));
        booking.choseHolder("1001", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA);

        assertThrows(DomainRuleException.class,
                () -> booking.choseHolder("1002", "otro@mail.com", "3007654321", PhonePrefix.COLOMBIA));
        assertFalse(booking.getPassengerList().get(1).isHolder());
    }

    @Test
    void shouldThrowExceptionWhenHolderIsNotAPassenger() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);
        booking.addPassengers(List.of(passengerData("1001")));

        assertThrows(DomainRuleException.class,
                () -> booking.choseHolder("9999", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA));
        assertFalse(booking.getPassengerList().get(0).isHolder());
    }

    @Test
    void shouldThrowExceptionWhenHolderIsUnderage() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);
        PassengerData minor = new PassengerData("1001", DocumentType.IDENTITY_CARD, "Sofia", null,
                "Duarte", null, LocalDate.now().minusYears(17), Gender.FEMALE);
        booking.addPassengers(List.of(minor));

        assertThrows(DomainRuleException.class,
                () -> booking.choseHolder("1001", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA));
        assertFalse(booking.getPassengerList().get(0).isHolder());
    }

    @Test
    void shouldChooseHolderWhenPassengerTurnsEighteenToday() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);
        PassengerData adult = new PassengerData("1001", DocumentType.CITIZENSHIP_ID, "Sofia", null,
                "Duarte", null, LocalDate.now().minusYears(18), Gender.FEMALE);
        booking.addPassengers(List.of(adult));

        booking.choseHolder("1001", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA);

        assertTrue(booking.getPassengerList().get(0).isHolder());
    }

    private PassengerData passengerData(String idDocument) {
        return new PassengerData(idDocument, DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);
    }

}
