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

`SkylineApplicationTests` is a `@SpringBootTest`. JPA and the MariaDB driver are on the classpath, but `application.properties` configures no datasource. Expect this test to fail when a database is unavailable. Run specific test classes when you only need to check domain or use-case logic.

## Architecture

The code is organized in hexagonal/DDD layers under `src/main/java/com/uniquindio/skyline/`:

- `domain/` is plain Java with no Spring annotations. It depends only on Lombok (`@Getter`).
  - `entity/`: aggregates and entities (`Booking`, `Flight`, `Leg`, `Aircraft`, `Airline`, `Passenger`, …). Constructors are private, and objects are built through static factories such as `Booking.createPendingBooking(...)`. These factories validate their input. State changes happen through behavior methods (`addReturnFlight`, …), not through setters.
  - `valueObject/`: immutable value objects and enums (`Seat`, `Layover`, `LuggageDetail`, `BookingStatus`, `TripType`, …). They validate in their constructors.
  - `service/`: domain services for rules that span aggregates (`LegOverlapValidator`, `SeatsAvailableReducer`, `FlightSearcher`, `BookingExpirationValidator`, …). They receive repository interfaces through their constructors.
  - `repository/`: repository **interfaces** (ports).
  - `exception/DomainRuleException`: the single unchecked exception thrown for any violated invariant or business rule.
- `application/`
  - `usecase/`: one class per use case. Each has a single `execute(...)` method and gets its repositories and domain services through constructor injection. A use case typically loads data from repositories, delegates rules to entities and domain services, saves the result, and returns it.
  - `dto/request`, `dto/response`: DTOs for use cases.
- `infrastructure/persistence/`: `*RepositoryInMemory` implementations of the domain repository interfaces, backed by `HashMap`s. There are no JPA entities or Spring wiring yet; use cases are not registered as beans.

Aggregates reference each other by **id (String)**, not by object reference. For example, `Booking` holds `idOutboundFlight` and an `Optional<String> idReturnFlight`.

## Tests

`src/test` mirrors the main package structure, and the tests use JUnit 5.
- Domain entity, value object, and service tests construct objects directly and assert `DomainRuleException` for rule violations.
- Use-case tests use Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`, `@InjectMocks`) to mock repositories and domain services. They follow Arrange/Act/Assert comments.

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
