package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.CreateExtraServicerRequest;
import com.uniquindio.skyline.application.dto.response.ExtraServiceResponse;
import com.uniquindio.skyline.application.usecase.CreateExtraServiceUseCase;

import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.infrastructure.rest.mapper.ExtraServiceMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/extra_services")
public class ExtraServiceController {

    private final CreateExtraServiceUseCase createExtraServiceUseCase;
    private final ExtraServiceMapper mapper;

    public ExtraServiceController(CreateExtraServiceUseCase createExtraServiceUseCase, ExtraServiceMapper mapper) {
        this.createExtraServiceUseCase = createExtraServiceUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ExtraServiceResponse> create(@Valid @RequestBody CreateExtraServicerRequest request){
        ExtraService extraService = createExtraServiceUseCase.execute(request.airlineId(), request.nameService(),
                request.description(), request.nationalPrice(), request.internationalPrice());
        ExtraServiceResponse response = mapper.toExtraServiceResponse(extraService);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(extraService.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }
}
