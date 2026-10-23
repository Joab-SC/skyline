package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.entity.LegSeat;
import com.uniquindio.skyline.domain.entity.Passenger;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.PassengerData;
import com.uniquindio.skyline.domain.valueObject.Seat;
import com.uniquindio.skyline.domain.valueObject.TripType;
import com.uniquindio.skyline.infrastructure.persistence.AirlineRepositoryInMemory;
import com.uniquindio.skyline.infrastructure.persistence.FlightRepositoryInMemory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SeatAssignerTest {

    @Test
    void shouldAssignSeatOnOutboundLegAndAddSeatPrice() {
        // Arrange
        FlightRepository flightRepository = new FlightRepositoryInMemory();
        AirlineRepository airlineRepository = new AirlineRepositoryInMemory();
        Airline airline = createAirline(airlineRepository, 40.0);
        Leg leg = createLeg(airline.getId(), Airport.EL_EDEN, Airport.EL_DORADO);
        Flight flight = createFlight(flightRepository, leg);
        Booking booking = createBooking(flight.getId(), TripType.ONE_WAY);
        Passenger passenger = booking.getPassenger("1001").orElseThrow();
        LegSeat seat = leg.getSeats().get(0);

        SeatAssigner seatAssigner = new SeatAssigner(flightRepository, airlineRepository);

        // Act
        seatAssigner.assignSeat(booking, seat.getId(), passenger, leg);

        // Assert
        assertEquals(Optional.of("1001"), seat.getPassengerId());
        assertEquals(540.0, booking.getPrice(), 0.001);
    }

    @Test
    void shouldAssignSeatOnReturnLegUsingReturnAirlinePrice() {
        // Arrange
        FlightRepository flightRepository = new FlightRepositoryInMemory();
        AirlineRepository airlineRepository = new AirlineRepositoryInMemory();
        Airline outboundAirline = createAirline(airlineRepository, 40.0);
        Airline returnAirline = createAirline(airlineRepository, 70.0);
        Leg outboundLeg = createLeg(outboundAirline.getId(), Airport.EL_EDEN, Airport.EL_DORADO);
        Leg returnLeg = createLeg(returnAirline.getId(), Airport.EL_DORADO, Airport.EL_EDEN);
        Flight outboundFlight = createFlight(flightRepository, outboundLeg);
        Flight returnFlight = createFlight(flightRepository, returnLeg);
        Booking booking = createBooking(outboundFlight.getId(), TripType.ROUND_TRIP);
        booking.addReturnFlight(returnFlight.getId(), 300.0);
        Passenger passenger = booking.getPassenger("1001").orElseThrow();
        LegSeat seat = returnLeg.getSeats().get(0);

        SeatAssigner seatAssigner = new SeatAssigner(flightRepository, airlineRepository);

        // Act
        seatAssigner.assignSeat(booking, seat.getId(), passenger, returnLeg);

        // Assert
        assertEquals(Optional.of("1001"), seat.getPassengerId());
        assertEquals(870.0, booking.getPrice(), 0.001);
    }

    @Test
    void shouldNotChargeAgainWhenPassengerChangesSeat() {
        // Arrange
        FlightRepository flightRepository = new FlightRepositoryInMemory();
        AirlineRepository airlineRepository = new AirlineRepositoryInMemory();
        Airline airline = createAirline(airlineRepository, 40.0);
        Leg leg = createLeg(airline.getId(), Airport.EL_EDEN, Airport.EL_DORADO);
        Flight flight = createFlight(flightRepository, leg);
        Booking booking = createBooking(flight.getId(), TripType.ONE_WAY);
        Passenger passenger = booking.getPassenger("1001").orElseThrow();
        LegSeat oldSeat = leg.getSeats().get(0);
        LegSeat newSeat = leg.getSeats().get(1);

        SeatAssigner seatAssigner = new SeatAssigner(flightRepository, airlineRepository);
        seatAssigner.assignSeat(booking, oldSeat.getId(), passenger, leg);

        // Act
        seatAssigner.assignSeat(booking, newSeat.getId(), passenger, leg);

        // Assert
        assertEquals(Optional.empty(), oldSeat.getPassengerId());
        assertEquals(Optional.of("1001"), newSeat.getPassengerId());
        assertEquals(540.0, booking.getPrice(), 0.001);
    }

    @Test
    void shouldThrowExceptionWhenLegIsNotOnTheBooking() {
        // Arrange
        FlightRepository flightRepository = new FlightRepositoryInMemory();
        AirlineRepository airlineRepository = new AirlineRepositoryInMemory();
        Airline airline = createAirline(airlineRepository, 40.0);
        Leg bookingLeg = createLeg(airline.getId(), Airport.EL_EDEN, Airport.EL_DORADO);
        Leg otherLeg = createLeg(airline.getId(), Airport.EL_DORADO, Airport.EL_EDEN);
        Flight flight = createFlight(flightRepository, bookingLeg);
        Booking booking = createBooking(flight.getId(), TripType.ONE_WAY);
        Passenger passenger = booking.getPassenger("1001").orElseThrow();
        LegSeat seat = otherLeg.getSeats().get(0);

        SeatAssigner seatAssigner = new SeatAssigner(flightRepository, airlineRepository);

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> seatAssigner.assignSeat(booking, seat.getId(), passenger, otherLeg)
        );
        assertEquals(Optional.empty(), seat.getPassengerId());
        assertEquals(500.0, booking.getPrice(), 0.001);
    }

    @Test
    void shouldNotChargeWhenSeatIsOccupied() {
        // Arrange
        FlightRepository flightRepository = new FlightRepositoryInMemory();
        AirlineRepository airlineRepository = new AirlineRepositoryInMemory();
        Airline airline = createAirline(airlineRepository, 40.0);
        Leg leg = createLeg(airline.getId(), Airport.EL_EDEN, Airport.EL_DORADO);
        Flight flight = createFlight(flightRepository, leg);
        Booking booking = createBooking(flight.getId(), TripType.ONE_WAY);
        Passenger passenger = booking.getPassenger("1001").orElseThrow();
        LegSeat seat = leg.getSeats().get(0);
        seat.assignPassengerToLegSeat("2002");

        SeatAssigner seatAssigner = new SeatAssigner(flightRepository, airlineRepository);

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> seatAssigner.assignSeat(booking, seat.getId(), passenger, leg)
        );
        assertEquals(Optional.of("2002"), seat.getPassengerId());
        assertEquals(500.0, booking.getPrice(), 0.001);
    }

    @Test
    void shouldThrowExceptionWhenBookingIsCancelled() {
        // Arrange
        FlightRepository flightRepository = new FlightRepositoryInMemory();
        AirlineRepository airlineRepository = new AirlineRepositoryInMemory();
        Airline airline = createAirline(airlineRepository, 40.0);
        Leg leg = createLeg(airline.getId(), Airport.EL_EDEN, Airport.EL_DORADO);
        Flight flight = createFlight(flightRepository, leg);
        Booking booking = createBooking(flight.getId(), TripType.ONE_WAY);
        Passenger passenger = booking.getPassenger("1001").orElseThrow();
        LegSeat seat = leg.getSeats().get(0);
        booking.cancelBooking();

        SeatAssigner seatAssigner = new SeatAssigner(flightRepository, airlineRepository);

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> seatAssigner.assignSeat(booking, seat.getId(), passenger, leg)
        );
        assertEquals(Optional.empty(), seat.getPassengerId());
    }

    @Test
    void shouldThrowExceptionWhenPassengerIsNotInBooking() {
        // Arrange
        FlightRepository flightRepository = new FlightRepositoryInMemory();
        AirlineRepository airlineRepository = new AirlineRepositoryInMemory();
        Airline airline = createAirline(airlineRepository, 40.0);
        Leg leg = createLeg(airline.getId(), Airport.EL_EDEN, Airport.EL_DORADO);
        Flight flight = createFlight(flightRepository, leg);
        Booking booking = createBooking(flight.getId(), TripType.ONE_WAY);
        Booking otherBooking = Booking.createPendingBooking(1, 500.0, flight.getId(), TripType.ONE_WAY);
        otherBooking.addPassengers(List.of(new PassengerData("2002", DocumentType.CITIZENSHIP_ID, "Juan", null,
                "Perez", null, LocalDate.of(1995, 3, 2), Gender.MALE)));
        Passenger otherPassenger = otherBooking.getPassenger("2002").orElseThrow();
        LegSeat seat = leg.getSeats().get(0);

        SeatAssigner seatAssigner = new SeatAssigner(flightRepository, airlineRepository);

        // Act & Assert
        assertThrows(
                DomainRuleException.class,
                () -> seatAssigner.assignSeat(booking, seat.getId(), otherPassenger, leg)
        );
        assertEquals(Optional.empty(), seat.getPassengerId());
    }

    private Airline createAirline(AirlineRepository airlineRepository, double priceSeats) {
        Airline airline = Airline.createAirline("Avianca", 3, priceSeats);
        airlineRepository.save(airline);
        return airline;
    }

    private Leg createLeg(String airlineId, Airport origin, Airport destination) {
        return Leg.createLeg(origin, destination, LocalDateTime.of(2026, 11, 5, 10, 0),
                LocalDateTime.of(2026, 11, 5, 12, 0), "AIR-001", airlineId,
                List.of(new Seat("001"), new Seat("002")), 100, 500);
    }

    private Flight createFlight(FlightRepository flightRepository, Leg leg) {
        Flight flight = Flight.createFlight(List.of(leg.toSummary()));
        flightRepository.save(flight);
        return flight;
    }

    private Booking createBooking(String idOutboundFlight, TripType tripType) {
        Booking booking = Booking.createPendingBooking(1, 500.0, idOutboundFlight, tripType);
        booking.addPassengers(List.of(new PassengerData("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE)));
        return booking;
    }
}
