package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.CreateAirlineRequest;
import com.uniquindio.skyline.application.dto.response.AirlineResponse;
import com.uniquindio.skyline.application.dto.response.ExtraServiceResponse;
import com.uniquindio.skyline.application.dto.response.TopAirlineResponse;
import com.uniquindio.skyline.application.usecase.*;
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
    private final DeleteExtraServiceUseCase deleteExtraServiceUseCase;
    private final AirlineMapper mapper;
    private final ExtraServiceMapper extraServiceMapper;

    public AirlineController(CreateAirlineUseCase createAirlineUseCase,
                             ListAirlineExtraServicesUseCase listAirlineExtraServicesUseCase, GetTopAirlinesUseCase getTopAirlinesUseCase, ListAirlinesUseCase listAirlinesUseCase, DeleteExtraServiceUseCase deleteExtraServiceUseCase,
                             AirlineMapper mapper, ExtraServiceMapper extraServiceMapper) {
        this.createAirlineUseCase = createAirlineUseCase;
        this.listAirlineExtraServicesUseCase = listAirlineExtraServicesUseCase;
        this.getTopAirlinesUseCase = getTopAirlinesUseCase;
        this.listAirlinesUseCase = listAirlinesUseCase;
        this.deleteExtraServiceUseCase = deleteExtraServiceUseCase;
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

    // Soft delete of an extra service: it is no longer offered, but old bookings keep it.
    @DeleteMapping("/{id}/extra-services/{extraServiceId}")
    public ResponseEntity<Void> deleteExtraService(@PathVariable String id, @PathVariable String extraServiceId) {
        deleteExtraServiceUseCase.execute(id, extraServiceId);
        return ResponseEntity.noContent().build(); // 204
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