# GetLegSeatMap

**Status:** Implemented
**Class:** `application/usecase/GetLegSeatMapUseCase.java`

## Purpose
Returns a leg with its seats so the client can show the seat map before choosing a seat with AssignSeatToPassenger. It only reads data and changes nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `legId` | `String` | Leg whose seats are requested. |

## Output
The `Leg`, including its `LegSeat` list with each seat's code and status.

## Dependencies
- Repositories: `LegRepository`

## Steps
1. Find the leg by `legId`.
2. Return the leg.

## Rules and errors
- The leg must exist, or `NoSuchElementException` is thrown.
- The REST response does not include the passenger of each seat, so other bookings' document ids are never exposed.