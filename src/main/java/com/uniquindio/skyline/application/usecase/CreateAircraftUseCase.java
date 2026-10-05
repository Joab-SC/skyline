package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AircraftRepository;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;

public class CreateAircraftUseCase {

    private final AircraftRepository aircraftRepository;
    private final AirlineRepository airlineRepository;

    public CreateAircraftUseCase(AircraftRepository aircraftRepository, AirlineRepository airlineRepository) {
        this.aircraftRepository = aircraftRepository;
        this.airlineRepository = airlineRepository;
    }

    public Aircraft execute(AircraftModel aircraftModel, String airlineId){
        Aircraft aircraft = Aircraft.createAircraft(aircraftModel);
        Airline airline = airlineRepository.findById(airlineId).orElseThrow(() -> new DomainRuleException("Airline not found to create the aircraft"));
        airline.addAircraft(aircraft.getId());
        aircraftRepository.save(aircraft);
        return aircraft;
    }

}
