package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.repository.PageResult;
import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.Seat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAirlineLegsUseCaseTest {

    @Mock
    private LegRepository legRepository;

    @InjectMocks
    private ListAirlineLegsUseCase useCase;

    @Test
    void shouldReturnThePageOfLegsOfTheAirline() {
        // Arrange
        Leg leg = Leg.createLeg(Airport.EL_DORADO, Airport.EL_EDEN,
                LocalDateTime.of(2026, 12, 10, 14, 30), LocalDateTime.of(2026, 12, 10, 16, 0),
                "aircraft-1", "airline-1", List.of(new Seat("1A")), 50, 200);
        PageResult<Leg> page = new PageResult<>(List.of(leg), 1, 1, 0, 10);
        when(legRepository.findByAirline("airline-1", 0, 10)).thenReturn(page);

        // Act
        PageResult<Leg> result = useCase.execute("airline-1", 0, 10);

        // Assert
        assertEquals(page, result);
    }

    @Test
    void shouldThrowWhenPageIsNegative() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute("airline-1", -1, 10));
        verify(legRepository, never()).findByAirline(anyString(), anyInt(), anyInt());
    }

    @Test
    void shouldThrowWhenSizeIsOutOfRange() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute("airline-1", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> useCase.execute("airline-1", 0, 51));
        verify(legRepository, never()).findByAirline(anyString(), anyInt(), anyInt());
    }
}
