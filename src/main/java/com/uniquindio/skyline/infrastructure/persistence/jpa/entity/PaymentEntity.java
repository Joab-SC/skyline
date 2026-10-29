package com.uniquindio.skyline.infrastructure.persistence.jpa.entity;

import com.uniquindio.skyline.domain.valueObject.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// One payment attempt of a booking. The booking_id column is filled by the
// @OneToMany @JoinColumn in BookingEntity.
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "booking_payments")
public class PaymentEntity {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(length = 255)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
