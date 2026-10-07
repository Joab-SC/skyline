package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Flight;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class FlightSearcher {

    private final LegRepository legRepository;
    private final AirlineRepository airlineRepository;

    public FlightSearcher(LegRepository legRepository, AirlineRepository airlineRepository) {
        this.legRepository = legRepository;
        this.airlineRepository = airlineRepository;
    }

    private ArrayList<Flight> searchFlights(Airline airline, Airport origin, Airport destination) {
        HashMap<Airport, ArrayList<AirportConexion>> graph = createGraph(airline);

        LinkedList<ArrayList<AirportConexion>> queue = new LinkedList<>();
        ArrayList<Flight> resultFlights = new ArrayList<>();

        ArrayList<AirportConexion> initialAirportConexions = new ArrayList<>();
        initialAirportConexions.add(new AirportConexion(null, origin));

        queue.add(initialAirportConexions);

        while (!queue.isEmpty()) {
            ArrayList<AirportConexion> conexions = queue.poll();
            Airport currentAirport = conexions.getLast().airport();
            if (currentAirport == destination) {
                Leg leg = conexions.getFirst().leg;
                Flight flight = Flight.createFlight(0,leg.getId(), leg.getOriginAirport(), leg.getDestinationAirport(), leg.getDepartureTime(), leg.getArrivalTime());
                for (int i=1; i< conexions.size(); i++) {
                    Leg nextLeg = conexions.get(i).leg;
                    flight.addLeg(nextLeg.getId(), leg.getDepartureTime(), leg.getArrivalTime(), leg.getDestinationAirport());

                }
                resultFlights.add(flight);
            }
            for (AirportConexion airportConexion : graph.get(currentAirport)) {
                if (!conexions.contains(airportConexion)) {
                    ArrayList<AirportConexion> nextAirportConexions = new ArrayList<>(conexions);
                    nextAirportConexions.add(airportConexion);
                    queue.add(nextAirportConexions);
                }
            }
        }
        return resultFlights;
    }

    private record AirportConexion(Leg leg, Airport airport){};

    private HashMap<Airport, ArrayList<AirportConexion>> createGraph(Airline airline) {
        HashMap<Airport, ArrayList<AirportConexion>> graph = new HashMap<>();
        for (String id: airline.getLegIds()){
            Leg leg= legRepository.findById(id).orElseThrow(() -> new DomainRuleException("There's no leg to be found"));
            if (!graph.containsKey(leg.getOriginAirport())){
                graph.put(leg.getOriginAirport(), new ArrayList<>());
            }
            graph.get(leg.getOriginAirport()).add(new AirportConexion(leg,leg.getDestinationAirport()));
        }
        return graph;
    }



}
