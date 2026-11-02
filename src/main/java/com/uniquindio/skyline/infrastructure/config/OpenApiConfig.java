package com.uniquindio.skyline.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // Name of the security scheme; the requirement below refers to it.
    private static final String BEARER_SCHEME = "bearer-jwt";

    // Title and version of the Swagger UI page, plus the "Authorize" button for the JWT token.
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Skyline API")
                        .description("Flight booking platform")
                        .version("1.0"))
                // Tells Swagger the API uses "Authorization: Bearer <token>".
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                // Sends the token on every request, so each endpoint shows the lock.
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}