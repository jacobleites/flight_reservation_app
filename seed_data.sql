USE flight_reservation_system;

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
(1005, 'UA', 101, '08:20:00', '11:55:00', 'DEN', 'EWR'),
(5005, 'UA', 101, '13:10:00', '16:40:00', 'EWR', 'DEN'),
(1006, 'UA', 102, '13:35:00', '15:25:00', 'LAX', 'DEN'),
(5006, 'UA', 102, '17:00:00', '20:45:00', 'DEN', 'LAX'),
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
(1405, 'AA', 301, '09:25:00', '12:05:00', 'ORD', 'LGA'),
(5405, 'AA', 301, '13:10:00', '15:50:00', 'LGA', 'ORD'),
(1406, 'AA', 302, '13:35:00', '16:05:00', 'LAX', 'ORD'),
(5406, 'AA', 302, '17:05:00', '19:30:00', 'ORD', 'LAX'),
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
    fare,
    status
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
    ROUND(120 + ((f.flight_num % 10) * 18) + (d.day_offset * 4), 2) AS fare,
    CASE
        WHEN d.day_offset = 0 THEN 'Scheduled'
        WHEN d.day_offset = 1 AND f.flight_num % 4 = 0 THEN 'Delayed'
        WHEN d.day_offset = 2 AND f.flight_num % 7 = 0 THEN 'Cancelled'
        ELSE 'Scheduled'
    END AS status
FROM Flight f
JOIN Aircraft a ON a.aircraft_id = f.aircraft_id
JOIN (
    SELECT 0 AS day_offset UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
    UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
) d;

INSERT INTO Ticket_Class (ticket_class, change_fee) VALUES
('Economy', 50.00),
('Business', 0.00),
('First', 0.00);

INSERT INTO Flight_Class_Inventory (instance_id, ticket_class, total_seats, available_seats, base_price)
SELECT
    fi.instance_id,
    classes.ticket_class,
    CASE classes.ticket_class
        WHEN 'Economy' THEN a.economy_class
        WHEN 'Business' THEN a.business_class
        WHEN 'First' THEN a.first_class
    END AS total_seats,
    CASE classes.ticket_class
        WHEN 'Economy' THEN a.economy_class
        WHEN 'Business' THEN a.business_class
        WHEN 'First' THEN a.first_class
    END AS available_seats,
    CASE classes.ticket_class
        WHEN 'Economy' THEN fi.fare
        WHEN 'Business' THEN ROUND(fi.fare * 2.00, 2)
        WHEN 'First' THEN ROUND(fi.fare * 3.00, 2)
    END AS base_price
FROM Flight_Instance fi
JOIN Aircraft a ON a.aircraft_id = fi.aircraft_id
JOIN (
    SELECT 'Economy' AS ticket_class
    UNION ALL SELECT 'Business'
    UNION ALL SELECT 'First'
) classes;

INSERT INTO Employee (employee_ssn, firstName, lastName, acc_username, acc_password, role) VALUES
('22222222222', 'Casey', 'Morgan', 'customerrep1', 'test', 'CUSTOMER_REPRESENTATIVE'),
('33333333333', 'Jordan', 'Patel', 'customerrep2', 'test', 'CUSTOMER_REPRESENTATIVE'),
('11111111111', 'Alex', 'Admin', 'admin', 'test', 'ADMIN');

INSERT INTO Customer (customer_ssn, email, gender, dob, firstName, lastName, phone, account_id, username, acc_password) VALUES
('111-11-1111', 'jason.billings@example.com', 'male', '2004-08-10', 'Jason', 'Billings', '201-555-0101', 1001, 'jasonb', 'password'),
('222-22-2222', 'maya.chen@example.com', 'female', '1998-03-22', 'Maya', 'Chen', '201-555-0102', 1002, 'mayac', 'password'),
('333-33-3333', 'liam.ortiz@example.com', 'male', '1995-11-12', 'Liam', 'Ortiz', '201-555-0103', 1003, 'liamo', 'password'),
('444-44-4444', 'sofia.khan@example.com', 'female', '2001-01-08', 'Sofia', 'Khan', '201-555-0104', 1004, 'sofiak', 'password'),
('555-55-5555', 'ethan.reed@example.com', 'male', '1992-07-30', 'Ethan', 'Reed', '201-555-0105', 1005, 'ethanr', 'password'),
('666-66-6666', 'ava.murphy@example.com', 'female', '1999-09-14', 'Ava', 'Murphy', '201-555-0106', 1006, 'avam', 'password'),
('777-77-7777', 'noah.garcia@example.com', 'male', '1996-12-05', 'Noah', 'Garcia', '201-555-0107', 1007, 'noahg', 'password'),
('888-88-8888', 'mia.patel@example.com', 'female', '2000-06-18', 'Mia', 'Patel', '201-555-0108', 1008, 'miap', 'password'),
('999-99-9999', 'elijah.ross@example.com', 'male', '1994-10-27', 'Elijah', 'Ross', '201-555-0109', 1009, 'elijahr', 'password'),
('123-45-6789', 'zoe.foster@example.com', 'female', '1997-04-03', 'Zoe', 'Foster', '201-555-0110', 1010, 'zoef', 'password');

INSERT INTO Reservations (reservation_id, customer_ssn, reservation_date, status, booking_fee, total_price, trip_type) VALUES
(2001, '111-11-1111', '2026-05-02 08:15:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2002, '111-11-1111', '2026-05-03 14:20:00', 'Booked', 25.00, 0.00, 'Round_Trip'),
(2003, '222-22-2222', '2026-05-03 09:10:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2004, '222-22-2222', '2026-05-04 10:35:00', 'Booked', 25.00, 0.00, 'Round_Trip'),
(2005, '333-33-3333', '2026-05-05 11:45:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2006, '333-33-3333', '2026-05-02 07:55:00', 'Booked', 25.00, 0.00, 'Round_Trip'),
(2007, '444-44-4444', '2026-05-06 13:30:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2008, '444-44-4444', '2026-05-07 16:40:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2009, '555-55-5555', '2026-05-01 06:05:00', 'Booked', 25.00, 0.00, 'Round_Trip'),
(2010, '555-55-5555', '2026-05-05 18:25:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2011, '666-66-6666', '2026-05-03 12:10:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2012, '666-66-6666', '2026-05-04 15:20:00', 'Booked', 25.00, 0.00, 'Round_Trip'),
(2013, '777-77-7777', '2026-05-02 11:50:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2014, '777-77-7777', '2026-05-05 09:00:00', 'Booked', 25.00, 0.00, 'Round_Trip'),
(2015, '888-88-8888', '2026-05-08 08:35:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2016, '888-88-8888', '2026-05-04 19:05:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2017, '999-99-9999', '2026-05-03 07:30:00', 'Booked', 25.00, 0.00, 'Round_Trip'),
(2018, '999-99-9999', '2026-05-02 20:10:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2019, '123-45-6789', '2026-05-08 06:50:00', 'Booked', 25.00, 0.00, 'One_Way'),
(2020, '123-45-6789', '2026-05-01 17:45:00', 'Booked', 25.00, 0.00, 'Round_Trip');

INSERT INTO Ticket (reservation_id, instance_id, segment_num, fare, pay_date, special_meal, direction, ticket_class, status)
SELECT
    p.reservation_id,
    fi.instance_id,
    p.segment_num,
    fci.base_price,
    DATE_ADD(fi.dep_datetime, INTERVAL -2 DAY),
    p.special_meal,
    p.direction,
    p.ticket_class,
    'Booked'
FROM (
    SELECT 2001 AS reservation_id, 'UA' AS airline_id, 1001 AS flight_num, '2026-05-02' AS dep_date, 1 AS segment_num, FALSE AS special_meal, 'Outbound' AS direction, 'Economy' AS ticket_class
    UNION ALL SELECT 2002, 'AA', 1402, '2026-05-03', 1, TRUE, 'Outbound', 'Business'
    UNION ALL SELECT 2002, 'AA', 5402, '2026-05-06', 1, TRUE, 'Return', 'Business'
    UNION ALL SELECT 2003, 'DL', 1204, '2026-05-03', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2004, 'B6', 1601, '2026-05-04', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2004, 'B6', 5601, '2026-05-07', 1, FALSE, 'Return', 'Economy'
    UNION ALL SELECT 2005, 'UA', 1003, '2026-05-05', 1, TRUE, 'Outbound', 'First'
    UNION ALL SELECT 2006, 'DL', 1202, '2026-05-02', 1, FALSE, 'Outbound', 'Business'
    UNION ALL SELECT 2006, 'DL', 5202, '2026-05-08', 1, FALSE, 'Return', 'Business'
    UNION ALL SELECT 2007, 'AA', 1404, '2026-05-06', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2008, 'B6', 1603, '2026-05-07', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2009, 'UA', 1005, '2026-05-01', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2009, 'UA', 5005, '2026-05-09', 1, FALSE, 'Return', 'Economy'
    UNION ALL SELECT 2010, 'DL', 5204, '2026-05-05', 1, TRUE, 'Outbound', 'First'
    UNION ALL SELECT 2011, 'AA', 5405, '2026-05-03', 1, FALSE, 'Outbound', 'Business'
    UNION ALL SELECT 2012, 'UA', 1002, '2026-05-04', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2012, 'UA', 5002, '2026-05-06', 1, FALSE, 'Return', 'Economy'
    UNION ALL SELECT 2013, 'B6', 1604, '2026-05-02', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2014, 'DL', 1201, '2026-05-05', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2014, 'DL', 5201, '2026-05-07', 1, FALSE, 'Return', 'Economy'
    UNION ALL SELECT 2015, 'AA', 1401, '2026-05-08', 1, TRUE, 'Outbound', 'First'
    UNION ALL SELECT 2016, 'UA', 5006, '2026-05-04', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2017, 'B6', 1602, '2026-05-03', 1, FALSE, 'Outbound', 'Business'
    UNION ALL SELECT 2017, 'B6', 5602, '2026-05-09', 1, FALSE, 'Return', 'Business'
    UNION ALL SELECT 2018, 'AA', 5406, '2026-05-02', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2019, 'UA', 1004, '2026-05-08', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2020, 'B6', 1601, '2026-05-01', 1, FALSE, 'Outbound', 'Economy'
    UNION ALL SELECT 2020, 'B6', 5601, '2026-05-10', 1, FALSE, 'Return', 'Economy'
) p
JOIN Flight_Instance fi
    ON fi.airline_id = p.airline_id
    AND fi.flight_num = p.flight_num
    AND DATE(fi.dep_datetime) = p.dep_date
JOIN Flight_Class_Inventory fci
    ON fci.instance_id = fi.instance_id
    AND fci.ticket_class = p.ticket_class;

UPDATE Reservations r
JOIN (
    SELECT reservation_id, ROUND(SUM(fare), 2) AS fare_total
    FROM Ticket
    WHERE status = 'Booked'
    GROUP BY reservation_id
) t ON t.reservation_id = r.reservation_id
SET r.total_price = ROUND(r.booking_fee + t.fare_total, 2)
WHERE r.reservation_id BETWEEN 2001 AND 2020;

UPDATE Flight_Class_Inventory fci
LEFT JOIN (
    SELECT instance_id, ticket_class, COUNT(*) AS booked_count
    FROM Ticket
    WHERE status = 'Booked'
    GROUP BY instance_id, ticket_class
) bt
    ON bt.instance_id = fci.instance_id
    AND bt.ticket_class = fci.ticket_class
SET fci.available_seats = GREATEST(fci.total_seats - COALESCE(bt.booked_count, 0), 0);
