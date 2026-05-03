USE flight_reservation;

-- Core reference data required by Flight and Flight_Instance FKs
INSERT INTO Airport (airport_id, airport_name, airport_city) VALUES
('EWR', 'Newark Liberty International Airport', 'Newark'),
('JFK', 'John F. Kennedy International Airport', 'New York'),
('LGA', 'LaGuardia Airport', 'New York'),
('BOS', 'Logan International Airport', 'Boston'),
('IAD', 'Dulles International Airport', 'Washington'),
('ORD', 'O''Hare International Airport', 'Chicago'),
('ATL', 'Hartsfield-Jackson Atlanta International Airport', 'Atlanta'),
('MIA', 'Miami International Airport', 'Miami'),
('DFW', 'Dallas/Fort Worth International Airport', 'Dallas'),
('DEN', 'Denver International Airport', 'Denver'),
('SFO', 'San Francisco International Airport', 'San Francisco'),
('LAX', 'Los Angeles International Airport', 'Los Angeles'),
('SEA', 'Seattle-Tacoma International Airport', 'Seattle'),
('PHX', 'Phoenix Sky Harbor International Airport', 'Phoenix');

INSERT INTO Airline (airline_id, airline_name) VALUES
('UA', 'United Airlines'),
('DL', 'Delta Air Lines'),
('AA', 'American Airlines'),
('B6', 'JetBlue Airways');

INSERT INTO Aircraft (airline_id, aircraft_id, capacity, economy_class, business_class, first_class, model) VALUES
('UA', 101, 166, 138, 20, 8, 'Boeing 737-800'),
('UA', 102, 179, 150, 21, 8, 'Airbus A320neo'),
('DL', 201, 160, 132, 20, 8, 'Boeing 737-900ER'),
('DL', 202, 191, 159, 24, 8, 'Airbus A321neo'),
('AA', 301, 172, 144, 20, 8, 'Boeing 737 MAX 8'),
('AA', 302, 196, 164, 24, 8, 'Airbus A321'),
('B6', 401, 162, 150, 12, 0, 'Airbus A320'),
('B6', 402, 200, 186, 14, 0, 'Airbus A321');

-- A bunch of scheduled flights
INSERT INTO Flight (flight_num, airline_id, aircraft_id, dep_time, arr_time, arr_airport, dep_airport) VALUES
(1001, 'UA', 101, '06:30:00', '08:05:00', 'BOS', 'EWR'),
(5001, 'UA', 101, '09:00:00', '10:35:00', 'EWR', 'BOS'),
(1002, 'UA', 101, '09:00:00', '11:35:00', 'ATL', 'EWR'),
(5002, 'UA', 101, '12:30:00', '15:05:00', 'EWR', 'ATL'),
(1003, 'UA', 102, '13:20:00', '16:10:00', 'MIA', 'EWR'),
(5003, 'UA', 102, '17:10:00', '20:00:00', 'EWR', 'MIA'),
(1004, 'UA', 102, '17:45:00', '20:35:00', 'DFW', 'EWR'),
(5004, 'UA', 102, '21:30:00', '00:20:00', 'EWR', 'DFW'),
(1201, 'DL', 201, '07:10:00', '09:20:00', 'IAD', 'JFK'),
(5201, 'DL', 201, '10:10:00', '12:20:00', 'JFK', 'IAD'),
(1202, 'DL', 201, '10:15:00', '12:55:00', 'ORD', 'JFK'),
(5202, 'DL', 201, '13:45:00', '16:25:00', 'JFK', 'ORD'),
(1203, 'DL', 202, '14:30:00', '17:15:00', 'DEN', 'JFK'),
(5203, 'DL', 202, '18:05:00', '20:50:00', 'JFK', 'DEN'),
(1204, 'DL', 202, '18:00:00', '21:10:00', 'LAX', 'JFK'),
(5204, 'DL', 202, '22:00:00', '01:10:00', 'JFK', 'LAX'),
(1401, 'AA', 301, '06:50:00', '09:55:00', 'MIA', 'LGA'),
(5401, 'AA', 301, '10:45:00', '13:50:00', 'LGA', 'MIA'),
(1402, 'AA', 301, '11:40:00', '14:30:00', 'DFW', 'LGA'),
(5402, 'AA', 301, '15:20:00', '18:10:00', 'LGA', 'DFW'),
(1403, 'AA', 302, '15:10:00', '18:25:00', 'PHX', 'LGA'),
(5403, 'AA', 302, '19:15:00', '22:30:00', 'LGA', 'PHX'),
(1404, 'AA', 302, '19:20:00', '22:40:00', 'SEA', 'LGA'),
(5404, 'AA', 302, '23:30:00', '02:50:00', 'LGA', 'SEA'),
(1601, 'B6', 401, '07:25:00', '10:20:00', 'ATL', 'BOS'),
(5601, 'B6', 401, '11:10:00', '14:05:00', 'BOS', 'ATL'),
(1602, 'B6', 401, '12:00:00', '15:25:00', 'DEN', 'BOS'),
(5602, 'B6', 401, '16:15:00', '19:40:00', 'BOS', 'DEN'),
(1603, 'B6', 402, '16:15:00', '19:30:00', 'SFO', 'BOS'),
(5603, 'B6', 402, '20:20:00', '23:35:00', 'BOS', 'SFO'),
(1604, 'B6', 402, '20:05:00', '23:35:00', 'LAX', 'BOS'),
(5604, 'B6', 402, '00:25:00', '03:55:00', 'BOS', 'LAX');

-- Generate 10 days of instances for each flight (320 total instances)
-- dep_datetime/arr_datetime are computed from flight_date + scheduled times.
INSERT INTO Flight_Instance (
    airline_id,
    flight_num,
    dep_datetime,
    arr_datetime,
    aircraft_id,
    status,
    seats_available
)
SELECT
    f.airline_id,
    f.flight_num,
    TIMESTAMP(DATE_ADD('2026-05-01', INTERVAL d.day_offset DAY), f.dep_time) AS dep_datetime,
    TIMESTAMP(
        DATE_ADD('2026-05-01', INTERVAL d.day_offset + CASE WHEN f.arr_time < f.dep_time THEN 1 ELSE 0 END DAY),
        f.arr_time
    ) AS arr_datetime,
    f.aircraft_id,
    CASE
        WHEN d.day_offset = 0 THEN 'Scheduled'
        WHEN d.day_offset = 1 AND f.flight_num % 4 = 0 THEN 'Delayed'
        WHEN d.day_offset = 2 AND f.flight_num % 7 = 0 THEN 'Cancelled'
        ELSE 'Scheduled'
    END AS status,
    GREATEST(0, a.capacity - (10 + ((f.flight_num + d.day_offset) % 40))) AS seats_available
FROM Flight f
JOIN Aircraft a ON a.aircraft_id = f.aircraft_id
JOIN (
    SELECT 0 AS day_offset UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
) d;

Insert into Employee (employee_ssn, firstName, lastName, acc_username, acc_password, role) Values 
('22222222222', 'customerrep', 'test', 'customerrep', 'test', 'CUSTOMER_REPRESENTATIVE'),
('11111111111', 'admin', 'test', 'admin', 'test', 'ADMIN');