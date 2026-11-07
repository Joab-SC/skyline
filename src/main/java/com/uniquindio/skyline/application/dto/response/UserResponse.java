package com.uniquindio.skyline.application.dto.response;

// The user basic data. It never carries the password, not even the hash.
public record UserResponse(String id, String email, String role, String airlineId) {}
