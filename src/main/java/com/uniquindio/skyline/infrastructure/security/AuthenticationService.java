package com.uniquindio.skyline.infrastructure.security;

import com.uniquindio.skyline.application.dto.request.LoginRequest;
import com.uniquindio.skyline.application.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expiry}")
    private long minutesToLive;

    public TokenResponse login(LoginRequest request) {
        // Checks email + password (UserDetailsService + PasswordEncoder).
        // Wrong credentials throw an AuthenticationException, answered with 401 by GlobalExceptionHandler.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = List.of(details.user().getRole().name());

        Instant now = Instant.now();
        Instant expiresAt = now.plus(minutesToLive, ChronoUnit.MINUTES);

        String token = jwtTokenProvider.generate(
                authentication.getName(), details.user().getId(), roles,
                details.user().getAirlineId(), now, expiresAt);

        return new TokenResponse(token, TOKEN_TYPE, expiresAt, roles);
    }
}
