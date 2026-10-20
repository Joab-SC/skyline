package com.uniquindio.skyline.domain.valueObject;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import java.util.List;

public record LuggageDetail(List<String> luggageIds, int quantity, double subTotal) {

    public LuggageDetail {
        validate(luggageIds, quantity, subTotal);
    }

    private static void validate(List<String> luggageIds, int quantity, double subTotal) {
        if (subTotal < 0) {
            throw new DomainRuleException("The luggage subtotal cannot be negative");
        }

        if (quantity <= 0) {
            throw new DomainRuleException("The luggage quantity must be greater than 0");
        }

        if (luggageIds == null || luggageIds.isEmpty()) {
            throw new DomainRuleException("At least one luggage id is required");
        }

        if (luggageIds.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new DomainRuleException("Luggage ids cannot be null or empty");
        }
    }

}
