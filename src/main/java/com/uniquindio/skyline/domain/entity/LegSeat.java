package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
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

    public static LegSeat createLegSeat(String seatId) {
        if(seatId == null || seatId.isEmpty()){
            throw new DomainRuleException("The legSeat must include the seat");
        }
        return new  LegSeat(seatId);
    }
}
