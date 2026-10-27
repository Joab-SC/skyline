package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.LegRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class GetLegSeatMapUseCaseTest {

    @Mock
    private LegRepository legRepository;

    @Mock
    private Leg leg;

    @InjectMocks
    private GetLegSeatMapUseCase useCase;

    @Test
    void shouldReturnLegWhenItExists() {
        // Arrange
        when(legRepository.findById("leg-1")).thenReturn(Optional.of(leg));

        // Act
        Leg result = useCase.execute("leg-1");

        // Assert
        assertSame(leg, result);
        verify(legRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenLegDoesNotExist() {
        // Arrange
        when(legRepository.findById("leg-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute("leg-1"));
    }
}