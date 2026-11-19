# ListAirlineAdmins

**Status:** Implemented
**Class:** `application/usecase/ListAirlineAdminsUseCase.java`
**Endpoint:** `GET /api/airlines/{id}/admins`

## Purpose
Returns the admins of an airline, so the platform `ADMIN` can see who was registered with [RegisterAirlineAdmin](register-airline-admin.md). It only reads data and changes nothing.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `airlineId` | `String` | Airline whose admins are requested (path variable). |

## Output
The airline's `AIRLINE_ADMIN` users, ordered by email. The REST response maps each one to a `UserResponse` (id, email, role, airlineId), never with the password.

## Dependencies
- Repositories: `AirlineRepository`, `UserRepository`

## Steps
1. Find the airline by `airlineId`.
2. Return `UserRepository.findByAirlineAndRole(airlineId, AIRLINE_ADMIN)`.

## Rules and errors
- Only `ADMIN` can call it (401 without a token, 403 with another role). The rule is before the public `GET /api/airlines/**` in `SecurityConfig`.
- The airline must exist, or `NoSuchElementException` is thrown (404 in REST).
- An airline with no admins returns an empty list. This is not an error.
