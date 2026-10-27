package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.BookingStatus;
import com.uniquindio.skyline.domain.valueObject.TripType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "bookings")

public class BookingEntity {

    @Id
    private String id;

    @Column(name = "num_passengers", nullable = false, length = 50)
    private int numPassengers;

    @Column(nullable = false, length = 50)
    private double price;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "booking_id")
    private List<PassengerEntity> passengerList;

    @Column(name = "id_outbound_flight", nullable = false, length = 50)
    private String idOutboundFlight;

    @Column(name = "id_return_flight", nullable = true, length = 50)
    private String idReturnFlight;

    @Enumerated(EnumType.STRING)
    @Column(name = "trip_type",  nullable = false, length = 50)
    private TripType tripType;

    @ElementCollection
    @CollectionTable(name = "booking_extraservices", joinColumns = @JoinColumn(name = "booking_id"))
    private List<DetailExtraServiceEmbeddable> detailExtraServices;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status",  nullable = false, length = 50)
    private BookingStatus bookingStatus;

    @Column(nullable = false, length = 100)
    private LocalDateTime paymentDeadline;
}
