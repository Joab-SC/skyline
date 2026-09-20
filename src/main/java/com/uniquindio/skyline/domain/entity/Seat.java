package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Seat {

    String id;
    String code;
    SeatStatus seatStatus;

    public Seat(String code, SeatStatus seatStatus) {
        this.code = code;
        this.seatStatus = seatStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seat)) return false;
        Seat other = (Seat) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
