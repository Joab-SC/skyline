package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.Airport;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public static Flight createFlightBasic(String id, String airlineId, double price, List<String> idLegs, Airport originAirport, Airport destinationAirport,
                                    LocalDateTime departureTime, LocalDateTime arrivalTime){
        return new Flight(id, airlineId, price, idLegs, originAirport, destinationAirport,
                 departureTime, arrivalTime);
    }

}
