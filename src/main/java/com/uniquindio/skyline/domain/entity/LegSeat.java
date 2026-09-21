package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.SeatStatus;

import java.util.Optional;

public class LegSeat {
    private String id;
    private String seatId;
    private SeatStatus seatStatus;
    private Optional<String> passengerId;

    private LegSeat(String seatId) {
        this.seatId = seatId;
        this.seatStatus = SeatStatus.AVALIABLE;
        this.passengerId = Optional.empty();
    }

    public static LegSeat createLegSeat(Seat seat) {
        return new  LegSeat(seat.getId());
    }
}
