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
    ('leg-bog-mde-01', 'EL_DORADO', 'JOSE_MARIA_CORDOVA', '2026-12-01 08:00:00', '2026-12-01 09:00:00',
     'aircraft-a320-01', 'airline-avianca', 250000, 3),
    ('leg-mde-ctg-01', 'JOSE_MARIA_CORDOVA', 'RAFAEL_NUNEZ', '2026-12-01 11:00:00', '2026-12-01 12:15:00',
     'aircraft-a320-01', 'airline-avianca', 300000, 1),
    ('leg-bog-cli-01', 'EL_DORADO', 'ALFONSO_BONILLA_ARAGON', '2026-12-02 07:00:00', '2026-12-02 08:00:00',
     'aircraft-a321-01', 'airline-latam', 200000, 0);

-- Luggage per leg
INSERT INTO luggages (id, price, leg_id) VALUES
    ('luggage-bog-mde-01', 80000, 'leg-bog-mde-01'),
    ('luggage-mde-ctg-01', 90000, 'leg-mde-ctg-01'),
    ('luggage-bog-cli-01', 70000, 'leg-bog-cli-01');

-- Seats per leg (passenger_id is set only on OCCUPIED seats)
INSERT INTO leg_seats (id, seat_code, seat_status, passenger_id, leg_id) VALUES
    ('seat-bog-mde-1A', '1A', 'AVAILABLE', NULL, 'leg-bog-mde-01'),
    ('seat-bog-mde-1B', '1B', 'AVAILABLE', NULL, 'leg-bog-mde-01'),
    ('seat-bog-mde-1C', '1C', 'OCCUPIED', 'passenger-01', 'leg-bog-mde-01'),
    ('seat-bog-mde-2A', '2A', 'AVAILABLE', NULL, 'leg-bog-mde-01'),
    ('seat-mde-ctg-1A', '1A', 'OCCUPIED', 'passenger-02', 'leg-mde-ctg-01'),
    ('seat-mde-ctg-1B', '1B', 'AVAILABLE', NULL, 'leg-mde-ctg-01'),
    ('seat-bog-cli-1A', '1A', 'OCCUPIED', 'passenger-03', 'leg-bog-cli-01');

-- Extra services (description is optional)
INSERT INTO extra_services (id, name_service, description, national_price, international_price, airline_id) VALUES
    ('extra-avianca-meal', 'Meal', 'Hot meal on board', 30000, 60000, 'airline-avianca'),
    ('extra-avianca-wifi', 'Wifi', NULL, 20000, 40000, 'airline-avianca'),
    ('extra-latam-priority', 'Priority boarding', 'Board before other passengers', 25000, 50000, 'airline-latam');
