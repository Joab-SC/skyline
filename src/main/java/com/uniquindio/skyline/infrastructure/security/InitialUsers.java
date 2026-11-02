package com.uniquindio.skyline.infrastructure.security;

import com.uniquindio.skyline.infrastructure.persistence.jpa.UserJpaDataRepository;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.SecurityRole;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

// Development users, created once on startup (after data.sql).
// Works without duplicates because H2 uses create-drop (the DB is recreated on every start).
// With a persistent DB (MariaDB) it must check existsById before saving.
@Component
@RequiredArgsConstructor
public class InitialUsers implements CommandLineRunner {

    private record Seed(String id, String email, String password, SecurityRole role) {}

    private final UserJpaDataRepository repository;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        List<Seed> seeds = List.of(
                new Seed("admin", "admin@skyline.com", "admin123", SecurityRole.ADMIN),
                new Seed("airline1", "airline1@skyline.com", "airline123", SecurityRole.AIRLINE),
                new Seed("customer1", "customer1@skyline.com", "customer123", SecurityRole.CUSTOMER)
        );
        for (Seed seed : seeds) {
            // The password is always stored hashed.
            repository.save(new UserEntity(seed.id(), seed.email(), encoder.encode(seed.password()), seed.role()));
        }
    }
}
