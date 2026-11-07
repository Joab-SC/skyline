package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.application.exception.EmailAlreadyRegisteredException;
import com.uniquindio.skyline.application.port.PasswordHasher;
import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterCustomerUseCaseTest {

    private static final String EMAIL = "laura@mail.com";
    private static final String PASSWORD = "Secret123";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private RegisterCustomerUseCase useCase;

    private User register(String password) {
        return useCase.execute(EMAIL, password, "1001", DocumentType.CITIZENSHIP_ID,
                "Laura", null, "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);
    }

    @Test
    void shouldRegisterCustomerWithHashedPasswordAndSaveIt() {
        // Arrange
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);
        when(passwordHasher.hash(PASSWORD)).thenReturn("hashed-password");

        // Act
        User result = register(PASSWORD);

        // Assert
        assertEquals(UserRole.CUSTOMER, result.getRole());
        assertEquals(Optional.empty(), result.getAirlineId());
        assertEquals(EMAIL, result.getEmail());
        assertEquals("hashed-password", result.getPasswordHash());
        verify(passwordHasher).hash(PASSWORD);
        verify(userRepository).save(result);
    }

    @Test
    void shouldThrowWhenEmailIsAlreadyRegistered() {
        // Arrange
        when(userRepository.existsByEmail(EMAIL)).thenReturn(true);

        // Act & Assert
        assertThrows(EmailAlreadyRegisteredException.class, () -> register(PASSWORD));
        verify(passwordHasher, never()).hash(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowWhenPasswordIsWeakWithoutHashingIt() {
        // Arrange
        when(userRepository.existsByEmail(EMAIL)).thenReturn(false);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> register("weak"));
        verify(passwordHasher, never()).hash(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
