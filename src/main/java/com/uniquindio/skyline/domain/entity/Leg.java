package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.Airport;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Leg {
    private String id;
    private Airport originAirport;
    private Airport destinationAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private Aircraft aircraft;
    private double price;
    private List<Passenger> passengers;

}
