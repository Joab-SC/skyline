package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class SeatTest {

    @Test
    void twoSeatsWithSameDataAreEqual(){
        // Checks value equality for the same seat code.

        Seat seat = new Seat("A21");
        Seat duplicateSeat = new Seat("A21");

        assertEquals(seat, duplicateSeat);
    }

    @Test
    void SeatWithInvalidDataThrowsException(){
        // Checks seat code validation errors.
        assertThrows(DomainRuleException.class, () -> {
            new Seat(" ");
        });
        assertThrows(DomainRuleException.class, () -> {
            new Seat(null);
        });

    }
}
