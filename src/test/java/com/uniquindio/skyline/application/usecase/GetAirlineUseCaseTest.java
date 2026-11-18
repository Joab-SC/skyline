package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAirlineUseCaseTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private GetAirlineUseCase useCase;

    @Test
    void shouldReturnTheAirline() {
        // Arrange
        Airline avianca = Airline.createAirline("Avianca", 2, 50000);
        when(airlineRepository.findById(avianca.getId())).thenReturn(Optional.of(avianca));

        // Act
        Airline result = useCase.execute(avianca.getId());

        // Assert
        assertEquals(avianca, result);
        verify(airlineRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenAirlineDoesNotExist() {
        // Arrange
        when(airlineRepository.findById("no-airline")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute("no-airline"));
    }
}
