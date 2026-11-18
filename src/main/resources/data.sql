-- Test data loaded on startup (H2 in memory).
-- Tests that use this data depend on these exact values.

-- Airlines
INSERT INTO airlines (id, name, maximum_luggage_amount, price_seats) VALUES
    ('airline-avianca', 'Avianca', 3, 45000),
    ('airline-latam', 'LATAM', 2, 38000),
    ('airline-wingo', 'Wingo', 1, 25000);

-- Aircrafts (seats are not needed for the tests that use this data)
INSERT INTO aircrafts (id, aircraft_model) VALUES
    ('aircraft-a320-01', 'AIRBUS_A320_NEO'),
    ('aircraft-a320-02', 'AIRBUS_A320_NEO'),
    ('aircraft-b787-01', 'BOEING_737_9_MAX'),
    ('aircraft-a321-01', 'AIRBUS_A321_NEO'),
    ('aircraft-a320-03', 'AIRBUS_A320_NEO'),
    ('aircraft-b737-01', 'BOEING_737_8_MAX');

-- Aircraft ids per airline
-- Ranking by aircraft count: Avianca 3, LATAM 2, Wingo 1
INSERT INTO airline_aircrafts (airline_id, aircraft_id) VALUES
    ('airline-avianca', 'aircraft-a320-01'),
    ('airline-avianca', 'aircraft-a320-02'),
    ('airline-avianca', 'aircraft-b787-01'),
    ('airline-latam', 'aircraft-a321-01'),
    ('airline-latam', 'aircraft-a320-03'),
    ('airline-wingo', 'aircraft-b737-01');

-- Leg ids per airline
INSERT INTO airline_legs (airline_id, leg_id) VALUES
    ('airline-avianca', 'leg-bog-mde-01'),
    ('airline-avianca', 'leg-mde-ctg-01'),
    ('airline-latam', 'leg-bog-cli-01');

-- Legs (available_seats matches the AVAILABLE seats of each leg)
-- aircraft-a320-01 flies two legs; available seats: bog-mde 3, mde-ctg 1, bog-cli 0
INSERT INTO legs (id, origin_airport, destination_airport, departure_time, arrival_time,
                  aircraft_id, airline_id, price, available_seats) VALUES
    ('leg-bog-mde-01', 'BOG', 'MDE', '2026-12-01 08:00:00', '2026-12-01 09:00:00',
     'aircraft-a320-01', 'airline-avianca', 250000, 3),
    ('leg-mde-ctg-01', 'MDE', 'CTG', '2026-12-01 11:00:00', '2026-12-01 12:15:00',
     'aircraft-a320-01', 'airline-avianca', 300000, 1),
    ('leg-bog-cli-01', 'BOG', 'CLO', '2026-12-02 07:00:00', '2026-12-02 08:00:00',
     'aircraft-a321-01', 'airline-latam', 200000, 0);

-- Luggage per leg
INSERT INTO luggages (id, price, leg_id) VALUES
    ('luggage-bog-mde-01', 80000, 'leg-bog-mde-01'),
    ('luggage-mde-ctg-01', 90000, 'leg-mde-ctg-01'),
    ('luggage-bog-cli-01', 70000, 'leg-bog-cli-01');

-- Seats per leg. A seat taken in a booking has booking_id and passenger_id.
-- The OCCUPIED seats below have no booking: they are blocked by the airline.
INSERT INTO leg_seats (id, seat_code, seat_status, booking_id, passenger_id, leg_id) VALUES
    ('seat-bog-mde-1A', '1A', 'AVAILABLE', NULL, NULL, 'leg-bog-mde-01'),
    ('seat-bog-mde-1B', '1B', 'AVAILABLE', NULL, NULL, 'leg-bog-mde-01'),
    ('seat-bog-mde-1C', '1C', 'OCCUPIED', NULL, NULL, 'leg-bog-mde-01'),
    ('seat-bog-mde-2A', '2A', 'AVAILABLE', NULL, NULL, 'leg-bog-mde-01'),
    ('seat-mde-ctg-1A', '1A', 'OCCUPIED', NULL, NULL, 'leg-mde-ctg-01'),
    ('seat-mde-ctg-1B', '1B', 'AVAILABLE', NULL, NULL, 'leg-mde-ctg-01'),
    ('seat-bog-cli-1A', '1A', 'OCCUPIED', NULL, NULL, 'leg-bog-cli-01');

-- Extra services (description is optional)
-- Extra services (description is optional). A deleted service is no longer offered, but old bookings keep it.
INSERT INTO extra_services (id, name_service, description, national_price, international_price, airline_id, deleted) VALUES
     ('extra-avianca-meal', 'Meal', 'Hot meal on board', 30000, 60000, 'airline-avianca', FALSE),
     ('extra-avianca-wifi', 'Wifi', NULL, 20000, 40000, 'airline-avianca', FALSE),
     ('extra-latam-priority', 'Priority boarding', 'Board before other passengers', 25000, 50000, 'airline-latam', FALSE),
     ('extra-latam-pet', 'Pet in cabin', 'Small pet in the cabin', 80000, 150000, 'airline-latam', TRUE);

-- =====================================================================
-- Seat-map data for manual testing (seat selector in the front).
-- Only LATAM and Wingo get new legs: the tests pin Avianca's legs, the
-- airline ranking, the extra services and the seats of leg-bog-mde-01.
-- Each leg has 10 rows x 6 columns (A-F) = 60 seats. Some are OCCUPIED
-- (blocked by the airline, no booking) so the map shows both states.
--   LATAM  aircraft-a320-03: BOG -> AXM 2026-12-15, AXM -> BOG 2026-12-20
--   Wingo  aircraft-b737-01: BOG -> CTG 2026-12-15, CTG -> BOG 2026-12-20
-- =====================================================================

INSERT INTO airline_legs (airline_id, leg_id) VALUES
    ('airline-latam', 'leg-bog-axm-01'),
    ('airline-latam', 'leg-axm-bog-01'),
    ('airline-wingo', 'leg-bog-ctg-01'),
    ('airline-wingo', 'leg-ctg-bog-01');

-- available_seats = 60 minus the blocked seats of each leg (see leg_seats below)
INSERT INTO legs (id, origin_airport, destination_airport, departure_time, arrival_time,
                  aircraft_id, airline_id, price, available_seats) VALUES
    ('leg-bog-axm-01', 'BOG', 'AXM', '2026-12-15 07:30:00', '2026-12-15 08:30:00',
     'aircraft-a320-03', 'airline-latam', 180000, 52),
    ('leg-axm-bog-01', 'AXM', 'BOG', '2026-12-20 18:00:00', '2026-12-20 19:00:00',
     'aircraft-a320-03', 'airline-latam', 190000, 54),
    ('leg-bog-ctg-01', 'BOG', 'CTG', '2026-12-15 10:00:00', '2026-12-15 11:30:00',
     'aircraft-b737-01', 'airline-wingo', 150000, 50),
    ('leg-ctg-bog-01', 'CTG', 'BOG', '2026-12-20 13:00:00', '2026-12-20 14:30:00',
     'aircraft-b737-01', 'airline-wingo', 160000, 56);

INSERT INTO luggages (id, price, leg_id) VALUES
    ('luggage-bog-axm-01', 65000, 'leg-bog-axm-01'),
    ('luggage-axm-bog-01', 65000, 'leg-axm-bog-01'),
    ('luggage-bog-ctg-01', 55000, 'leg-bog-ctg-01'),
    ('luggage-ctg-bog-01', 55000, 'leg-ctg-bog-01');

-- 60 seats per leg (rows 1-10, columns A-F), ids like 'seat-bog-axm-10A'. All start AVAILABLE.
INSERT INTO leg_seats (id, seat_code, seat_status, booking_id, passenger_id, leg_id)
SELECT 'seat-' || l.prefix || '-' || r.X || c.col, r.X || c.col, 'AVAILABLE', NULL, NULL, l.leg_id
FROM (VALUES ('leg-bog-axm-01', 'bog-axm'), ('leg-axm-bog-01', 'axm-bog'),
             ('leg-bog-ctg-01', 'bog-ctg'), ('leg-ctg-bog-01', 'ctg-bog')) AS l(leg_id, prefix)
CROSS JOIN SYSTEM_RANGE(1, 10) AS r
CROSS JOIN (VALUES ('A'), ('B'), ('C'), ('D'), ('E'), ('F')) AS c(col);

-- Seats blocked by the airline (OCCUPIED, no booking). The counts match available_seats above.
UPDATE leg_seats SET seat_status = 'OCCUPIED' WHERE id IN (
    -- bog-axm: 8 blocked -> 52 available
    'seat-bog-axm-1A', 'seat-bog-axm-1B', 'seat-bog-axm-1C', 'seat-bog-axm-3D',
    'seat-bog-axm-5E', 'seat-bog-axm-5F', 'seat-bog-axm-8C', 'seat-bog-axm-10A',
    -- axm-bog: 6 blocked -> 54 available
    'seat-axm-bog-2B', 'seat-axm-bog-4C', 'seat-axm-bog-4D', 'seat-axm-bog-7A',
    'seat-axm-bog-9E', 'seat-axm-bog-9F',
    -- bog-ctg: 10 blocked -> 50 available
    'seat-bog-ctg-1A', 'seat-bog-ctg-1F', 'seat-bog-ctg-2A', 'seat-bog-ctg-2F',
    'seat-bog-ctg-4B', 'seat-bog-ctg-4C', 'seat-bog-ctg-6D', 'seat-bog-ctg-6E',
    'seat-bog-ctg-10C', 'seat-bog-ctg-10D',
    -- ctg-bog: 4 blocked -> 56 available
    'seat-ctg-bog-3A', 'seat-ctg-bog-3F', 'seat-ctg-bog-7C', 'seat-ctg-bog-7D'
);


-- =====================================================================
-- Flights with layovers (LATAM). The search joins legs of the same airline
-- with a layover of 1 to 12 hours, so together with the legs above:
--   BOG -> CTG 2026-12-15: Wingo direct; LATAM BOG-AXM-CTG (1 layover, 2 h in AXM);
--                          LATAM BOG-PEI-MDE-CTG (2 layovers: 1.5 h in PEI, 1.75 h in MDE)
--   BOG -> MDE 2026-12-15: LATAM BOG-PEI-MDE (1 layover)
--   CTG -> BOG 2026-12-20: Wingo direct; LATAM CTG-MDE-BOG (1 layover, 1.75 h in MDE)
-- aircraft-a320-03 continues from AXM; aircraft-a321-01 flies the PEI/MDE chain.
-- =====================================================================

INSERT INTO airline_legs (airline_id, leg_id) VALUES
    ('airline-latam', 'leg-axm-ctg-01'),
    ('airline-latam', 'leg-bog-pei-01'),
    ('airline-latam', 'leg-pei-mde-01'),
    ('airline-latam', 'leg-mde-ctg-02'),
    ('airline-latam', 'leg-ctg-mde-01'),
    ('airline-latam', 'leg-mde-bog-01');

-- available_seats = 60 minus the blocked seats of each leg (see the UPDATE below)
INSERT INTO legs (id, origin_airport, destination_airport, departure_time, arrival_time,
                  aircraft_id, airline_id, price, available_seats) VALUES
    ('leg-axm-ctg-01', 'AXM', 'CTG', '2026-12-15 10:30:00', '2026-12-15 12:00:00',
     'aircraft-a320-03', 'airline-latam', 210000, 56),
    ('leg-bog-pei-01', 'BOG', 'PEI', '2026-12-15 06:00:00', '2026-12-15 07:00:00',
     'aircraft-a321-01', 'airline-latam', 140000, 58),
    ('leg-pei-mde-01', 'PEI', 'MDE', '2026-12-15 08:30:00', '2026-12-15 09:15:00',
     'aircraft-a321-01', 'airline-latam', 120000, 60),
    ('leg-mde-ctg-02', 'MDE', 'CTG', '2026-12-15 11:00:00', '2026-12-15 12:15:00',
     'aircraft-a321-01', 'airline-latam', 170000, 54),
    ('leg-ctg-mde-01', 'CTG', 'MDE', '2026-12-20 09:00:00', '2026-12-20 10:15:00',
     'aircraft-a321-01', 'airline-latam', 170000, 57),
    ('leg-mde-bog-01', 'MDE', 'BOG', '2026-12-20 12:00:00', '2026-12-20 13:00:00',
     'aircraft-a321-01', 'airline-latam', 160000, 55);

INSERT INTO luggages (id, price, leg_id) VALUES
    ('luggage-axm-ctg-01', 65000, 'leg-axm-ctg-01'),
    ('luggage-bog-pei-01', 60000, 'leg-bog-pei-01'),
    ('luggage-pei-mde-01', 60000, 'leg-pei-mde-01'),
    ('luggage-mde-ctg-02', 65000, 'leg-mde-ctg-02'),
    ('luggage-ctg-mde-01', 65000, 'leg-ctg-mde-01'),
    ('luggage-mde-bog-01', 60000, 'leg-mde-bog-01');

-- 60 seats per leg (rows 1-10, columns A-F), ids like 'seat-axm-ctg-10A'. All start AVAILABLE.
INSERT INTO leg_seats (id, seat_code, seat_status, booking_id, passenger_id, leg_id)
SELECT 'seat-' || l.prefix || '-' || r.X || c.col, r.X || c.col, 'AVAILABLE', NULL, NULL, l.leg_id
FROM (VALUES ('leg-axm-ctg-01', 'axm-ctg'), ('leg-bog-pei-01', 'bog-pei'),
             ('leg-pei-mde-01', 'pei-mde'), ('leg-mde-ctg-02', 'mde-ctg-02'),
             ('leg-ctg-mde-01', 'ctg-mde'), ('leg-mde-bog-01', 'mde-bog')) AS l(leg_id, prefix)
CROSS JOIN SYSTEM_RANGE(1, 10) AS r
CROSS JOIN (VALUES ('A'), ('B'), ('C'), ('D'), ('E'), ('F')) AS c(col);

-- Seats blocked by the airline (OCCUPIED, no booking). pei-mde has none.
UPDATE leg_seats SET seat_status = 'OCCUPIED' WHERE id IN (
    -- axm-ctg: 4 blocked -> 56 available
    'seat-axm-ctg-1A', 'seat-axm-ctg-1B', 'seat-axm-ctg-6E', 'seat-axm-ctg-6F',
    -- bog-pei: 2 blocked -> 58 available
    'seat-bog-pei-2C', 'seat-bog-pei-2D',
    -- mde-ctg-02: 6 blocked -> 54 available
    'seat-mde-ctg-02-1A', 'seat-mde-ctg-02-1F', 'seat-mde-ctg-02-3B', 'seat-mde-ctg-02-3C',
    'seat-mde-ctg-02-9D', 'seat-mde-ctg-02-9E',
    -- ctg-mde: 3 blocked -> 57 available
    'seat-ctg-mde-4A', 'seat-ctg-mde-4B', 'seat-ctg-mde-10F',
    -- mde-bog: 5 blocked -> 55 available
    'seat-mde-bog-1C', 'seat-mde-bog-1D', 'seat-mde-bog-5A', 'seat-mde-bog-7E', 'seat-mde-bog-7F'
);
