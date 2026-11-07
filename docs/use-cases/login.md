# Login

**Status:** Implemented
**Class:** `infrastructure/security/AuthenticationService.java` (it is not a use case class: it uses Spring Security)
**Endpoint:** `POST /api/auth/login` (public)

## Purpose
Checks the email and password of any user (`ADMIN`, `AIRLINE_ADMIN`, `AIRLINE_SELLER`, `CUSTOMER`) and returns a JWT. The client sends it as `Authorization: Bearer <token>` on protected endpoints.

## Inputs
| Name | Type | Description |
|------|------|-------------|
| `username` | `String` | The user's email. |
| `password` | `String` | Plain password. |

## Output
`TokenResponse`: `token`, `type` (always `Bearer`), `expireAt` and `roles`. The roles are returned so the front can decide what to show without decoding the token.

The token has these claims: `sub` (email), `uid` (user id), `roles` and, only for airline users, `airlineId`. It lasts `jwt.expiry` minutes (60 by default).

## Dependencies
- Spring Security: `AuthenticationManager` (with `CustomUserDetails` and the bcrypt `PasswordEncoder`)
- `JwtTokenProvider`

## Steps
1. Authenticate `username` and `password` with the `AuthenticationManager`.
2. Read the user and its role.
3. Generate the token with its claims and expiration.
4. Return the `TokenResponse`.

## Rules and errors
- `400`: `username` or `password` is empty.
- `401`: wrong email or password. The message is the same in both cases (`Invalid email or password`), so it does not reveal which emails exist.
- The development users are created on startup by `InitialUsers` (see the README).
