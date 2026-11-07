package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.CreateAirlineRequest;
import com.uniquindio.skyline.application.dto.request.RegisterUserRequest;
import com.uniquindio.skyline.application.dto.response.AirlineResponse;
import com.uniquindio.skyline.application.dto.response.ExtraServiceResponse;
import com.uniquindio.skyline.application.dto.response.TopAirlineResponse;
import com.uniquindio.skyline.application.dto.response.UserResponse;
import com.uniquindio.skyline.application.usecase.*;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.User;
import com.uniquindio.skyline.infrastructure.rest.mapper.AirlineMapper;
import com.uniquindio.skyline.infrastructure.rest.mapper.ExtraServiceMapper;
import com.uniquindio.skyline.infrastructure.rest.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/airlines")
public class AirlineController {

    private final CreateAirlineUseCase createAirlineUseCase;
    private final ListAirlineExtraServicesUseCase listAirlineExtraServicesUseCase;
    private final GetTopAirlinesUseCase getTopAirlinesUseCase;
    private final ListAirlinesUseCase listAirlinesUseCase;
    private final RegisterAirlineAdminUseCase registerAirlineAdminUseCase;
    private final AirlineMapper mapper;
    private final ExtraServiceMapper extraServiceMapper;
    private final UserMapper userMapper;

    public AirlineController(CreateAirlineUseCase createAirlineUseCase,
                             ListAirlineExtraServicesUseCase listAirlineExtraServicesUseCase, GetTopAirlinesUseCase getTopAirlinesUseCase, ListAirlinesUseCase listAirlinesUseCase,
                             RegisterAirlineAdminUseCase registerAirlineAdminUseCase,
                             AirlineMapper mapper, ExtraServiceMapper extraServiceMapper, UserMapper userMapper) {
        this.createAirlineUseCase = createAirlineUseCase;
        this.listAirlineExtraServicesUseCase = listAirlineExtraServicesUseCase;
        this.getTopAirlinesUseCase = getTopAirlinesUseCase;
        this.listAirlinesUseCase = listAirlinesUseCase;
        this.registerAirlineAdminUseCase = registerAirlineAdminUseCase;
        this.mapper = mapper;
        this.extraServiceMapper = extraServiceMapper;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<AirlineResponse> create(@Valid @RequestBody CreateAirlineRequest request) {
        Airline airline = createAirlineUseCase.execute(
                request.name(), request.maximumLuggageAmount(), request.seatPrice());
        AirlineResponse response = mapper.toAirlineResponse(airline);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(airline.getId())
                .toUri();
        return ResponseEntity.created(location).body(response); // 201
    }

    // Only an ADMIN creates the admin of an airline (see SecurityConfig).
    @PostMapping("/{id}/admins")
    public ResponseEntity<UserResponse> registerAdmin(@PathVariable String id,
                                                      @Valid @RequestBody RegisterUserRequest request) {
        User user = registerAirlineAdminUseCase.execute(
                id,
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
                .body(userMapper.toRegisterResponse(user)); // 201
    }

    @GetMapping
    public ResponseEntity<List<AirlineResponse>> list() {
        List<AirlineResponse> response = listAirlinesUseCase.execute().stream()
                .map(mapper::toAirlineResponse)
                .toList();
        return ResponseEntity.ok(response); // 200
    }

    @GetMapping("/{id}/extra-services")
    public ResponseEntity<List<ExtraServiceResponse>> listExtraServices(@PathVariable String id) {
        List<ExtraServiceResponse> response = listAirlineExtraServicesUseCase.execute(id).stream()
                .map(extraServiceMapper::toExtraServiceResponse)
                .toList();
        return ResponseEntity.ok(response); // 200
    }

    // Ranking of the airlines with the most aircraft. limit is 5 by default.
    @GetMapping("/top")
    public ResponseEntity<List<TopAirlineResponse>> getTop(
            @RequestParam(name = "limit", defaultValue = "5") int limit) {
        List<TopAirlineResponse> response = getTopAirlinesUseCase.execute(limit).stream()
                .map(mapper::toTopAirlineResponse)
                .toList();
        return ResponseEntity.ok(response); // 200
    }
}