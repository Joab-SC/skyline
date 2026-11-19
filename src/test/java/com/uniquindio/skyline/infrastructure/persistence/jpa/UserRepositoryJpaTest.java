package com.uniquindio.skyline.infrastructure.persistence.jpa;

import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.domain.repository.UserRepository;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class UserRepositoryJpaTest {

    private static final LocalDate BIRTH_DATE = LocalDate.of(2000, 5, 10);

    @Autowired
    private UserRepository userRepository;

    private User seller(String airlineId, String email) {
        return User.registerAirlineSeller(airlineId, "1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, BIRTH_DATE, Gender.FEMALE, email, "hash");
    }

    private User admin(String airlineId, String email) {
        return User.registerAirlineAdmin(airlineId, "1002", DocumentType.CITIZENSHIP_ID, "Juan", null,
                "Perez", null, BIRTH_DATE, Gender.MALE, email, "hash");
    }

    // Two airlines with sellers and admins: only the users of airline-test with the role must come back.
    private void saveUsers() {
        userRepository.save(seller("airline-test", "b-seller@test.com"));
        userRepository.save(seller("airline-test", "a-seller@test.com"));
        userRepository.save(admin("airline-test", "admin@test.com"));
        userRepository.save(seller("airline-other", "other-seller@test.com"));
        userRepository.save(admin("airline-other", "other-admin@test.com"));
    }

    private List<String> emailsOf(List<User> users) {
        return users.stream().map(User::getEmail).toList();
    }

    @Test
    void shouldFindTheSellersOfTheAirlineOrderedByEmail() {
        // Arrange
        saveUsers();

        // Act
        List<User> found = userRepository.findByAirlineAndRole("airline-test", UserRole.AIRLINE_SELLER);

        // Assert
        assertEquals(List.of("a-seller@test.com", "b-seller@test.com"), emailsOf(found));
    }

    @Test
    void shouldFindTheAdminsOfTheAirline() {
        // Arrange
        saveUsers();

        // Act
        List<User> found = userRepository.findByAirlineAndRole("airline-test", UserRole.AIRLINE_ADMIN);

        // Assert
        assertEquals(List.of("admin@test.com"), emailsOf(found));
    }
}
