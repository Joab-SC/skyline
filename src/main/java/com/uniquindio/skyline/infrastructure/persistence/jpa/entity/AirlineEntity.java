package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "airlines")
@Getter
@Setter
@NoArgsConstructor
public class AirlineEntity {
    @Id
    private String id;

    @Column(length =50, nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "airline_aircrafts", joinColumns = @JoinColumn(name = "airline_id"))
    @Column(name = "aircraft_id")
    private List<String> aircraftIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "airline_legs", joinColumns = @JoinColumn(name = "airline_id"))
    @Column(name = "leg_id")
    private List<String> legIds = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "airline_id")
    private List<ExtraServiceEntity> extraServices = new ArrayList<>();

    @Column(nullable = false)
    private int maximumLuggageAmount;

    @Column(nullable = false)
    private double priceSeats;


}
