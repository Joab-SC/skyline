package com.uniquindio.skyline.domain.entity;


import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.LuggageDetail;
import com.uniquindio.skyline.domain.valueObject.Seat;
import lombok.Getter;

@Getter
public class Passenger  {
    private Seat seat;
    private boolean holder;
    private String email;
    private String phoneNumber;
    private Person person;
    private LuggageDetail luggageDetail;


    private Passenger(Seat seat, boolean holder, String email, String phoneNumber, Person person, LuggageDetail luggageDetail) {
        this.seat = seat;
        this.holder = holder;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.person = person;
        this.luggageDetail = luggageDetail;
    }

    // Creates a passenger without seat or luggage. Validations are pending (AddPassengersToBooking).
    public static Passenger createPassenger(Person person, boolean holder, String email, String phoneNumber) {
        return new Passenger(null, holder, email, phoneNumber, person, null);
    }

    // Replaces the luggage detail of the passenger.
    void registerLuggage(LuggageDetail luggageDetail) {
        if (luggageDetail == null){
            throw new DomainRuleException("LuggageDetail is required");
        }
        this.luggageDetail = luggageDetail;
    }


}