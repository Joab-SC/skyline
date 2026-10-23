package com.uniquindio.skyline.domain.entity;


import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Layover;
import com.uniquindio.skyline.domain.entity.Leg.LegSummary;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Getter
public class Flight {
    private static final int MAX_LEGS= 4;
    private String id;
    private final String airlineId;
    private double price;
    private List<String> idLegs;
    private Airport originAirport;
    private Airport destinationAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private List<Layover> layovers;

    private Flight(String id, String airlineId, double price, List<String> idLegs, Airport originAirport, Airport destinationAirport,
                   LocalDateTime departureTime, LocalDateTime arrivalTime,  List<Layover> layovers) {
        this.id = id;
        this.airlineId = airlineId;
        this.price = price;
        this.idLegs = idLegs;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.layovers = layovers;
    }

    public static Flight createFlight(List<LegSummary> legs) {
        if (legs == null || legs.isEmpty())
            throw new DomainRuleException("A flight needs at least one leg");
        if (legs.size() > MAX_LEGS)
            throw new DomainRuleException("The maximum number of legs for a flight is " + MAX_LEGS);

        List<String> idLegs = new ArrayList<>();
        List<Layover> layovers = new ArrayList<>();
        double price = 0;

        for (int i = 0; i < legs.size(); i++) {
            LegSummary current = legs.get(i);
            if (i > 0) {
                LegSummary previous = legs.get(i - 1);
                validateNewLeg(previous, current);
                layovers.add(new Layover(previous.getDestinationAirport(),
                        previous.getArrivalTime(), current.getDepartureTime()));
            }
            idLegs.add(current.getId());
            price += current.getPrice();
        }

        LegSummary first = legs.getFirst();
        LegSummary last = legs.getLast();
        return new Flight(UUID.randomUUID().toString(), first.getAirlineId(), price,
                List.copyOf(idLegs), first.getOriginAirport(), last.getDestinationAirport(),
                first.getDepartureTime(), last.getArrivalTime(), List.copyOf(layovers));
    }




    public static boolean isNewLegValid(LegSummary previousLeg, LegSummary newLeg) {
       return isAirlineIdValid(previousLeg.getAirlineId(), newLeg.getAirlineId())
                && isNewOriginAirportValid(previousLeg.getDestinationAirport(), newLeg.getOriginAirport())
                && isNewDepartureTimeValid(newLeg.getDepartureTime(), previousLeg.getArrivalTime());
    }

    private static void validateNewLeg(LegSummary previousLeg, LegSummary newLeg) {
        if (!isAirlineIdValid(previousLeg.getAirlineId(), newLeg.getAirlineId())){
            throw new DomainRuleException(
                    "All the legs for a flight must be from the same airline"
            );
        }

        if (!isNewOriginAirportValid(previousLeg.getDestinationAirport(), newLeg.getOriginAirport())) {
            throw new DomainRuleException(
                    "The origin airport of the new leg must match the current destination airport"
            );
        }

        if (!isNewDepartureTimeValid(newLeg.getDepartureTime(), previousLeg.getArrivalTime())) {
            throw new DomainRuleException(
                    "The departure time of the new leg must be after the current arrival time"
            );
        }
    }

    public static boolean canAddMoreLegs(int currentAmount){
        return currentAmount<MAX_LEGS;
    }


    private static boolean isNewOriginAirportValid(Airport previousArrivalAirport, Airport legOriginAirport) {
        return previousArrivalAirport.equals(legOriginAirport);
    }

    private static boolean isNewDepartureTimeValid(LocalDateTime newDepartureTime, LocalDateTime flightArrivalTime) {
        return newDepartureTime.isAfter(flightArrivalTime);
    }

    private static boolean isAirlineIdValid(String flightAirlineId, String legAirlineId) {
        return flightAirlineId.equals(legAirlineId);
    }

}

