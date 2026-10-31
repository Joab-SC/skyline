package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateAirlineUseCase {

    private final AirlineRepository airlineRepository;

    @Transactional
    public Airline execute(String name, int maximumLuggageAmount, double seatPrice){
        Airline airline = Airline.createAirline(name, maximumLuggageAmount, seatPrice);
        airlineRepository.save(airline);
        return airline;
    }
}
