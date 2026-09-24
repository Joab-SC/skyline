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

    public void validateNoOverlap(String aircraftId, LocalDateTime newDepartureTime, LocalDateTime newArrivalTime){
        List<Leg> legs = legRepository.findLegsByAircraft(aircraftId);
        for(Leg leg : legs){
            if (leg.getDepartureTime().isBefore(newArrivalTime.plusMinutes(5)) && newDepartureTime.isBefore(leg.getArrivalTime().plusMinutes(5))){
                throw new DomainRuleException("There must be at least 5 minutes between legs of the same aircraft");
            }
        }
    }



}
