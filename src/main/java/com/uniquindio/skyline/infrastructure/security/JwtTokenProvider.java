package com.uniquindio.skyline.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String ISSUER = "skyline-api";

    private final JwtEncoder encoder;

    // Builds the token: header.payload.signature
    public String generate(String email, String userId, Collection<String> roles,
                           Instant issuedAt, Instant expiresAt) {

        // Header: the algorithm used to sign.
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        // Payload: what the token says about the user. It is Base64, not encrypted: anyone can read it,
        // so it must not carry sensitive data. The signature only prevents changing it.
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(email)
                .claim("uid", userId)
                .claim("roles", roles)
                .build();

        // Signature: HMAC of header + payload with the secret.
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
