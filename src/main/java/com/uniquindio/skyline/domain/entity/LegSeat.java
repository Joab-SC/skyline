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

    private LegSeat(String id, String seatCode) {
        this.id = id;
        this.seatCode = seatCode;
        this.seatStatus = SeatStatus.AVALIABLE;
        this.passengerId = Optional.empty();
    }

    // Creates an available leg seat.
    public static LegSeat createLegSeat(String id, String seatCode) {
        if(seatCode == null || seatCode.isBlank()){
            throw new DomainRuleException("The legSeat must include the seat");
        }

        if(id == null || id.isEmpty()){
            throw new DomainRuleException("The legSeat requires an id");
        }
        return new  LegSeat(id, seatCode);
    }


}
