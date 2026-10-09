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

    public ArrayList<Flight> searchFlights(Airport origin, Airport destination) {
        ArrayList<Flight> resultFlights = new ArrayList<>();
        if (origin.equals(destination)) return resultFlights;

        HashMap<Airport, ArrayList<AirportConexion>> graph = createGraph();

        LinkedList<ArrayList<AirportConexion>> queue = new LinkedList<>();


        ArrayList<AirportConexion> initialAirportConexions = new ArrayList<>();
        initialAirportConexions.add(new AirportConexion(null, origin));

        queue.add(initialAirportConexions);

        while (!queue.isEmpty()) {
            ArrayList<AirportConexion> conexions = queue.poll();
            Airport currentAirport = conexions.getLast().airport();
            if (currentAirport == destination) {
                Leg firstLeg = conexions.get(1).leg();
                Flight flight = Flight.createFlight(1, firstLeg.getAirlineId(), firstLeg.getId(),
                        firstLeg.getOriginAirport(), firstLeg.getDestinationAirport(),
                        firstLeg.getDepartureTime(), firstLeg.getArrivalTime());

                for (int i = 2; i < conexions.size(); i++) {
                    Leg nextLeg = conexions.get(i).leg();
                    flight.addLeg(nextLeg.getId(), nextLeg.getAirlineId(),
                            nextLeg.getOriginAirport(), nextLeg.getDestinationAirport(),
                            nextLeg.getDepartureTime(), nextLeg.getArrivalTime());
                }
                resultFlights.add(flight);
                continue;
            }
            for (AirportConexion airportConexion : graph.getOrDefault(currentAirport, new ArrayList<>())) {
                Leg nextLeg = airportConexion.leg();
                boolean validLeg;

                if (conexions.size() == 1) {validLeg = true;}
                else {
                    Leg previousLeg = conexions.getLast().leg();
                    validLeg = Flight.isNewLegValid(previousLeg.getAirlineId(), nextLeg.getAirlineId(),
                            previousLeg.getDestinationAirport(), nextLeg.getOriginAirport(),
                            previousLeg.getArrivalTime(), nextLeg.getDepartureTime(), nextLeg.getArrivalTime()
                    );
                }
                if (conexions.size()< 6 && !conexions.stream().anyMatch(
                        selected -> selected.airport().equals(airportConexion.airport()))
                        && validLeg) {
                    ArrayList<AirportConexion> nextAirportConexions = new ArrayList<>(conexions);
                    nextAirportConexions.add(airportConexion);
                    queue.add(nextAirportConexions);
                }
            }
        }
        return resultFlights;
    }


    private record AirportConexion(Leg leg, Airport airport){};

    private HashMap<Airport, ArrayList<AirportConexion>> createGraph() {
        HashMap<Airport, ArrayList<AirportConexion>> graph = new HashMap<>();
        for (Leg leg: legRepository.findAll()){
            if (!graph.containsKey(leg.getOriginAirport())){
                graph.put(leg.getOriginAirport(), new ArrayList<>());
            }
            graph.get(leg.getOriginAirport()).add(new AirportConexion(leg,leg.getDestinationAirport()));
        }
        return graph;
    }



}
