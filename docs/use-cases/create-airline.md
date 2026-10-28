# CreateAirline

**Status:** Implemented
**Class:** `application/usecase/CreateAirlineUseCase.java`
**Endpoint:** `POST /api/airlines`

## Purpose
Creates an airline with no aircraft, legs or extra services. Those are added later with [CreateAircraft](create-aircraft.md), [CreateLeg](create-leg.md) and [CreateExtraService](create-extra-service.md).

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | Airline name. |
| `maximumLuggageAmount` | `int` | Maximum pieces of luggage a passenger can carry on each leg. Used by [AddLuggage](add-luggage.md). |
| `seatPrice` | `double` | Price charged when a passenger chooses a seat on a leg. Used by [AssignSeatToPassenger](assign-seat-to-passenger.md). |

## Output
The created `Airline`.

## Dependencies
- Repositories: `AirlineRepository`

## Steps
1. Create the airline with `Airline.createAirline(name, maximumLuggageAmount, seatPrice)`. It gets a UUID and empty lists of aircraft, legs and extra services.
2. Save the airline.
3. Return the airline.

## Rules and errors (`DomainRuleException`)
- The name cannot be null or empty.
- `maximumLuggageAmount > 0`.
- `seatPrice` cannot be negative (0 is allowed).
