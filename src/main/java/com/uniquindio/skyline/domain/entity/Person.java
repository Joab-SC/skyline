package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.valueObject.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Person{

    private String iddocument;
    private String firstName;
    private LocalDateTime arrivalTime;
    private LocalDate birthDate;
    private Gender gender;
}
