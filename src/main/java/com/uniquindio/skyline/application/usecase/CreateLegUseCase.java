package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.AircraftRepository;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.service.LegOverlapValidator;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Seat;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateLegUseCase {
    private final LegRepository legRepository;
    private final AirlineRepository airlineRepository;
    private final LegOverlapValidator legOverlapValidator;
    private final AircraftRepository aircraftRepository;


    // Creates a leg and links it to the airline.
    @Transactional
    public Leg execute(Airport originAirport, Airport destinationAirport, LocalDateTime departureTime,
                       LocalDateTime arrivalTime, String airlineId, String aircraftId, double luggagePrice, double price) {

        Airline airline = airlineRepository.findById(airlineId).orElseThrow();
        Aircraft aircraft = aircraftRepository.findById(aircraftId).orElseThrow();

        airline.validateHasAircraft(aircraftId);
        // Checks that the aircraft has no overlapping legs.
        legOverlapValidator.validateNoOverlap(aircraftId, departureTime, arrivalTime);

        List<Seat> aircraftSeats = aircraft.getSeats();
        Leg leg = Leg.createLeg(originAirport, destinationAirport, departureTime, arrivalTime, aircraftId, airlineId, aircraftSeats, luggagePrice, price);

        airline.addLeg(leg.getId());
        legRepository.save(leg);
        airlineRepository.save(airline);
        return leg;
    }
}