package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Seat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Leg {
    private final String id;
    private final Airport originAirport;
    private final Airport destinationAirport;
    private final LocalDateTime departureTime;
    private final LocalDateTime arrivalTime;
    private final String aircraftId;
    private double price;
    private Luggage luggage;
    private final List<LegSeat> seats;

    private Leg(String id, Airport originAirport, Airport destinationAirport, LocalDateTime departureTime,
               LocalDateTime arrivalTime, String aircraftId, double price, Luggage luggage, List<LegSeat> seats) {
        this.id = id;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraftId = aircraftId;
        this.price = price;
        this.luggage = luggage;
        this.seats = seats;
    }

    public static Leg createLeg(String id, Airport originAirport, Airport destinationAirport, LocalDateTime departureTime,
                                LocalDateTime arrivalTime, Aircraft aircraft, double luggagePrice, String idLuggage, double price) {

        validateLeg(id, originAirport, destinationAirport, departureTime, arrivalTime, aircraft, luggagePrice, price);

        List<LegSeat> legSeats = new ArrayList<>(List.of());
        List<Seat> seats = aircraft.getSeats();

        for (Seat seat : seats) {
            LegSeat legSeat = LegSeat.createLegSeat(seat.code());
            legSeats.add(legSeat);
        }

        Luggage luggage = Luggage.createLuggage(idLuggage,luggagePrice);

        return new Leg(id, originAirport, destinationAirport, departureTime, arrivalTime, aircraft.getId(), price, luggage, legSeats);
    }


    private static void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new DomainRuleException("A valid identifier is required");
        }
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

    private static void validateAircraft(Aircraft aircraft) {
        if (aircraft == null) {
            throw new DomainRuleException("An aircraft is required");
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

    private static void validateLeg(
            String id,
            Airport originAirport,
            Airport destinationAirport,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            Aircraft aircraft,
            Double luggagePrice,
            Double price) {

        validateId(id);
        validateOriginAirport(originAirport);
        validateDestinationAirport(destinationAirport);
        validateDepartureTime(departureTime);
        validateArrivalTime(arrivalTime);
        validateTimes(departureTime, arrivalTime);
        validateAircraft(aircraft);
        validateLuggagePrice(luggagePrice);
        validatePrice(price);
    }
}
