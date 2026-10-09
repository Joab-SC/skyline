package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.entity.Layover;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Flight {

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

    public static Flight createFlight(double price, String airlineId, String idleg, Airport originAirport, Airport destinationAirport, LocalDateTime departureTime,
            LocalDateTime arrivalTime) {
        ArrayList<String> idLegs = new ArrayList<>();
        idLegs.add(idleg);
        validateFlight(price, idLegs, originAirport, destinationAirport, departureTime, arrivalTime
        );

        return new Flight(UUID.randomUUID().toString(), airlineId, price, idLegs, originAirport, destinationAirport, departureTime, arrivalTime);

    }

    public Flight addLeg(
            String idLeg, String airlineId, Airport legOriginAirport, Airport legDestinationAirport,
            LocalDateTime departureTime, LocalDateTime arrivalTime) {

        if (idLeg == null || idLeg.isBlank()) {
            throw new DomainRuleException("The leg ID cannot be null or empty");
        }

        if (legDestinationAirport == null) {
            throw new DomainRuleException("The leg destination airport cannot be null");
        }

        if (!isAirlineIdValid(this.airlineId, airlineId)) {
            throw new DomainRuleException(
                    "All the legs for a flight must be from the same airline"
            );
        }

        if (!isNewOriginAirportValid(this.destinationAirport, legOriginAirport)) {
            throw new DomainRuleException(
                    "The origin airport of the new leg must match the current destination airport"
            );
        }

        if (!isNewDepartureTimeValid(departureTime, this.arrivalTime)) {
            throw new DomainRuleException(
                    "The departure time of the new leg must be after the current arrival time"
            );
        }

        if (!isArrivalTimeAfterDepartureTime(departureTime, arrivalTime)) {
            throw new DomainRuleException(
                    "The arrival time must be after the departure time"
            );
        }

        Layover layover = Layover.createLayover(
                this.destinationAirport,
                this.arrivalTime,
                departureTime
        );

        idLegs.add(idLeg);
        layovers.add(layover);

        this.arrivalTime = arrivalTime;
        this.destinationAirport = legDestinationAirport;

        return this;
    }


    public static boolean isNewLegValid(
            String flightAirlineId, String legAirlineId, Airport previousArrivalAirport, Airport legOriginAirport,
            LocalDateTime previousArrivalTime, LocalDateTime legDepartureTime, LocalDateTime legArrivalTime) {

        return isAirlineIdValid(flightAirlineId, legAirlineId)
                && isNewOriginAirportValid(previousArrivalAirport, legOriginAirport)
                && isNewDepartureTimeValid(legDepartureTime, previousArrivalTime)
                && isArrivalTimeAfterDepartureTime(legDepartureTime, legArrivalTime);
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

    private static boolean isArrivalTimeAfterDepartureTime(LocalDateTime departureTime, LocalDateTime arrivalTime) {
        return departureTime.isBefore(arrivalTime);
    }

    private static void validateFlight(double price, List<String> idLegs, Airport originAirport, Airport destinationAirport,
                                       LocalDateTime departureTime, LocalDateTime arrivalTime) {
        if (price <= 0) {
            throw new DomainRuleException("The price must be greater than 0");
        }

        if (idLegs == null || idLegs.isEmpty()) {
            throw new DomainRuleException(
                    "The leg IDs cannot be null or empty"
            );
        }

        if (idLegs.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new DomainRuleException(
                    "The leg IDs cannot be null or empty"
            );
        }

        if (originAirport == null) {
            throw new DomainRuleException(
                    "The origin airport cannot be null"
            );
        }

        if (destinationAirport == null) {
            throw new DomainRuleException(
                    "The destination airport cannot be null"
            );
        }

        if (departureTime == null) {
            throw new DomainRuleException(
                    "The departure time cannot be null"
            );
        }

        if (arrivalTime == null) {
            throw new DomainRuleException(
                    "The arrival time cannot be null"
            );
        }

        if (arrivalTime.isBefore(departureTime)) {
            throw new DomainRuleException(
                    "The arrival time cannot be before the departure time"
            );
        }
    }

}