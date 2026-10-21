package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import lombok.Getter;

import java.util.*;

@Getter
public class Airline {
    private String id;
    private String name;
    private List<String> aircraftsId;
    private List<String> legIds;
    private int maximumLuggageAmount;
    private double priceSeats;

    private Airline(String id, String name, int maximumLuggageAmount, double priceSeats) {
        this.id = id;
        this.name = name;
        this.maximumLuggageAmount = maximumLuggageAmount;
        this.priceSeats = priceSeats;
        this.aircraftsId = new ArrayList<>();
        this.legIds = new ArrayList<>();

    }

    // Creates an airline without aircrafts or legs.
    public static Airline createAirline(String name, int maximumLuggageAmount, double seatPrice) {
        validateAirline(name, maximumLuggageAmount, seatPrice);
        return new Airline(UUID.randomUUID().toString(), name, maximumLuggageAmount, seatPrice);
    }

    private static void validateAirline(String name, int maximumLuggageAmount, double seatPrice) {
        if (name == null || name.isBlank()) {
            throw new DomainRuleException("The airline name cannot be null or empty");
        }
        if (maximumLuggageAmount <= 0) {
            throw new DomainRuleException("The maximum luggage amount must be greater than 0");
        }
        if (seatPrice < 0) {
            throw new DomainRuleException("The seat price cannot be negative");
        }
    }


    // Adds a leg id to the airline.
    public void addLeg(String legId){
        if(this.legIds.contains(legId)){
            throw new DomainRuleException("Leg already exists in the airline");
        }
        if(legId == null || legId.isBlank()){
            throw new DomainRuleException("Leg id cannot be empty in the airline");
        }
        this.legIds.add(legId);
    }

    public void addAircraft(String aircraftId){
        if(this.aircraftsId.contains(aircraftId)){
            throw new DomainRuleException("Aircraft already exists in the airline");
        }
        if(aircraftId == null || aircraftId.isBlank()){
            throw new DomainRuleException("Aircraft id cannot be empty in the airline");
        }
        this.aircraftsId.add(aircraftId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Airline)) return false;
        Airline other = (Airline) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
