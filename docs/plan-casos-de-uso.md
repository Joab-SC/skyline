# Plan de casos de uso – Skyline (backend)

Contexto para retomar el trabajo en otro chat.

## Proyecto

- Plataforma de reservas de vuelos. Spring Boot 4, Java 21, Gradle, Lombok, JPA con H2 en memoria.
- Arquitectura hexagonal / DDD: `domain` (entidades, value objects, servicios, puertos), `application/usecase` (un caso de uso por clase con un método `execute`), `infrastructure` (JPA, REST).
- Persistencia JPA completa para todas las entidades: `*Entity`, `*PersistenceMapper`, `*JpaDataRepository` y `*RepositoryJpa`. Los repositorios en memoria ya no son beans de Spring.
- Pasajeros: `PersonEntity` es `@MappedSuperclass` y `PassengerEntity` hereda de ella. Cada pasajero es una "foto" de la persona en esa reserva.
- Pendiente de diseño: la clave de `passengers` debe ser compuesta `(booking_id, id_document)` para que la misma persona pueda estar en varias reservas.
- Plazo: 1 mes, haciendo también el front con ayuda de IA.

## Casos de uso implementados (12)

| # | Caso de uso | Clase | Endpoint REST |
|---|---|---|---|
| 1 | Crear aerolínea | `CreateAirlineUseCase` | `POST /api/airlines` |
| 2 | Crear avión | `CreateAircraftUseCase` (sin test) | `POST /api/aircrafts` |
| 3 | Crear trayecto (leg) | `CreateLegUseCase` (sin test) | `POST /api/legs` |
| 4 | Crear servicio extra | `CreateExtraServiceUseCase` | `POST /api/extra_services` |
| 5 | Buscar vuelos | `SearchFlightsUseCase` | `GET /api/flights` |
| 6 | Seleccionar vuelo de ida (crea la reserva `PENDING`) | `SelectOutboundFlightUseCase` | `POST /api/bookings` |
| 7 | Seleccionar vuelo de regreso | `SelectReturnFlightUseCase` | `PUT /api/bookings/{id}/return-flight` |
| 8 | Agregar pasajeros y elegir titular | `AddPassengerUseCase` | `PUT /api/bookings/{id}/passengers` |
| 9 | Ver mapa de asientos de un leg | `GetLegSeatMap` | revisar |
| 10 | Asignar asiento a pasajero | `SelectLegSeatUseCase` | **falta endpoint** |
| 11 | Agregar equipaje | `AddLuggageUseCase` | **falta endpoint** |
| 12 | Agregar servicios extra a la reserva | `AddExtraServiceUseCase` | **falta endpoint** |

## Casos de uso por hacer en el mes (21)

### Prioridad 1 – Cerrar el flujo de compra (4)
1. **Listar servicios extra de una aerolínea**: el cliente necesita verlos para elegirlos.
2. **Resumen de la reserva (`CalculateBooking`)**: precio desglosado (vuelos, asientos, equipaje, extras, impuestos) antes de pagar.
3. **Pagar reserva (`PayBooking`)**: pasa la reserva a `CONFIRMED`. Necesita `Booking.confirm()` que valide `PENDING` y que no esté vencida. El pago puede ser simulado.
4. **Consultar reserva por id**: ver el estado y el detalle en cualquier momento.

Más: exponer en REST los casos 10, 11 y 12 de la tabla anterior.

### Prioridad 2 – Ciclo de vida de la reserva (4)
5. **Cancelar reserva pendiente (`CancelBooking`)**: valida `PENDING` y devuelve los asientos a los legs. Hoy `Booking.cancelBooking()` no valida el estado.
6. **Expirar reservas vencidas**: `BookingExpirationValidator` ya existe pero el `for` está vacío y falta `@EnableScheduling`. Debe reutilizar la lógica de cancelar con estado `EXPIRED` (revisar que exista en `BookingStatus`).
7. **Quitar o cambiar asiento de un pasajero.**
8. **Quitar equipaje o un servicio extra de la reserva.**

### Prioridad 3 – Usuarios y seguridad (5)
9. **Registrar usuario**: `UserEntity` hereda de `PersonEntity`.
10. **Iniciar sesión (login con JWT).**
11. **Roles: administrador y cliente** (proteger endpoints de catálogo para admin).
12. **Ver mis reservas** (las del usuario logueado).
13. **Ver y editar mi perfil.**

### Catálogo – Lo que necesita el front (8)
14. Listar aerolíneas
15. Ver detalle de aerolínea
16. Listar aviones
17. Ver detalle de avión
18. Listar legs
19. Ver detalle de leg
20. Listar servicios extra
21. Ver detalle de servicio extra

### Opcional si sobra tiempo
- Check-in simple del pasajero.

**Total al final del mes: 33 casos de uso** (12 actuales + 21 nuevos).

## Fuera del mes (versión 2)

- Pase de abordar, cancelación de reserva confirmada con reembolso, cambio de vuelo, correo de confirmación.
- Editar y dar de baja en el catálogo (cambiar horario de un leg, cancelar un leg, retirar avión, editar precios de extras).
- Reportes: ranking de aerolíneas (ya existe la consulta `AirlineAircraftCount`), ocupación por leg, ventas, reservas por estado.

## Plan por semanas

| Semana | Back | Front |
|---|---|---|
| 1 | Prioridad 1 + endpoints faltantes | Estructura, búsqueda de vuelos, selección de ida y regreso |
| 2 | Prioridad 2 | Pasajeros, asientos, equipaje, extras, resumen y pago |
| 3 | Prioridad 3 (Spring Security + JWT) | Login, registro, mis reservas, rutas protegidas por rol |
| 4 | Listados del catálogo, arreglos y pruebas de punta a punta | Panel de admin y detalles finales |

## Convenciones para cada caso nuevo

- Una clase `XxxUseCase` en `application/usecase` con un único `execute(...)`, con repositorios y servicios inyectados por constructor.
- Las reglas van en las entidades o servicios de dominio y lanzan `DomainRuleException`.
- Test unitario del caso con Mockito (`@ExtendWith(MockitoExtension.class)`, Arrange/Act/Assert).
- Endpoint REST en el controller correspondiente, con DTOs de request/response y su mapper REST.
- Commits con Conventional Commits (`feat:`, `test:`, `refactor:`) y sin mencionar a ninguna IA.
