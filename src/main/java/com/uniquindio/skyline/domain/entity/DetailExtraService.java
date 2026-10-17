package com.uniquindio.skyline.domain.entity;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class DetailExtraService {

    private String id;
    private String idExtraService;
    private int quantity;
    private double subtotal;

    private DetailExtraService(String idExtraService, int quantity) {
        this.id = UUID.randomUUID().toString();
        this.idExtraService = idExtraService;
        this.quantity = quantity;
        this.subtotal = 0;
    }

    /*public static DetailExtraService createDetailExtraService(String idExtraService, int quantity, double price) {
        if (idExtraService == null || idExtraService.isBlank()) {
            throw new IllegalArgumentException("The extra service ID cannot be null or empty");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("The quantity must be greater than zero");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("The quantity must be greater than zero");
        }


        return new DetailExtraService(idExtraService, quantity);
    }*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DetailExtraService)) return false;
        DetailExtraService other = (DetailExtraService) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
