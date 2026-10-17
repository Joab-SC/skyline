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
    private static final Duration MAXDURATION =  Duration.ofHours(12);
    private static final Duration MINDURATION =  Duration.ofHours(1);
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
        if (airport == null) {
            throw new DomainRuleException("The airport is required");
        }
        Duration duration = Duration.between(endFirstLeg, startNextLeg);
        if (!canCreateLayoverWithDuration(duration)) {
            throw new DomainRuleException("The duration must be between " + MINDURATION + " and " + MAXDURATION);
        }

        return new Layover(UUID.randomUUID().toString(),airport, duration);
    }

    public static boolean canCreateLayoverWithDuration(Duration duration) {
        return duration.compareTo(MINDURATION) >= 0 && duration.compareTo(MAXDURATION) <= 0;
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
