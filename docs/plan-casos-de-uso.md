# Plan de casos de uso – Skyline (backend)

Contexto para retomar el trabajo en otro chat.

## Proyecto

- Plataforma de reservas de vuelos. Spring Boot 4, Java 21, Gradle, Lombok, JPA con H2 en memoria.
- Arquitectura hexagonal / DDD: `domain` (entidades, value objects, servicios, puertos), `application/usecase` (un caso de uso por clase con un método `execute`), `infrastructure` (JPA, REST).
- Persistencia JPA completa para todas las entidades: `*Entity`, `*PersistenceMapper`, `*JpaDataRepository` y `*RepositoryJpa`. Los repositorios en memoria ya no son beans de Spring.
- Pasajeros: `PersonEntity` es `@MappedSuperclass` y `PassengerEntity` hereda de ella. Cada pasajero es una "foto" de la persona en esa reserva.
- La clave de `passengers` es compuesta `(booking_id, id_document)`, así que la misma persona puede estar en varias reservas.
- Plazo: 1 mes, haciendo también el front con ayuda de IA.
- Los detalles de cada caso están en [use-cases/README.md](use-cases/README.md) y lo que falta en [pending.md](pending.md).

## Casos de uso implementados (19)

| # | Caso de uso | Clase | Endpoint REST |
|---|---|---|---|
| 1 | Crear aerolínea | `CreateAirlineUseCase` | `POST /api/airlines` |
| 2 | Crear avión | `CreateAircraftUseCase` | `POST /api/aircrafts` |
| 3 | Crear trayecto (leg) | `CreateLegUseCase` | `POST /api/legs` |
| 4 | Crear servicio extra | `CreateExtraServiceUseCase` | `POST /api/extra_services` |
| 5 | Buscar vuelos | `SearchFlightsUseCase` | `GET /api/flights` |
| 6 | Seleccionar vuelo de ida (crea la reserva `PENDING`) | `SelectOutboundFlightUseCase` | `POST /api/bookings` |
| 7 | Seleccionar vuelo de regreso | `SelectReturnFlightUseCase` | `PUT /api/bookings/{id}/return-flight` |
| 8 | Agregar pasajeros y elegir titular | `AddPassengerUseCase` | `PUT /api/bookings/{id}/passengers` |
| 9 | Ver mapa de asientos de un leg | `GetLegSeatMapUseCase` | `GET /api/legs/{id}/seats` |
| 10 | Asignar asiento a pasajero | `SelectLegSeatUseCase` | `PUT /api/bookings/{id}/seats` |
| 11 | Agregar equipaje | `AddLuggageUseCase` | `PUT /api/bookings/{id}/luggage` |
| 12 | Agregar servicios extra a la reserva | `AddExtraServiceUseCase` | `PUT /api/bookings/{id}/extra-services` |
| 13 | Listar servicios extra de una aerolínea | `ListAirlineExtraServicesUseCase` | `GET /api/airlines/{id}/extra-services` |
| 14 | Consultar reserva por id | `GetBookingUseCase` | `GET /api/bookings/{id}` |
| 15 | Pagar reserva (pago simulado con `FakePaymentGateway`) | `PayBookingUseCase` | `POST /api/bookings/{id}/payment` |
| 16 | Cancelar reserva pendiente | `CancelBookingUseCase` | `PUT /api/bookings/{id}/cancel` |
| 17 | Expirar reservas vencidas | `ExpireBookingUseCase` (+ `FindExpiredBookingsUseCase`) | — (scheduler cada 10 s) |
| 18 | Ranking de aerolíneas por cantidad de aviones | `GetTopAirlinesUseCase` | `GET /api/airlines/top` |
| 19 | Listar aerolíneas | `ListAirlinesUseCase` | `GET /api/airlines` |

## Casos de uso por hacer en el mes (15)

### Prioridad 1 – Cerrar el flujo de compra (1)
1. **Resumen de la reserva (`CalculateBooking`)**: precio desglosado (vuelos, asientos, equipaje, extras, impuestos) antes de pagar. Opcional: la reserva ya acumula el precio total.

### Prioridad 2 – Ciclo de vida de la reserva (2)
Cancelar y expirar ya están implementados (casos 16 y 17).

2. **Quitar o cambiar asiento de un pasajero.** Antes conviene arreglar `LegSeat`, que guarda solo el documento del pasajero (ver [pending.md](pending.md), Seats).
3. **Quitar equipaje o un servicio extra de la reserva.**

### Prioridad 3 – Usuarios y seguridad (5)
4. **Registrar usuario**: `UserEntity` hereda de `PersonEntity`.
5. **Iniciar sesión (login con JWT).**
6. **Roles: administrador y cliente** (proteger endpoints de catálogo para admin).
7. **Ver mis reservas** (las del usuario logueado). Obliga a que `Booking` tenga un dueño.
8. **Ver y editar mi perfil.**

### Catálogo – Lo que necesita el front (7)
`AircraftRepository` todavía no tiene `findAll`; `AirlineRepository` y `LegRepository` sí. Listar aerolíneas ya está implementado (caso 19).

9. Ver detalle de aerolínea
10. Listar aviones
11. Ver detalle de avión
12. Listar legs
13. Ver detalle de leg
14. Listar servicios extra (todos; los de una aerolínea ya están en el caso 13 implementado)
15. Ver detalle de servicio extra

### Opcional si sobra tiempo
- Check-in simple del pasajero.

**Total al final del mes: 34 casos de uso** (19 actuales + 15 nuevos).

## Fuera del mes (versión 2)

- Pase de abordar, cancelación de reserva confirmada con reembolso, cambio de vuelo, correo de confirmación.
- Editar y dar de baja en el catálogo (cambiar horario de un leg, cancelar un leg, retirar avión, editar precios de extras).
- Reportes: ocupación por leg, ventas, reservas por estado. El ranking de aerolíneas ya está (caso 18).

## Plan por semanas

| Semana | Back | Front |
|---|---|---|
| 1 | Prioridad 1 | Estructura, búsqueda de vuelos, selección de ida y regreso |
| 2 | Prioridad 2 | Pasajeros, asientos, equipaje, extras, resumen y pago |
| 3 | Prioridad 3 (Spring Security + JWT) | Login, registro, mis reservas, rutas protegidas por rol |
| 4 | Listados del catálogo, arreglos y pruebas de punta a punta | Panel de admin y detalles finales |

## Convenciones para cada caso nuevo

- Una clase `XxxUseCase` en `application/usecase` con un único `execute(...)`, con repositorios y servicios inyectados por constructor.
- `execute` lleva `@Transactional` de Spring (`org.springframework.transaction.annotation`), o `@Transactional(readOnly = true)` si solo lee, como en el repo de la profe. La excepción es `PayBookingUseCase`, que guarda cada intento de pago por separado.
- Las reglas van en las entidades o servicios de dominio y lanzan `DomainRuleException`.
- Test unitario del caso con Mockito (`@ExtendWith(MockitoExtension.class)`, Arrange/Act/Assert).
- Endpoint REST en el controller correspondiente, con DTOs de request/response y su mapper REST.
- Commits con Conventional Commits (`feat:`, `test:`, `refactor:`) y sin mencionar a ninguna IA.
