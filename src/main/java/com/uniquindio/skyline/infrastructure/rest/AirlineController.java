package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.CreateAirlineRequest;
import com.uniquindio.skyline.application.dto.response.AirlineResponse;
import com.uniquindio.skyline.application.dto.response.ExtraServiceResponse;
import com.uniquindio.skyline.application.usecase.CreateAirlineUseCase;
import com.uniquindio.skyline.application.usecase.ListAirlineExtraServicesUseCase;
import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.infrastructure.rest.mapper.AirlineMapper;
import com.uniquindio.skyline.infrastructure.rest.mapper.ExtraServiceMapper;
import jakarta.validation.Valid;
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
    private final AirlineMapper mapper;
    private final ExtraServiceMapper extraServiceMapper;

    public AirlineController(CreateAirlineUseCase createAirlineUseCase,
                             ListAirlineExtraServicesUseCase listAirlineExtraServicesUseCase,
                             AirlineMapper mapper, ExtraServiceMapper extraServiceMapper) {
        this.createAirlineUseCase = createAirlineUseCase;
        this.listAirlineExtraServicesUseCase = listAirlineExtraServicesUseCase;
        this.mapper = mapper;
        this.extraServiceMapper = extraServiceMapper;
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

    @GetMapping("/{id}/extra-services")
    public ResponseEntity<List<ExtraServiceResponse>> listExtraServices(@PathVariable String id) {
        List<ExtraServiceResponse> response = listAirlineExtraServicesUseCase.execute(id).stream()
                .map(extraServiceMapper::toExtraServiceResponse)
                .toList();
        return ResponseEntity.ok(response); // 200
    }
}