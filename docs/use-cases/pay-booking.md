# PayBooking

**Status:** Implemented
**Class:** `application/usecase/PayBooking.java`
**Endpoint:** `POST /api/bookings/{id}/payment`

## Purpose
Charges a `PENDING` booking through a payment provider and confirms it when the payment is approved. Every attempt is kept as a `Payment` inside the booking, so the booking has the history of its attempts.

The provider is reached through the `PaymentGateway` port (`application/port`). Today the only adapter is `FakePaymentGateway` (`infrastructure/payment`), which simulates a provider; a real provider only needs another adapter.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `paymentToken` | `String` | Token that represents the card. The provider gives it to the front end; the card never reaches the backend. REST receives it in `PayBookingRequest`. |
| `bookingId` | `String` | Booking id. |

## Output
The `Booking`, `CONFIRMED` when the payment is approved, with its list of payments.

## Dependencies
- Repositories: `BookingRepository`
- Ports: `PaymentGateway`

## Steps
1. Find the booking.
2. `booking.startPayment()`:
   1. checks that the booking can be paid (see rules);
   2. if an attempt is still `PENDING`, returns it, so it is charged again with the same reference. It assumes that attempt was never charged;
   3. otherwise creates a new `PENDING` `Payment` with a new id and the booking price as amount.
3. Save the booking, so the attempt exists before calling the provider.
4. `paymentGateway.charge(...)` with a `PaymentRequest`: the payment id as reference, the amount, the token and the holder's email.
5. `booking.registerPaymentResult(...)` with the provider's answer. An `APPROVED` payment changes the booking to `CONFIRMED`.
6. Save the booking again, so the result is kept even when the payment failed.
7. `payment.validateApproved()`: throws when the payment did not end approved.
8. Return the booking.

The use case is **not** `@Transactional`: the pending attempt of step 3 must stay saved even if the provider call fails.

## Payment attempts (`Payment`)
| Field | Meaning |
|-------|---------|
| `id` | UUID created by the backend. It is also the reference sent to the provider. |
| `amount` | Amount charged: the booking price when the attempt started. |
| `status` | `PENDING` (no known result yet), `APPROVED`, `DECLINED` (card problem) or `ERROR` (provider failure). |
| `transactionId` | Id the provider gives to the charge. Only when it processed it. |
| `message` | Reason given by the provider, for example "Insufficient funds". |
| `createdAt` | When the attempt started. |

## Rules and errors
- `DomainRuleException` (400 in REST) before charging, so nothing is charged or saved:
  - the booking is already paid (`CONFIRMED`);
  - the booking is not `PENDING` (`CANCELLED`, `EXPIRED`);
  - the payment deadline passed. Expiring the booking and releasing its seats belongs to [ExpireBooking](expire-booking.md);
  - not every passenger was added, or there is no holder.
- `DomainRuleException` (400) after charging, with the attempt saved and the booking still `PENDING`:
  - the payment was declined (the message includes the provider's reason);
  - the provider answered with an error;
  - the provider is still processing it (the attempt stays `PENDING`).
- The booking must exist, or `NoSuchElementException` is thrown (404).
- An attempt gets its result only once, and an approved attempt needs a transaction id.
- The deadline is checked when the attempt starts, not when its result arrives: if the provider approves after the deadline, the booking is confirmed anyway, because the money was charged.

## Fake provider (`FakePaymentGateway`)
| Token | Answer |
|-------|--------|
| `tok_declined` | `DECLINED`, "Insufficient funds" |
| `tok_error` | `ERROR`, "Payment provider unavailable" |
| any other | `APPROVED`, with a `FAKE-<uuid>` transaction id |

## Known limits
- A `PENDING` attempt is charged again assuming it was not charged. With a real provider, the backend should first ask the provider what happened to that reference (see [pending.md](../pending.md)).
- CancelBooking and ExpireBooking do not look at the payments (see [pending.md](../pending.md)).
