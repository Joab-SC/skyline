package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Layover;
import com.uniquindio.skyline.domain.valueObject.LegSummary;
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
                   LocalDateTime departureTime, LocalDateTime arrivalTime) {
        this.id = id;
        this.airlineId = airlineId;
        this.price = price;
        this.idLegs = idLegs;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.layovers = new ArrayList<>();
    }

    public static Flight createFlight(String idleg, LegSummary leg) {
        ArrayList<String> idLegs = new ArrayList<>();
        idLegs.add(idleg);
        if(leg == null){
            throw new DomainRuleException("The first leg for the flight is required");
        }
        validateFlight(leg.price(), idLegs, leg.airlineId(), leg.originAirport(), leg.destinationAirport(),
                leg.departureTime(), leg.arrivalTime());

        return new Flight(UUID.randomUUID().toString(), leg.airlineId(), leg.price(), idLegs,
                leg.originAirport(), leg.destinationAirport(), leg.departureTime(), leg.arrivalTime());
    }

    public Flight addLeg(String idLeg, LegSummary newLeg) {

        if (idLeg == null || idLeg.isBlank()) {
            throw new DomainRuleException("The leg ID cannot be null or empty");
        }
        validateNewLeg(newLeg);


        if(!canAddMoreLegs(idLegs.size())) {
            throw new DomainRuleException("The maximum number of legs for a flight is " + MAX_LEGS);
        }
        Layover layover = new Layover(this.destinationAirport, this.arrivalTime, newLeg.departureTime());
        idLegs.add(idLeg);
        layovers.add(layover);
        this.price += newLeg.price();
        this.arrivalTime = newLeg.arrivalTime();
        this.destinationAirport = newLeg.destinationAirport();

        return this;
    }


    public static boolean isNewLegValid(LegSummary previousLeg, LegSummary newLeg) {
       return isAirlineIdValid(previousLeg.airlineId(), newLeg.airlineId())
                && isNewOriginAirportValid(previousLeg.destinationAirport(), newLeg.originAirport())
                && isNewDepartureTimeValid(newLeg.departureTime(), previousLeg.arrivalTime());
    }

    private void validateNewLeg(LegSummary newLeg) {
        if (!isAirlineIdValid(airlineId, newLeg.airlineId())){
            throw new DomainRuleException(
                    "All the legs for a flight must be from the same airline"
            );
        }

        if (!isNewOriginAirportValid(destinationAirport, newLeg.originAirport())) {
            throw new DomainRuleException(
                    "The origin airport of the new leg must match the current destination airport"
            );
        }

        if (!isNewDepartureTimeValid(newLeg.departureTime(), arrivalTime)) {
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

    private static void validateFlight(double price, List<String> idLegs, String airlineId, Airport originAirport, Airport destinationAirport,
                                       LocalDateTime departureTime, LocalDateTime arrivalTime) {
        if (price <= 0) {
            throw new DomainRuleException("The price must be greater than 0");
        }

        if (idLegs == null || idLegs.isEmpty()) {
            throw new DomainRuleException("The leg IDs cannot be null or empty");
        }

        if (idLegs.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new DomainRuleException("The leg IDs cannot be null or empty");
        }

        if (airlineId == null || airlineId.isBlank()) {
            throw new DomainRuleException("The airline id cannot be null or empty");
        }

        if (originAirport == null) {
            throw new DomainRuleException("The origin airport cannot be null");
        }

        if (destinationAirport == null) {
            throw new DomainRuleException("The destination airport cannot be null");
        }

        if (departureTime == null) {
            throw new DomainRuleException("The departure time cannot be null");
        }

        if (arrivalTime == null) {
            throw new DomainRuleException("The arrival time cannot be null");
        }

        if (arrivalTime.isBefore(departureTime)) {
            throw new DomainRuleException("The arrival time cannot be before the departure time");
        }
    }
}