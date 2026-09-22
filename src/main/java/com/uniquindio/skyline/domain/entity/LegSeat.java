package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import lombok.Getter;

import java.util.Optional;
@Getter
public class LegSeat {
    private String id;
    private String seatCode;
    private SeatStatus seatStatus;
    private Optional<String> passengerId;

    private LegSeat(String seatCode) {
        this.seatCode = seatCode;
        this.seatStatus = SeatStatus.AVALIABLE;
        this.passengerId = Optional.empty();
    }

    public static LegSeat createLegSeat(String seatCode) {
        if(seatCode == null || seatCode.isEmpty()){
            throw new DomainRuleException("The legSeat must include the seat");
        }
        return new  LegSeat(seatCode);
    }
}
