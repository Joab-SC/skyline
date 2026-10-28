package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PassengerId implements Serializable {

    private String bookingId;
    private String idDocument;
}
