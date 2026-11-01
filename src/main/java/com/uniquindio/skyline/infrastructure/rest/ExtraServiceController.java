package com.uniquindio.skyline.infrastructure.rest;

import com.uniquindio.skyline.application.dto.request.CreateExtraServicerRequest;
import com.uniquindio.skyline.application.dto.response.ExtraServiceResponse;
import com.uniquindio.skyline.application.dto.response.PageResponse;
import com.uniquindio.skyline.application.usecase.CreateExtraServiceUseCase;

import com.uniquindio.skyline.application.usecase.SearchExtraServicesUseCase;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.infrastructure.rest.mapper.ExtraServiceMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/extra_services")
public class ExtraServiceController {

    private final CreateExtraServiceUseCase createExtraServiceUseCase;
    private final SearchExtraServicesUseCase searchExtraServicesUseCase;
    private final ExtraServiceMapper mapper;

    public ExtraServiceController(CreateExtraServiceUseCase createExtraServiceUseCase, SearchExtraServicesUseCase searchExtraServicesUseCase, ExtraServiceMapper mapper) {
        this.createExtraServiceUseCase = createExtraServiceUseCase;
        this.searchExtraServicesUseCase = searchExtraServicesUseCase;
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

    // Paginated search of the extra services that are not deleted. search and airlineId are optional.
    @GetMapping
    public ResponseEntity<PageResponse<ExtraServiceResponse>> search(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "airlineId", required = false) String airlineId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        PageResponse<ExtraServiceResponse> response = PageResponse.from(
                searchExtraServicesUseCase.execute(search, airlineId, page, size),
                mapper::toExtraServiceResponse);
        return ResponseEntity.ok(response); // 200
    }
}
