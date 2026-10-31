package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAirlineExtraServicesUseCaseTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private ListAirlineExtraServicesUseCase useCase;

    @Test
    void shouldReturnExtraServicesOfTheAirline() {
        // Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 100);
        ExtraService meal = airline.createExtraService("Meal", "Hot meal", 20, 40);
        ExtraService pet = airline.createExtraService("Pet", null, 50, 80);
        when(airlineRepository.findById(airline.getId())).thenReturn(Optional.of(airline));

        // Act
        List<ExtraService> result = useCase.execute(airline.getId());

        // Assert
        assertEquals(List.of(meal, pet), result);
        verify(airlineRepository, never()).save(any());
    }

    @Test
    void shouldReturnEmptyListWhenAirlineHasNoExtraServices() {
        // Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 100);
        when(airlineRepository.findById(airline.getId())).thenReturn(Optional.of(airline));

        // Act
        List<ExtraService> result = useCase.execute(airline.getId());

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowWhenAirlineDoesNotExist() {
        // Arrange
        when(airlineRepository.findById("airline-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute("airline-1"));
    }
}
