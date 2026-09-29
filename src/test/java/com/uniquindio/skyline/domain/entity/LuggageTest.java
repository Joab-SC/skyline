package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LuggageTest {

    @Test
    public void luggageThrowsExceptionWithNegativePrice(){
        // Checks negative price validation.
        assertThrows(DomainRuleException.class, () -> {
            Luggage.createLuggage("001",-2000.0);

        });
    }

    @Test
    public void luggageIsCreatedWithMaxWeight23(){
        // Checks the default max weight.
        Luggage luggage = Luggage.createLuggage("002", 50000.0);
        assertEquals(23,luggage.getMaxWeight());
    }

    @Test
    public void twoLuggageWithTheSameIdAreEqual(){
        // Checks equality by luggage id.
        Luggage luggageOriginal = Luggage.createLuggage("001", 50000.0);
        Luggage luggageDuplicated = Luggage.createLuggage("001", 80000.0);
        assertEquals(luggageOriginal,luggageDuplicated);
    }
}
