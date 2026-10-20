package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PersonTest {

    @Test
    void personIsCreatedWithCorrectData() {
        Person person = Person.createPerson("1001", DocumentType.CITIZENSHIP_ID, "Laura", "Camila",
                "Duarte", "Quintero", LocalDate.of(2000, 5, 10), Gender.FEMALE);

        assertEquals("1001", person.getIdDocument());
        assertEquals(DocumentType.CITIZENSHIP_ID, person.getDocumentType());
        assertEquals("Laura", person.getFirstName());
        assertEquals(Optional.of("Camila"), person.getSecondName());
        assertEquals("Duarte", person.getLastName1());
        assertEquals(Optional.of("Quintero"), person.getLastName2());
        assertEquals(LocalDate.of(2000, 5, 10), person.getBirthDate());
        assertEquals(Gender.FEMALE, person.getGender());
    }

    @Test
    void personIsCreatedWithEmptyOptionalsWhenSecondNamesAreNull() {
        Person person = Person.createPerson("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE);

        assertEquals(Optional.empty(), person.getSecondName());
        assertEquals(Optional.empty(), person.getLastName2());
    }

    @Test
    void personThrowsExceptionWhenIdDocumentIsBlank() {
        assertThrows(DomainRuleException.class, () ->
                Person.createPerson("   ", DocumentType.CITIZENSHIP_ID, "Laura", null,
                        "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE)
        );
    }

    @Test
    void personThrowsExceptionWhenDocumentTypeIsNull() {
        assertThrows(DomainRuleException.class, () ->
                Person.createPerson("1001", null, "Laura", null,
                        "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE)
        );
    }

    @Test
    void personThrowsExceptionWhenFirstNameIsNull() {
        assertThrows(DomainRuleException.class, () ->
                Person.createPerson("1001", DocumentType.CITIZENSHIP_ID, null, null,
                        "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE)
        );
    }

    @Test
    void personThrowsExceptionWhenSecondNameIsBlank() {
        assertThrows(DomainRuleException.class, () ->
                Person.createPerson("1001", DocumentType.CITIZENSHIP_ID, "Laura", "  ",
                        "Duarte", null, LocalDate.of(2000, 5, 10), Gender.FEMALE)
        );
    }

    @Test
    void personThrowsExceptionWhenLastName1IsBlank() {
        assertThrows(DomainRuleException.class, () ->
                Person.createPerson("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                        "  ", null, LocalDate.of(2000, 5, 10), Gender.FEMALE)
        );
    }

    @Test
    void personThrowsExceptionWhenLastName2IsBlank() {
        assertThrows(DomainRuleException.class, () ->
                Person.createPerson("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                        "Duarte", "  ", LocalDate.of(2000, 5, 10), Gender.FEMALE)
        );
    }

    @Test
    void personThrowsExceptionWhenBirthDateIsNull() {
        assertThrows(DomainRuleException.class, () ->
                Person.createPerson("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                        "Duarte", null, null, Gender.FEMALE)
        );
    }

    @Test
    void personThrowsExceptionWhenBirthDateIsInTheFuture() {
        assertThrows(DomainRuleException.class, () ->
                Person.createPerson("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                        "Duarte", null, LocalDate.now().plusDays(1), Gender.FEMALE)
        );
    }

    @Test
    void personThrowsExceptionWhenGenderIsNull() {
        assertThrows(DomainRuleException.class, () ->
                Person.createPerson("1001", DocumentType.CITIZENSHIP_ID, "Laura", null,
                        "Duarte", null, LocalDate.of(2000, 5, 10), null)
        );
    }
}
