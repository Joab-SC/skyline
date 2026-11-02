# Skyline

**Nicho:** reserva de vuelos. Las aerolíneas (vendedores) publican sus aviones, tramos y servicios extra, y los clientes (compradores) buscan vuelos, reservan, eligen asiento, maletas y servicios, y pagan.

**Integrantes:**
- Laura Duarte
- Joab Suárez

## Cómo ejecutar el proyecto

Requisitos: Java 21. No hace falta instalar Gradle ni una base de datos: el proyecto trae el wrapper de Gradle y usa H2 en memoria, que se llena al arrancar con `src/main/resources/data.sql`.

```bash
./gradlew bootRun      # en Windows: gradlew.bat bootRun
```

Si en Mac o Linux sale "permission denied", ejecuten antes `chmod +x gradlew` (o usen `bash gradlew bootRun`).

- Swagger: http://localhost:8080/swagger-ui.html (usen el botón **Authorize** con el token del login)
- Consola H2: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:skylinedb`, usuario `sa`, sin contraseña)
- Pruebas: `./gradlew test`

## Usuarios de prueba

Se crean al arrancar la aplicación (`InitialUsers.java`). El login es `POST /api/auth/login` con `username` (el correo) y `password`.

| Correo | Contraseña | Rol | Aerolínea |
|---|---|---|---|
| admin@skyline.com | admin123 | ADMIN | — |
| airline1@skyline.com | airline123 | AIRLINE | Avianca (`airline-avianca`) |
| airline2@skyline.com | airline123 | AIRLINE | Wingo (`airline-wingo`) |
| customer1@skyline.com | customer123 | CUSTOMER | — |

## Endpoints principales

### Mapeo Dominio -> API - Autenticación

| Operación | Método HTTP | Endpoint | Acceso |
|---|---|---|---|
| registrar un cliente | POST | /api/auth/register | público |
| iniciar sesión (devuelve el JWT) | POST | /api/auth/login | público |

### Mapeo Dominio -> API - Aerolínea (Airline)

| Operación del dominio | Método HTTP | Endpoint | Acceso |
|---|---|---|---|
| Airline.createAirline(...) | POST | /api/airlines | ADMIN |
| consultar todas las aerolíneas | GET | /api/airlines | público |
| ranking de aerolíneas con más aviones | GET | /api/airlines/top | ADMIN |
| airline.getActiveExtraServices() | GET | /api/airlines/{id}/extra-services | público |

### Mapeo Dominio -> API - Servicio extra (ExtraService)

| Operación del dominio | Método HTTP | Endpoint | Acceso |
|---|---|---|---|
| airline.createExtraService(...) | POST | /api/extra_services | AIRLINE |
| buscar servicios extra (texto, aerolínea, paginado) | GET | /api/extra_services | público |
| airline.deleteExtraService(id) | DELETE | /api/extra_services/{extraServiceId} | AIRLINE (la aerolínea sale del token) |

### Mapeo Dominio -> API - Avión (Aircraft)

| Operación del dominio | Método HTTP | Endpoint | Acceso |
|---|---|---|---|
| Aircraft.createAircraft(...) + airline.addAircraft(...) | POST | /api/aircrafts | AIRLINE |

### Mapeo Dominio -> API - Tramo (Leg)

| Operación del dominio | Método HTTP | Endpoint | Acceso |
|---|---|---|---|
| Leg.createLeg(...) + airline.addLeg(...) | POST | /api/legs | AIRLINE |
| consultar el mapa de asientos de un tramo | GET | /api/legs/{id}/seats | público |

### Mapeo Dominio -> API - Vuelo (Flight)

| Operación del dominio | Método HTTP | Endpoint | Acceso |
|---|---|---|---|
| flightSearcher.searchFlights(...) | GET | /api/flights | público |

### Mapeo Dominio -> API - Reserva (Booking)

| Operación del dominio | Método HTTP | Endpoint | Acceso |
|---|---|---|---|
| Booking.createPendingBooking(...) | POST | /api/bookings | público (con token CUSTOMER queda ligada al cliente) |
| booking.addReturnFlight(...) | PUT | /api/bookings/{id}/return-flight | público |
| booking.addPassengers(...) + booking.choseHolder(...) | PUT | /api/bookings/{id}/passengers | público |
| seatAssigner.assignSeat(...) | PUT | /api/bookings/{id}/seats | público |
| booking.registerPassengerLuggage(...) | PUT | /api/bookings/{id}/luggage | público |
| booking.addExtraService(...) | PUT | /api/bookings/{id}/extra-services | público |
| booking.startPayment(...) + booking.registerPaymentResult(...) | POST | /api/bookings/{id}/payment | público |
| booking.cancelBooking() | PUT | /api/bookings/{id}/cancel | público |
| consultar una reserva | GET | /api/bookings/{id} | público |
| consultar las reservas del cliente | GET | /api/bookings/mine | CUSTOMER |

## Correcciones de la Entrega 1

| Observación que nos dieron | Qué cambiamos | Dónde se ve (archivo o rama) |
|---|---|---|
| Las validaciones del DTO no tenían mensaje, así que cuando un dato era inválido la respuesta no decía qué campo falló ni por qué | Le pusimos `message` a cada validación (`@NotNull(message = "The origin airport is required")`, `@Positive(message = "The price must be greater than 0")`, …). El `GlobalExceptionHandler` responde 400 con un mapa `errors` campo → mensaje. En el mismo cambio los aeropuertos pasaron de `String` al Value Object `Airport`. Los DTOs que creamos después ya nacieron con mensaje | `CreateLegRequest.java`. Antes: commit `3c4f0e8`, rama `feature/leg-dtos-and-uuid` (PR #5). Después: commit `dba59cb`, rama `feat/leg-controller` (PR #32) |

## Pruebas avanzadas

### 1. Probar un endpoint con seguridad

Se eligió la ruta de `GET /api/airlines/top`, la cual está protegida porque es para hacer análisis de todas las aerolíneas que hay registradas en la plataforma, por tanto no es una acción de un usuario o vendedor en particular sino del propio administrador.

Las 3 pruebas realizadas fueron: una sin token que da 401, otra con rol equivocado de "CUSTOMER" que da 403 y otra con rol correcto que da 200.

La diferencia entre el 401 y el 403 es que el 401 corresponde a un error de Unauthorized, es decir, el servidor no pudo validar el token enviado, ya sea porque el usuario no existe, el token está vencido o simplemente es inválido; mientras que el 403 corresponde a un error de Forbidden, es decir, el servidor sí pudo validar el token y reconocer al usuario, pero su rol no tiene permiso para la ruta solicitada.

Spring Security Test es una librería que trae herramientas para probar código con Spring Security, y en este caso se usa la herramienta `jwt()`, que permite saltarse la parte de generar y validar el token para simplemente indicar que se hizo una solicitud con un usuario autenticado con x roles, permitiendo probar entonces los permisos de cada endpoint.

Es necesario el `@SpringBootTest` + `@AutoConfigureMockMvc` porque con la primera podemos levantar la aplicación con los beans y reglas de seguridad, mientras que con la segunda podemos hacer peticiones HTTP falsas a este contexto real levantado. En cambio, solo con `@WebMvcTest`, solo se levantan los controladores, ignorando los beans configurados de seguridad.

Los roles van sin prefijo porque nosotros a propósito lo quitamos con `authorities.setAuthorityPrefix("");` en el `JwtConfig`.

Lo que más nos costó fue entender cómo `jwt()` se salta las reglas de validación del token para solo simular un usuario con roles, y cómo configurar los claims y las authorities en el `JwtConfig` y en el `JwtTokenProvider`.

### 2. `@DataJpaTest` contra `@SpringBootTest`

`@SpringBootTest` levanta toda la aplicación, eso incluye controllers, casos de uso, repositorios y la base de datos H2. Esta anotacion la usamos en `AirlineRepositoryJpaTest` y `AircraftRepositoryJpaTest`, pues en estas necesitabamos guardar una entidad y recuperarla para ver que los datos y los Value Objects volvieran iguales. En cambio `@DataJpaTest` solo levanta la capa de persistencia (entidades, `JpaRepository` y H2), eso hace que sea más rápida, tambien se encarga de que cada prueba haga rollback sola cuando termine. Como no carga nuestras clases propias, en `AirlineRepositoryDataJpaTest`, `AircraftRepositoryDataJpaTest` y `AirlineRankingDataJpaTest` tuvimos que traer el adaptador y los mappers con `@Import` ya que si falta alguno, la prueba falla con "No qualifying bean".
En Spring Boot 4 `@DataJpaTest` ya no está en el paquete de antes: el import correcto es `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest` y necesita la dependencia `spring-boot-starter-data-jpa-test` en el `build.gradle`. Por defecto @DataJpaTest no usa la base de application.properties, sino que crea una H2 en memoria aparte; si quisiéramos probar contra la base configurada habría que poner @AutoConfigureTestDatabase(replace = Replace.NONE). En nuestro caso dejamos el comportamiento por defecto porque ya usamos H2. Lo que más nos costó fue la prueba del ranking: `data.sql` tenía los ids de los aviones de cada aerolínea, pero no los aviones como tal, y como la consulta hace JOIN con la tabla `aircrafts`necesitabamos que existieran los aircrafts o el resultado saldría vacío, Por eso mismo los agregamos usando los ids que ya existían.

### 3. Prueba de integración

Una prueba de integración recorre el sistema real sin simular nada. En `CreateExtraServiceIntegrationTest` hacemos un `POST /api/extra_services` con MockMvc; la petición pasa por el controller, el caso de uso, el repositorio JPA y llega a H2, sin ningún `@MockitoBean`. Como el endpoint está protegido, la prueba genera un token real de un usuario de Wingo con `JwtTokenProvider` y lo manda en el header `Authorization`, así que también pasa por la seguridad. Además tiene un segundo caso: con un token de `ADMIN` responde 403, porque crear servicios extra es solo del rol `AIRLINE`. Después de la respuesta 201 buscamos la aerolínea en el repositorio y comprobamos que el servicio extra sí quedó guardado. En cambio, una prueba unitaria prueba una sola clase y simula con Mockito todo lo que esa clase usa, así que es rápida pero no garantiza que las piezas funcionen juntas. La de integración sí detecta errores de conexión entre capas, como un mapeo JPA mal hecho, un bean que no se inyecta o un JSON con nombres distintos. Como es más lenta y depende de `data.sql`, conviene tener pocas y solo para los flujos importantes. Lo que más nos dificultó fue probar la igualdad entre los objetos sin tener el id a mano pues es generado con UUID y entender la anotación de @Autowired qu eliego vimos que era para inyectar Beans automaticamente.