package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;

import java.time.Duration;
import java.time.LocalDateTime;

public record Layover(
        Airport airport,
        Duration duration
) {

    private static final Duration MAX_DURATION = Duration.ofHours(12);
    private static final Duration MIN_DURATION = Duration.ofHours(1);

    public Layover(
            Airport airport,
            LocalDateTime endFirstLeg,
            LocalDateTime startNextLeg
    ) {
        this(
                validateAirport(airport),
                calculateDuration(endFirstLeg, startNextLeg)
        );
    }

    public Layover {
        if (airport == null) {
            throw new DomainRuleException("The airport is required");
        }

        if (duration == null || !canCreateLayoverWithDuration(duration)) {
            throw new DomainRuleException(
                    "The duration must be between "
                            + MIN_DURATION + " and " + MAX_DURATION
            );
        }
    }

    private static Airport validateAirport(Airport airport) {
        if (airport == null) {
            throw new DomainRuleException("The airport is required");
        }
        return airport;
    }

    private static Duration calculateDuration(
            LocalDateTime endFirstLeg,
            LocalDateTime startNextLeg
    ) {
        if (endFirstLeg == null || startNextLeg == null) {
            throw new DomainRuleException(
                    "The hours for the layover cannot be null"
            );
        }

        return Duration.between(endFirstLeg, startNextLeg);
    }

    public static boolean canCreateLayoverWithDuration(Duration duration) {
        return duration != null
                && duration.compareTo(MIN_DURATION) >= 0
                && duration.compareTo(MAX_DURATION) <= 0;
    }
}