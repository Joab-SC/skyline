package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.SeatStatus;

public class LegSeat {
    private String id;
    private Seat seat;
    private SeatStatus seatStatus;

    private LegSeat(Seat seat, SeatStatus seatStatus) {
        this.seat = seat;
        this.seatStatus = seatStatus;
    }
}
