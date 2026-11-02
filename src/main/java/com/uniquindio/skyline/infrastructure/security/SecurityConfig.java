package com.uniquindio.skyline.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String AIRLINE = "AIRLINE";
    private static final String CUSTOMER = "CUSTOMER";

    private final JwtDecoder jwtDecoder;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CSRF protects cookie sessions; this API uses a token sent by hand in a header.
                .csrf(AbstractHttpConfigurer::disable)
                // Uses the corsConfigurationSource bean below (Angular on :4200).
                .cors(Customizer.withDefaults())
                // Only so the H2 console (iframes) can load. Remove when H2 is no longer used.
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                // No HTTP session: every request is authenticated by its own token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Rules are checked top to bottom and the FIRST match wins: specific rules go first.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",
                                "/error",
                                "/api-docs/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/h2-console/**"
                        ).permitAll()

                        // Must stay BEFORE "GET /api/airlines/**", which would also match /top and make it public.
                        .requestMatchers(HttpMethod.GET, "/api/airlines/top").hasAuthority(ADMIN)

                        // What a buyer needs to read to build a booking.
                        .requestMatchers(HttpMethod.GET,
                                "/api/flights/**",
                                "/api/airlines/**",
                                "/api/legs/*/seats",
                                "/api/extra_services"
                        ).permitAll()

                        // Must stay BEFORE "/api/bookings/**", which would also match /mine and make it public.
                        .requestMatchers(HttpMethod.GET, "/api/bookings/mine").hasAuthority(CUSTOMER)

                        // Guest checkout: the whole booking flow is public (any HTTP method).
                        // With a CUSTOMER token, the new booking is linked to that customer.
                        .requestMatchers("/api/bookings/**").permitAll()

                        // Seller operations.
                        .requestMatchers(HttpMethod.POST, "/api/airlines").hasAuthority(ADMIN)
                        // Only airline users: the airline is taken from their token.
                        .requestMatchers(HttpMethod.POST,
                                "/api/aircrafts",
                                "/api/legs",
                                "/api/extra_services"
                        ).hasAuthority(AIRLINE)
                        .requestMatchers(HttpMethod.DELETE, "/api/airlines/*/extra-services/*")
                                .hasAuthority(AIRLINE)

                        // Default deny: a new endpoint without a rule is blocked until a rule is added for it.
                        .anyRequest().denyAll()
                )
                // Adds the filter that reads "Authorization: Bearer <token>", validates it with the decoder
                // and takes the roles with the converter.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .build();
    }

    // bcrypt with the {bcrypt} prefix, so the algorithm can change later without breaking old hashes.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // Exposes the manager Spring built with our UserDetailsService and PasswordEncoder,
    // so AuthenticationService can inject it.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
