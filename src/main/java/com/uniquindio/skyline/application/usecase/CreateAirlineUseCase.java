package com.uniquindio.skyline.application.usecase;

import com.uniquindio.skyline.domain.entity.Airline;
import com.uniquindio.skyline.domain.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAirlineUseCase {

    private final AirlineRepository airlineRepository;

    public Airline execute(String name, int maximumLuggageAmount, double seatPrice){
        Airline airline = Airline.createAirline(name, maximumLuggageAmount, seatPrice);
        airlineRepository.save(airline);
        return airline;
    }
}
