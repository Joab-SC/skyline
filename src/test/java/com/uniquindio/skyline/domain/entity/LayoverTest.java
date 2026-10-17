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
    private static final Duration MAXIMUM_DURATION_EXPECTED = Duration.ofHours(12);
    private static final Duration MINIMUM_DURATION_EXPECTED = Duration.ofHours(1);


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
                () -> Layover.createLayover(AIRPORT, END_FIRST_LEG, END_FIRST_LEG.plusHours(MAXIMUM_DURATION_EXPECTED.toHours()).plusHours(1)));
    }

    @Test
    void createLayoverWithExactMaximumDurationIsAccepted() {
        Layover layover = assertDoesNotThrow(() -> Layover.createLayover(AIRPORT, END_FIRST_LEG, END_FIRST_LEG.plusHours(MAXIMUM_DURATION_EXPECTED.toHours())));
        assertEquals(AIRPORT, layover.getAirport());
    }

    @Test
    void createLayoverShorterThanMinimumIsNotAccepted() {
        assertThrows(DomainRuleException.class,
                () -> Layover.createLayover(AIRPORT, END_FIRST_LEG, END_FIRST_LEG.plusHours(MINIMUM_DURATION_EXPECTED.toHours()).minusMinutes(1)));
    }

    @Test
    void createLayoverWithExactMinimumDurationIsAccepted() {
        Layover layover = assertDoesNotThrow(
                () -> Layover.createLayover(AIRPORT, END_FIRST_LEG, END_FIRST_LEG.plusHours(MINIMUM_DURATION_EXPECTED.toHours())));
        assertEquals(AIRPORT, layover.getAirport());
    }

    @Test
    void createLayoverStoresExpectedDuration() {
        LocalDateTime startSecondLeg = END_FIRST_LEG.plusHours(3);
        Layover layover = Layover.createLayover(AIRPORT, END_FIRST_LEG, startSecondLeg);

        assertEquals(Duration.between(END_FIRST_LEG, startSecondLeg), layover.getDuration());
    }


    @Test
    void canCreateLayoverWithBetweenMaximumAndMinimumDuration() {
        assertTrue(Layover.canCreateLayoverWithDuration(Duration.ofHours(5)));
    }

    @Test
    void canCreateLayoverWithExactMaximumDuration() {
        assertTrue(Layover.canCreateLayoverWithDuration(MAXIMUM_DURATION_EXPECTED));
    }

    @Test
    void cannotCreateLayoverWithDurationOverMaximum() {
        assertFalse(Layover.canCreateLayoverWithDuration(MAXIMUM_DURATION_EXPECTED.plusMinutes(1)));
    }

    @Test
    void canCreateLayoverWithExactMinimumDuration() {
        assertTrue(Layover.canCreateLayoverWithDuration(MINIMUM_DURATION_EXPECTED));
    }

    @Test
    void cannotCreateLayoverWithDurationUnderTheMinimum() {
        assertFalse(Layover.canCreateLayoverWithDuration(MINIMUM_DURATION_EXPECTED.minusMinutes(1)));
    }

}