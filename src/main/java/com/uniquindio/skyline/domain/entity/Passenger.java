package com.uniquindio.skyline.domain.entity;


import com.uniquindio.skyline.domain.valueObject.Seat;

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


}