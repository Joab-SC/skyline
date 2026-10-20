package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import lombok.Getter;

import java.time.LocalDate;

import java.util.Objects;

import java.util.Optional;
@Getter
public class Person {

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

    public static Person createPerson(
            String idDocument,
            DocumentType documentType,
            String firstName,
            String secondName,
            String lastName1,
            String lastName2,
            LocalDate birthDate,
            Gender gender) {

        if (idDocument == null || idDocument.isBlank()) {
            throw new DomainRuleException("Document ID cannot be empty");
        }

        if (documentType == null) {
            throw new DomainRuleException("Document type cannot be null");
        }

        if (firstName == null || firstName.isBlank()) {
            throw new DomainRuleException("First name cannot be empty");
        }

        if (secondName != null && secondName.isBlank()) {
            throw new DomainRuleException("Second name cannot be blank");
        }

        if (lastName1 == null || lastName1.isBlank()) {
            throw new DomainRuleException("First last name cannot be empty");
        }

        if (lastName2 != null && lastName2.isBlank()) {
            throw new DomainRuleException("Second last name cannot be blank");
        }

        if (birthDate == null) {
            throw new DomainRuleException("Birth date cannot be null");
        }

        if (birthDate.isAfter(LocalDate.now())) {
            throw new DomainRuleException("Birth date cannot be in the future");
        }

        if (gender == null) {
            throw new DomainRuleException("Gender cannot be null");
        }

        return new Person(
                idDocument,
                documentType,
                firstName,
                Optional.ofNullable(secondName),
                lastName1,
                Optional.ofNullable(lastName2),
                birthDate,
                gender
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person other = (Person) o;
        return idDocument.equals(other.idDocument);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDocument);
    }
}

