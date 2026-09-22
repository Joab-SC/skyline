package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;

import java.time.LocalDateTime;
import java.util.List;

public class CreateLegUseCase {
    private final LegRepository legRepository;
    private final AirlineRepository airlineRepository;

    public CreateLegUseCase(LegRepository legRepository, AirlineRepository airlineRepository) {
        this.legRepository = legRepository;
        this.airlineRepository = airlineRepository;
    }

    public Leg execute(String id, Airport originAirport, Airport destinationAirport, LocalDateTime departureTime,
                        LocalDateTime arrivalTime, String airlineId, String aircraftId, String idLuggage, double luggagePrice, double price) {

        Airline airline = airlineRepository.findById(airlineId).orElseThrow(() -> new DomainRuleException("Airline not found to create the leg"));
        Aircraft aircraft = airline.getAircraft(aircraftId).orElseThrow(() -> new DomainRuleException("Aircraft not found to create the leg"));
        Leg leg = Leg.createLeg(id, originAirport, destinationAirport, departureTime, arrivalTime, aircraft, luggagePrice, idLuggage, price);
        airline.addLeg(leg.getId());
        legRepository.save(leg);
        return leg;
    }
}
