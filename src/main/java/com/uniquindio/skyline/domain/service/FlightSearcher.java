package com.uniquindio.skyline.domain.service;

import com.uniquindio.skyline.domain.valueObject.Layover;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.domain.valueObject.LegSummary;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class FlightSearcher {

    private final LegRepository legRepository;

    public FlightSearcher(LegRepository legRepository) {
        this.legRepository = legRepository;
    }

    public List<Flight> searchFlights(Airport origin, Airport destination, LocalDate departureDay, int numberOfPassengers) {
        List<Flight> resultFlights = new ArrayList<>();
        Map<Airport, List<Leg>> graph = createGraph(numberOfPassengers);

        Queue<List<Leg>> queue = new LinkedList<>();

        // Fill deque with paths starting from the origin airport
        for (Leg first : graph.getOrDefault(origin, new ArrayList<>())) {
            if (first.getDepartureTime().toLocalDate().equals(departureDay)) {
                queue.add(new ArrayList<>(List.of(first)));
            }
        }
        while (!queue.isEmpty()) {
            List<Leg> path = queue.poll();
            Airport currentAirport = path.getLast().getDestinationAirport(); // Represents the last airport of the path

            // Build flight with current path if already in destination
            if (currentAirport.equals(destination)) {
                Flight flight = buildFlight(path);
                resultFlights.add(flight);
                continue;
            }

            // Create new paths with the legs starting from the current airport
            for (Leg nextLeg : graph.getOrDefault(currentAirport, new ArrayList<>())) {

                Leg previousLeg = path.getLast();
                LegSummary previousSummary = previousLeg.toSummary();
                LegSummary nextSummary = nextLeg.toSummary();

                if (canExtendPath(path, previousSummary, nextSummary)) {
                    List<Leg> newPath = new ArrayList<>(path);
                    newPath.add(nextLeg);
                    queue.add(newPath);
                }
            }
        }
        return resultFlights;
    }

    private boolean canExtendPath(List<Leg> path, LegSummary previousSummary, LegSummary nextSummary){
        return Flight.isNewLegValid(previousSummary, nextSummary) && Flight.canAddMoreLegs(path.size()) &&
                Layover.canCreateLayoverWithDuration(Duration.between(previousSummary.arrivalTime(), nextSummary.departureTime())) &&!path.stream().anyMatch(
                selected -> selected.getOriginAirport().equals(nextSummary.originAirport()));
    }

    private Flight buildFlight(List<Leg> path) {
        Leg first = path.getFirst();
        Flight flight = Flight.createFlight(first.getId(), first.toSummary());
        for (int i = 1; i < path.size(); i++) {
            LegSummary nextLegSummary = path.get(i).toSummary();
            flight.addLeg(path.get(i).getId(), nextLegSummary);
        }
        return flight;
    }

    private Map<Airport, List<Leg>> createGraph(int numberOfPassengers) {
        Map<Airport, List<Leg>> graph = new HashMap<>();
        for (Leg leg: legRepository.findLegsWithAtLeastAvailableSeats(numberOfPassengers)){
            if (!graph.containsKey(leg.getOriginAirport())){
                graph.put(leg.getOriginAirport(), new ArrayList<>());
            }
            graph.get(leg.getOriginAirport()).add(leg);

        }
        return graph;
    }



}
