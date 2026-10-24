package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.FlightScope;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExtraServiceTest {

    @Test
    void shouldCreateExtraService() {
        // Act
        ExtraService extraService = ExtraService.createExtraService("Priority boarding", "Board first", 20.0, 35.0);

        // Assert
        assertNotNull(extraService.getId());
        assertEquals("Priority boarding", extraService.getNameService());
        assertEquals(Optional.of("Board first"), extraService.getDescription());
        assertEquals(20.0, extraService.getPrice(FlightScope.NATIONAL));
        assertEquals(35.0, extraService.getPrice(FlightScope.INTERNATIONAL));
    }

    @Test
    void shouldCreateExtraServiceWithoutDescription() {
        // Act
        ExtraService extraService = ExtraService.createExtraService("Priority boarding", null, 20.0, 35.0);

        // Assert
        assertTrue(extraService.getDescription().isEmpty());
    }

    @Test
    void shouldCreateExtraServiceWithoutDescriptionWhenItIsBlank() {
        // Act
        ExtraService extraService = ExtraService.createExtraService("Priority boarding", "   ", 20.0, 35.0);

        // Assert
        assertTrue(extraService.getDescription().isEmpty());
    }

    @Test
    void shouldCreateFreeExtraService() {
        // Act
        ExtraService extraService = ExtraService.createExtraService("Snack", "Free snack", 0.0, 0.0);

        // Assert
        assertEquals(0.0, extraService.getPrice(FlightScope.NATIONAL));
        assertEquals(0.0, extraService.getPrice(FlightScope.INTERNATIONAL));
    }

    @Test
    void shouldCreateExtraServicesWithDifferentIds() {
        // Act
        ExtraService first = ExtraService.createExtraService("Priority boarding", null, 20.0, 35.0);
        ExtraService second = ExtraService.createExtraService("Priority boarding", null, 20.0, 35.0);

        // Assert
        assertNotEquals(first, second);
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(DomainRuleException.class,
                () -> ExtraService.createExtraService(null, "Board first", 20.0, 35.0));
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(DomainRuleException.class,
                () -> ExtraService.createExtraService("   ", "Board first", 20.0, 35.0));
    }

    @Test
    void shouldThrowExceptionWhenNationalPriceIsNegative() {
        assertThrows(DomainRuleException.class,
                () -> ExtraService.createExtraService("Priority boarding", "Board first", -1.0, 35.0));
    }

    @Test
    void shouldThrowExceptionWhenInternationalPriceIsNegative() {
        assertThrows(DomainRuleException.class,
                () -> ExtraService.createExtraService("Priority boarding", "Board first", 20.0, -1.0));
    }
}
