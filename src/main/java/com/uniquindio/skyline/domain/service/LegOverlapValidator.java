package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.LegRepository;

import java.time.LocalDateTime;
import java.util.List;

public class LegOverlapValidator {
    private final LegRepository legRepository;

    public LegOverlapValidator(LegRepository legRepository) {
        this.legRepository = legRepository;
    }

    // Validates that the aircraft has enough time between its legs.
    public void validateNoOverlap(String aircraftId, LocalDateTime newDepartureTime, LocalDateTime newArrivalTime){
        // Gets all legs currently assigned to the aircraft.
        List<Leg> legs = legRepository.findLegsByAircraft(aircraftId);

        // Checks the new leg against each existing leg.
        for(Leg leg : legs){
            if (leg.getDepartureTime().isBefore(newArrivalTime.plusMinutes(5)) && newDepartureTime.isBefore(leg.getArrivalTime().plusMinutes(5))){
                throw new DomainRuleException("There must be at least 5 minutes between legs of the same aircraft");
            }
        }
    }
}