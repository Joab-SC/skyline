package com.uniquindio.skyline.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String AIRLINE_ADMIN = "AIRLINE_ADMIN";
    private static final String AIRLINE_SELLER = "AIRLINE_SELLER";
    private static final String UNAUTHORIZED_MESSAGE = "A valid token is required";
    private static final String FORBIDDEN_MESSAGE = "You do not have permission for this action";
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
                                "/api/auth/register",
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
                        ).hasAnyAuthority(AIRLINE_ADMIN, AIRLINE_SELLER)
                        .requestMatchers(HttpMethod.DELETE, "/api/extra_services/*")
                                .hasAnyAuthority(AIRLINE_ADMIN, AIRLINE_SELLER)

                        // Default deny: a new endpoint without a rule is blocked until a rule is added for it.
                        .anyRequest().denyAll()
                )
                // Adds the filter that reads "Authorization: Bearer <token>", validates it with the decoder
                // and takes the roles with the converter.
                // These errors happen in the security filter, before the controllers,
                // so GlobalExceptionHandler never sees them. They are answered here with the same JSON.
                .exceptionHandling(ex -> ex
                        // No token: 401
                        .authenticationEntryPoint((request, response, e) ->
                                writeError(response, HttpStatus.UNAUTHORIZED, UNAUTHORIZED_MESSAGE))
                        // Valid token, but the role is not allowed: 403
                        .accessDeniedHandler((request, response, e) ->
                                writeError(response, HttpStatus.FORBIDDEN, FORBIDDEN_MESSAGE)))
                .oauth2ResourceServer(oauth2 -> oauth2
                        // Expired, tampered or badly signed token: also 401
                        .authenticationEntryPoint((request, response, e) ->
                                writeError(response, HttpStatus.UNAUTHORIZED, UNAUTHORIZED_MESSAGE))
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .build();
    }

    // Same JSON as GlobalExceptionHandler: timestamp, status and message.
    // The messages are constants without quotes, so they can be written without a JSON library.
    private static void writeError(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        if (status == HttpStatus.UNAUTHORIZED) {
            // Standard header that tells the client which kind of token to send.
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        }
        response.getWriter().write("""
                {"timestamp":"%s","status":%d,"message":"%s"}"""
                .formatted(LocalDateTime.now(), status.value(), message));
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
