package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;

import java.util.List;

public class SeatsAvailableReducer {

    private final LegRepository legRepository;
    private final FlightRepository flightRepository;

    public SeatsAvailableReducer(LegRepository legRepository, FlightRepository flightRepository) {
        this.legRepository = legRepository;
        this.flightRepository = flightRepository;
    }

    public void reduceAvailableSeats (String idFlight, int numPassengers){
        Flight flight = flightRepository.findById(idFlight).orElseThrow(() -> new DomainRuleException(" The flight doesn't exist"));
        for (String idLeg : flight.getIdLegs()){
            Leg leg = legRepository.findById(idLeg).orElseThrow(() -> new DomainRuleException(" The leg cannot be found"));
            leg.reduceAvailableSeats(numPassengers);
        }
    }
}
