package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.PageResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchExtraServicesUseCaseTest {

    @Mock
    private AirlineRepository airlineRepository;

    @InjectMocks
    private SearchExtraServicesUseCase useCase;

    @Test
    void shouldReturnThePageFromTheRepository() {
        // Arrange
        ExtraService wifi = ExtraService.createExtraService("Wifi", null, 20, 40);
        PageResult<ExtraService> page = new PageResult<>(List.of(wifi), 1, 1, 0, 10);
        when(airlineRepository.searchExtraServices("wi", "airline-1", 0, 10)).thenReturn(page);

        // Act
        PageResult<ExtraService> result = useCase.execute("wi", "airline-1", 0, 10);

        // Assert
        assertEquals(page, result);
    }

    @Test
    void shouldThrowExceptionWhenPageIsNegative() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute("wi", null, -1, 10));
        verify(airlineRepository, never()).searchExtraServices(any(), any(), anyInt(), anyInt());
    }

    @Test
    void shouldThrowExceptionWhenSizeIsZero() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute("wi", null, 0, 0));
        verify(airlineRepository, never()).searchExtraServices(any(), any(), anyInt(), anyInt());
    }

    @Test
    void shouldThrowExceptionWhenSizeIsGreaterThanFifty() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> useCase.execute("wi", null, 0, 51));
        verify(airlineRepository, never()).searchExtraServices(any(), any(), anyInt(), anyInt());
    }
}