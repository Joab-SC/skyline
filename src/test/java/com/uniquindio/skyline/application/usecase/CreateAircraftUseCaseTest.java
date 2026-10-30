package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateAircraftUseCaseTest {

    private static final String AIRLINE_ID = "airline-1";

    @Mock
    private AircraftRepository aircraftRepository;

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private CreateAircraftUseCase useCase;

    @Test
    void shouldCreateAircraftAndSaveItLinkedToTheAirline() {
        // Arrange
        Airline airline = Airline.createAirline("Avianca", 3, 45000);
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.of(airline));

        // Act
        Aircraft result = useCase.execute(AircraftModel.EMBRAER_E175, AIRLINE_ID);

        // Assert
        assertEquals(List.of(result.getId()), airline.getAircraftIds());
        verify(aircraftRepository).save(result);
        verify(airlineRepository).save(airline);
    }

    @Test
    void shouldThrowExceptionWhenAirlineDoesNotExist() {
        // Arrange
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
                () -> useCase.execute(AircraftModel.EMBRAER_E175, AIRLINE_ID));

        verifyNoInteractions(aircraftRepository);
        verify(airlineRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAircraftModelIsNull() {
        // Act & Assert
        assertThrows(DomainRuleException.class, () -> useCase.execute(null, AIRLINE_ID));

        verifyNoInteractions(aircraftRepository, airlineRepository);
    }
}