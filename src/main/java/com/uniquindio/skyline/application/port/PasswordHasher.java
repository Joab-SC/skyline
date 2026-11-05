package com.uniquindio.skyline.application.port;

public interface PasswordHasher {

    String hash(String plainPassword);

    boolean matches(String plainPassword, String passwordHash);
}