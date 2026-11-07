# RegisterAirlineAdmin

**Status:** Implemented
**Class:** `application/usecase/RegisterAirlineAdminUseCase.java`
**Endpoint:** `POST /api/airlines/{id}/admins` (only `ADMIN`)

## Purpose
The platform admin creates the `AIRLINE_ADMIN` account of an airline. That user manages the airline's catalog (aircraft, legs, extra services) and creates its sellers ([RegisterAirlineSeller](register-airline-seller.md)).

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `airlineId` | `String` | Path variable `{id}`: the airline the admin belongs to. |
| `email`, `password`, `idDocument`, `documentType`, `firstName`, `secondName`, `lastName1`, `lastName2`, `birthDate`, `gender` | | Same body as [RegisterCustomer](register-customer.md) (`RegisterUserRequest`). |

## Output
The created `User`. The REST response (`UserResponse`) has `id`, `email`, `role` (`AIRLINE_ADMIN`) and `airlineId`.

## Dependencies
- Repositories: `UserRepository`, `AirlineRepository`
- Ports: `PasswordHasher`

## Steps
1. If the email is already registered, throw `EmailAlreadyRegisteredException`.
2. Check that the airline exists.
3. Check the plain password with `User.validatePasswordStrength` and hash it.
4. Create the user with `User.registerAirlineAdmin(airlineId, ...)`.
5. Save the user and return it.

## Rules and errors
- `401`: no token, or an invalid or expired token.
- `403`: the token is not from an `ADMIN`.
- `400`, `409`, `422`: same as [RegisterCustomer](register-customer.md).
- `404` (`NoSuchElementException`): the airline does not exist.
- An airline user must have an airline (`DomainRuleException`).
