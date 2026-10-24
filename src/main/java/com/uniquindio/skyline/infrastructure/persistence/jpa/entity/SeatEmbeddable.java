package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class SeatEmbeddable {

    @Column(name = "seat_code", nullable = false, length = 5)
    private String code;
}
