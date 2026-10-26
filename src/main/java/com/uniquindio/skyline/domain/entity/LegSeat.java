package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import lombok.Getter;

import java.util.Objects;
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
        this.seatStatus = SeatStatus.AVAILABLE;
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
        return new LegSeat(id, seatCode);
    }

    // Rebuilds a leg seat that was already persisted, with its status and passenger.
    public static LegSeat reconstruct(String id, String seatCode, SeatStatus seatStatus, String passengerId) {
        LegSeat legSeat = new LegSeat(id, seatCode);
        legSeat.seatStatus = seatStatus;
        legSeat.passengerId = Optional.ofNullable(passengerId);
        return legSeat;
    }

    public void assignPassengerToLegSeat(String idPassenger){
         if (idPassenger == null || idPassenger.isBlank()){
             throw new DomainRuleException("id passenger cannot be null");
         }
         if (this.seatStatus == SeatStatus.OCCUPIED){
             throw new DomainRuleException("The seat is alredy occupied");
         }
         this.passengerId = Optional.of(idPassenger);
         this.seatStatus = SeatStatus.OCCUPIED;
    }

    public void releaseSeat(){
        this.passengerId = Optional.empty();
        this.seatStatus = SeatStatus.AVAILABLE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LegSeat)) return false;
        LegSeat other = (LegSeat) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
