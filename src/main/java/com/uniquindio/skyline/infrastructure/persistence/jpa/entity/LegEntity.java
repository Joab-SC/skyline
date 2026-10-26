package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.Airport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "legs")
@Getter
@Setter
@NoArgsConstructor
public class LegEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin_airport", nullable = false, length = 50)
    private Airport originAirport;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination_airport", nullable = false, length = 50)
    private Airport destinationAirport;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "aircraft_id", nullable = false, length = 36)
    private String aircraftId;

    @Column(name = "airline_id", nullable = false, length = 36)
    private String airlineId;

    @Column(nullable = false)
    private double price;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @OneToOne(mappedBy = "leg", cascade = CascadeType.ALL, orphanRemoval = true)
    private LuggageEntity luggage;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "leg_id")
    private List<LegSeatEntity> seats = new ArrayList<>();
}
