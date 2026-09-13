package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;

public record Airport(String name, String acronym, City city) {

    public Airport{
        if (name.isBlank() || acronym.isBlank()){
            throw new DomainRuleException("The name or acronym can not be blank");
        }
    }
}
