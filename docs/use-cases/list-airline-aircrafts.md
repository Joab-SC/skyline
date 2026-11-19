# ListAirlineAircrafts

**Status:** Implemented
**Class:** `application/usecase/ListAirlineAircraftsUseCase.java`
**Endpoint:** `GET /api/aircrafts`

## Purpose
Returns the aircraft of the logged-in airline user's airline, so the airline panel can list them and the client can choose one when creating a leg with [CreateLeg](create-leg.md) (`POST /api/legs`) instead of typing its id. It only reads data and changes nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `airlineId` | `String` | Airline whose aircraft are requested. The controller takes it from the `airlineId` claim of the token, never from the request. |

## Output
The airline's `Aircraft` list, in no particular order. The REST response maps each one to an `AircraftResponse` (id, model and seat codes).

## Dependencies
- Repositories: `AirlineRepository`, `AircraftRepository`

## Steps
1. Find the airline by `airlineId`.
2. Find its aircraft with `AircraftRepository.findAllById(airline.getAircraftIds())`.

## Rules and errors
- Only `AIRLINE_ADMIN` and `AIRLINE_SELLER` can call it (401 without a token, 403 with another role).
- The airline must exist, or `NoSuchElementException` is thrown (404 in REST).
- An airline with no aircraft returns an empty list. This is not an error.
