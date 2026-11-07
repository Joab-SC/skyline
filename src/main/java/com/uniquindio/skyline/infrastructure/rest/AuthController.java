package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.LoginRequest;
import com.uniquindio.skyline.application.dto.request.RegisterUserRequest;
import com.uniquindio.skyline.application.dto.response.UserResponse;
import com.uniquindio.skyline.application.dto.response.TokenResponse;
import com.uniquindio.skyline.application.usecase.RegisterCustomerUseCase;
import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.infrastructure.rest.mapper.UserMapper;
import com.uniquindio.skyline.infrastructure.security.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RegisterCustomerUseCase registerCustomerUseCase;
    private final UserMapper userMapper;

    public AuthController(AuthenticationService authenticationService, RegisterCustomerUseCase registerCustomerUseCase, UserMapper userMapper) {
        this.authenticationService = authenticationService;
        this.registerCustomerUseCase = registerCustomerUseCase;
        this.userMapper = userMapper;
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request)); // 200
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerCustomer(@Valid @RequestBody RegisterUserRequest request) {
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
                .body(userMapper.toRegisterResponse(user));  // 201
    }

}
