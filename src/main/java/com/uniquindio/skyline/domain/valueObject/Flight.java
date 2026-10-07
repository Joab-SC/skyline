package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.entity.Layover;
import com.uniquindio.skyline.domain.exception.DomainRuleException;

import java.time.LocalDateTime;
import java.util.List;

public record Flight(double price, List<String > idLegs, Airport originAirport, Airport destinationAirport,
                     LocalDateTime departureTime,LocalDateTime arrivalTime, List<String> idLayovers){

    public Flight {
        if (price <= 0) {
            throw new DomainRuleException("El precio debe ser mayor que 0");
        }

        if (idLegs == null || idLegs.isEmpty()) {
            throw new DomainRuleException("Los IDs de los legs no pueden ser null ni estar vacíos");
        }

        if (idLegs.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new DomainRuleException("Los IDs de los legs no pueden ser null ni estar vacíos");
        }

        if (originAirport == null) {
            throw new DomainRuleException("El aeropuerto de origen no puede ser null");
        }

        if (destinationAirport == null) {
            throw new DomainRuleException("El aeropuerto de destino no puede ser null");
        }

        if (departureTime == null) {
            throw new DomainRuleException("La hora de salida no puede ser null");
        }

        if (arrivalTime == null) {
            throw new DomainRuleException("La hora de llegada no puede ser null");
        }
        if (idLayovers.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new DomainRuleException("Los IDs de los layovers no pueden ser null ni estar vacíos");
        }
    }

}
