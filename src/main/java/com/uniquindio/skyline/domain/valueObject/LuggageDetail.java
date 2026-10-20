package com.uniquindio.skyline.domain.valueObject;

import java.util.List;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import java.util.List;

public record LuggageDetail(List<String> luggageIds, int quantity, double subTotal, int maxQuantity) {

    public LuggageDetail {
        validate(luggageIds, quantity, subTotal, maxQuantity);
    }

    private static void validate(List<String> luggageIds, int quantity, double subTotal, int maxQuantity) {
        if (subTotal < 0) {
            throw new DomainRuleException("The luggage subtotal cannot be negative");
        }

        if (quantity <= 0) {
            throw new DomainRuleException("The luggage quantity must be greater than 0");
        }

        if (maxQuantity <= 0) {
            throw new DomainRuleException("The maximum luggage quantity must be greater than 0");
        }

        if (luggageIds == null || luggageIds.isEmpty()) {
            throw new DomainRuleException("At least one luggage id is required");
        }

        if (luggageIds.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new DomainRuleException("Luggage ids cannot be null or empty");
        }

        if (quantity > maxQuantity) {
            throw new DomainRuleException("The luggage quantity cannot exceed the maximum quantity");
        }
    }

}
