package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.domain.repository.UserRepository;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAirlineSellersUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListAirlineSellersUseCase useCase;

    @Test
    void shouldReturnTheSellersOfTheAirline() {
        // Arrange
        User seller = User.registerAirlineSeller("airline-1", "1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE, "laura@mail.com", "hash");
        when(userRepository.findByAirlineAndRole("airline-1", UserRole.AIRLINE_SELLER)).thenReturn(List.of(seller));

        // Act
        List<User> result = useCase.execute("airline-1");

        // Assert
        assertEquals(List.of(seller), result);
    }

    @Test
    void shouldReturnEmptyListWhenAirlineHasNoSellers() {
        // Arrange
        when(userRepository.findByAirlineAndRole("airline-1", UserRole.AIRLINE_SELLER)).thenReturn(List.of());

        // Act
        List<User> result = useCase.execute("airline-1");

        // Assert
        assertTrue(result.isEmpty());
    }
}
