package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.entity.ExtraService;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateExtraServiceUseCase {

    private final AirlineRepository airlineRepository;

    @Transactional
    public ExtraService execute(String airlineId, String nameService, String description, double nationalPrice, double internationalPrice) {
        Airline airline = airlineRepository.findById(airlineId).orElseThrow();
        ExtraService extraService = airline.createExtraService(nameService, description, nationalPrice, internationalPrice);
        airlineRepository.save(airline);
        return extraService;
    }
}
