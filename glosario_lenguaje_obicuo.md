# Glosario del Lenguaje Ubicuo - Skyline

## Conceptos Centrales

### Escala
**Definición:**  Parada intermedia que realiza un vuelo entre su origen y destino final

**Sinónimos aceptados:** Layover

**No usar:** Conexion

**Ejemplo de uso en código:**
- "El vuelo de Pereira a Cartagena tiene una escala en Bogota"
- "La escala dura 6 horas"
---

### Origen
**Definición:** Aeropuerto y ciudad desde donde inicia un vuelo comercial 

**Sinónimos aceptados:** Origin

**No usar:** Salida, inicio

**Ejemplo de uso:**
- Origin origin = createOrigin(airport, city)
- flight.setOrigin(origin)
---

### Destino
**Definición:** Aeropuerto y ciudad donde finaliza un vuelo comercial

**Sinónimos aceptados:** Destination

**No usar:** Llegada, final

**Ejemplo de uso:**
- "El destino de este vuelo es en New York en el aeropuerto Jhon F.Kennedy"
---

### Reserva
**Definición:** Registro que representa el proceso de apartar uno o más asientos en un vuelo (ya sea de solo ida o de ida y vuelta) para una fecha y ruta específica

**Sinónimos aceptados:** Booking

**No usar:** Ticket

**Ejemplo de uso:**
- "Estas reservas están pendientes de pago"

**Estados validos:**
- PENDIENTE: Reserva recien creada, en ella el usuario está llenando sus datos y preferencias.
- CONFIRMADA: Reserva paga, el usuario ya realizó la transacción y es el mismo comprobante del proceso.   
- CANCELADA: Reserva finalizada sin haber sido pagada, el usuario decide no pagar o seguir con el proceso, o tarda mucho en hacerlo. 
---

### Comision
**Definición:** Valor descontado por la plataforma a las aerolineas  sobre cada operación de reserva realizada a través de la página.

**Sinónimos aceptados:** Commision, Fee

**No usar:** Impuesto, tax

**Ejemplo de uso:**
- Double commission = calculate_commission(booking)
---


### Vuelo
**Definición:** Recorrido áreo que realiza un pasajero desde un lugar de origen hasta un lugar de destino. Puede incluir uno más trayectos cuando existen escalas.

**Sinónimos aceptados:** Flight

**No usar:** Viaje, trayecto

**Ejemplo de uso:**
- List<Flight> flights = flightService.searchAvailables(origin, destination, departureDate, arrivalDate)
---

### Asiento
**Definición:** Ubicación individual dentro de un avión que puede ser seleccionada o asignada a un pasajero.

**Sinónimos aceptados:** Seat
**No usar:** Silla, lugar

**Ejemplo de uso:**
- "Todos los asientos del vuelo AV123 están ocupados"
---


### Asignar
**Definición:** Acción de asociar un asiento disponible con un pasajero dentro de una reserva.

**Sinónimos aceptados:** Asign

**No usar:** Ubicar

**Ejemplo de uso:**
- "Su asiento asignado es el 32B"
---

### Pasabordo
**Definición:** Documento que acredita que un pasajero está autorizado para abordar un vuelo específico. Se genera después de realizar el check-in y contiene la información necesaria para el embarque.

**Sinónimos aceptados:** Boarding pass, pase de abordar

**No usar:** Ticket, boleto, reserva

**Precondiciones:**
- El pasajero debe tener una reserva confirmada.
- El pasajero debe haber realizado el check-in.

**Ejemplo de uso:**
- BoardingPass boardingPass = passenger.performCheckIn(booking);
---

## Anti-patrones (Términos a EVITAR en nuestro proyecto)

| No usar | Usar |

| Viaje   | Flight/Vuelo|

| Parada  | Escala/ Layover |

| Ticket  | Reserva/Booking |

| Pase    | Pasabordo/Boarding pass |

| Silla   | Asiento/Seat |
