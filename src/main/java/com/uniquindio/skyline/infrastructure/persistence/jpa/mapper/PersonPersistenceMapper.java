package com.uniquindio.skyline.infrastructure.persistence.jpa.mapper;

import com.uniquindio.skyline.domain.entity.Person;
import com.uniquindio.skyline.infrastructure.persistence.jpa.entity.PersonEntity;
import org.springframework.stereotype.Component;

// Maps the person fields shared by every entity that extends PersonEntity (passenger, user, ...).
@Component
public class PersonPersistenceMapper {


    public void fillEntity(Person person, PersonEntity personEntity) {
        personEntity.setIdDocument(person.getIdDocument());
        personEntity.setDocumentType(person.getDocumentType());
        personEntity.setFirstName(person.getFirstName());
        personEntity.setSecondName(person.getSecondName().orElse(null));
        personEntity.setLastName1(person.getLastName1());
        personEntity.setLastName2(person.getLastName2().orElse(null));
        personEntity.setBirthDate(person.getBirthDate());
        personEntity.setGender(person.getGender());
    }

    public Person toDomain(PersonEntity personEntity) {
        return Person.createPerson(
                personEntity.getIdDocument(),
                personEntity.getDocumentType(),
                personEntity.getFirstName(),
                personEntity.getSecondName(),
                personEntity.getLastName1(),
                personEntity.getLastName2(),
                personEntity.getBirthDate(),
                personEntity.getGender());
    }
}
