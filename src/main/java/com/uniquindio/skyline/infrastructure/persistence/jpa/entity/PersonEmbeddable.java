package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// The Person value object as columns of the table that embeds it (@Embedded).
// It has no id: the owner entity decides its own key.
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class PersonEmbeddable {

    @Column(name = "id_document", nullable = false, length = 30)
    private String idDocument;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 20)
    private DocumentType documentType;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "second_name", length = 100)
    private String secondName;

    @Column(name = "last_name1", nullable = false, length = 100)
    private String lastName1;

    @Column(name = "last_name2", length = 100)
    private String lastName2;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;
}
