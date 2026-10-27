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

    @Enumerated(EnumType.STRING)
    Airport airport;

    @Column(length = 100, nullable = false)
    Duration duration;
}
