package com.uniquindio.skyline.domain.valueObject;


import com.uniquindio.skyline.domain.exception.DomainRuleException;

public record Seat (String code) {

    // Validates the seat code.
    public Seat{
        if (code == null || code.isBlank()) {
            throw new DomainRuleException("Seat code is required");
        }
    }

}
