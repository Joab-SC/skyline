# CreateLeg

**Status:** Implemented
**Class:** `application/usecase/CreateLegUseCase.java`
**Endpoint:** `POST /api/legs`

## Purpose
Creates a leg (*trayecto*) operated by an aircraft and links it to an airline. The leg copies the aircraft seats as `LegSeat`s and gets its own luggage configuration.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `originAirport` | `Airport` | Origin airport. |
| `destinationAirport` | `Airport` | Destination airport. |
| `departureTime` | `LocalDateTime` | Departure date and time. |
| `arrivalTime` | `LocalDateTime` | Arrival date and time. |
| `airlineId` | `String` | Airline that operates the leg. |
| `aircraftId` | `String` | Aircraft that flies the leg. |
| `luggagePrice` | `double` | Price of one piece of luggage on this leg. |
| `price` | `double` | Price of the leg. |

DTOs: `CreateLegRequest` and `LegResponse` (`application/dto`).

## Output
The created `Leg`.

## Dependencies
- Repositories: `LegRepository`, `AirlineRepository`, `AircraftRepository`
- Domain services: `LegOverlapValidator`

## Steps
1. Find the airline by `airlineId`.
2. Find the aircraft by `aircraftId`.
3. Check that the aircraft belongs to the airline (`airline.validateHasAircraft(aircraftId)`, which throws `DomainRuleException` otherwise).
4. Check that the aircraft has no overlapping leg (`LegOverlapValidator.validateNoOverlap`).
5. Create the leg with `Leg.createLeg(...)`:
   - one `LegSeat` per aircraft seat, all `AVAILABLE`;
   - `availableSeats` = number of seats;
   - one `Luggage` with `luggagePrice` (max weight 23 kg).
6. Link the leg with `airline.addLeg(leg.getId())`.
7. Save the leg in `LegRepository`.
8. Save the airline in `AirlineRepository`, so the link (`airline_legs`) is stored.
9. Return the leg.

The use case is `@Transactional`: the leg and the airline are saved together, or neither is.

## Rules and errors
- `DomainRuleException`:
  - the aircraft must belong to the airline;
  - there must be at least 5 minutes between legs of the same aircraft;
  - origin, destination, departure time, arrival time, `aircraftId` and `airlineId` are required;
  - `arrivalTime` must be after `departureTime`;
  - origin and destination airports must be different;
  - the aircraft must have at least one seat;
  - `price` and `luggagePrice` cannot be negative;
  - the leg id cannot already be in the airline.
- The airline or the aircraft does not exist: `NoSuchElementException` (404 in REST).
- Leg invariants: see [leg-aggregate/Invariants.md](../leg-aggregate/Invariants.md).
