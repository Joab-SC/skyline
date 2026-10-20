package com.uniquindio.skyline.domain.entity;
import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.DocumentType;
import com.uniquindio.skyline.domain.valueObject.Gender;
import com.uniquindio.skyline.domain.valueObject.LuggageDetail;
import com.uniquindio.skyline.domain.valueObject.PhonePrefix;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;


@Getter
public class Passenger  {

    private boolean holder;
    private Optional<String> email;
    private Optional<String> phoneNumber;
    private Optional<PhonePrefix> phonePrefix;
    private Person person;
    private Optional<LuggageDetail> luggageDetail;


    private Passenger(boolean holder, Optional<String> email, Optional<String> phoneNumber,Optional<PhonePrefix> phonePrefix, Person person,
                      Optional<LuggageDetail> luggageDetail) {


        this.holder = holder;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.person = person;
        this.luggageDetail = luggageDetail;
        this.phonePrefix=phonePrefix;
    }

    public static Passenger createPassenger(
            String idDocument,
            DocumentType documentType,
            String firstName,
            String secondName,
            String lastName1,
            String lastName2,
            LocalDate birthDate,
            Gender gender) {


        Person person = Person.createPerson(
                idDocument,
                documentType,
                firstName,
                secondName,
                lastName1,
                lastName2,
                birthDate,
                gender
        );

        return new Passenger(
                false,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                person,
                Optional.empty()
        );
    }

    public void addInfoHolder(String email,
                              String phoneNumber, PhonePrefix phonePrefix){

        if (email == null || email.isBlank()) {
            throw new DomainRuleException("Email cannot be empty");
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new DomainRuleException("Email format is invalid");
        }

        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new DomainRuleException("Phone number cannot be empty");
        }

        if (phonePrefix == null) {
            throw new DomainRuleException("Phone prefix cannot be null");
        }

        this.phonePrefix = Optional.of(phonePrefix);
        this.phoneNumber = Optional.of(phoneNumber);
        this.email = Optional.of(email);
        this.holder = true;

    }

    // Replaces the luggage detail of the passenger.
    void registerLuggage(LuggageDetail luggageDetail) {
        if (luggageDetail == null){
            throw new DomainRuleException("LuggageDetail is required");
        }
        this.luggageDetail = Optional.of(luggageDetail);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Passenger)) return false;
        Passenger other = (Passenger) o;
        return person.getIdDocument()
                .equals(other.getPerson().getIdDocument());
    }

    @Override
    public int hashCode() {
        return Objects.hash(person.getIdDocument());
    }
}



