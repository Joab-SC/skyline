package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.SeatStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "leg_seats")
public class LegSeatEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "seat_code", nullable = false, length = 5)
    private String seatCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_status", nullable = false, length = 20)
    private SeatStatus seatStatus;

    @Column(name = "passenger_id", length = 50)
    private String passengerId;

    @Column(name = "booking_id", length = 36)
    private String bookingId;
}
