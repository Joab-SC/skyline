package com.uniquindio.skyline.application.dto.response;

import java.time.Instant;
import java.util.Collection;

// type is always "Bearer": the client sends the token as "Authorization: Bearer <token>".
// roles are returned so the frontend can decide what to show without decoding the token.
public record TokenResponse(String token, String type, Instant expireAt, Collection<String> roles) {}
