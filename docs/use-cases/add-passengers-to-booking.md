# AddPassengersToBooking

**Status:** Implemented
**Class:** `application/usecase/AddPassengerUseCase.java`
**Endpoint:** `PUT /api/bookings/{id}/passengers`

## Purpose
Registers all the passengers of a `PENDING` booking in a single call and chooses the holder. It does **not** touch seats: seats are reduced when the flights are selected and assigned in [AssignSeatToPassenger](assign-seat-to-passenger.md).

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `idBooking` | `String` | Booking id. |
| `passengers` | `List<PassengerData>` | Passenger data. REST receives it as `AddPassengersRequest.passengers` (`PassengerRequest`). |
| `idHolder` | `String` | Document of the passenger who will be the holder. |
| `email` | `String` | Holder email. |
| `phoneNumber` | `String` | Holder phone number. |
| `phonePrefix` | `PhonePrefix` | Holder phone prefix. |

## Output
The updated `Booking`.

## Dependencies
- Repositories: `BookingRepository`

## Steps
1. Find the booking.
2. `booking.addPassengers(passengers)`: validate the list and create one `Passenger` per item.
3. `booking.choseHolder(idHolder, email, phoneNumber, phonePrefix)`: mark the holder and store their contact data.
4. Save and return the booking.

## Rules and errors (`DomainRuleException`)
- The booking is `PENDING`.
- Passengers can be added only once. A booking that already has passengers is rejected; the list does not replace them.
- The list is not null, has no null items and has exactly `numPassengers` passengers.
- No two passengers have the same document (`documentType` + `idDocument`).
- `Person`:
  - `idDocument` is not empty;
  - `documentType`, `firstName`, `lastName1`, `birthDate` and `gender` are required;
  - `secondName` and `lastName2` are optional, but not blank when sent;
  - `birthDate` cannot be in the future.
- Holder:
  - is one of the booking passengers, and the booking has exactly one holder;
  - is at least 18 years old;
  - has a valid `email`, a `phoneNumber` and a `phonePrefix`. The other passengers have no contact data.
- The booking must exist, or `NoSuchElementException` is thrown (404 in REST).
