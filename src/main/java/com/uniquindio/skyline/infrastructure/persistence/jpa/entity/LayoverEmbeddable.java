package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.Airport;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@Embeddable
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class LayoverEmbeddable{

    // Stored as the IATA code ("BOG") by AirportConverter.
    @Column(length = 3)
    Airport airport;

    @Column(length = 100, nullable = false)
    Duration duration;
}
