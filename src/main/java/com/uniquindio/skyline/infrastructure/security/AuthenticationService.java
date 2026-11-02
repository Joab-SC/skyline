package com.uniquindio.skyline.infrastructure.security;

import com.uniquindio.skyline.application.dto.request.LoginRequest;
import com.uniquindio.skyline.application.dto.request.RegisterRequest;
import com.uniquindio.skyline.application.dto.response.RegisterResponse;
import com.uniquindio.skyline.application.dto.response.TokenResponse;
import com.uniquindio.skyline.infrastructure.persistence.jpa.UserJpaDataRepository;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.SecurityRole;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserJpaDataRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    // Anyone can register, always as CUSTOMER: the role is never taken from the request,
    // so nobody can register as ADMIN or AIRLINE.
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyRegisteredException();
        }
        UserEntity user = new UserEntity(UUID.randomUUID().toString(), request.email(),
                passwordEncoder.encode(request.password()), SecurityRole.CUSTOMER, null);
        userRepository.save(user);
        return new RegisterResponse(user.getId(), user.getEmail(), user.getRole().name());
    }
}
