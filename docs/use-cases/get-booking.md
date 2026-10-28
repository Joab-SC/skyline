# GetBooking

**Status:** Implemented
**Class:** `application/usecase/GetBookingUseCase.java`

## Purpose
Returns a booking so the client can see its current state at any step of the booking flow. It only reads data and changes nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `bookingId` | `String` | Booking that is requested. |

## Output
The `Booking`, with its flights, status, price, payment deadline, passengers, luggage and extra services.

## Dependencies
- Repositories: `BookingRepository`

## Steps
1. Find the booking by `bookingId`.
2. Return the booking.

## Rules and errors
- The booking must exist, or `NoSuchElementException` is thrown.
- A booking can be read in any status (`PENDING`, `CONFIRMED`, `CANCELLED`).