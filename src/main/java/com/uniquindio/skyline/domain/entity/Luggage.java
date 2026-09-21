package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;

public class Luggage {
    private final int maxWeight = 23;
    double price;

    private Luggage(double price) {
        this.price = price;
    }


    public static Luggage createLuggage(Double luggagePrice) {
        if(luggagePrice <  0 ){
            throw new DomainRuleException("The price of the luggage must be positive");
        }
        return new Luggage(luggagePrice);
    }

}