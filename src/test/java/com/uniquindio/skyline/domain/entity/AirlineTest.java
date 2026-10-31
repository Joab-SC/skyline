package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AirlineTest {

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(DomainRuleException.class,
                () -> Airline.createAirline(null, 3, 100.0));
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(DomainRuleException.class,
                () -> Airline.createAirline("   ", 3, 100.0));
    }

    @Test
    void shouldThrowExceptionWhenMaximumLuggageAmountIsZero() {
        assertThrows(DomainRuleException.class,
                () -> Airline.createAirline("Skyline Air", 0, 100.0));
    }

    @Test
    void shouldThrowExceptionWhenMaximumLuggageAmountIsNegative() {
        assertThrows(DomainRuleException.class,
                () -> Airline.createAirline("Skyline Air", -1, 100.0));
    }

    @Test
    void shouldThrowExceptionWhenSeatPriceIsNegative() {
        assertThrows(DomainRuleException.class,
                () -> Airline.createAirline("Skyline Air", 3, -1.0));
    }

    // addLeg

    @Test
    void shouldAddLeg() {
        // Arrange
        Airline airline = createValidAirline();

        // Act
        airline.addLeg("leg-1");

        // Assert
        assertEquals(List.of("leg-1"), airline.getLegIds());
    }

    @Test
    void shouldThrowExceptionWhenLegAlreadyExists() {
        // Arrange
        Airline airline = createValidAirline();
        airline.addLeg("leg-1");

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> airline.addLeg("leg-1"));
        assertEquals(1, airline.getLegIds().size());
    }

    @Test
    void shouldThrowExceptionWhenLegIdIsNull() {
        // Arrange
        Airline airline = createValidAirline();

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> airline.addLeg(null));
        assertTrue(airline.getLegIds().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenLegIdIsEmpty() {
        // Arrange
        Airline airline = createValidAirline();

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> airline.addLeg(""));
        assertTrue(airline.getLegIds().isEmpty());
    }

    @Test
    void shouldHaveAircraftWhenItWasAdded() {
        // Arrange
        Airline airline = createValidAirline();
        airline.addAircraft("aircraft-1");

        // Act & Assert
        assertTrue(airline.hasAircraft("aircraft-1"));
    }

    @Test
    void shouldNotHaveAircraftOfAnotherAirline() {
        // Arrange
        Airline airline = createValidAirline();
        airline.addAircraft("aircraft-1");

        // Act & Assert
        assertFalse(airline.hasAircraft("aircraft-2"));
    }

    @Test
    void shouldThrowExceptionWhenValidatingAircraftOfAnotherAirline() {
        // Arrange
        Airline airline = createValidAirline();
        airline.addAircraft("aircraft-1");

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> airline.validateHasAircraft("aircraft-2"));
    }
    // addAircraft

    @Test
    void shouldAddAircraft() {
        // Arrange
        Airline airline = createValidAirline();

        // Act
        airline.addAircraft("aircraft-1");

        // Assert
        assertEquals(List.of("aircraft-1"), airline.getAircraftIds());
    }

    @Test
    void shouldThrowExceptionWhenAircraftAlreadyExists() {
        // Arrange
        Airline airline = createValidAirline();
        airline.addAircraft("aircraft-1");

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> airline.addAircraft("aircraft-1"));
        assertEquals(1, airline.getAircraftIds().size());
    }

    @Test
    void shouldThrowExceptionWhenAircraftIdIsNull() {
        // Arrange
        Airline airline = createValidAirline();

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> airline.addAircraft(null));
        assertTrue(airline.getAircraftIds().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenAircraftIdIsEmpty() {
        // Arrange
        Airline airline = createValidAirline();

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> airline.addAircraft(""));
        assertTrue(airline.getAircraftIds().isEmpty());
    }

    // createExtraService

    @Test
    void shouldCreateExtraServiceAndAddItToAirline() {
        // Arrange
        Airline airline = createValidAirline();

        // Act
        ExtraService extraService = airline.createExtraService("Priority boarding", "Board first", 20.0, 35.0);

        // Assert
        assertEquals(List.of(extraService), airline.getExtraServices());
        assertEquals(Optional.of(extraService), airline.getExtraService(extraService.getId()));
    }

    @Test
    void shouldNotAddExtraServiceWhenDataIsInvalid() {
        // Arrange
        Airline airline = createValidAirline();

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> airline.createExtraService("", "Board first", 20.0, 35.0));
        assertTrue(airline.getExtraServices().isEmpty());
    }

    // deleteExtraService (soft delete)

    @Test
    void shouldDeleteExtraServiceButKeepItInTheAirline() {
        // Arrange
        Airline airline = createValidAirline();
        ExtraService extraService = airline.createExtraService("Priority boarding", "Board first", 20.0, 35.0);

        // Act
        airline.deleteExtraService(extraService.getId());

        // Assert
        assertTrue(extraService.isDeleted());
        assertEquals(Optional.of(extraService), airline.getExtraService(extraService.getId()));
    }

    @Test
    void shouldReturnOnlyActiveExtraServices() {
        // Arrange
        Airline airline = createValidAirline();
        ExtraService meal = airline.createExtraService("Meal", null, 20.0, 35.0);
        ExtraService pet = airline.createExtraService("Pet", null, 50.0, 80.0);
        airline.deleteExtraService(pet.getId());

        // Act
        List<ExtraService> active = airline.getActiveExtraServices();

        // Assert
        assertEquals(List.of(meal), active);
        assertEquals(2, airline.getExtraServices().size());
    }

    @Test
    void shouldThrowExceptionWhenDeletingExtraServiceTheAirlineDoesNotHave() {
        // Arrange
        Airline airline = createValidAirline();

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> airline.deleteExtraService("missing"));
    }

    @Test
    void shouldThrowExceptionWhenDeletingExtraServiceTwice() {
        // Arrange
        Airline airline = createValidAirline();
        ExtraService extraService = airline.createExtraService("Meal", null, 20.0, 35.0);
        airline.deleteExtraService(extraService.getId());

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> airline.deleteExtraService(extraService.getId()));
    }

    private Airline createValidAirline() {
        return Airline.createAirline("Skyline Air", 3, 100.0);
    }
}
