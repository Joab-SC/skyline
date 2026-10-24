package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;

public record DetailExtraService(String idExtraService, int quantity, double subtotal) {

    public DetailExtraService {
        if (idExtraService == null || idExtraService.isBlank()) {
            throw new DomainRuleException("The extra service ID is required");
        }

        if (quantity <= 0) {
            throw new DomainRuleException("The quantity must be greater than zero");
        }

        if (subtotal < 0) {
            throw new DomainRuleException("The subtotal cannot be negative");
        }
    }
}
