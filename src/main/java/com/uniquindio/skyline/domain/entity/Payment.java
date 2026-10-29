package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.PaymentStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

// One attempt to charge a booking. It belongs to the Booking aggregate.
// Its id is also the reference sent to the payment provider.
@Getter
public class Payment {


    private final String id;
    private final double amount;
    private PaymentStatus status;
    private Optional<String> transactionId;
    private Optional<String> message;
    private final LocalDateTime createdAt;

    private Payment(String id, double amount,  PaymentStatus status,
                    Optional<String> transactionId, Optional<String> message, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.transactionId = transactionId;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Only Booking creates attempts.
    static Payment createPending(double amount) {
        if (amount <= 0) {
            throw new DomainRuleException("The payment amount must be greater than 0");
        }
        return new Payment(UUID.randomUUID().toString(), amount, PaymentStatus.PENDING,
                 Optional.empty(),  Optional.empty(), LocalDateTime.now());
    }

    public static Payment reconstruct(String id, double amount, PaymentStatus status,
                                      String transactionId, String message, LocalDateTime createdAt) {
        return new Payment(id, amount, status,
                Optional.ofNullable(transactionId), Optional.ofNullable(message), createdAt);
    }

    // Only Booking changes attempts. A result can be saved once.
    public void registerResult(PaymentStatus newStatus, String transactionId, String message) {
        if (status != PaymentStatus.PENDING) {
            throw new DomainRuleException("This payment already has a result");
        }
        if (newStatus == null) {
            throw new DomainRuleException("The payment status is required");
        }
        if (newStatus == PaymentStatus.APPROVED && (transactionId == null || transactionId.isBlank())) {
            throw new DomainRuleException("An approved payment needs a transaction id");
        }
        this.status = newStatus;
        this.transactionId = Optional.ofNullable(transactionId);
        this.message = Optional.ofNullable(message);
    }

    // Throws when the attempt did not end approved, with a message for the client.
    public void validateApproved() {
        if (status == PaymentStatus.DECLINED) {
            throw new DomainRuleException("The payment was declined: " + message.orElse("no reason given"));
        }
        if (status == PaymentStatus.ERROR) {
            throw new DomainRuleException("The payment could not be processed, try again");
        }
        if (status == PaymentStatus.PENDING) {
            throw new DomainRuleException("The payment is still being processed");
        }
    }

    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }

    public boolean isApproved() {
        return status == PaymentStatus.APPROVED;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment)) return false;
        Payment other = (Payment) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}