package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;

public record Airport(String name, String acronym, City city) {

    public Airport{
        if (name == null || name.isBlank()){
            throw new DomainRuleException("The name or acronym can not be blank");
        }
        if (acronym == null || acronym.isBlank()){
            throw new DomainRuleException("The acronym can not be blank");
        }
        if (city == null){
            throw new DomainRuleException("The city can not be blank");
        }
    }
}
