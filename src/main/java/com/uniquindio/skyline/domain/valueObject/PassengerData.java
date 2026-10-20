package com.uniquindio.skyline.domain.valueObject;

import java.time.LocalDate;

// Datos para crear un pasajero dentro de una reserva.
// secondName y lastName2 pueden ser null. La validación la hace Person.createPerson.
public record PassengerData(
        String idDocument,
        DocumentType documentType,
        String firstName,
        String secondName,
        String lastName1,
        String lastName2,
        LocalDate birthDate,
        Gender gender) {
}
