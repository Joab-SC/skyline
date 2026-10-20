# AddLuggage

**Status:** Implemented
**Class:** `application/usecase/AddLuggageUseCase.java`, `domain/service/LuggageDetailCreator.java`

## Purpose
Registers the luggage detail of a passenger in a `PENDING` booking and adds its subtotal to the booking price. Each passenger has **one** `LuggageDetail` for the whole booking (outbound, or outbound and return). Calling it again **replaces** the previous detail.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `passengerId` | `String` | Passenger document (`person.idDocument`). |
| `bookingId` | `String` | Booking id. |
| `amount` | `int` | Pieces of luggage the passenger carries on every leg. |

## Output
The updated `Booking`.

## Dependencies
- Repositories: `BookingRepository` (use case); `FlightRepository`, `LegRepository`, `AirlineRepository` (through `LuggageDetailCreator`)
- Domain services: `LuggageDetailCreator`

## Steps
1. The use case finds the booking (`BookingRepository`).
2. `LuggageDetailCreator.createDetail(booking, amount)`:
   1. Gets the flights: the outbound flight, plus the return flight when `idReturnFlight` is present (`FlightRepository`).
   2. Gets every leg of those flights (`flight.getIdLegs()` → `LegRepository`).
   3. Gets the airline from the outbound flight (`flight.getAirlineId()` → `AirlineRepository`). The outbound and return flights belong to the same airline.
   4. Calculates:
      - `luggageIds` = the `leg.getLuggage().getId()` of every leg;
      - `subTotal` = `amount × Σ leg.getLuggage().getPrice()`.
   5. Returns `booking.createLuggageDetail(luggageIds, amount, subTotal, airline.getMaximumLuggageAmount())`, which builds the `LuggageDetail`.
3. The use case calls `booking.registerPassengerLuggage(passengerId, luggageDetail)`, which:
   1. checks that the booking is `PENDING`;
   2. finds the passenger by `idDocument`;
   3. subtracts the old `subTotal` if the passenger already had a detail;
   4. replaces the detail and adds the new `subTotal` to the booking price.
4. The use case saves and returns the booking.

## Rules and errors (`DomainRuleException`)
- The booking exists and is `PENDING`; the passenger belongs to the booking.
- The flights, legs and airline exist.
- `LuggageDetail(luggageIds, quantity, subTotal, maxQuantity)` validates in its constructor:
  - `quantity > 0`;
  - `subTotal >= 0` (luggage can be free, since `Leg` allows `luggagePrice = 0`);
  - `maxQuantity > 0`;
  - at least one luggage id, with no empty ids;
  - `quantity ≤ maxQuantity`, applied **per leg** (the same amount on every leg).
- Each piece of luggage weighs 23 kg (`Luggage.maxWeight`).

## Tests
- `LuggageDetailTest`: record validations, including `maxQuantity`.
- `LuggageDetailCreatorTest`: no mocks. It uses real `Leg`, `Flight`, `Booking` and `Airline` objects with `FlightRepositoryInMemory`, `LegRepositoryInMemory` and `AirlineRepositoryInMemory`. Cases:
  - one-way and round-trip bookings;
  - flight, leg or airline not found.
- `BookingTest`:
  - `createLuggageDetail`;
  - `registerPassengerLuggage`: registration, replacement and price, passenger not found, null detail, non-pending booking.
- `AddLuggageUseCaseTest` (Mockito):
  - coordination (creator → register → save);
  - booking not found;
  - no save when the creator fails.
