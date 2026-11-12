package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.entity.LegSeat;
import com.uniquindio.skyline.domain.repository.BookingSeat;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.domain.valueObject.PassengerBookingId;
import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.LegPersistenceMapper;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.LegSeatPersistenceMapper;
import com.uniquindio.skyline.infrastructure.persistence.jpa.mapper.LuggagePersistenceMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({LegRepositoryJpa.class, LegPersistenceMapper.class, LegSeatPersistenceMapper.class,
        LuggagePersistenceMapper.class})
class LegRepositoryDataJpaTest {

    private static final LocalDateTime DEPARTURE_TIME = LocalDateTime.of(2026, 12, 10, 14, 30);
    private static final LocalDateTime ARRIVAL_TIME = LocalDateTime.of(2026, 12, 10, 16, 0);

    @Autowired
    private LegRepository legRepository;

    @Autowired
    private EntityManager entityManager;


    // Creates a new leg with three seats: 1A, 1B and 1C.
    private Leg createLeg() {
        return Leg.createLeg(Airport.EL_DORADO, Airport.EL_EDEN, DEPARTURE_TIME, ARRIVAL_TIME,
                "aircraft-a320-02", "airline-avianca",
                List.of(new Seat("1A"), new Seat("1B"), new Seat("1C")), 75000, 180000);
    }

    // Saves the leg and reads it back from the database, not from the Hibernate cache.
    private Leg saveAndRecover(Leg leg) {
        legRepository.save(leg);
        return legRepository.findById(leg.getId()).orElseThrow();
    }

    private Set<String> idsOf(List<Leg> legs) {
        return legs.stream().map(Leg::getId).collect(Collectors.toSet());
    }

    @Test
    void shouldSaveAndRecoverLegWithItsData() {
        // Arrange
        Leg leg = createLeg();

        // Act
        Leg recovered = saveAndRecover(leg);

        // Assert
        assertEquals(leg.getId(), recovered.getId());
        assertEquals(Airport.EL_DORADO, recovered.getOriginAirport());
        assertEquals(Airport.EL_EDEN, recovered.getDestinationAirport());
        assertEquals(DEPARTURE_TIME, recovered.getDepartureTime());
        assertEquals(ARRIVAL_TIME, recovered.getArrivalTime());
        assertEquals("aircraft-a320-02", recovered.getAircraftId());
        assertEquals("airline-avianca", recovered.getAirlineId());
        assertEquals(180000, recovered.getPrice());
        assertEquals(3, recovered.getAvailableSeats());
    }

    @Test
    void shouldStoreAirportsAsTheirIataCode() {
        // Arrange
        Leg leg = createLeg();

        // Act
        legRepository.save(leg);
        entityManager.flush();
        Object[] row = (Object[]) entityManager
                .createNativeQuery("SELECT origin_airport, destination_airport FROM legs WHERE id = ?1")
                .setParameter(1, leg.getId())
                .getSingleResult();

        // Assert: the column holds "BOG", not the constant name "EL_DORADO" (AirportConverter).
        assertEquals("BOG", row[0]);
        assertEquals("AXM", row[1]);
    }

    @Test
    void shouldSaveAndRecoverLegWithItsLuggage() {
        // Arrange
        Leg leg = createLeg();

        // Act
        Leg recovered = saveAndRecover(leg);

        // Assert
        assertNotNull(recovered.getLuggage());
        assertEquals(leg.getLuggage().getId(), recovered.getLuggage().getId());
        assertEquals(75000, recovered.getLuggage().getPrice());
    }

    @Test
    void shouldSaveAndRecoverLegWithItsSeatsInAvailableStateAndEmptyPassenger() {
        // Arrange
        Leg leg = createLeg();

        // Act
        Leg recovered = saveAndRecover(leg);

        // Assert
        assertEquals(leg.getSeats().size(), recovered.getSeats().size());
        for (LegSeat seat : leg.getSeats()) {
            LegSeat recoveredSeat = recovered.getLegSeatById(seat.getId()).orElseThrow();
            assertEquals(seat.getSeatCode(), recoveredSeat.getSeatCode());
            assertEquals(SeatStatus.AVAILABLE, recoveredSeat.getSeatStatus());
            assertTrue(recoveredSeat.getPassengerBookingId().isEmpty());
        }
    }

    @Test
    void shouldPersistSeatAsOccupiedAfterAssigningIt() {
        // Arrange
        Leg leg = saveAndRecover(createLeg());
        String seatId = leg.getSeats().getFirst().getId();

        // Act
        PassengerBookingId occupant = new PassengerBookingId("booking-01", "passenger-01");
        leg.assignSeat(seatId, occupant);
        Leg recovered = saveAndRecover(leg);

        // Assert
        LegSeat recoveredSeat = recovered.getLegSeatById(seatId).orElseThrow();
        assertEquals(SeatStatus.OCCUPIED, recoveredSeat.getSeatStatus());
        assertEquals(Optional.of(occupant), recoveredSeat.getPassengerBookingId());
    }

    @Test
    void shouldFindLegsWithAtLeastAvailableSeats() {
        // Arrange: three legs with 3, 1 and 0 available seats
        Leg legWithThree = createLeg();
        Leg legWithOne = createLeg();
        legWithOne.reduceAvailableSeats(2);
        Leg legWithNone = createLeg();
        legWithNone.reduceAvailableSeats(3);
        legRepository.save(legWithThree);
        legRepository.save(legWithOne);
        legRepository.save(legWithNone);

        // Act
        Set<String> found = idsOf(legRepository.findLegsWithAtLeastAvailableSeats(2));

        // Assert
        assertTrue(found.contains(legWithThree.getId()));
        assertFalse(found.contains(legWithOne.getId()));
        assertFalse(found.contains(legWithNone.getId()));
    }


    @Test
    void shouldFindOnlyTheSeatsOfTheBookingWithTheirLeg() {
        // Arrange: booking-01 holds 1A on the first leg and 1B on the second; booking-02 holds 1C on the second
        Leg firstLeg = createLeg();
        Leg secondLeg = Leg.createLeg(Airport.EL_EDEN, Airport.EL_DORADO,
                DEPARTURE_TIME.plusDays(1), ARRIVAL_TIME.plusDays(1),
                "aircraft-a320-02", "airline-avianca",
                List.of(new Seat("1A"), new Seat("1B"), new Seat("1C")), 75000, 180000);
        String firstLegSeat1A = firstLeg.getSeats().get(0).getId();
        String secondLegSeat1B = secondLeg.getSeats().get(1).getId();
        String secondLegSeat1C = secondLeg.getSeats().get(2).getId();
        firstLeg.assignSeat(firstLegSeat1A, new PassengerBookingId("booking-01", "passenger-01"));
        secondLeg.assignSeat(secondLegSeat1B, new PassengerBookingId("booking-01", "passenger-01"));
        secondLeg.assignSeat(secondLegSeat1C, new PassengerBookingId("booking-02", "passenger-02"));
        legRepository.save(firstLeg);
        legRepository.save(secondLeg);

        // Act
        List<BookingSeat> seats = legRepository.findSeatsByBookingId("booking-01");

        // Assert: ordered by the leg's departure time
        assertEquals(List.of(
                new BookingSeat(firstLegSeat1A, "1A", "passenger-01", firstLeg.getId()),
                new BookingSeat(secondLegSeat1B, "1B", "passenger-01", secondLeg.getId())), seats);
    }

    @Test
    void shouldFindNoSeatsWhenTheBookingHoldsNone() {
        // Arrange
        legRepository.save(createLeg());

        // Act
        List<BookingSeat> seats = legRepository.findSeatsByBookingId("booking-without-seats");

        // Assert
        assertTrue(seats.isEmpty());
    }

    @Test
    void shouldLoadLegFromDataSqlWithItsLuggageAndSeats() {
        // Arrange: "leg-bog-mde-01" comes from data.sql with 4 seats, 1C occupied

        // Act
        Leg leg = legRepository.findById("leg-bog-mde-01").orElseThrow();

        // Assert
        assertEquals(Airport.EL_DORADO, leg.getOriginAirport());
        assertEquals(Airport.JOSE_MARIA_CORDOVA, leg.getDestinationAirport());
        assertEquals(LocalDateTime.of(2026, 12, 1, 8, 0), leg.getDepartureTime());
        assertEquals(LocalDateTime.of(2026, 12, 1, 9, 0), leg.getArrivalTime());
        assertEquals("aircraft-a320-01", leg.getAircraftId());
        assertEquals("airline-avianca", leg.getAirlineId());
        assertEquals(250000, leg.getPrice());
        assertEquals(3, leg.getAvailableSeats());

        assertEquals("luggage-bog-mde-01", leg.getLuggage().getId());
        assertEquals(80000, leg.getLuggage().getPrice());

        assertEquals(4, leg.getSeats().size());
        LegSeat occupied = leg.getLegSeatById("seat-bog-mde-1C").orElseThrow();
        assertEquals("1C", occupied.getSeatCode());
        assertEquals(SeatStatus.OCCUPIED, occupied.getSeatStatus());
        // data.sql has no booking for this seat, so it is loaded as occupied with no occupant.
        assertTrue(occupied.getPassengerBookingId().isEmpty());
        assertTrue(leg.getLegSeatById("seat-bog-mde-1A").orElseThrow().getPassengerBookingId().isEmpty());
    }


}
