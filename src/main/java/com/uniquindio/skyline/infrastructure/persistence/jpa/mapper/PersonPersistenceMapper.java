package com.uniquindio.skyline.infrastructure.persistence.jpa.mapper;

import com.uniquindio.skyline.domain.valueObject.Person;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.PersonEmbeddable;
import org.springframework.stereotype.Component;

// Maps the Person value object to its embeddable and back (user, passenger, ...).
@Component
public class PersonPersistenceMapper {

    public PersonEmbeddable toEmbeddable(Person person) {
        PersonEmbeddable embeddable = new PersonEmbeddable();
        fillEntity(person, embeddable);
        return embeddable;
    }

    public void fillEntity(Person person, PersonEmbeddable personEmbeddable) {
        personEmbeddable.setIdDocument(person.getIdDocument());
        personEmbeddable.setDocumentType(person.getDocumentType());
        personEmbeddable.setFirstName(person.getFirstName());
        personEmbeddable.setSecondName(person.getSecondName().orElse(null));
        personEmbeddable.setLastName1(person.getLastName1());
        personEmbeddable.setLastName2(person.getLastName2().orElse(null));
        personEmbeddable.setBirthDate(person.getBirthDate());
        personEmbeddable.setGender(person.getGender());
    }

    public Person toDomain(PersonEmbeddable personEmbeddable) {
        return Person.createPerson(
                personEmbeddable.getIdDocument(),
                personEmbeddable.getDocumentType(),
                personEmbeddable.getFirstName(),
                personEmbeddable.getSecondName(),
                personEmbeddable.getLastName1(),
                personEmbeddable.getLastName2(),
                personEmbeddable.getBirthDate(),
                personEmbeddable.getGender());
    }
}
