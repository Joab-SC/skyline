package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.CreateAircraftRequest;
import com.uniquindio.skyline.application.dto.response.AircraftResponse;
import com.uniquindio.skyline.application.usecase.CreateAircraftUseCase;
import com.uniquindio.skyline.domain.entity.Aircraft;
import com.uniquindio.skyline.infrastructure.rest.mapper.AircraftMapper;
import com.uniquindio.skyline.infrastructure.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/aircrafts")
public class AircraftController {

    private final CreateAircraftUseCase createAircraftUseCase;
    private final AircraftMapper mapper;

    public AircraftController(CreateAircraftUseCase createAircraftUseCase, AircraftMapper mapper) {
        this.createAircraftUseCase = createAircraftUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<AircraftResponse> create(@Valid @RequestBody CreateAircraftRequest request,
                                                   JwtAuthenticationToken authentication) {
        // The airline comes from the token, so a user can only add aircraft to its own airline.
        String airlineId = authentication.getToken().getClaimAsString(JwtTokenProvider.AIRLINE_ID_CLAIM);
        Aircraft aircraft = createAircraftUseCase.execute(request.aircraftModel(), airlineId);
        AircraftResponse response = mapper.toAircraftResponse(aircraft);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(aircraft.getId())
                .toUri();
        return ResponseEntity.created(location).body(response); // 201
    }
}
