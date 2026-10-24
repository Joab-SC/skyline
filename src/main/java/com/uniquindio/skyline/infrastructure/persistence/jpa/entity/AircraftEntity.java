package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.AircraftModel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Aircrafts")
@Getter @Setter
@NoArgsConstructor
public class AircraftEntity {


    @Id
    @Column(length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AircraftModel aircraftModel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "aircraft_seats",
            joinColumns = @JoinColumn(name = "aircraft_id")
    )
    private List<SeatEmbeddable> seats = new ArrayList<>();

}
