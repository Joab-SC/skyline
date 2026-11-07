# ListCustomerBookings

**Status:** Implemented
**Class:** `application/usecase/ListCustomerBookingsUseCase.java`
**Endpoint:** `GET /api/bookings/mine` (only `CUSTOMER`)

## Purpose
Returns the bookings made by the logged-in customer, newest first, so the customer can see them again.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `idCustomer` | `String` | Not sent by the client: the controller takes it from the `uid` claim of the token. |

## Output
A list of `Booking` (`BookingResponse` in REST), possibly empty.

## Dependencies
- Repositories: `BookingRepository` (`findByIdCustomer`)

## Steps
1. Find the bookings whose `idCustomer` is the customer's id.
2. Return them.

## Rules and errors
- `401`: no token, or an invalid or expired token.
- `403`: the token is not from a `CUSTOMER`.
- A booking is linked to a customer only when [SelectOutboundFlight](select-outbound-flight.md) (`POST /api/bookings`) is called with a `CUSTOMER` token. Guest bookings (no token) are not linked to anyone and are not listed here.
