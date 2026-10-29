package com.uniquindio.skyline.domain.entity;

import com.uniquindio.skyline.domain.exception.DomainRuleException;
import com.uniquindio.skyline.domain.valueObject.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private static final double AMOUNT = 500000.0;

    // Create pending

    @Test
    void shouldCreatePendingPaymentWithValidAmount() {
        // Act
        Payment payment = Payment.createPending(AMOUNT);

        // Assert
        assertNotNull(payment.getId());
        assertEquals(AMOUNT, payment.getAmount(), 0.001);
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        assertTrue(payment.isPending());
        assertFalse(payment.isApproved());
        assertTrue(payment.getTransactionId().isEmpty());
        assertTrue(payment.getMessage().isEmpty());
        assertNotNull(payment.getCreatedAt());
    }

    @Test
    void shouldGiveEachPaymentADifferentId() {
        // Act
        Payment first = Payment.createPending(AMOUNT);
        Payment second = Payment.createPending(AMOUNT);

        // Assert
        assertNotEquals(first.getId(), second.getId());
    }

    @Test
    void shouldThrowExceptionWhenAmountIsZero() {
        assertThrows(DomainRuleException.class, () -> Payment.createPending(0));
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        assertThrows(DomainRuleException.class, () -> Payment.createPending(-1));
    }

    // Reconstruct

    @Test
    void shouldReconstructPaymentWithAllItsData() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 29, 10, 30);

        // Act
        Payment payment = Payment.reconstruct("payment-1", AMOUNT, PaymentStatus.APPROVED,
                "TX-1", "Approved by bank", createdAt);

        // Assert
        assertEquals("payment-1", payment.getId());
        assertEquals(AMOUNT, payment.getAmount(), 0.001);
        assertEquals(PaymentStatus.APPROVED, payment.getStatus());
        assertEquals(Optional.of("TX-1"), payment.getTransactionId());
        assertEquals(Optional.of("Approved by bank"), payment.getMessage());
        assertEquals(createdAt, payment.getCreatedAt());
    }

    @Test
    void shouldReconstructPaymentWithoutTransactionIdOrMessage() {
        // Act
        Payment payment = Payment.reconstruct("payment-1", AMOUNT, PaymentStatus.PENDING,
                null, null, LocalDateTime.now());

        // Assert
        assertTrue(payment.getTransactionId().isEmpty());
        assertTrue(payment.getMessage().isEmpty());
    }

    // Register result

    @Test
    void shouldRegisterApprovedResultWithTransactionId() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);

        // Act
        payment.registerResult(PaymentStatus.APPROVED, "TX-1", null);

        // Assert
        assertEquals(PaymentStatus.APPROVED, payment.getStatus());
        assertTrue(payment.isApproved());
        assertFalse(payment.isPending());
        assertEquals(Optional.of("TX-1"), payment.getTransactionId());
        assertTrue(payment.getMessage().isEmpty());
    }

    @Test
    void shouldRegisterDeclinedResultWithMessage() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);

        // Act
        payment.registerResult(PaymentStatus.DECLINED, null, "Insufficient funds");

        // Assert
        assertEquals(PaymentStatus.DECLINED, payment.getStatus());
        assertFalse(payment.isApproved());
        assertFalse(payment.isPending());
        assertTrue(payment.getTransactionId().isEmpty());
        assertEquals(Optional.of("Insufficient funds"), payment.getMessage());
    }

    @Test
    void shouldRegisterErrorResult() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);

        // Act
        payment.registerResult(PaymentStatus.ERROR, null, "Provider unavailable");

        // Assert
        assertEquals(PaymentStatus.ERROR, payment.getStatus());
        assertEquals(Optional.of("Provider unavailable"), payment.getMessage());
    }

    @Test
    void shouldStayPendingWhenProviderIsStillProcessing() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);

        // Act
        payment.registerResult(PaymentStatus.PENDING, null, null);

        // Assert
        assertTrue(payment.isPending());
    }

    @Test
    void shouldThrowExceptionWhenApprovedResultHasNoTransactionId() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> payment.registerResult(PaymentStatus.APPROVED, null, null));
        assertThrows(DomainRuleException.class,
                () -> payment.registerResult(PaymentStatus.APPROVED, " ", null));
        assertTrue(payment.isPending());
    }

    @Test
    void shouldThrowExceptionWhenResultStatusIsNull() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);

        // Act & Assert
        assertThrows(DomainRuleException.class, () -> payment.registerResult(null, "TX-1", null));
        assertTrue(payment.isPending());
    }

    @Test
    void shouldThrowExceptionWhenPaymentAlreadyHasResult() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);
        payment.registerResult(PaymentStatus.DECLINED, null, "Insufficient funds");

        // Act & Assert
        assertThrows(DomainRuleException.class,
                () -> payment.registerResult(PaymentStatus.APPROVED, "TX-1", null));
        assertEquals(PaymentStatus.DECLINED, payment.getStatus());
        assertTrue(payment.getTransactionId().isEmpty());
    }

    // Validate approved

    @Test
    void shouldNotThrowWhenPaymentIsApproved() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);
        payment.registerResult(PaymentStatus.APPROVED, "TX-1", null);

        // Act & Assert
        assertDoesNotThrow(payment::validateApproved);
    }

    @Test
    void shouldThrowExceptionWithReasonWhenPaymentIsDeclined() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);
        payment.registerResult(PaymentStatus.DECLINED, null, "Insufficient funds");

        // Act
        DomainRuleException exception = assertThrows(DomainRuleException.class, payment::validateApproved);

        // Assert
        assertTrue(exception.getMessage().contains("Insufficient funds"));
    }

    @Test
    void shouldThrowExceptionWhenPaymentIsDeclinedWithoutReason() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);
        payment.registerResult(PaymentStatus.DECLINED, null, null);

        // Act
        DomainRuleException exception = assertThrows(DomainRuleException.class, payment::validateApproved);

        // Assert
        assertTrue(exception.getMessage().contains("no reason given"));
    }

    @Test
    void shouldThrowExceptionWhenPaymentEndedWithError() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);
        payment.registerResult(PaymentStatus.ERROR, null, "Provider unavailable");

        // Act & Assert
        assertThrows(DomainRuleException.class, payment::validateApproved);
    }

    @Test
    void shouldThrowExceptionWhenPaymentIsStillPending() {
        // Arrange
        Payment payment = Payment.createPending(AMOUNT);

        // Act & Assert
        assertThrows(DomainRuleException.class, payment::validateApproved);
    }

    // Equality

    @Test
    void shouldBeEqualWhenPaymentsHaveTheSameId() {
        // Arrange
        Payment first = Payment.reconstruct("payment-1", AMOUNT, PaymentStatus.PENDING, null, null, LocalDateTime.now());
        Payment second = Payment.reconstruct("payment-1", 100.0, PaymentStatus.DECLINED, null, "No funds", LocalDateTime.now());

        // Act & Assert
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenPaymentsHaveDifferentIds() {
        assertNotEquals(Payment.createPending(AMOUNT), Payment.createPending(AMOUNT));
    }
}
