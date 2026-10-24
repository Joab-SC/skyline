package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.FlightScope;
import lombok.Getter;

import java.util.Objects;

@Getter
public class ExtraService {
    private String id;
    private String nameService;
    private String description;
    private double nationalPrice;
    private double internationalPrice;

    // Returns the unit price that applies to the given flight scope.
    public double getPrice(FlightScope flightScope) {
        if (flightScope == null) {
            throw new DomainRuleException("The flight scope is required");
        }
        return switch (flightScope) {
            case NATIONAL -> nationalPrice;
            case INTERNATIONAL -> internationalPrice;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExtraService)) return false;
        ExtraService other = (ExtraService) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
