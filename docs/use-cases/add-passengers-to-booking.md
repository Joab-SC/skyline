# AddPassengersToBooking

**Status:** Not implemented
**Class:** none yet

## Purpose
Registers all the passengers of a `PENDING` booking in a single call. It does **not** touch seats: seats are reduced when the flights are selected and assigned in `AssignSeatToPassenger`.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `bookingId` | `String` | Booking id. |
| passengers | list | Passenger data. Input format (DTO or other) is pending. |

## Output
The updated `Booking`.

## Dependencies
- Repositories: `BookingRepository`

## Steps
1. Find the booking and check that it is `PENDING`.
2. Validate the list and each passenger (see rules).
3. Add the passengers to the booking. If the booking already has passengers, the list replaces them.
4. Save and return the booking.

## Rules and errors (`DomainRuleException`)
- The list has exactly `numPassengers` passengers.
- Exactly one passenger is the holder.
- No repeated passengers. A passenger is identified by `person.idDocument` (`Person` is used by composition).
- `Person`:
  - `idDocument` is not empty;
  - `documentType`, `firstName`, `lastName1`, `birthDate` and `gender` are required;
  - `secondName` and `lastName2` are optional;
  - `birthDate` cannot be in the future.
- `Passenger`: the holder must have a valid `email` and `phoneNumber`. They are optional for the other passengers.
