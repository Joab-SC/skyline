package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AircraftRepository;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAircraftUseCase {

    private final AircraftRepository aircraftRepository;
    private final AirlineRepository airlineRepository;

    @Transactional
    public Aircraft execute(AircraftModel aircraftModel, String airlineId){
        Aircraft aircraft = Aircraft.createAircraft(aircraftModel);
        Airline airline = airlineRepository.findById(airlineId).orElseThrow();
        airline.addAircraft(aircraft.getId());
        aircraftRepository.save(aircraft);
        airlineRepository.save(airline);
        return aircraft;
    }

}
