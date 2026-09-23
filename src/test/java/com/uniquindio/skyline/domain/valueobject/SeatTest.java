package com.uniquindio.skyline.domain.valueobject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.Seat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class SeatTest {

    @Test
    void twoSeatsWithSameDataAreEqual(){

        Seat seat = new Seat("A21");
        Seat duplicateSeat = new Seat("A21");

        assertEquals(seat, duplicateSeat);
    }

    @Test
    void SeatWithInvalidDataThrowsException(){
        assertThrows(DomainRuleException.class, () -> {
            new Seat(" ");
        });
        assertThrows(DomainRuleException.class, () -> {
            new Seat(null);
        });

    }
}
