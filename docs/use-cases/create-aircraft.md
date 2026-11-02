# CreateAircraft

**Status:** Implemented
**Class:** `application/usecase/CreateAircraftUseCase.java`
**Endpoint:** `POST /api/aircrafts`

## Purpose
Creates an aircraft from its model, generates its seats and links it to an airline.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `aircraftModel` | `AircraftModel` | Model that defines `maxSeats` and `seatColumns`. |
| `airlineId` | `String` | Airline that owns the aircraft. It is not in the request body: the controller takes it from the `airlineId` claim of the logged-in `AIRLINE` user's token. |

## Output
The created `Aircraft`.

## Dependencies
- Repositories: `AircraftRepository`, `AirlineRepository`

## Steps
1. Create the aircraft with `Aircraft.createAircraft(aircraftModel)`. It gets a UUID and its seats:
   - `rows = ceil(maxSeats / seatColumns.size())`;
   - seat codes are `row + column` (`1A`, `1B`, …). Generation stops at `maxSeats`.
2. Find the airline by `airlineId`.
3. Link the aircraft with `airline.addAircraft(aircraft.getId())`.
4. Save the aircraft in `AircraftRepository`.
5. Save the airline in `AirlineRepository`, so the link (`airline_aircrafts`) is stored.
6. Return the aircraft.

The use case is `@Transactional`: the aircraft and the airline are saved together, or neither is.

## Rules and errors
- `DomainRuleException`:
  - `aircraftModel` cannot be null;
  - the aircraft id cannot already be in the airline, and cannot be empty.
- The airline must exist, or `NoSuchElementException` is thrown (404 in REST).
- Only `AIRLINE` users can call the endpoint (no token: 401, other role: 403).
- Aircraft invariants: see [aircraft-aggregate/invariants.md](../aircraft-aggregate/invariants.md).
