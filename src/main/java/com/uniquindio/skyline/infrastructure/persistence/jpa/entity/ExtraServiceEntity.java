package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "extra_services")
@Getter
@Setter
@NoArgsConstructor
public class ExtraServiceEntity {
    @Id
    private String id;

    @Column(length = 50, nullable = false)
    private String nameService;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private double nationalPrice;

    @Column(nullable = false)
    private double internationalPrice;
}
