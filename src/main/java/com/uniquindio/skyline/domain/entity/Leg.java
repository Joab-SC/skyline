package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Seat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Leg {
    private final String id;
    private final Airport originAirport;
    private final Airport destinationAirport;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
    private final String aircraftId;
    private final String airlineId;
    private double price;
    private Luggage luggage;
    private final List<LegSeat> seats;

    private Leg(String id, Airport originAirport, Airport destinationAirport, LocalDateTime departureTime,
               LocalDateTime arrivalTime, String aircraftId, String airlineId, double price, Luggage luggage, List<LegSeat> seats) {
        this.id = id;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraftId = aircraftId;
        this.airlineId = airlineId;
        this.price = price;
        this.luggage = luggage;
        this.seats = seats;
    }

    // Creates a leg with seats and luggage.
    public static Leg createLeg(Airport originAirport, Airport destinationAirport, LocalDateTime departureTime,
                                LocalDateTime arrivalTime, String aircraftId, String airlineId, List<Seat> aircraftSeats, double luggagePrice, double price) {

        validateLeg( originAirport, destinationAirport, departureTime, arrivalTime, aircraftId, airlineId, aircraftSeats, luggagePrice, price);

        // Creates the seats available for this leg.
        List<LegSeat> legSeats = new ArrayList<>(List.of());

        for (Seat seat : aircraftSeats) {
            LegSeat legSeat = LegSeat.createLegSeat(UUID.randomUUID().toString(), seat.code());
            legSeats.add(legSeat);
        }

        // Creates the luggage configuration for this leg.
        Luggage luggage = Luggage.createLuggage(UUID.randomUUID().toString(),luggagePrice);

        return new Leg(UUID.randomUUID().toString(), originAirport, destinationAirport, departureTime, arrivalTime, aircraftId, airlineId, price, luggage, legSeats);
    }


    private static void validateOriginAirport(Airport originAirport) {
        if (originAirport == null) {
            throw new DomainRuleException("An origin airport is required");
        }
    }

    private static void validateDestinationAirport(Airport destinationAirport) {
        if (destinationAirport == null) {
            throw new DomainRuleException("A destination airport is required");
        }
    }

    private static void validateDepartureTime(LocalDateTime departureTime) {
        if (departureTime == null) {
            throw new DomainRuleException("A departure time is required");
        }
    }

    private static void validateArrivalTime(LocalDateTime arrivalTime) {
        if (arrivalTime == null) {
            throw new DomainRuleException("An arrival time is required");
        }
    }

    private static void validateAircraft(String aircraftId) {
        if (aircraftId == null || aircraftId.isBlank()) {
            throw new DomainRuleException("An aircraft is required");
        }
    }

    private static void validateAirline(String airlineId) {
        if (airlineId == null || airlineId.isBlank()) {
            throw new DomainRuleException("An airline is required");
        }
    }

    private static void validateAircraftSeats(List<Seat> aircraftSeats) {
        if (aircraftSeats == null || aircraftSeats.isEmpty()) {
            throw new DomainRuleException("The aircraft seats are required and there must be at least one");
        }
    }

    private static void validatePrice(Double price) {
        if (price == null) {
            throw new DomainRuleException("A price is required");
        }

        if (price < 0) {
            throw new DomainRuleException("The price cannot be less than zero");
        }
    }

    private static void validateLuggagePrice(Double luggagePrice) {
        if (luggagePrice == null) {
            throw new DomainRuleException("A luggage price is required");
        }

        if (luggagePrice < 0) {
            throw new DomainRuleException("The luggage price cannot be less than zero");
        }
    }

    private static void validateTimes(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        if (!arrivalTime.isAfter(departureTime)) {
            throw new DomainRuleException("The arrival time must be later than the departure time");
        }
    }

    // Runs all leg validations.
    private static void validateLeg(
            Airport originAirport,
            Airport destinationAirport,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            String aircraftId,
            String airlineId,
            List<Seat> aircraftSeats,
            Double luggagePrice,
            Double price) {

        validateOriginAirport(originAirport);
        validateDestinationAirport(destinationAirport);
        validateDepartureTime(departureTime);
        validateArrivalTime(arrivalTime);
        validateTimes(departureTime, arrivalTime);
        validateAircraft(aircraftId);
        validateAirline(airlineId);
        validateAircraftSeats(aircraftSeats);
        validateLuggagePrice(luggagePrice);
        validatePrice(price);
        validateDifferentAirports(originAirport,destinationAirport);


    }

    private static void validateDifferentAirports(
            Airport originAirport,
            Airport destinationAirport) {

        if (originAirport.equals(destinationAirport)) {
            throw new DomainRuleException(
                    "The origin and destination airports cannot be the same"
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Leg)) return false;
        Leg other = (Leg) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
