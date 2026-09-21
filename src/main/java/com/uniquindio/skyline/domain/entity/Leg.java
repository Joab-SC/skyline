package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Leg {
    private String id;
    private Airport originAirport;
    private Airport destinationAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Aircraft aircraft;
    private double price;
    private List<Passenger> passengers;
    private List<ExtraService> extraServices;
    private Luggage luggage;
    private List<LegSeat> seats;

    private Leg(String id, Airport originAirport, Airport destinationAirport, LocalDateTime departureTime,
               LocalDateTime arrivalTime, Aircraft aircraft, double price, List<Passenger> passengers,
               List<ExtraService> extraServices, Luggage luggage, List<LegSeat> seats) {
        this.id = id;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraft = aircraft;
        this.price = price;
        this.passengers = passengers;
        this.extraServices = extraServices;
        this.luggage = luggage;
        this.seats = seats;
    }

    private void validateId() {
        if (id == null || id.isBlank()) {
            throw new DomainRuleException("Error: id cannot be null or blank");
        }
    }

    private void validateOriginAirport() {
        if (originAirport == null) {
            throw new DomainRuleException("Error: origin airport cannot be null");
        }
    }

    private void validateDestinationAirport() {
        if (destinationAirport == null) {
            throw new DomainRuleException("Error: destination airport cannot be null");
        }
    }

    private void validateDepartureTime() {
        if (departureTime == null) {
            throw new DomainRuleException("Error: departure time cannot be null");
        }
    }

    private void validateArrivalTime() {
        if (arrivalTime == null) {
            throw new DomainRuleException("Error: arrival time cannot be null");
        }
    }

    private void validateAircraft() {
        if (aircraft == null) {
            throw new DomainRuleException("Error: aircraft cannot be null");
        }
    }

    private void validatePrice() {
        if (price < 0) {
            throw new DomainRuleException("Error: price cannot be negative");
        }
    }

    private void validatePassengers() {
        if (passengers == null || passengers.isEmpty()) {
            throw new DomainRuleException("Error: passengers cannot be null or empty");
        }
    }

    private void validateExtraServices() {
        if (extraServices == null || extraServices.isEmpty()) {
            throw new DomainRuleException("Error: extra services cannot be null or empty");
        }
    }

    private void validateLuggage() {
        if (luggage == null) {
            throw new DomainRuleException("Error: luggage cannot be null");
        }
    }

    private void validateSeats() {
        if (seats == null || seats.isEmpty()) {
            throw new DomainRuleException("Error: seats cannot be null or empty");
        }
    }

    private void validateLeg() {
        validateId();
        validateOriginAirport();
        validateDestinationAirport();
        validateDepartureTime();
        validateArrivalTime();
        validateAircraft();
        validatePrice();
        validatePassengers();
        validateExtraServices();
        validateLuggage();
        validateSeats();
    }
}
