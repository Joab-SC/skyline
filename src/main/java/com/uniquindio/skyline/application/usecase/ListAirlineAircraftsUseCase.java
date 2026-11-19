package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AircraftRepository;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListAirlineAircraftsUseCase {

    private final AirlineRepository airlineRepository;
    private final AircraftRepository aircraftRepository;

    // Returns the aircraft of the airline. The list can be empty.
    @Transactional(readOnly = true)
    public List<Aircraft> execute(String airlineId) {
        Airline airline = airlineRepository.findById(airlineId).orElseThrow();
        return aircraftRepository.findAllById(airline.getAircraftIds());
    }
}
