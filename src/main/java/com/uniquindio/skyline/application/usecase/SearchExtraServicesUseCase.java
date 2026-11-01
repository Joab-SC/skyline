package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import com.uniquindio.skyline.domain.repository.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchExtraServicesUseCase {

    private static final int MAX_PAGE_SIZE = 50;

    private final AirlineRepository airlineRepository;

    // Searches the extra services that are not deleted by name or description, optionally of one airline.
    @Transactional(readOnly = true)
    public PageResult<ExtraService> execute(String text, String airlineId, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("The page cannot be negative");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("The page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        return airlineRepository.searchExtraServices(text, airlineId, page, size);
    }
}