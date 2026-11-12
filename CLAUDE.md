# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Skyline is a flight-booking platform (university course project, Universidad del Quindío) built with Spring Boot 4 on Java 21, using Gradle and Lombok. It follows Domain-Driven Design. Documentation and the ubiquitous language are in Spanish, but code identifiers are in English.

## Commands

Use `gradlew.bat` on Windows and `./gradlew` in bash.

```bash
./gradlew build                       # compile + run all tests
./gradlew test                        # run all tests
./gradlew test --tests "com.uniquindio.skyline.domain.entity.BookingTest"            # one test class
./gradlew test --tests "com.uniquindio.skyline.domain.entity.BookingTest.someMethod" # one test method
./gradlew bootRun                     # run the app
```

There is no linter or formatter configured.

The app uses an in-memory H2 database (`application.properties`), created on startup (`ddl-auto=create-drop`) and filled from `src/main/resources/data.sql`. The H2 console is at `/h2-console`. The MariaDB driver is also on the classpath but not configured. `SkylineApplicationTests` is a `@SpringBootTest`. Run specific test classes when you only need to check domain or use-case logic.

## Architecture

The code is organized in hexagonal/DDD layers under `src/main/java/com/uniquindio/skyline/`:

- `domain/` is plain Java with no Spring annotations. It depends only on Lombok (`@Getter`).
  - `entity/`: aggregates and entities (`Booking`, `Flight`, `Leg`, `Aircraft`, `Airline`, `Passenger`, `User`, …). Constructors are private, and objects are built through static factories such as `Booking.createPendingBooking(...)`. These factories validate their input. State changes happen through behavior methods (`addReturnFlight`, …), not through setters.
  - `valueObject/`: immutable value objects and enums (`Seat`, `Layover`, `LuggageDetail`, `BookingStatus`, `TripType`, `UserRole`, …). They validate in their constructors. `PassengerBookingId` (booking id + passenger document) is who holds a `LegSeat`: the document alone is not enough, because the same person can be in two bookings on the same leg.
  - `service/`: domain services for rules that span aggregates (`LegOverlapValidator`, `SeatsAvailableReducer`, `FlightSearcher`, `BookingExpirationValidator`, …). They receive repository interfaces through their constructors.
  - `repository/`: repository **interfaces** (ports), plus the plain records they return: `PageResult<T>` (a page of results, so the domain does not use Spring's `Page`), `AirlineAircraftCount` (a row of the airline ranking) and `BookingSeat` (a seat held by a booking's passenger, with its leg).
  - `exception/DomainRuleException`: the single unchecked exception thrown for any violated invariant or business rule.
- `application/`
  - `usecase/`: one class per use case, annotated `@Service` with `@RequiredArgsConstructor`. Each has a single `execute(...)` method and gets its repositories and domain services through constructor injection. A use case typically loads data from repositories, delegates rules to entities and domain services, saves the result, and returns it. A missing entity is usually `findById(...).orElseThrow()`, which throws `NoSuchElementException`.
  - `dto/request`, `dto/response`: DTOs for use cases. Requests use Jakarta Bean Validation (`@NotBlank`, `@Positive`, …).
  - `dto/result`: what a use case returns when it is more than one domain object, such as `BookingDetail` (the booking and its seats, returned by `GetBookingUseCase`).
  - `port/`: ports to external services used by the use cases. `PaymentGateway` (with `PaymentRequest` and `PaymentResult`) charges a booking through a payment provider. `PasswordHasher` hashes passwords when a user registers.
  - `exception/EmailAlreadyRegisteredException`: thrown when a user registers with an email that already exists (409 in REST).
- `infrastructure/`
  - `config/DomainServiceConfig`: registers the domain services as Spring beans, since the domain has no Spring annotations.
  - `persistence/jpa/`: the active adapters. For each aggregate there is a `*Entity` (`jpa/entity`), a `*PersistenceMapper` (`jpa/mapper`) with `reconstruct` on the way back to the domain, a Spring Data `*JpaDataRepository`, and a `*RepositoryJpa` that implements the domain port.
  - `persistence/`: `FlightSearchCacheInMemory` (the only in-memory bean, it keeps searched flights until one is selected) and older `*RepositoryInMemory` classes that are not beans; the domain service tests use them.
  - `security/`: Spring Security with JWT. `SecurityConfig` holds the access rule of every endpoint (default deny), `AuthenticationService` does the login, `JwtTokenProvider` builds the token (claims `uid`, `roles` and, for airline users, `airlineId`), and `InitialUsers` creates the development users on startup. Roles: `ADMIN`, `AIRLINE_ADMIN`, `AIRLINE_SELLER`, `CUSTOMER`. For airline users, controllers take the airline from the token, never from the body. The booking flow is public (guest checkout); with a `CUSTOMER` token the booking is linked to that customer.
  - `payment/`: `FakePaymentGateway`, the only `PaymentGateway` adapter. It simulates a provider: it approves every token except `tok_declined` and `tok_error`.
  - `rest/`: one controller per aggregate (`/api/auth`, `/api/airlines`, `/api/airports`, `/api/aircrafts`, `/api/legs`, `/api/flights`, `/api/bookings`, `/api/extra_services`), REST mappers in `rest/mapper`, and `GlobalExceptionHandler`. It answers `DomainRuleException` (a broken business rule) with 422, `NoSuchElementException` with 404, `EmailAlreadyRegisteredException` with 409, wrong login credentials with 401, and invalid input with 400: DTO validation errors (with an `errors` map of field → message), an unreadable JSON body, a param with the wrong type or missing, and `IllegalArgumentException` (input checks in a use case, such as a limit out of range). Every error body is JSON with `timestamp`, `status` and `message`. A missing or invalid token (401) and a role without access (403) are answered by `SecurityConfig` with the same JSON.

Airports travel through the API as their **IATA code** (`"BOG"`), in requests and responses, never as the `Airport` constant name. `GET /api/airports` returns the catalog. A use case turns a code into an `Airport` with `Airport.getByAcronym` and throws `IllegalArgumentException` (400) for an unknown code. The database also stores the code: `AirportConverter` (`persistence/jpa/converter`, `autoApply`) converts every `Airport` field, so entities have no `@Enumerated` on airports. `SkylineH2Dialect` (set in `application.properties`) stops Hibernate from adding a `CHECK (... IN (...))` with every code, which breaks H2 when the connection that created it is closed (`@DataJpaTest`).

Aggregates reference each other by **id (String)**, not by object reference. For example, `Booking` holds `idOutboundFlight` and an `Optional<String> idReturnFlight`.

## Tests

`src/test` mirrors the main package structure, and the tests use JUnit 5.
- Domain entity, value object, and service tests construct objects directly and assert `DomainRuleException` for rule violations.
- Use-case tests use Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`) to mock repositories and domain services. They follow Arrange/Act/Assert comments.
- Controller tests use `@WebMvcTest(XController.class)` with `MockMvc` and `@MockitoBean` for the use cases and mappers.
- Security tests use `@SpringBootTest` + `@AutoConfigureMockMvc` with `jwt()` from Spring Security Test to check 401/403/2xx per role.
- Persistence tests (`infrastructure/persistence/jpa/*DataJpaTest`) use `@DataJpaTest` against H2. `*RepositoryJpaTest` load the full context with `@SpringBootTest`.
- Domain service tests use the `*RepositoryInMemory` classes instead of mocks.

## Domain documentation

Check these before changing domain rules:
- `glosario_lenguaje_obicuo.md`: the ubiquitous language glossary. Each entry gives the accepted English term to use in code and the terms to avoid (for example, use `Layover`, not "Conexion"; use `Booking`, not "Ticket").
- `diagrama-agregado.md` and `docs/<aggregate>-aggregate/`: the invariants and aggregate diagrams for the Booking, Leg, and Aircraft aggregates. Enforce these invariants inside the aggregate or a domain service by throwing `DomainRuleException`.
- Use cases of the system: see @docs/use-cases/README.md
- Pending work: see @docs/pending.md

## Conventions

- Commits use Conventional Commit prefixes (`feat:`, `fix:`, `test:`, `refactor:`, `docs:`, `merge:`). Work happens on `feature/*`, `feat/*`, `fix/*`, `test/*` or `docs/*` branches and is merged into `main` through pull requests.
- Declare collection fields and variables using their interfaces (`List`, `Map`), not their implementations.
- Constants use UPPER_SNAKE_CASE.
