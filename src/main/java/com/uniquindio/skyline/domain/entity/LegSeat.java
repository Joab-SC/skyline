package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.PassengerBookingId;
import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import lombok.Getter;

import java.util.Objects;
import java.util.Optional;
@Getter
public class LegSeat {
    private String id;
    private String seatCode;
    private SeatStatus seatStatus;
    private Optional<PassengerBookingId> passengerBookingId;

    private LegSeat(String id, String seatCode) {
        this.id = id;
        this.seatCode = seatCode;
        this.seatStatus = SeatStatus.AVAILABLE;
        this.passengerBookingId = Optional.empty();
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
    public static LegSeat reconstruct(String id, String seatCode, SeatStatus seatStatus, String passengerId, String bookingId) {
        LegSeat legSeat = new LegSeat(id, seatCode);
        legSeat.seatStatus = seatStatus;
        if (bookingId != null && passengerId != null) {
            legSeat.passengerBookingId = Optional.of(new PassengerBookingId(bookingId, passengerId));
        }
        return legSeat;
    }

    public void assignPassengerToLegSeat(PassengerBookingId passengerBookingId) {
         if (passengerBookingId == null) {
             throw new DomainRuleException("The passenger of the booking is required");
         }
         if (this.seatStatus == SeatStatus.OCCUPIED){
             throw new DomainRuleException("The seat is alredy occupied");
         }
         this.passengerBookingId = Optional.of(passengerBookingId);
         this.seatStatus = SeatStatus.OCCUPIED;
    }

    public void releaseSeat(){
        this.passengerBookingId = Optional.empty();
        this.seatStatus = SeatStatus.AVAILABLE;
    }

    public boolean isOccupiedBy(PassengerBookingId passengerBookingId) {
        return this.passengerBookingId.map(occupant -> occupant.equals(passengerBookingId)).orElse(false);
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
