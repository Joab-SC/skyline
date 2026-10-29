package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.entity.Payment;
import com.uniquindio.skyline.domain.entity.Person;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.BookingRepository;
import com.uniquindio.skyline.domain.valueObject.*;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.BookingPersistenceMapper;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.PersonPersistenceMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({BookingRepositoryJpa.class, BookingPersistenceMapper.class, PersonPersistenceMapper.class})
class BookingRepositoryDataJpaTest {

    private static final String OUTBOUND_FLIGHT_ID = "flight-bog-mde-01";
    private static final String RETURN_FLIGHT_ID = "flight-mde-bog-01";
    private static final String HOLDER_DOCUMENT = "1094000001";
    private static final String COMPANION_DOCUMENT = "1094000002";

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager entityManager;

    // Round trip booking with two passengers, a holder, luggage for the holder and two extra services.
    private Booking createBooking() {
        Booking booking = Booking.createPendingBooking(2, 300000, OUTBOUND_FLIGHT_ID, TripType.ROUND_TRIP);
        booking.addReturnFlight(RETURN_FLIGHT_ID, 250000);
        booking.addPassengers(List.of(
                new PassengerData(HOLDER_DOCUMENT, DocumentType.CITIZENSHIP_ID, "Ana", "Maria",
                        "Gomez", "Lopez", LocalDate.of(1990, 5, 20), Gender.FEMALE),
                new PassengerData(COMPANION_DOCUMENT, DocumentType.CITIZENSHIP_ID, "Luis", null,
                        "Perez", null, LocalDate.of(2015, 8, 10), Gender.MALE)));
        booking.choseHolder(HOLDER_DOCUMENT, "ana.gomez@mail.com", "3001234567", PhonePrefix.COLOMBIA);
        booking.registerPassengerLuggage(HOLDER_DOCUMENT,
                booking.createLuggageDetail(List.of("luggage-bog-mde-01", "luggage-mde-bog-01"), 2, 160000, 3));
        booking.addExtraService("extra-wifi", 1, 20000);
        booking.addExtraService("extra-meal", 2, 15000);
        return booking;
    }


    private Booking saveAndRecover(Booking booking) {
        bookingRepository.save(booking);
        return bookingRepository.findById(booking.getId()).orElseThrow();
    }

    @Test
    void shouldSaveAndRecoverBookingWithItsBasicData() {
        // Arrange
        Booking booking = createBooking();

        // Act
        Booking recovered = saveAndRecover(booking);

        // Assert
        assertEquals(booking.getId(), recovered.getId());
        assertEquals(2, recovered.getNumPassengers());
        assertEquals(booking.getPrice(), recovered.getPrice());
        assertEquals(OUTBOUND_FLIGHT_ID, recovered.getIdOutboundFlight());
        assertEquals(RETURN_FLIGHT_ID, recovered.getIdReturnFlight().orElseThrow());
        assertEquals(TripType.ROUND_TRIP, recovered.getTripType());
        assertEquals(BookingStatus.PENDING, recovered.getBookingStatus());
        assertTrue(Duration.between(booking.getPaymentDeadline(), recovered.getPaymentDeadline()).abs().toMillis() < 1);
    }

    @Test
    void shouldSaveAndRecoverBookingWithItsPassengers() {
        // Arrange
        Booking booking = createBooking();

        // Act
        Booking recovered = saveAndRecover(booking);

        // Assert
        assertEquals(booking.getPassengerList().size(), recovered.getPassengerList().size());
        for (Passenger expected : booking.getPassengerList()) {
            Person expectedPerson = expected.getPerson();
            Passenger actual = recovered.getPassenger(expectedPerson.getIdDocument()).orElseThrow();
            Person actualPerson = actual.getPerson();

            assertEquals(expectedPerson.getDocumentType(), actualPerson.getDocumentType());
            assertEquals(expectedPerson.getFirstName(), actualPerson.getFirstName());
            assertEquals(expectedPerson.getSecondName(), actualPerson.getSecondName());
            assertEquals(expectedPerson.getLastName1(), actualPerson.getLastName1());
            assertEquals(expectedPerson.getLastName2(), actualPerson.getLastName2());
            assertEquals(expectedPerson.getBirthDate(), actualPerson.getBirthDate());
            assertEquals(expectedPerson.getGender(), actualPerson.getGender());
            assertEquals(expected.isHolder(), actual.isHolder());
            assertEquals(expected.getEmail(), actual.getEmail());
            assertEquals(expected.getPhoneNumber(), actual.getPhoneNumber());
            assertEquals(expected.getPhonePrefix(), actual.getPhonePrefix());
        }
    }

    @Test
    void shouldSaveAndRecoverLuggageDetailOfEachPassenger() {
        // Arrange: only the holder has luggage
        Booking booking = createBooking();

        // Act
        Booking recovered = saveAndRecover(booking);

        // Assert
        for (Passenger expected : booking.getPassengerList()) {
            Passenger actual = recovered.getPassenger(expected.getPerson().getIdDocument()).orElseThrow();
            assertEquals(expected.getLuggageDetail().isPresent(), actual.getLuggageDetail().isPresent());

            if (expected.getLuggageDetail().isPresent()) {
                LuggageDetail expectedLuggage = expected.getLuggageDetail().get();
                LuggageDetail actualLuggage = actual.getLuggageDetail().get();
                assertEquals(expectedLuggage.quantity(), actualLuggage.quantity());
                assertEquals(expectedLuggage.subTotal(), actualLuggage.subTotal());
                assertEquals(expectedLuggage.maxQuantity(), actualLuggage.maxQuantity());
                assertEquals(expectedLuggage.luggageIds().size(), actualLuggage.luggageIds().size());
                assertTrue(actualLuggage.luggageIds().containsAll(expectedLuggage.luggageIds()));
            }
        }
    }

    @Test
    void shouldSaveAndRecoverBookingWithItsExtraServices() {
        // Arrange
        Booking booking = createBooking();

        // Act
        Booking recovered = saveAndRecover(booking);

        // Assert
        assertEquals(booking.getDetailExtraServices().size(), recovered.getDetailExtraServices().size());
        for (DetailExtraService expected : booking.getDetailExtraServices()) {
            DetailExtraService actual = recovered.getDetailExtraServices().stream()
                    .filter(detail -> detail.idExtraService().equals(expected.idExtraService()))
                    .findFirst().orElseThrow();
            assertEquals(expected.quantity(), actual.quantity());
            assertEquals(expected.subtotal(), actual.subtotal());
        }
    }

    @Test
    void shouldSaveSamePersonAsPassengerInTwoBookings() {
        // Arrange: the passenger key is (booking, document), so the same person can be in both bookings
        Booking firstBooking = createBooking();
        Booking secondBooking = createBooking();

        // Act
        bookingRepository.save(firstBooking);
        bookingRepository.save(secondBooking);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Booking firstRecovered = bookingRepository.findById(firstBooking.getId()).orElseThrow();
        Booking secondRecovered = bookingRepository.findById(secondBooking.getId()).orElseThrow();
        assertTrue(firstRecovered.getPassenger(HOLDER_DOCUMENT).isPresent());
        assertTrue(secondRecovered.getPassenger(HOLDER_DOCUMENT).isPresent());
        assertEquals(2, firstRecovered.getPassengerList().size());
        assertEquals(2, secondRecovered.getPassengerList().size());
    }

    @Test
    void shouldReturnEmptyWhenBookingDoesNotExist() {
        // Arrange
        bookingRepository.save(createBooking());

        // Act
        Optional<Booking> result = bookingRepository.findById("booking-does-not-exist");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindOnlyPendingBookingsWhosePaymentDeadlineHasPassed() {
        // Arrange: a new booking has its deadline 20 minutes ahead, so it is not expired yet
        LocalDateTime now = LocalDateTime.now();
        Booking pendingExpired = createBookingWith(BookingStatus.PENDING, now.minusMinutes(5));
        Booking pendingNotExpired = Booking.createPendingBooking(1, 300000, OUTBOUND_FLIGHT_ID, TripType.ONE_WAY);
        Booking cancelledExpired = createBookingWith(BookingStatus.CANCELLED, now.minusMinutes(5));
        bookingRepository.save(pendingExpired);
        bookingRepository.save(pendingNotExpired);
        bookingRepository.save(cancelledExpired);

        // Act
        Set<String> found = bookingRepository
                .findByStatusAndPaymentDeadlineLessThanEqual(BookingStatus.PENDING, now).stream()
                .map(Booking::getId)
                .collect(Collectors.toSet());

        // Assert
        assertEquals(Set.of(pendingExpired.getId()), found);
    }

    @Test
    void shouldSaveAndRecoverOneWayBookingWithoutReturnFlight() {
        // Arrange
        Booking booking = Booking.createPendingBooking(1, 300000, OUTBOUND_FLIGHT_ID, TripType.ONE_WAY);

        // Act
        Booking recovered = saveAndRecover(booking);

        // Assert
        assertEquals(TripType.ONE_WAY, recovered.getTripType());
        assertTrue(recovered.getIdReturnFlight().isEmpty());
    }

    // Tests payments. Payments are saved with their booking, because they belong to the Booking aggregate.

    @Test
    void shouldSaveAndRecoverBookingWithoutPayments() {
        // Arrange
        Booking booking = createBooking();

        // Act
        Booking recovered = saveAndRecover(booking);

        // Assert
        assertTrue(recovered.getPayments().isEmpty());
    }

    @Test
    void shouldSaveAndRecoverPendingPaymentWithAllItsData() {
        // Arrange
        Booking booking = createBooking();
        Payment payment = booking.startPayment();

        // Act
        Booking recovered = saveAndRecover(booking);

        // Assert: the only test that checks every column of the payment
        assertEquals(List.of(payment), recovered.getPayments());
        Payment actual = recovered.getPayments().get(0);
        assertEquals(payment.getAmount(), actual.getAmount());
        assertEquals(PaymentStatus.PENDING, actual.getStatus());
        assertTrue(actual.getTransactionId().isEmpty());
        assertTrue(actual.getMessage().isEmpty());
        assertTrue(Duration.between(payment.getCreatedAt(), actual.getCreatedAt()).abs().toMillis() < 1);
        assertEquals(Optional.of(actual), recovered.findPendingPayment());
    }

    @Test
    void shouldSaveAndRecoverApprovedPaymentAndConfirmedBooking() {
        // Arrange: same two saves as the PayBooking use case
        Booking booking = createBooking();
        Payment payment = booking.startPayment();
        saveAndRecover(booking);
        booking.registerPaymentResult(payment.getId(), PaymentStatus.APPROVED, "TX-1", null);

        // Act
        Booking recovered = saveAndRecover(booking);

        // Assert
        assertEquals(BookingStatus.CONFIRMED, recovered.getBookingStatus());
        assertEquals(List.of(payment), recovered.getPayments());
        Payment actual = recovered.getPayments().get(0);
        assertEquals(PaymentStatus.APPROVED, actual.getStatus());
        assertEquals(Optional.of("TX-1"), actual.getTransactionId());
        assertTrue(actual.getMessage().isEmpty());
    }

    @Test
    void shouldSaveAndRecoverDeclinedPaymentWithItsMessage() {
        // Arrange
        Booking booking = createBooking();
        Payment payment = booking.startPayment();
        saveAndRecover(booking);
        booking.registerPaymentResult(payment.getId(), PaymentStatus.DECLINED, null, "Insufficient funds");

        // Act
        Booking recovered = saveAndRecover(booking);

        // Assert
        assertEquals(BookingStatus.PENDING, recovered.getBookingStatus());
        assertEquals(List.of(payment), recovered.getPayments());
        Payment actual = recovered.getPayments().get(0);
        assertEquals(PaymentStatus.DECLINED, actual.getStatus());
        assertEquals(Optional.of("Insufficient funds"), actual.getMessage());
        assertTrue(actual.getTransactionId().isEmpty());
        assertTrue(recovered.findPendingPayment().isEmpty());
    }

    @Test
    void shouldSaveAndRecoverEveryPaymentAttempt() {
        // Arrange: the first attempt is declined and the second one is approved
        Booking booking = createBooking();
        Payment declined = booking.startPayment();
        booking.registerPaymentResult(declined.getId(), PaymentStatus.DECLINED, null, "Insufficient funds");
        Payment approved = booking.startPayment();
        booking.registerPaymentResult(approved.getId(), PaymentStatus.APPROVED, "TX-2", null);

        // Act
        Booking recovered = saveAndRecover(booking);

        // Assert: the database does not keep the order, so the attempts are found by equals
        assertEquals(2, recovered.getPayments().size());
        assertTrue(recovered.getPayments().containsAll(List.of(declined, approved)));
        assertEquals(PaymentStatus.DECLINED, findPayment(recovered, declined).getStatus());
        assertEquals(PaymentStatus.APPROVED, findPayment(recovered, approved).getStatus());
        assertEquals(BookingStatus.CONFIRMED, recovered.getBookingStatus());
    }

    @Test
    void shouldUpdatePaymentResultWithoutDuplicatingIt() {
        // Arrange
        Booking booking = createBooking();
        Payment payment = booking.startPayment();
        saveAndRecover(booking);
        booking.registerPaymentResult(payment.getId(), PaymentStatus.APPROVED, "TX-1", null);

        // Act
        Booking recovered = saveAndRecover(booking);

        // Assert
        assertEquals(List.of(payment), recovered.getPayments());
        assertEquals(PaymentStatus.APPROVED, recovered.getPayments().get(0).getStatus());
    }

    @Test
    void shouldRecoverOnlyThePaymentsOfEachBooking() {
        // Arrange
        Booking firstBooking = createBooking();
        Booking secondBooking = createBooking();
        Payment firstPayment = firstBooking.startPayment();
        Payment secondPayment = secondBooking.startPayment();

        // Act
        Booking firstRecovered = saveAndRecover(firstBooking);
        Booking secondRecovered = saveAndRecover(secondBooking);

        // Assert
        assertEquals(List.of(firstPayment), firstRecovered.getPayments());
        assertEquals(List.of(secondPayment), secondRecovered.getPayments());
    }

    @Test
    void shouldResumePendingPaymentOfRecoveredBooking() {
        // Arrange
        Booking booking = createBooking();
        Payment pending = booking.startPayment();
        Booking recovered = saveAndRecover(booking);

        // Act
        Payment resumed = recovered.startPayment();

        // Assert
        assertEquals(pending, resumed);
        assertEquals(List.of(pending), recovered.getPayments());
    }

    @Test
    void shouldNotStartPaymentOnRecoveredConfirmedBooking() {
        // Arrange
        Booking booking = createBooking();
        Payment payment = booking.startPayment();
        booking.registerPaymentResult(payment.getId(), PaymentStatus.APPROVED, "TX-1", null);
        Booking recovered = saveAndRecover(booking);

        // Act & Assert
        assertThrows(DomainRuleException.class, recovered::startPayment);
        assertEquals(List.of(payment), recovered.getPayments());
    }

    private Payment findPayment(Booking booking, Payment expected) {
        return booking.getPayments().stream()
                .filter(expected::equals)
                .findFirst()
                .orElseThrow();
    }

    // One way booking without passengers or extras, with the given status and payment deadline.
    // Only for bookings whose payment deadline already passed: createPendingBooking always sets
    // the deadline 20 minutes ahead, so this is the only way to get one without waiting.
    private Booking createBookingWith(BookingStatus status, LocalDateTime paymentDeadline) {
        return Booking.reconstruct(UUID.randomUUID().toString(), 1, 300000, new ArrayList<>(),
                OUTBOUND_FLIGHT_ID, null, TripType.ONE_WAY, new ArrayList<>(), status, paymentDeadline, new ArrayList<>());
    }
}
