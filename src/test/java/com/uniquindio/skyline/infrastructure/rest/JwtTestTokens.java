package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.infrastructure.security.JwtTokenProvider;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

// Authenticated users for controller tests. @WebMvcTest has no security filters, so the token is sent
// with MockMvc's principal(...) and the controller reads its claims as in a real request.
final class JwtTestTokens {

    private JwtTestTokens() {}

    static JwtAuthenticationToken airlineUser(String airlineId) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("roles", List.of("AIRLINE_ADMIN"))
                .claim(JwtTokenProvider.AIRLINE_ID_CLAIM, airlineId)
                .build();
        return new JwtAuthenticationToken(jwt);
    }

    static JwtAuthenticationToken customerUser(String userId) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "HS256")
                .claim("roles", List.of("CUSTOMER"))
                .claim(JwtTokenProvider.USER_ID_CLAIM, userId)
                .build();
        return new JwtAuthenticationToken(jwt);
    }
}
