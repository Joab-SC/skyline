package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
@Getter
public class Person{

    private String idDocument;
    private DocumentType documentType;
    private String firstName;
    private Optional<String> secondName;
    private String lastName1;
    private Optional <String> lastName2;
    private LocalDate birthDate;
    private Gender gender;

    private Person(String idDocument, DocumentType documentType, String firstName, Optional<String> secondName, String lastName1, Optional<String> lastName2, LocalDate birthDate, Gender gender) {
        this.idDocument = idDocument;
        this.documentType = documentType;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName1 = lastName1;
        this.lastName2 = lastName2;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    // Creates a person. Validations are pending (AddPassengersToBooking).
    public static Person createPerson(String idDocument, DocumentType documentType, String firstName, String secondName,
                                      String lastName1, String lastName2, LocalDate birthDate, Gender gender) {
        return new Person(idDocument, documentType, firstName, Optional.ofNullable(secondName), lastName1,
                Optional.ofNullable(lastName2), birthDate, gender);
    }

}