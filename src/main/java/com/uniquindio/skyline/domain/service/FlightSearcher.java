package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Flight;

import java.util.ArrayList;
import java.util.HashMap;

public class FlightSearcher {

    private final LegRepository legRepository;
    private final AirlineRepository airlineRepository;

    public FlightSearcher(LegRepository legRepository, AirlineRepository airlineRepository) {
        this.legRepository = legRepository;
        this.airlineRepository = airlineRepository;
    }


    private HashMap<Airport, ArrayList<Airport>> createGraph(Airline airline) {
        HashMap<Airport, ArrayList<Airport>> graph = new HashMap<>();
        for (String id: airline.getLegIds()){
            Leg leg= legRepository.findById(id).orElseThrow(() -> new DomainRuleException("There's no leg to be found"));
            if (!graph.containsKey(leg.getOriginAirport())){
                graph.put(leg.getOriginAirport(), new ArrayList<>());
            }
            graph.get(leg.getOriginAirport()).add(leg.getDestinationAirport());
        }
        return graph;
    }


}
