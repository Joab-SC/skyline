package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
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
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAirlineAdminsUseCaseTest {

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ListAirlineAdminsUseCase useCase;

    @Test
    void shouldReturnTheAdminsOfTheAirline() {
        // Arrange
        Airline airline = Airline.createAirline("Mega airline", 10, 100);
        User admin = User.registerAirlineAdmin(airline.getId(), "1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE, "laura@mail.com", "hash");
        when(airlineRepository.findById(airline.getId())).thenReturn(Optional.of(airline));
        when(userRepository.findByAirlineAndRole(airline.getId(), UserRole.AIRLINE_ADMIN)).thenReturn(List.of(admin));

        // Act
        List<User> result = useCase.execute(airline.getId());

        // Assert
        assertEquals(List.of(admin), result);
    }

    @Test
    void shouldThrowWhenAirlineDoesNotExist() {
        // Arrange
        when(airlineRepository.findById("airline-1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> useCase.execute("airline-1"));
        verify(userRepository, never()).findByAirlineAndRole(any(), any());
    }
}
