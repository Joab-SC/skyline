package com.uniquindio.skyline.application.dto.response;

// The created user. It never carries the password, not even the hash.
public record RegisterResponse(String id, String email, String role) {}
