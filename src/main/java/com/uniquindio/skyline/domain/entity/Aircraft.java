package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import com.uniquindio.skyline.domain.valueObject.Seat;
import lombok.Getter;

import java.util.List;

@Getter
public class Aircraft {
    private String id;
    private AircraftModel aircraftModel;
    private int numSeats;
    private List<Seat> seats;
    private int availableSeats;

    private Aircraft(AircraftModel aircraftModel, int numSeats, List<Seat> seats, int availableSeats) {
        this.aircraftModel = aircraftModel;
        this.numSeats = numSeats;
        this.seats = seats;
        this.availableSeats = availableSeats;
    }


}
