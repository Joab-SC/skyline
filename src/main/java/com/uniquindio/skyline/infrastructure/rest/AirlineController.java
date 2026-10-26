package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.CreateAirlineRequest;
import com.uniquindio.skyline.application.dto.response.AirlineResponse;
import com.uniquindio.skyline.application.usecase.CreateAirlineUseCase;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.infrastructure.rest.mapper.AirlineMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/airlines")
public class AirlineController {

    private final CreateAirlineUseCase createAirlineUseCase;
    private final AirlineMapper mapper;

    public AirlineController(CreateAirlineUseCase createAirlineUseCase, AirlineMapper mapper) {
        this.createAirlineUseCase = createAirlineUseCase;
        this.mapper = mapper;
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
}