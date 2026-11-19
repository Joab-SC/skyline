# ListAirlineLegs

**Status:** Implemented
**Class:** `application/usecase/ListAirlineLegsUseCase.java`
**Endpoint:** `GET /api/legs?page=0&size=10`

## Purpose
Returns one page of the legs of the logged-in airline user's airline, so the airline panel can list the legs it has scheduled with [CreateLeg](create-leg.md). It works like [SearchExtraServices](search-extra-services.md), without a text search. It only reads data and changes nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `airlineId` | `String` | Airline whose legs are requested. The controller takes it from the `airlineId` claim of the token, never from the request. |
| `page` | `int` | Page number, from 0. Default 0. |
| `size` | `int` | Legs per page, from 1 to 50. Default 10. |

## Output
A `PageResult<Leg>` with the airline's legs ordered by departure time (earliest first). The REST response is a `PageResponse<LegResponse>` (`content`, `totalElements`, `totalPages`, `number`, `size`).

## Dependencies
- Repositories: `LegRepository`

## Steps
1. Check `page` and `size`.
2. Return `LegRepository.findByAirline(airlineId, page, size)`.

## Rules and errors
- Only `AIRLINE_ADMIN` and `AIRLINE_SELLER` can call it (401 without a token, 403 with another role).
- A negative `page`, or a `size` out of 1..50, throws `IllegalArgumentException` (400 in REST).
- An airline with no legs, or a page after the last one, returns an empty page. This is not an error.
