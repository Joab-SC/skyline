# GetBooking

**Status:** Implemented
**Class:** `application/usecase/GetBookingUseCase.java`
**Endpoint:** `GET /api/bookings/{id}`

## Purpose
Returns a booking so the client can see its current state at any step of the booking flow. It only reads data and changes nothing. `POST /api/bookings` returns a `Location` header that points to this endpoint.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `bookingId` | `String` | Booking that is requested. |

## Output
The `Booking`, with its flights, status, price, payment deadline, passengers, luggage and extra services. The response does not include the passengers' seats: they are stored in the legs (`LegSeat`), not in the booking.

## Dependencies
- Repositories: `BookingRepository`

## Steps
1. Find the booking by `bookingId`.
2. Return the booking.

## Rules and errors
- The booking must exist, or `NoSuchElementException` is thrown (404 in REST).
- A booking can be read in any status (`PENDING`, `CONFIRMED`, `CANCELLED`, `EXPIRED`).
