package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.PhonePrefix;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "passengers")
public class PassengerEntity extends PersonEntity {

    @Column(nullable = false)
    private boolean holder;

    @Column(length = 100)
    private String email;

    @Column(length = 15)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private PhonePrefix phonePrefix;

    @Embedded
    private LuggageDetailEmbeddable luggageDetail;
}
