package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "luggages")
@Getter
@Setter
@NoArgsConstructor
public class LuggageEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private double price;

    @OneToOne
    @JoinColumn(name = "leg_id", nullable = false, unique = true)
    private LegEntity leg;
}
