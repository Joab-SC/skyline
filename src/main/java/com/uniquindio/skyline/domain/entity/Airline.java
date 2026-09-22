package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Airline {
    private String name;
    private HashMap<String, Aircraft> aircrafts;
    private List<String> legIds;


    private Airline(String name, HashMap<String, Aircraft> aircrafts) {
        this.name = name;
        this.aircrafts = aircrafts;
        this.legIds = new ArrayList<>()   ;
    }


    public void addLeg(String legId){
        if(this.legIds.contains(legId)){
            throw new DomainRuleException("Leg already exists in the airline");
        }
        if(legId == null || legId.isEmpty()){
            throw new DomainRuleException("Leg id cannot be empty in the airline");
        }
        this.legIds.add(legId);
    }

    public Optional<Aircraft> getAircraft(String aircraftId) {
        return Optional.ofNullable(aircrafts.get(aircraftId));
    }

}
