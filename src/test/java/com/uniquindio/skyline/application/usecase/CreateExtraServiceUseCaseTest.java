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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateExtraServiceUseCaseTest {

    private static final String AIRLINE_ID = "airline-1";

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private Airline airline;

    @Mock
    private ExtraService extraService;

    @InjectMocks
    private CreateExtraServiceUseCase useCase;

    @Test
    void shouldCreateExtraServiceAndSaveAirline() {
        // Arrange
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.of(airline));
        when(airline.createExtraService("Priority boarding", "Board first", 20.0, 35.0)).thenReturn(extraService);

        // Act
        ExtraService result = useCase.execute(AIRLINE_ID, "Priority boarding", "Board first", 20.0, 35.0);

        // Assert
        assertSame(extraService, result);
        verify(airline).createExtraService("Priority boarding", "Board first", 20.0, 35.0);
        verify(airlineRepository).save(airline);
    }

    @Test
    void shouldThrowExceptionWhenAirlineDoesNotExist() {
        // Arrange
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
                () -> useCase.execute(AIRLINE_ID, "Priority boarding", "Board first", 20.0, 35.0));
        verify(airlineRepository, never()).save(any());
    }

    @Test
    void shouldNotSaveAirlineWhenExtraServiceIsInvalid() {
        // Arrange
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.of(airline));
        when(airline.createExtraService("", "Board first", 20.0, 35.0))
                .thenThrow(new DomainRuleException("The extra service name cannot be null or empty"));

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> useCase.execute(AIRLINE_ID, "", "Board first", 20.0, 35.0));
        verify(airlineRepository, never()).save(any());
    }

    @Test
    void shouldNotSaveWhenAirlineDoesNotExist() {
        // Arrange
        when(airlineRepository.findById(AIRLINE_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class,
                () -> useCase.execute(AIRLINE_ID, "Priority boarding", "Board first", 20.0, 35.0));
        verify(airlineRepository, never()).save(any());
    }
}
