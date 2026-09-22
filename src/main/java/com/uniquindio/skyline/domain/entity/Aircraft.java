package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import com.uniquindio.skyline.domain.valueObject.Seat;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class Aircraft {
    private String id;
    private AircraftModel aircraftModel;
    private int numSeats;
    private List<Seat> seats;
    private int availableSeats;

    private Aircraft(String id, AircraftModel aircraftModel, int numSeats, List<Seat> seats, int availableSeats) {
        this.id = id;
        this.aircraftModel = aircraftModel;
        this.numSeats = numSeats;
        this.seats = seats;
        this.availableSeats = availableSeats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aircraft)) return false;
        Aircraft other = (Aircraft) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
