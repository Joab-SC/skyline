package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.PassengerData;
import com.uniquindio.skyline.domain.valueObject.PaymentStatus;
import com.uniquindio.skyline.domain.valueObject.PhonePrefix;
import com.uniquindio.skyline.domain.valueObject.LuggageDetail;
import com.uniquindio.skyline.domain.valueObject.TripType;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.NoSuchElementException;
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
        assertTrue(booking.getDetailExtraServices().isEmpty());
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

    @Test
    void shouldThrowExceptionWhenAddingReturnFlightToNonPendingBooking() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2,
                500.0,
                "flight-outbound",
                TripType.ROUND_TRIP
        );
        booking.cancelBooking();

        // Act & Assert
        DomainRuleException exception = assertThrows(
                DomainRuleException.class,
                () -> booking.addReturnFlight("flight-return", 300.0)
        );
        assertEquals("Only pending bookings can be modified", exception.getMessage());
        assertTrue(booking.getIdReturnFlight().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenReturnFlightIsAlreadySelected() {
        // Arrange
        Booking booking = Booking.createPendingBooking(
                2,
                500.0,
                "flight-outbound",
                TripType.ROUND_TRIP
        );
        booking.addReturnFlight("flight-return", 300.0);

        // Act & Assert
        DomainRuleException exception = assertThrows(
                DomainRuleException.class,
                () -> booking.addReturnFlight("other-return", 400.0)
        );
        assertEquals("The booking already has a return flight", exception.getMessage());
        assertEquals(Optional.of("flight-return"), booking.getIdReturnFlight());
        assertEquals(1600.0, booking.getPrice(), 0.001);
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

    @Test
    void shouldCreateLuggageDetail() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight-outbound", TripType.ONE_WAY);

        // Act
        LuggageDetail luggageDetail = booking.createLuggageDetail(List.of("luggage-1"), 2, 100.0, 3);

        // Assert
        assertEquals(new LuggageDetail(List.of("luggage-1"), 2, 100.0, 3), luggageDetail);
    }

    @Test
    void shouldThrowExceptionWhenLuggageQuantityExceedsMaximum() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight-outbound", TripType.ONE_WAY);

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> booking.createLuggageDetail(List.of("luggage-1"), 4, 100.0, 3)
        );
    }

    //Tests register passenger luggage

    @Test
    void shouldRegisterPassengerLuggageAndUpdatePrice() {
        // Arrange
        Booking booking = createBookingWithPassenger("123");
        LuggageDetail luggageDetail = new LuggageDetail(List.of("luggage-1"), 2, 100.0, 3);

        // Act
        booking.registerPassengerLuggage("123", luggageDetail);

        // Assert
        assertEquals(luggageDetail, booking.getPassengerList().getFirst().getLuggageDetail().get());
        assertEquals(600.0, booking.getPrice(), 0.001);
    }

    @Test
    void shouldReplacePreviousLuggageAndAdjustPrice() {
        // Arrange
        Booking booking = createBookingWithPassenger("123");
        booking.registerPassengerLuggage("123", new LuggageDetail(List.of("luggage-1"), 2, 100.0, 3));
        LuggageDetail newLuggageDetail = new LuggageDetail(List.of("luggage-1"), 1, 50.0, 3);

        // Act
        booking.registerPassengerLuggage("123", newLuggageDetail);

        // Assert
        assertEquals(newLuggageDetail, booking.getPassengerList().getFirst().getLuggageDetail().get());
        assertEquals(550.0, booking.getPrice(), 0.001);
    }

    @Test
    void shouldThrowExceptionWhenPassengerIsNotInBooking() {
        // Arrange
        Booking booking = createBookingWithPassenger("123");

        // Act & Assert
        assertThrows(
                NoSuchElementException.class,
                () -> booking.registerPassengerLuggage("999", new LuggageDetail(List.of("luggage-1"), 1, 50.0, 3))
        );
    }

    @Test
    void shouldThrowExceptionWhenLuggageDetailIsNull() {
        // Arrange
        Booking booking = createBookingWithPassenger("123");

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> booking.registerPassengerLuggage("123", null)
        );
    }

    @Test
    void shouldThrowExceptionWhenRegisteringLuggageOnNonPendingBooking() {
        // Arrange
        Booking booking = createBookingWithPassenger("123");
        booking.cancelBooking();

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> booking.registerPassengerLuggage("123", new LuggageDetail(List.of("luggage-1"), 1, 50.0, 3))
        );

    }

    //Tests select leg seat

    @Test
    void shouldGetPassengerByIdDocument() {
        Booking booking = createBookingWithPassenger("1001");

        Passenger passenger = booking.getPassenger("1001").orElseThrow();

        assertEquals("1001", passenger.getPerson().getIdDocument());
    }

    @Test
    void shouldThrowExceptionWhenGettingPassengerNotInBooking() {
        Booking booking = createBookingWithPassenger("1001");

        assertTrue(booking.getPassenger("9999").isEmpty());
    }

    @Test
    void shouldNotThrowWhenPassengerIsInBooking() {
        Booking booking = createBookingWithPassenger("1001");
        Passenger passenger = booking.getPassenger("1001").orElseThrow();

        assertDoesNotThrow(() -> booking.isPassengerInBooking(passenger));
    }

    @Test
    void shouldThrowExceptionWhenPassengerBelongsToAnotherBooking() {
        Booking booking = createBookingWithPassenger("1001");
        Passenger otherPassenger = createBookingWithPassenger("2002").getPassenger("2002").orElseThrow();

        assertThrows(DomainRuleException.class, () -> booking.isPassengerInBooking(otherPassenger));
    }

    @Test
    void shouldNotThrowWhenBookingIsPending() {
        Booking booking = createBookingWithPassenger("1001");

        assertDoesNotThrow(booking::validateIsModifiable);
    }

    @Test
    void shouldThrowExceptionWhenBookingIsCancelled() {
        Booking booking = createBookingWithPassenger("1001");
        booking.cancelBooking();

        assertThrows(DomainRuleException.class, booking::validateIsModifiable);
    }

    @Test
    void shouldAddSeatsPriceToBooking() {
        // Arrange
        Booking booking = createBookingWithPassenger("1001");

        // Act
        booking.addPriceSeats(40.0);

        // Assert
        assertEquals(540.0, booking.getPrice(), 0.001);
    }

    @Test
    void shouldThrowExceptionWhenSeatsPriceIsNegative() {
        Booking booking = createBookingWithPassenger("1001");

        assertThrows(DomainRuleException.class, () -> booking.addPriceSeats(-1.0));
        assertEquals(500.0, booking.getPrice(), 0.001);
    }

    // Tests cancel and expire

    @Test
    void shouldCancelPendingBooking() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);

        // Act
        booking.cancelBooking();

        // Assert
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
    }

    @Test
    void shouldThrowExceptionWhenCancellingConfirmedBooking() {
        // Arrange
        Booking booking = createConfirmedBooking();

        // Act & Assert
        assertThrows(DomainRuleException.class, booking::cancelBooking);
        assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());
    }

    @Test
    void shouldThrowExceptionWhenCancellingCancelledBooking() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);
        booking.cancelBooking();

        // Act & Assert
        assertThrows(DomainRuleException.class, booking::cancelBooking);
    }

    @Test
    void shouldExpireBookingWhenPaymentDeadlinePassed() {
        // Arrange
        Booking booking = bookingWith(BookingStatus.PENDING, LocalDateTime.now().minusMinutes(1));

        // Act
        booking.expireBooking();

        // Assert
        assertEquals(BookingStatus.EXPIRED, booking.getBookingStatus());
    }

    @Test
    void shouldThrowExceptionWhenExpiringBeforePaymentDeadline() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);

        // Act & Assert
        assertThrows(DomainRuleException.class, booking::expireBooking);
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
    }

    @Test
    void shouldThrowExceptionWhenExpiringNonPendingBooking() {
        // Arrange
        Booking booking = bookingWith(BookingStatus.CANCELLED, LocalDateTime.now().minusMinutes(1));

        // Act & Assert
        assertThrows(DomainRuleException.class, booking::expireBooking);
        assertEquals(BookingStatus.CANCELLED, booking.getBookingStatus());
    }

    @Test
    void shouldReturnOnlyOutboundFlightIdWhenThereIsNoReturnFlight() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight-outbound", TripType.ONE_WAY);

        assertEquals(List.of("flight-outbound"), booking.getFlightIds());
    }

    @Test
    void shouldReturnOutboundAndReturnFlightIds() {
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight-outbound", TripType.ROUND_TRIP);
        booking.addReturnFlight("flight-return", 300.0);

        assertEquals(List.of("flight-outbound", "flight-return"), booking.getFlightIds());
    }

    // Tests payment deadline on modifications

    @Test
    void shouldThrowExceptionWhenAddingReturnFlightAfterDeadline() {
        // Arrange
        Booking booking = expiredBooking(TripType.ROUND_TRIP, new ArrayList<>());

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> booking.addReturnFlight("flight-return", 300.0));
        assertTrue(booking.getIdReturnFlight().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenAddingPassengersAfterDeadline() {
        // Arrange
        Booking booking = expiredBooking(TripType.ONE_WAY, new ArrayList<>());

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> booking.addPassengers(List.of(passengerData("1001"))));
        assertTrue(booking.getPassengerList().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenChoosingHolderAfterDeadline() {
        // Arrange
        Booking booking = expiredBooking(TripType.ONE_WAY, new ArrayList<>(List.of(passenger("1001"))));

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> booking.choseHolder("1001", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA));
        assertFalse(booking.getPassengerList().getFirst().isHolder());
    }

    @Test
    void shouldThrowExceptionWhenRegisteringLuggageAfterDeadline() {
        // Arrange
        Booking booking = expiredBooking(TripType.ONE_WAY, new ArrayList<>(List.of(passenger("1001"))));

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> booking.registerPassengerLuggage("1001", new LuggageDetail(List.of("luggage-1"), 1, 50.0, 3)));
        assertEquals(500.0, booking.getPrice(), 0.001);
    }

    // Tests payments

    @Test
    void shouldCreateBookingWithoutPayments() {
        // Act
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);

        // Assert
        assertTrue(booking.getPayments().isEmpty());
        assertTrue(booking.findPendingPayment().isEmpty());
    }

    @Test
    void shouldStartPendingPaymentForTheBookingPrice() {
        // Arrange
        Booking booking = createPayableBooking();

        // Act
        Payment payment = booking.startPayment();

        // Assert
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertEquals(booking.getPrice(), payment.getAmount(), 0.001);
        assertEquals(List.of(payment), booking.getPayments());
        assertEquals(Optional.of(payment), booking.findPendingPayment());
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
    }

    @Test
    void shouldThrowExceptionWhenStartingPaymentWithoutAllPassengers() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 500.0, "flight001", TripType.ONE_WAY);

        // Act & Assert
        assertThrows(DomainRuleException.class, booking::startPayment);
        assertTrue(booking.getPayments().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenStartingPaymentWithoutHolder() {
        // Arrange
        Booking booking = createBookingWithPassenger("1001");

        // Act & Assert
        assertThrows(DomainRuleException.class, booking::startPayment);
        assertTrue(booking.getPayments().isEmpty());
    }

    @Test
    void shouldResumePendingPaymentInsteadOfCreatingAnother() {
        // Arrange
        Booking booking = createPayableBooking();
        Payment pending = booking.startPayment();

        // Act
        Payment resumed = booking.startPayment();

        // Assert
        assertEquals(pending.getId(), resumed.getId());
        assertEquals(List.of(pending), booking.getPayments());
        assertTrue(resumed.isPending());
    }

    @Test
    void shouldStartNewPaymentAfterADeclinedOne() {
        // Arrange
        Booking booking = createPayableBooking();
        Payment declined = booking.startPayment();
        booking.registerPaymentResult(declined.getId(), PaymentStatus.DECLINED, null, "Insufficient funds");

        // Act
        Payment retry = booking.startPayment();

        // Assert
        assertNotEquals(declined.getId(), retry.getId());
        assertEquals(List.of(declined, retry), booking.getPayments());
        assertEquals(Optional.of(retry), booking.findPendingPayment());
    }

    @Test
    void shouldThrowExceptionWhenStartingPaymentOnConfirmedBooking() {
        // Arrange
        Booking booking = createConfirmedBooking();

        // Act & Assert
        assertThrows(DomainRuleException.class, booking::startPayment);
        assertEquals(1, booking.getPayments().size());
    }

    @Test
    void shouldThrowExceptionWhenStartingPaymentOnCancelledBooking() {
        // Arrange
        Booking booking = createPayableBooking();
        booking.cancelBooking();

        // Act & Assert
        assertThrows(DomainRuleException.class, booking::startPayment);
        assertTrue(booking.getPayments().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenStartingPaymentAfterDeadline() {
        // Arrange
        Booking booking = bookingWith(BookingStatus.PENDING, LocalDateTime.now().minusMinutes(1));

        // Act & Assert
        assertThrows(DomainRuleException.class, booking::startPayment);
        assertTrue(booking.getPayments().isEmpty());
    }

    @Test
    void shouldConfirmBookingWhenPaymentIsApproved() {
        // Arrange
        Booking booking = createPayableBooking();
        Payment payment = booking.startPayment();

        // Act
        booking.registerPaymentResult(payment.getId(), PaymentStatus.APPROVED, "TX-1", null);

        // Assert
        assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());
        assertTrue(payment.isApproved());
        assertEquals(Optional.of("TX-1"), payment.getTransactionId());
        assertTrue(booking.findPendingPayment().isEmpty());
    }

    @Test
    void shouldKeepBookingPendingWhenPaymentIsDeclined() {
        // Arrange
        Booking booking = createPayableBooking();
        Payment payment = booking.startPayment();

        // Act
        booking.registerPaymentResult(payment.getId(), PaymentStatus.DECLINED, null, "Insufficient funds");

        // Assert
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
        assertEquals(PaymentStatus.DECLINED, payment.getStatus());
        assertEquals(Optional.of("Insufficient funds"), payment.getMessage());
    }

    @Test
    void shouldKeepBookingPendingWhenPaymentEndsWithError() {
        // Arrange
        Booking booking = createPayableBooking();
        Payment payment = booking.startPayment();

        // Act
        booking.registerPaymentResult(payment.getId(), PaymentStatus.ERROR, null, "Provider unavailable");

        // Assert
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
        assertEquals(PaymentStatus.ERROR, payment.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenRegisteringResultOfUnknownPayment() {
        // Arrange
        Booking booking = createPayableBooking();
        booking.startPayment();

        // Act & Assert
        assertThrows(NoSuchElementException.class,
                () -> booking.registerPaymentResult("payment-not-in-booking", PaymentStatus.APPROVED, "TX-1", null));
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
    }

    @Test
    void shouldThrowExceptionWhenRegisteringResultTwice() {
        // Arrange
        Booking booking = createPayableBooking();
        Payment payment = booking.startPayment();
        booking.registerPaymentResult(payment.getId(), PaymentStatus.DECLINED, null, "Insufficient funds");

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> booking.registerPaymentResult(payment.getId(), PaymentStatus.APPROVED, "TX-1", null));
        assertEquals(BookingStatus.PENDING, booking.getBookingStatus());
        assertEquals(PaymentStatus.DECLINED, payment.getStatus());
    }

    @Test
    void shouldReturnHolderEmail() {
        // Arrange
        Booking booking = createPayableBooking();

        // Act & Assert
        assertEquals("laura@mail.com", booking.getHolderEmail());
    }

    @Test
    void shouldThrowExceptionWhenBookingHasNoHolderEmail() {
        // Arrange
        Booking booking = createBookingWithPassenger("1001");

        // Act & Assert
        assertThrows(DomainRuleException.class, booking::getHolderEmail);
    }

    // Pending booking with its passenger and holder, ready to be paid.
    private Booking createPayableBooking() {
        Booking booking = createBookingWithPassenger("1001");
        booking.choseHolder("1001", "laura@mail.com", "3001234567", PhonePrefix.COLOMBIA);
        return booking;
    }

    // Pays the booking through its payment flow, so it ends CONFIRMED.
    private Booking createConfirmedBooking() {
        Booking booking = createPayableBooking();
        Payment payment = booking.startPayment();
        booking.registerPaymentResult(payment.getId(), PaymentStatus.APPROVED, "TX-1", null);
        return booking;
    }

    // Only for bookings whose payment deadline already passed: createPendingBooking always sets
    // the deadline 20 minutes ahead, so this is the only way to get one without waiting.
    private Booking bookingWith(BookingStatus status, LocalDateTime paymentDeadline) {
        return Booking.reconstruct("booking-1", 1, 500.0, new ArrayList<>(), "flight001", null,
                TripType.ONE_WAY, new ArrayList<>(), status, paymentDeadline, new ArrayList<>());
    }

    // A pending booking whose payment deadline passed one minute ago.
    private Booking expiredBooking(TripType tripType, List<Passenger> passengers) {
        return Booking.reconstruct("booking-1", 1, 500.0, passengers, "flight001", null,
                tripType, new ArrayList<>(), BookingStatus.PENDING, LocalDateTime.now().minusMinutes(1), new ArrayList<>());
    }

    private Passenger passenger(String idDocument) {
        return Passenger.createPassenger(idDocument, DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);
    }

    private PassengerData passengerData(String idDocument) {
        return new PassengerData(idDocument, DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);
    }

    private Booking createBookingWithPassenger(String idDocument) {
        Booking booking = Booking.createPendingBooking(
                1,
                500.0,
                "flight-outbound",
                TripType.ONE_WAY
        );
        booking.addPassengers(List.of(passengerData(idDocument)));
        return booking;
    }
}
