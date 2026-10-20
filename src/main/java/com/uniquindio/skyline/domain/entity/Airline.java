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
    private final int maximumLuggageAmount;


    private Airline(String id, String name, int maximumLuggageAmount) {
        this.id = id;
        this.name = name;
        this.maximumLuggageAmount = maximumLuggageAmount;
        this.aircraftsId = new ArrayList<>();
        this.legIds = new ArrayList<>();

    }


    // Adds a leg id to the airline.
    public void addLeg(String legId){
        if(this.legIds.contains(legId)){
            throw new DomainRuleException("Leg already exists in the airline");
        }
        if(legId == null || legId.isEmpty()){
            throw new DomainRuleException("Leg id cannot be empty in the airline");
        }
        this.legIds.add(legId);
    }

    public void addAircraft(String aircraftId){
        if(this.aircraftsId.contains(aircraftId)){
            throw new DomainRuleException("Aircraft already exists in the airline");
        }
        if(aircraftId == null || aircraftId.isEmpty()){
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
