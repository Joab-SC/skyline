package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Layover {
    private String id;
    private Airport airport;
    private Duration duration;

    // Creates a layover for an airport.
    private Layover(String id, Airport airport, Duration duration) {
        this.id = id;
        this.airport = airport;
        this.duration = duration;
    }

    public static Layover createLayover(Airport airport, LocalDateTime endFirstLeg, LocalDateTime startNextLeg) {
        if(endFirstLeg == null || startNextLeg == null){
            throw new DomainRuleException("The hours for the layover cannot be null");
        }
        if(startNextLeg.isBefore(endFirstLeg)){
            throw new DomainRuleException("The hours for the layover cannot be before the end of the first leg");
        }
        if (airport == null) {
            throw new DomainRuleException("The airport is required");
        }
        return new Layover(UUID.randomUUID().toString(),airport, Duration.between(endFirstLeg, startNextLeg));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Layover)) return false;
        Layover other = (Layover) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
