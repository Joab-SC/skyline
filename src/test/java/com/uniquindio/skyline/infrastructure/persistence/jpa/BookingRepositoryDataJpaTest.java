package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.entity.Person;
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

    // One way booking without passengers or extras, with the given status and payment deadline.
    // Only for bookings whose payment deadline already passed: createPendingBooking always sets
    // the deadline 20 minutes ahead, so this is the only way to get one without waiting.
    private Booking createBookingWith(BookingStatus status, LocalDateTime paymentDeadline) {
        return Booking.reconstruct(UUID.randomUUID().toString(), 1, 300000, new ArrayList<>(),
                OUTBOUND_FLIGHT_ID, null, TripType.ONE_WAY, new ArrayList<>(), status, paymentDeadline, new ArrayList<>());
    }
}
