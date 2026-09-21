package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;

import java.util.ArrayList;
import java.util.List;

public class Airline {
    private String name;
    private List<Aircraft> aircrafts;
    private List<String> legIds;


    private Airline(String name, List<Aircraft> aircrafts) {
        this.name = name;
        this.aircrafts = aircrafts;
        this.legIds = new ArrayList<>()   ;
    }


    public void addLeg(String legId){
        if(this.legIds.contains(legId)){
            throw new DomainRuleException("Leg already exists in the airline");
        }
        if(legId == null || legId.isEmpty()){
            throw new DomainRuleException("Leg id cannot be empty");
        }
        this.legIds.add(legId);
    }
}
