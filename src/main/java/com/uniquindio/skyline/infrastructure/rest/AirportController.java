package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.response.AirportResponse;
import com.uniquindio.skyline.application.usecase.ListAirportsUseCase;
import com.uniquindio.skyline.infrastructure.rest.mapper.AirportMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final ListAirportsUseCase listAirportsUseCase;
    private final AirportMapper mapper;

    public AirportController(ListAirportsUseCase listAirportsUseCase, AirportMapper mapper) {
        this.listAirportsUseCase = listAirportsUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<AirportResponse>> list() {
        List<AirportResponse> response = listAirportsUseCase.execute().stream()
                .map(mapper::toAirportResponse)
                .toList();
        return ResponseEntity.ok(response); // 200
    }
}
