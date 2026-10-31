package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineAircraftCount;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTopAirlinesUseCaseTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private GetTopAirlinesUseCase useCase;

    @Test
    void shouldReturnAirlinesWithMostAircraft() {
        // Arrange
        List<AirlineAircraftCount> ranking = List.of(
                new AirlineAircraftCount("airline-1", "Avianca", 3L),
                new AirlineAircraftCount("airline-2", "LATAM", 1L));
        when(airlineRepository.findTopByAircraftCount(5)).thenReturn(ranking);

        // Act
        List<AirlineAircraftCount> result = useCase.execute(5);

        // Assert
        assertEquals(ranking, result);
        verify(airlineRepository).findTopByAircraftCount(5);
        verify(airlineRepository, never()).save(any());
    }

    @Test
    void shouldReturnEmptyListWhenNoAirlineHasAircraft() {
        // Arrange
        when(airlineRepository.findTopByAircraftCount(5)).thenReturn(List.of());

        // Act
        List<AirlineAircraftCount> result = useCase.execute(5);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenLimitIsLessThanOne() {
        // Act & Assert
        assertThrows(DomainRuleException.class, () -> useCase.execute(0));
        verify(airlineRepository, never()).findTopByAircraftCount(anyInt());
    }

    @Test
    void shouldThrowExceptionWhenLimitIsGreaterThanTwenty() {
        // Act & Assert
        assertThrows(DomainRuleException.class, () -> useCase.execute(21));
        verify(airlineRepository, never()).findTopByAircraftCount(anyInt());
    }
}