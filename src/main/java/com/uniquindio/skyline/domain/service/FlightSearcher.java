package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.valueObject.LegSummary;

import java.time.LocalDate;
import java.util.*;

public class FlightSearcher {

    private final LegRepository legRepository;

    public FlightSearcher(LegRepository legRepository) {
        this.legRepository = legRepository;
    }

    public ArrayList<Flight> searchFlights(Airport origin, Airport destination, LocalDate departureDay, int numberOfPassengers) {
        ArrayList<Flight> resultFlights = new ArrayList<>();
        HashMap<Airport, ArrayList<Leg>> graph = createGraph(numberOfPassengers);

        LinkedList<ArrayList<Leg>> queue = new LinkedList<>();

        // Fill deque with paths starting from the origin airport
        for (Leg first : graph.getOrDefault(origin, new ArrayList<>())) {
            if (first.getDepartureTime().toLocalDate().equals(departureDay)) {
                queue.add(new ArrayList<>(List.of(first)));
            }
        }
        while (!queue.isEmpty()) {
            ArrayList<Leg> path = queue.poll();
            Airport currentAirport = path.getLast().getDestinationAirport(); // Represents the last airport of the path

            // Build flight with current path if already in destination
            if (currentAirport.equals(destination)) {
                Leg firstLeg = path.getFirst();
                Flight flight = Flight.createFlight(firstLeg.getId(), new LegSummary(firstLeg.getDepartureTime(),firstLeg.getArrivalTime(),
                                firstLeg.getOriginAirport(), firstLeg.getDestinationAirport(), firstLeg.getPrice(), firstLeg.getAirlineId()));

                for (int i = 1; i < path.size(); i++) {
                    Leg nextLeg = path.get(i);
                    Leg previousLeg = path.get(i-1);
                    LegSummary previousSummary = new LegSummary(previousLeg.getDepartureTime(),previousLeg.getArrivalTime(),
                            previousLeg.getOriginAirport(), previousLeg.getDestinationAirport(), previousLeg.getPrice(), previousLeg.getAirlineId());
                    LegSummary nextSummary = new LegSummary(nextLeg.getDepartureTime(),nextLeg.getArrivalTime(),
                            nextLeg.getOriginAirport(), nextLeg.getDestinationAirport(), nextLeg.getPrice(), nextLeg.getAirlineId());
                    flight.addLeg(nextLeg.getId(),previousSummary, nextSummary);
                }
                resultFlights.add(flight);
                continue;
            }

            // Create new paths with the legs starting from the current airport
            for (Leg nextLeg : graph.getOrDefault(currentAirport, new ArrayList<>())) {

                Leg previousLeg = path.getLast();
                LegSummary previousSummary = new LegSummary(previousLeg.getDepartureTime(),previousLeg.getArrivalTime(),
                        previousLeg.getOriginAirport(), previousLeg.getDestinationAirport(), previousLeg.getPrice(), previousLeg.getAirlineId());
                LegSummary nextSummary = new LegSummary(nextLeg.getDepartureTime(),nextLeg.getArrivalTime(),
                        nextLeg.getOriginAirport(), nextLeg.getDestinationAirport(), nextLeg.getPrice(), nextLeg.getAirlineId());
                boolean validLeg = Flight.isNewLegValid(previousSummary, nextSummary);

                if (path.size()-1 < Flight.getMaxLayovers() && !path.stream().anyMatch(
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

    private HashMap<Airport, ArrayList<Leg>> createGraph(int numberOfPassengers) {
        HashMap<Airport, ArrayList<Leg>> graph = new HashMap<>();
        for (Leg leg: legRepository.findLegsWithAtLeastAvailableSeats(numberOfPassengers)){
            if (!graph.containsKey(leg.getOriginAirport())){
                graph.put(leg.getOriginAirport(), new ArrayList<>());
            }
            graph.get(leg.getOriginAirport()).add(leg);

        }
        return graph;
    }



}
