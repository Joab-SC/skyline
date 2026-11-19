package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AircraftRepository;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.valueObject.AircraftModel;
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
class ListAirlineAircraftsUseCaseTest {

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private AircraftRepository aircraftRepository;

    @InjectMocks
    private ListAirlineAircraftsUseCase useCase;

    @Test
    void shouldReturnAircraftsOfTheAirline() {
        // Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 100);
        Aircraft a320 = Aircraft.createAircraft(AircraftModel.AIRBUS_A320_NEO);
        Aircraft e175 = Aircraft.createAircraft(AircraftModel.EMBRAER_E175);
        airline.addAircraft(a320.getId());
        airline.addAircraft(e175.getId());
        when(airlineRepository.findById(airline.getId())).thenReturn(Optional.of(airline));
        when(aircraftRepository.findAllById(List.of(a320.getId(), e175.getId()))).thenReturn(List.of(a320, e175));

        // Act
        List<Aircraft> result = useCase.execute(airline.getId());

        // Assert
        assertEquals(List.of(a320, e175), result);
        verify(aircraftRepository, never()).save(any());
    }

    @Test
    void shouldReturnEmptyListWhenAirlineHasNoAircrafts() {
        // Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 100);
        when(airlineRepository.findById(airline.getId())).thenReturn(Optional.of(airline));
        when(aircraftRepository.findAllById(List.of())).thenReturn(List.of());

        // Act
        List<Aircraft> result = useCase.execute(airline.getId());

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowWhenAirlineDoesNotExist() {
        // Arrange
        when(airlineRepository.findById("airline-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute("airline-1"));
        verify(aircraftRepository, never()).findAllById(any());
    }
}
