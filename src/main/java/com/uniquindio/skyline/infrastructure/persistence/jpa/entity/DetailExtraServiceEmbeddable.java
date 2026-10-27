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
@NoArgsConstructor
@AllArgsConstructor
public class DetailExtraServiceEmbeddable {

    @Column(name = "extra_service_id", nullable = false, length = 50)
    String idExtraService;

    @Column(nullable = false, length = 50)
    int quantity;

    @Column(nullable = false, length = 50)
    double subtotal;
}
