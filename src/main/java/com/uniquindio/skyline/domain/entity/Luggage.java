package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import lombok.Getter;

import java.util.Objects;

@Getter
public class Luggage {
    private final int maxWeight = 23;
    private String id;
    private double price;

    private Luggage(String id, double price) {
        this.id = id;
        this.price = price;
    }


    public static Luggage createLuggage(String id, Double luggagePrice) {
        if(luggagePrice <  0 ){
            throw new DomainRuleException("The price of the luggage must be positive");
        }
        if(id == null || id.isEmpty()){
            throw new DomainRuleException("The id of the luggage is required");
        }
        return new Luggage(id,luggagePrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Luggage)) return false;
        Luggage other = (Luggage) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}