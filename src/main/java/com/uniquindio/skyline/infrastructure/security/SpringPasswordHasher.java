package com.uniquindio.skyline.infrastructure.security;


import com.uniquindio.skyline.application.port.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String hash(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public boolean matches(String plainPassword, String passwordHash) {
        if (plainPassword == null || passwordHash == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, passwordHash);
    }
}