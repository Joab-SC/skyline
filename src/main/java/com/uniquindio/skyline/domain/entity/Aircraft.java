package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import lombok.Getter;

import java.util.List;

@Getter
public class Aircraft {
    private String id;
    private AircraftModel aircraftModel;
    private int numSeats;
    private List<Seat> seats;

    private Aircraft(AircraftModel aircraftModel, int numSeats, List<Seat> seats) {
        this.aircraftModel = aircraftModel;
        this.numSeats = numSeats;
        this.seats = seats;
    }


}
