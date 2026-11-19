package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Leg;
import com.uniquindio.skyline.domain.repository.LegRepository;
import com.uniquindio.skyline.domain.repository.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListAirlineLegsUseCase {

    private static final int MAX_PAGE_SIZE = 50;

    private final LegRepository legRepository;

    // Returns one page of the airline's legs, ordered by departure time. The page can be empty.
    @Transactional(readOnly = true)
    public PageResult<Leg> execute(String airlineId, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("The page cannot be negative");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("The page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        return legRepository.findByAirline(airlineId, page, size);
    }
}
