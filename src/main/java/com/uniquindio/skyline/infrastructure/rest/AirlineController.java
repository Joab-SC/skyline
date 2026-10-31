package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.CreateAirlineRequest;
import com.uniquindio.skyline.application.dto.response.AirlineResponse;
import com.uniquindio.skyline.application.dto.response.ExtraServiceResponse;
import com.uniquindio.skyline.application.dto.response.TopAirlineResponse;
import com.uniquindio.skyline.application.usecase.CreateAirlineUseCase;
import com.uniquindio.skyline.application.usecase.GetTopAirlinesUseCase;
import com.uniquindio.skyline.application.usecase.ListAirlineExtraServicesUseCase;
import com.uniquindio.skyline.application.usecase.ListAirlinesUseCase;
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
    private final GetTopAirlinesUseCase getTopAirlinesUseCase;
    private final ListAirlinesUseCase listAirlinesUseCase;
    private final AirlineMapper mapper;
    private final ExtraServiceMapper extraServiceMapper;

    public AirlineController(CreateAirlineUseCase createAirlineUseCase,
                             ListAirlineExtraServicesUseCase listAirlineExtraServicesUseCase, GetTopAirlinesUseCase getTopAirlinesUseCase, ListAirlinesUseCase listAirlinesUseCase,
                             AirlineMapper mapper, ExtraServiceMapper extraServiceMapper) {
        this.createAirlineUseCase = createAirlineUseCase;
        this.listAirlineExtraServicesUseCase = listAirlineExtraServicesUseCase;
        this.getTopAirlinesUseCase = getTopAirlinesUseCase;
        this.listAirlinesUseCase = listAirlinesUseCase;
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