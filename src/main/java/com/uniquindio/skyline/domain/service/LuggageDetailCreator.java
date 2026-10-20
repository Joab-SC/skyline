package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Booking;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.FlightRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.LuggageDetail;

import java.util.ArrayList;
import java.util.List;

public class LuggageDetailCreator {

    private final FlightRepository flightRepository;
    private final LegRepository legRepository;
    private final AirlineRepository airlineRepository;

    public LuggageDetailCreator(FlightRepository flightRepository, LegRepository legRepository, AirlineRepository airlineRepository) {
        this.flightRepository = flightRepository;
        this.legRepository = legRepository;
        this.airlineRepository = airlineRepository;
    }

    // Creates the luggage detail of a passenger for every leg of the booking.
    public LuggageDetail createDetail(Booking booking, int quantity){
        Flight outboundFlight = findFlight(booking.getIdOutboundFlight());
        List<Leg> legs = new ArrayList<>(findLegs(outboundFlight));

        if (booking.getIdReturnFlight().isPresent()) {
            legs.addAll(findLegs(findFlight(booking.getIdReturnFlight().get())));
        }

        // The outbound and return flights belong to the same airline.
        Airline airline = airlineRepository.findById(outboundFlight.getAirlineId()).orElseThrow(() -> new DomainRuleException("Airline not found"));

        List<String> luggageIds = legs.stream().map(leg -> leg.getLuggage().getId()).toList();
        double luggagePrice = legs.stream().mapToDouble(leg -> leg.getLuggage().getPrice()).sum();
        return booking.createLuggageDetail(luggageIds, quantity, quantity * luggagePrice, airline.getMaximumLuggageAmount());
    }


    private Flight findFlight(String flightId) {
        return flightRepository.findById(flightId).orElseThrow(() -> new DomainRuleException("Flight not found"));
    }

    private List<Leg> findLegs(Flight flight) {
        return flight.getIdLegs().stream()
                .map(legId -> legRepository.findById(legId).orElseThrow(() -> new DomainRuleException("Leg not found")))
                .toList();
    }
}
