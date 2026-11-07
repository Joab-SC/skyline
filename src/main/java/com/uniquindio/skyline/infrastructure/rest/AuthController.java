package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.LoginRequest;
import com.uniquindio.skyline.application.dto.request.RegisterCustomerRequest;
import com.uniquindio.skyline.application.dto.response.RegisterResponse;
import com.uniquindio.skyline.application.dto.response.TokenResponse;
import com.uniquindio.skyline.application.usecase.RegisterCustomerUseCase;
import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.infrastructure.security.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RegisterCustomerUseCase registerCustomerUseCase;

    public AuthController(AuthenticationService authenticationService, RegisterCustomerUseCase registerCustomerUseCase) {
        this.authenticationService = authenticationService;
        this.registerCustomerUseCase = registerCustomerUseCase;
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request)); // 200
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterCustomerRequest request) {
        User user = registerCustomerUseCase.execute(
                request.email(),
                request.password(),
                request.idDocument(),
                request.documentType(),
                request.firstName(),
                request.secondName(),
                request.lastName1(),
                request.lastName2(),
                request.birthDate(),
                request.gender());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(user.getId(), user.getEmail(), user.getRole().name()));  // 201
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterCustomerRequest request) {
        User user = registerCustomerUseCase.execute(
                request.email(),
                request.password(),
                request.idDocument(),
                request.documentType(),
                request.firstName(),
                request.secondName(),
                request.lastName1(),
                request.lastName2(),
                request.birthDate(),
                request.gender());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponse(user.getId(), user.getEmail(), user.getRole().name()));  // 201
    }

}
