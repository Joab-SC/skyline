package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.PhonePrefix;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@IdClass(PassengerId.class)
@Getter
@Setter
@NoArgsConstructor
@Table(name = "passengers")
public class PassengerEntity {

    @Id
    @Column(name = "booking_id", length = 50)
    private String bookingId;

    // Part of the key. The same column is also read by person.idDocument, which is read-only.
    @Id
    @Column(name = "id_document", length = 30)
    private String idDocument;

    @Embedded
    @AttributeOverride(name = "idDocument",
            column = @Column(name = "id_document", insertable = false, updatable = false))
    private PersonEmbeddable person;

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
