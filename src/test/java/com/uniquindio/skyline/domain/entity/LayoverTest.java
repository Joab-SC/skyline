package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Airport;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LayoverTest {

    private static final LocalDateTime END_FIRST_LEG = LocalDateTime.of(2026, 11, 10, 2, 0);
    private static final Airport AIRPORT = Airport.EL_DORADO;


    @Test
    void createLayoverWithNullEndFirstLegIsNotAccepted() {
        assertThrows(DomainRuleException.class,
                () -> Layover.createLayover(AIRPORT, null, END_FIRST_LEG.plusHours(3)));
    }

    @Test
    void createLayoverWithNullStartNextLegIsNotAccepted() {
        assertThrows(DomainRuleException.class,
                () -> Layover.createLayover(AIRPORT, END_FIRST_LEG, null));
    }

    @Test
    void createLayoverWithNullAirportIsNotAccepted() {
        assertThrows(DomainRuleException.class,
                () -> Layover.createLayover(null, END_FIRST_LEG, END_FIRST_LEG.plusHours(3)));
    }

    @Test
    void createLayoverLongerThanMaximumIsNotAccepted() {
        assertThrows(DomainRuleException.class,
                () -> Layover.createLayover(AIRPORT, END_FIRST_LEG, END_FIRST_LEG.plusHours(13)));
    }

    @Test
    void createLayoverWithExactMaximumDurationIsAccepted() {
        Layover layover = assertDoesNotThrow(
                () -> Layover.createLayover(AIRPORT, END_FIRST_LEG, END_FIRST_LEG.plusHours(12)));

        assertEquals(AIRPORT, layover.getAirport());
    }

    @Test
    void createLayoverStoresPositiveDuration() {
        Layover layover = Layover.createLayover(AIRPORT, END_FIRST_LEG, END_FIRST_LEG.plusHours(3));

        assertEquals(Duration.ofHours(3), layover.getDuration());
    }


    @Test
    void canCreateLayoverWithShortDuration() {
        assertTrue(Layover.canCreateLayoverWithDuration(Duration.ofHours(5)));
    }

    @Test
    void canCreateLayoverWithExactMaximumDuration() {
        assertTrue(Layover.canCreateLayoverWithDuration(Duration.ofHours(12)));
    }

    @Test
    void cannotCreateLayoverWithDurationOverMaximum() {
        assertFalse(Layover.canCreateLayoverWithDuration(Duration.ofHours(12).plusMinutes(1)));
    }
}