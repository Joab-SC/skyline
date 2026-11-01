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

- `domain/` is plain Java with no Spring annotations. It depends only on Lombok (`@Getter`). One exception today: `BookingExpirationValidator` imports Spring's `@Scheduled` (see pending).
  - `entity/`: aggregates and entities (`Booking`, `Flight`, `Leg`, `Aircraft`, `Airline`, `Passenger`, …). Constructors are private, and objects are built through static factories such as `Booking.createPendingBooking(...)`. These factories validate their input. State changes happen through behavior methods (`addReturnFlight`, …), not through setters.
  - `valueObject/`: immutable value objects and enums (`Seat`, `Layover`, `LuggageDetail`, `BookingStatus`, `TripType`, …). They validate in their constructors.
  - `service/`: domain services for rules that span aggregates (`LegOverlapValidator`, `SeatsAvailableReducer`, `FlightSearcher`, `BookingExpirationValidator`, …). They receive repository interfaces through their constructors.
  - `repository/`: repository **interfaces** (ports), plus the plain records they return: `PageResult<T>` (a page of results, so the domain does not use Spring's `Page`) and `AirlineAircraftCount` (a row of the airline ranking).
  - `exception/DomainRuleException`: the single unchecked exception thrown for any violated invariant or business rule.
- `application/`
  - `usecase/`: one class per use case, annotated `@Service` with `@RequiredArgsConstructor`. Each has a single `execute(...)` method and gets its repositories and domain services through constructor injection. A use case typically loads data from repositories, delegates rules to entities and domain services, saves the result, and returns it. A missing entity is usually `findById(...).orElseThrow()`, which throws `NoSuchElementException`.
  - `dto/request`, `dto/response`: DTOs for use cases. Requests use Jakarta Bean Validation (`@NotBlank`, `@Positive`, …).
  - `port/`: ports to external services used by the use cases. `PaymentGateway` (with `PaymentRequest` and `PaymentResult`) charges a booking through a payment provider.
- `infrastructure/`
  - `config/DomainServiceConfig`: registers the domain services as Spring beans, since the domain has no Spring annotations.
  - `persistence/jpa/`: the active adapters. For each aggregate there is a `*Entity` (`jpa/entity`), a `*PersistenceMapper` (`jpa/mapper`) with `reconstruct` on the way back to the domain, a Spring Data `*JpaDataRepository`, and a `*RepositoryJpa` that implements the domain port.
  - `persistence/`: `FlightSearchCacheInMemory` (the only in-memory bean, it keeps searched flights until one is selected) and older `*RepositoryInMemory` classes that are not beans; the domain service tests use them.
  - `payment/`: `FakePaymentGateway`, the only `PaymentGateway` adapter. It simulates a provider: it approves every token except `tok_declined` and `tok_error`.
  - `rest/`: one controller per aggregate (`/api/airlines`, `/api/aircrafts`, `/api/legs`, `/api/flights`, `/api/bookings`, `/api/extra_services`), REST mappers in `rest/mapper`, and `GlobalExceptionHandler`. It answers `DomainRuleException` (a broken business rule) with 422, `NoSuchElementException` with 404, and invalid input with 400: DTO validation errors (with an `errors` map of field → message), an unreadable JSON body, a param with the wrong type or missing, and `IllegalArgumentException` (input checks in a use case, such as a limit out of range). Every error body is JSON with `timestamp`, `status` and `message`.

Aggregates reference each other by **id (String)**, not by object reference. For example, `Booking` holds `idOutboundFlight` and an `Optional<String> idReturnFlight`.

## Tests

`src/test` mirrors the main package structure, and the tests use JUnit 5.
- Domain entity, value object, and service tests construct objects directly and assert `DomainRuleException` for rule violations.
- Use-case tests use Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`) to mock repositories and domain services. They follow Arrange/Act/Assert comments.
- Controller tests use `@WebMvcTest(XController.class)` with `MockMvc` and `@MockitoBean` for the use cases and mappers.
- Persistence tests (`infrastructure/persistence/jpa/*DataJpaTest`) use `@DataJpaTest` against H2. `*RepositoryJpaTest` load the full context with `@SpringBootTest`.
- Domain service tests use the `*RepositoryInMemory` classes instead of mocks.

## Domain documentation

Check these before changing domain rules:
- `glosario_lenguaje_obicuo.md`: the ubiquitous language glossary. Each entry gives the accepted English term to use in code and the terms to avoid (for example, use `Layover`, not "Conexion"; use `Booking`, not "Ticket").
- `diagrama-agregado.md` and `docs/<aggregate>-aggregate/`: the invariants and aggregate diagrams for the Booking, Leg, and Aircraft aggregates. Enforce these invariants inside the aggregate or a domain service by throwing `DomainRuleException`.
- Use cases of the system: see @docs/use-cases/README.md
- Pending work: see @docs/pending.md

## Conventions

- Commits use Conventional Commit prefixes (`feat:`, `test:`, `refactor:`, `merge:`). Work happens on `feature/*`, `feat/*`, or `test/*` branches and is merged into `main` through pull requests.
- Declare collection fields and variables using their interfaces (`List`, `Map`), not their implementations.
- Constants use UPPER_SNAKE_CASE.
