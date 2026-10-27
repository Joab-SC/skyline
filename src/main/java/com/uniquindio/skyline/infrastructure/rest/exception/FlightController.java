package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.SearchFlightsRequest;
import com.uniquindio.skyline.application.dto.response.FlightResponse;
import com.uniquindio.skyline.application.usecase.SearchFlightsUseCase;
import com.uniquindio.skyline.domain.entity.Flight;
import com.uniquindio.skyline.infrastructure.rest.mapper.FlightMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final SearchFlightsUseCase searchFlightsUseCase;
    private final FlightMapper mapper;

    public FlightController(SearchFlightsUseCase searchFlightsUseCase, FlightMapper mapper) {
        this.searchFlightsUseCase = searchFlightsUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<FlightResponse>> search(@Valid @ModelAttribute SearchFlightsRequest request) {
        List<Flight> flights = searchFlightsUseCase.execute(
                request.origin(), request.destination(),
                request.departureDay(), request.passengers());

        return ResponseEntity.ok(mapper.toFlightResponseList(flights)); // 200
    }
}