package com.uniquindio.skyline.domain.valueObject;


import com.uniquindio.skyline.domain.exception.DomainRuleException;

public record Seat (String code) {

    public Seat{
        if (code == null || code.isEmpty()) {
            throw new DomainRuleException("Seat code is required");
        }
    }

}
