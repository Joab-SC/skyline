# RegisterCustomer

**Status:** Implemented
**Class:** `application/usecase/RegisterCustomerUseCase.java`
**Endpoint:** `POST /api/auth/register` (public)

## Purpose
Creates a `CUSTOMER` account so a person can log in ([Login](login.md)) and see their bookings ([ListCustomerBookings](list-customer-bookings.md)). The role is not sent: every account created here is a `CUSTOMER`.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `email` | `String` | Account email. It is also the `username` of the login. |
| `password` | `String` | Plain password. It is hashed before it is saved. |
| `idDocument` | `String` | Person's document. |
| `documentType` | `DocumentType` | Type of the document. |
| `firstName` | `String` | First name. |
| `secondName` | `String` | Optional. |
| `lastName1` | `String` | First last name. |
| `lastName2` | `String` | Optional. |
| `birthDate` | `LocalDate` | Must be in the past. |
| `gender` | `Gender` | Gender. |

## Output
The created `User`. The REST response (`UserResponse`) has `id`, `email`, `role` and `airlineId` (`null` for a customer). It never includes the password or its hash.

## Dependencies
- Repositories: `UserRepository`
- Ports: `PasswordHasher` (bcrypt adapter: `SpringPasswordHasher`)

## Steps
1. If the email is already registered, throw `EmailAlreadyRegisteredException`.
2. Check the plain password with `User.validatePasswordStrength`.
3. Hash the password.
4. Create the user with `User.registerCustomer(...)`. It builds the `Person` and gets a UUID.
5. Save the user and return it.

## Rules and errors
- `400` (DTO validation): a required field is missing, the email is not valid or the birth date is not in the past. The body has an `errors` map field → message.
- `409` (`EmailAlreadyRegisteredException`): the email is already registered.
- `422` (`DomainRuleException`):
  - the password has fewer than 8 characters, no uppercase letter or no number;
  - the email format is invalid;
  - a `Person` rule is broken (empty document, empty name, …).
- A customer has no airline.
- Open decisions are in [../pending.md](../pending.md), Users: email case, minors and repeated documents.
