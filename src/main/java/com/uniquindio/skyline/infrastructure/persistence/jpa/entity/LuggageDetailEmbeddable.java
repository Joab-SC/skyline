package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LuggageDetailEmbeddable {

    @ElementCollection
    // The passenger key is composite, so the luggage rows point to it with both columns.
    @CollectionTable(name = "passenger_luggages", joinColumns = {
            @JoinColumn(name = "booking_id", referencedColumnName = "booking_id"),
            @JoinColumn(name = "id_document", referencedColumnName = "id_document")})
    @Column(name = "luggage_id", nullable = false, length = 50)
    private List<String> luggageIds = new ArrayList<>();

    @Column(name = "luggage_quantity")
    private Integer quantity;

    @Column(name = "luggage_sub_total")
    private Double subTotal;

    @Column(name = "luggage_max_quantity")
    private Integer maxQuantity;
}