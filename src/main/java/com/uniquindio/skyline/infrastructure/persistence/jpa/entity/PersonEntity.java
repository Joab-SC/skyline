package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@MappedSuperclass
@Getter @Setter
@NoArgsConstructor
public abstract class PersonEntity {

    @Id
    @Column(name = "id_document", length = 30)
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

    @Column(name = "birth_date", nullable = false, length = 100)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Gender gender;

}