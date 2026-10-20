package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LuggageDetailTest {

    @Test
    void createsLuggageDetailWithValidData() {
        assertDoesNotThrow(() -> new LuggageDetail(List.of("luggage-1"), 1, 50.0, 2));
    }

    @Test
    void acceptsZeroSubtotal() {
        assertDoesNotThrow(() -> new LuggageDetail(List.of("luggage-1"), 1, 0, 2));
    }

    @Test
    void rejectsNegativeSubtotal() {
        assertThrows(DomainRuleException.class, () -> new LuggageDetail(List.of("luggage-1"), 1, -1, 2));
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThrows(DomainRuleException.class, () -> new LuggageDetail(List.of("luggage-1"), 0, 50.0, 2));
    }

    @Test
    void rejectsNonPositiveMaxQuantity() {
        assertThrows(DomainRuleException.class, () -> new LuggageDetail(List.of("luggage-1"), 1, 50.0, 0));
    }

    @Test
    void rejectsNullOrEmptyLuggageIds() {
        assertThrows(DomainRuleException.class, () -> new LuggageDetail(null, 1, 50.0, 2));
        assertThrows(DomainRuleException.class, () -> new LuggageDetail(List.of(), 1, 50.0, 2));
    }

    @Test
    void rejectsNullOrEmptyLuggageIdElements() {
        assertThrows(DomainRuleException.class, () -> new LuggageDetail(java.util.Arrays.asList("luggage-1", null), 1, 50.0, 2));
        assertThrows(DomainRuleException.class, () -> new LuggageDetail(List.of("luggage-1", " "), 1, 50.0, 2));
    }

    @Test
    void rejectsQuantityAboveMaximum() {
        assertThrows(DomainRuleException.class, () -> new LuggageDetail(List.of("luggage-1"), 3, 50.0, 2));
    }
}
