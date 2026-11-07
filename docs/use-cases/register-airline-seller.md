# RegisterAirlineSeller

**Status:** Implemented
**Class:** `application/usecase/RegisterAirlineSellerUseCase.java`
**Endpoint:** `POST /api/airlines/sellers` (only `AIRLINE_ADMIN`)

## Purpose
An airline admin creates an `AIRLINE_SELLER` account for their own airline. A seller can create aircraft, legs and extra services, and delete extra services, but cannot create other users.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `airlineId` | `String` | Not sent by the client: the controller takes it from the `airlineId` claim of the admin's token. An admin can only add sellers to their own airline. |
| `email`, `password`, `idDocument`, `documentType`, `firstName`, `secondName`, `lastName1`, `lastName2`, `birthDate`, `gender` | | Same body as [RegisterCustomer](register-customer.md) (`RegisterUserRequest`). |

## Output
The created `User`. The REST response (`UserResponse`) has `id`, `email`, `role` (`AIRLINE_SELLER`) and `airlineId`.

## Dependencies
- Repositories: `UserRepository`, `AirlineRepository`
- Ports: `PasswordHasher`

## Steps
1. If the email is already registered, throw `EmailAlreadyRegisteredException`.
2. Check that the airline exists.
3. Check the plain password with `User.validatePasswordStrength` and hash it.
4. Create the user with `User.registerAirlineSeller(airlineId, ...)`.
5. Save the user and return it.

## Rules and errors
- `401`: no token, or an invalid or expired token.
- `403`: the token is not from an `AIRLINE_ADMIN` (an `ADMIN`, a seller or a customer cannot create sellers).
- `400`, `409`, `422`: same as [RegisterCustomer](register-customer.md).
- `404` (`NoSuchElementException`): the airline in the token does not exist.
