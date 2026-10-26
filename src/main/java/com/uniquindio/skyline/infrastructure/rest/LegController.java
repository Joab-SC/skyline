package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.CreateLegRequest;
import com.uniquindio.skyline.application.dto.response.LegResponse;
import com.uniquindio.skyline.application.usecase.CreateLegUseCase;
import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.infrastructure.rest.mapper.LegMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/legs")
public class LegController {

    private final CreateLegUseCase createLegUseCase;
    private final LegMapper mapper;

    public LegController(CreateLegUseCase createLegUseCase, LegMapper mapper) {
        this.createLegUseCase = createLegUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<LegResponse> create(@Valid @RequestBody CreateLegRequest request) {
        Leg leg = createLegUseCase.execute(
                request.originAirport(), request.destinationAirport(),
                request.departureTime(), request.arrivalTime(),
                request.airlineId(), request.aircraftId(),
                request.luggagePrice(), request.price());
        LegResponse response = mapper.toLegResponse(leg);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(leg.getId())
                .toUri();
        return ResponseEntity.created(location).body(response); // 201
    }
}