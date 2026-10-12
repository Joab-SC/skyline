package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Flight;

import java.time.LocalDateTime;
import java.util.*;

public class FlightSearcher {

    private final LegRepository legRepository;
    private final AirlineRepository airlineRepository;

    public FlightSearcher(LegRepository legRepository, AirlineRepository airlineRepository) {
        this.legRepository = legRepository;
        this.airlineRepository = airlineRepository;
    }

    public ArrayList<Flight> searchFlights(Airport origin, Airport destination) {
        ArrayList<Flight> resultFlights = new ArrayList<>();
        HashMap<Airport, ArrayList<Leg>> graph = createGraph();

        LinkedList<ArrayList<Leg>> queue = new LinkedList<>();

        // Fill deque with paths starting from the origin airport
        for (Leg first : graph.getOrDefault(origin, new ArrayList<>()))
            queue.add(new ArrayList<>(List.of(first)));

        while (!queue.isEmpty()) {
            ArrayList<Leg> path = queue.poll();
            Airport currentAirport = path.getLast().getDestinationAirport();

            // Build leg with current path
            if (currentAirport == destination) {
                Leg firstLeg = path.getFirst();
                Flight flight = Flight.createFlight(1, firstLeg.getAirlineId(), firstLeg.getId(),
                        firstLeg.getOriginAirport(), firstLeg.getDestinationAirport(),
                        firstLeg.getDepartureTime(), firstLeg.getArrivalTime());

                for (int i = 1; i < path.size(); i++) {
                    Leg nextLeg = path.get(i);
                    flight.addLeg(nextLeg.getId(), nextLeg.getAirlineId(),
                            nextLeg.getOriginAirport(), nextLeg.getDestinationAirport(),
                            nextLeg.getDepartureTime(), nextLeg.getArrivalTime());
                }
                resultFlights.add(flight);
                continue;
            }

            // Create new paths with the conexions of the last leg
            for (Leg nextLeg : graph.get(currentAirport)) {

                Leg previousLeg = path.getLast();
                boolean validLeg = Flight.isNewLegValid(previousLeg.getAirlineId(), nextLeg.getAirlineId(),
                                previousLeg.getDestinationAirport(), nextLeg.getOriginAirport(),
                        previousLeg.getArrivalTime(), nextLeg.getDepartureTime(), nextLeg.getArrivalTime()
                );

                if (path.size()< 5 && !path.stream().anyMatch(
                        selected -> selected.getOriginAirport().equals(nextLeg.getOriginAirport()))
                        && validLeg) {
                    ArrayList<Leg> newPath = new ArrayList<>(path);
                    newPath.add(nextLeg);
                    queue.add(newPath);
                }
            }
        }
        return resultFlights;
    }

    private HashMap<Airport, ArrayList<Leg>> createGraph() {
        HashMap<Airport, ArrayList<Leg>> graph = new HashMap<>();
        for (Leg leg: legRepository.findAll()){
            if (!graph.containsKey(leg.getOriginAirport())){
                graph.put(leg.getOriginAirport(), new ArrayList<>());
            }
            graph.get(leg.getOriginAirport()).add(leg);
        }
        return graph;
    }



}
