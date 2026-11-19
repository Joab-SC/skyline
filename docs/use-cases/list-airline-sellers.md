# ListAirlineSellers

**Status:** Implemented
**Class:** `application/usecase/ListAirlineSellersUseCase.java`
**Endpoint:** `GET /api/airlines/sellers`

## Purpose
Returns the sellers of the logged-in `AIRLINE_ADMIN`'s airline, so the airline panel can list the sellers registered with [RegisterAirlineSeller](register-airline-seller.md). It only reads data and changes nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `airlineId` | `String` | Airline whose sellers are requested. The controller takes it from the `airlineId` claim of the token, never from the request. |

## Output
The airline's `AIRLINE_SELLER` users, ordered by email. The REST response maps each one to a `UserResponse` (id, email, role, airlineId), never with the password.

## Dependencies
- Repositories: `UserRepository`

## Steps
1. Return `UserRepository.findByAirlineAndRole(airlineId, AIRLINE_SELLER)`.

## Rules and errors
- Only `AIRLINE_ADMIN` can call it (401 without a token, 403 with another role). The rule is before the public `GET /api/airlines/**` in `SecurityConfig`.
- An airline with no sellers returns an empty list. This is not an error.
