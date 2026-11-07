package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.application.exception.EmailAlreadyRegisteredException;
import com.uniquindio.skyline.application.port.PasswordHasher;
import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.UserRepository;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RegisterAirlineSellerUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AirlineRepository airlineRepository;

    @Transactional
    public User execute(String airlineId, String email, String password,
                        String idDocument, DocumentType documentType,
                        String firstName, String secondName,
                        String lastName1, String lastName2,
                        LocalDate birthDate, Gender gender) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException();
        }
        airlineRepository.findById(airlineId).orElseThrow();
        User.validatePasswordStrength(password);
        String passwordHash = passwordHasher.hash(password);

        User user = User.registerAirlineSeller(airlineId, idDocument, documentType, firstName, secondName,
                lastName1, lastName2, birthDate, gender, email, passwordHash);
        userRepository.save(user);
        return user;
    }
}
