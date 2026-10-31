package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAirlinesUseCaseTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private ListAirlinesUseCase useCase;

    @Test
    void shouldReturnEveryAirline() {
        // Arrange
        Airline avianca = Airline.createAirline("Avianca", 2, 50000);
        Airline latam = Airline.createAirline("LATAM", 1, 40000);
        when(airlineRepository.findAll()).thenReturn(List.of(avianca, latam));

        // Act
        List<Airline> result = useCase.execute();

        // Assert
        assertEquals(List.of(avianca, latam), result);
        verify(airlineRepository, never()).save(any());
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoAirlines() {
        // Arrange
        when(airlineRepository.findAll()).thenReturn(List.of());

        // Act
        List<Airline> result = useCase.execute();

        // Assert
        assertTrue(result.isEmpty());
    }
}