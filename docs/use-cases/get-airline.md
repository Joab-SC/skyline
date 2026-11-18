# GetAirline

**Status:** Implemented
**Class:** `application/usecase/GetAirlineUseCase.java`
**Endpoint:** `GET /api/airlines/{id}`

## Purpose
Returns one airline by its id. In the booking flow the client uses it to know the airline's `maximumLuggageAmount` before [AddLuggage](add-luggage.md): the flight (`FlightResponse.airlineId`) only gives the id. It only reads data and changes nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `airlineId` | `String` | Airline id. |

## Output
The `Airline`. The REST response maps it to an `AirlineResponse` (`id`, `name`, `maximumLuggageAmount`, `priceSeats`).

## Dependencies
- Repositories: `AirlineRepository` (`findById`)

## Steps
1. `AirlineRepository.findById(airlineId)` finds the airline.
2. Return it.

## Rules and errors
- The airline does not exist: `NoSuchElementException` (404 in REST).
- The endpoint is public, like `GET /api/airlines`: a guest needs it to build a booking.
- The use case is `@Transactional(readOnly = true)`, since it only reads.
