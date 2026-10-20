package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.LegRepository;

public class SeatsAvailableReducer {

    private final LegRepository legRepository;

    public SeatsAvailableReducer(LegRepository legRepository) {
        this.legRepository = legRepository;
    }

    public void reduceAvailableSeats (Flight flight, int numPassengers){
        for (String idLeg : flight.getIdLegs()){
            Leg leg = legRepository.findById(idLeg).orElseThrow(() -> new DomainRuleException("The leg cannot be found"));
            leg.reduceAvailableSeats(numPassengers);
            legRepository.save(leg);
        }
    }
}
