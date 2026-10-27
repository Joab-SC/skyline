package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.Airport;
import com.uniquindio.skyline.domain.valueObject.FlightScope;
import com.uniquindio.skyline.domain.valueObject.Layover;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "flights")
@Getter
@Setter
@NoArgsConstructor
public class FlightEntity {

    @Id
    private String id;

    @Column(nullable = false, length = 50)
    private String airlineId;

    @Column(nullable = false)
    private double price;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "flight_legs", joinColumns = @JoinColumn(name = "flight_id", nullable = false))
    private List<String> idLegs;

    @Enumerated(EnumType.STRING)
    private Airport originAirport;

    @Enumerated(EnumType.STRING)
    private Airport destinationAirport;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<LayoverEmbeddable> layovers;

    @Enumerated(EnumType.STRING)
    private FlightScope flightScope;
}
