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
    private List<Seat> seats;

    private Aircraft(String id, AircraftModel aircraftModel, List<Seat> seats) {
        this.id = id;
        this.aircraftModel = aircraftModel;
        this.seats = seats;
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
