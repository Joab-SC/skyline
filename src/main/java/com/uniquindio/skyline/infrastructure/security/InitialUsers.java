package com.uniquindio.skyline.infrastructure.security;

import com.uniquindio.skyline.application.port.PasswordHasher;
import com.uniquindio.skyline.domain.valueObject.Person;
import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.domain.repository.UserRepository;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

// Development users, created once on startup (after data.sql).
// Works without duplicates because H2 uses create-drop (the DB is recreated on every start).
// With a persistent DB (MariaDB) it must check existsById before saving.
@Component
@RequiredArgsConstructor
public class InitialUsers implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    @Override
    public void run(String... args) {
        save("admin", "admin@skyline.com", "admin123", UserRole.ADMIN, null,
                person("1000000001", "Admin", "Skyline"));
        save("airline1", "airline1@skyline.com", "airline123", UserRole.AIRLINE_ADMIN, "airline-avianca",
                person("1000000002", "Avianca", "Admin"));
        save("airline2", "airline2@skyline.com", "airline123", UserRole.AIRLINE_ADMIN, "airline-wingo",
                person("1000000003", "Wingo", "Admin"));
        save("seller1", "seller1@skyline.com", "seller123", UserRole.AIRLINE_SELLER, "airline-avianca",
                person("1000000004", "Avianca", "Seller"));
        save("customer1", "customer1@skyline.com", "customer123", UserRole.CUSTOMER, null,
                person("1000000005", "Customer", "One"));
    }

    // reconstruct: fixed ids, and dev passwords skip the strength rule on purpose.
    private void save(String id, String email, String password, UserRole role, String airlineId, Person person) {
        userRepository.save(User.reconstruct(id, person, email, passwordHasher.hash(password), role, airlineId));
    }

    private Person person(String idDocument, String firstName, String lastName) {
        return Person.createPerson(idDocument, DocumentType.CITIZENSHIP_ID, firstName, null, lastName, null,
                LocalDate.of(1990, 1, 1), Gender.MALE);
    }
}