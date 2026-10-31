package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.FlightScope;
import lombok.Getter;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Getter
public class ExtraService {
    private String id;
    private String nameService;
    private Optional<String> description;
    private double nationalPrice;
    private double internationalPrice;
    private boolean deleted;

    private ExtraService(String id, String nameService, Optional<String> description, double nationalPrice, double internationalPrice, boolean deleted) {
        this.id = id;
        this.nameService = nameService;
        this.description = description;
        this.nationalPrice = nationalPrice;
        this.internationalPrice = internationalPrice;
        this.deleted = deleted;
    }

    // Rebuilds an extra service that already exists, keeping its id. Used when loading from persistence.
    public static ExtraService reconstruct(String id, String nameService, String description, double nationalPrice, double internationalPrice, boolean deleted) {
        return new ExtraService(id, nameService, normalizeDescription(description), nationalPrice, internationalPrice, deleted);
    }

    // Creates an extra service with a random id. The description is optional.
    public static ExtraService createExtraService(String nameService, String description, double nationalPrice, double internationalPrice) {
        validateExtraService(nameService, nationalPrice, internationalPrice);
        return new ExtraService(UUID.randomUUID().toString(), nameService, normalizeDescription(description), nationalPrice, internationalPrice, false);
    }

    // A null or blank description means the service has no description.
    private static Optional<String> normalizeDescription(String description) {
        return Optional.ofNullable(description).filter(text -> !text.isBlank());
    }

    private static void validateExtraService(String nameService, double nationalPrice, double internationalPrice) {
        if (nameService == null || nameService.isBlank()) {
            throw new DomainRuleException("The extra service name cannot be null or empty");
        }
        if (nationalPrice < 0) {
            throw new DomainRuleException("The national price cannot be negative");
        }
        if (internationalPrice < 0) {
            throw new DomainRuleException("The international price cannot be negative");
        }
    }

    // Soft delete: the service is no longer offered, but it is kept because old bookings point to its id.
    public void delete() {
        if (deleted) {
            throw new DomainRuleException("The extra service is already deleted");
        }
        this.deleted = true;
    }

    public void validateIsNotDeleted() {
        if (deleted) {
            throw new DomainRuleException("The extra service was deleted and cannot be added");
        }
    }

    // Returns the unit price that applies to the given flight scope.
    public double getPrice(FlightScope flightScope) {
        if (flightScope == null) {
            throw new DomainRuleException("The flight scope is required");
        }
        return switch (flightScope) {
            case NATIONAL -> nationalPrice;
            case INTERNATIONAL -> internationalPrice;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExtraService)) return false;
        ExtraService other = (ExtraService) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
