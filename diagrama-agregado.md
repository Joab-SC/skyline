# Invariantes

* Un pasajero nunca puede tener más de un asiento asignado en un mismo trayecto (leg) de una reserva.
* La fecha de nacimiento de un pasajero nunca puede ser una fecha futura.
* Un servicio extra nunca se puede incluir en una reserva si no pertenece a la aerolínea de los vuelos de esta.
* Una reserva siempre debe tener al menos un pasajero asociado.
* Una reserva siempre debe tener un único pasajero titular.
* Una reserva solo queda confirmada cuando uno de sus pagos es aprobado.
* Una reserva tiene como máximo un pago pendiente.
* Nunca se puede realizar el check-in de una reserva que no esté confirmada (pagada). *Fuera del alcance por ahora: el check-in no está implementado.*
