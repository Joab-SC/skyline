package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteExtraServiceUseCaseTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private DeleteExtraServiceUseCase useCase;

    @Test
    void shouldMarkExtraServiceAsDeletedAndSaveAirline() {
        // Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 100);
        ExtraService meal = airline.createExtraService("Meal", "Hot meal", 20, 40);
        when(airlineRepository.findById(airline.getId())).thenReturn(Optional.of(airline));

        // Act
        useCase.execute(airline.getId(), meal.getId());

        // Assert
        assertTrue(meal.isDeleted());
        assertTrue(airline.getActiveExtraServices().isEmpty());
        verify(airlineRepository).save(airline);
    }

    @Test
    void shouldThrowWhenAirlineDoesNotExist() {
        // Arrange
        when(airlineRepository.findById("airline-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute("airline-1", "service-1"));
        verify(airlineRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenAirlineDoesNotHaveTheExtraService() {
        // Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 100);
        when(airlineRepository.findById(airline.getId())).thenReturn(Optional.of(airline));

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute(airline.getId(), "missing"));
        verify(airlineRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenExtraServiceIsAlreadyDeleted() {
        // Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 100);
        ExtraService meal = airline.createExtraService("Meal", "Hot meal", 20, 40);
        airline.deleteExtraService(meal.getId());
        when(airlineRepository.findById(airline.getId())).thenReturn(Optional.of(airline));

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> useCase.execute(airline.getId(), meal.getId()));
        verify(airlineRepository, never()).save(any());
    }
}