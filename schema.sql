CREATE TABLE Airport (
	airport_id char(3), #changed airport id to their respective 3 character label
    airport_name varchar(100),
    airport_city varchar(100),
    PRIMARY KEY(airport_id)
);

CREATE TABLE Airline (
	airline_id char(2), # changed this datatype to char 2 (example, united is UA)
    airline_name varchar(100),
    PRIMARY KEY(airline_id) # dropped the aircraft_id in this primary key
);

CREATE TABLE Aircraft ( 
	airline_id char(2) NOT NULL, # airline that owns this aircraft 
    aircraft_id int,
    capacity int,
    economy_class int, # number of econonmy class seats on aircraft
    business_class int, # number of buisness class seats on aircraft
    first_class int, # of first class seats on aircraft
    model varchar(100),
    PRIMARY KEY (aircraft_id),
    FOREIGN KEY (airline_id) REFERENCES Airline(airline_id)
);

CREATE TABLE Operates ( #removed aircraft_id from operates
	airport_id char(3),
    airline_id char(2),
    PRIMARY KEY(airport_id, airline_id),
    FOREIGN KEY (airport_id) REFERENCES Airport(airport_id),
    FOREIGN KEY (airline_id) REFERENCES Airline(airline_id)
);

CREATE TABLE Flight ( #removed dotw_op entirely, will instead generate flight_instances of flights on certain dates.
	flight_num int,
    airline_id CHAR(2) NOT NULL,
    aircraft_id int NOT NULL,
    dep_time TIME,
    arr_time TIME,
    arr_airport char(3) NOT NULL,
    dep_airport char(3) NOT NULL,
    PRIMARY KEY (airline_id, flight_num), # removed aircraft_id from the primary key, unnecssary
    FOREIGN KEY (airline_id) REFERENCES Airline(airline_id),
    FOREIGN KEY (aircraft_id) REFERENCES Aircraft(aircraft_id),
    FOREIGN KEY (arr_airport) REFERENCES Airport(airport_id), # added this foreign key referencing airport
    FOREIGN KEY (dep_airport) REFERENCES Airport(airport_id) # added this foreign key referencing airport
); 

CREATE TABLE Flight_Instance (
	instance_id int AUTO_INCREMENT,
    airline_id CHAR(2),
	flight_num INT NOT NULL,
    dep_datetime DATETIME NOT NULL,
    arr_datetime DATETIME NOT NULL,
    aircraft_id INT NOT NULL,
    fare DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    status ENUM('Scheduled', 'Cancelled', 'Delayed', 'Completed') DEFAULT 'Scheduled',
    PRIMARY KEY (instance_id),
    FOREIGN KEY (airline_id, flight_num) REFERENCES Flight(airline_id, flight_num),
    FOREIGN KEY (aircraft_id) REFERENCES Aircraft(aircraft_id),
	UNIQUE (airline_id, flight_num, dep_datetime)
);


CREATE TABLE DomesticFlight ( # not neccesary to have this table make references to everything, all that matters is flights 
	airline_id CHAR(2) NOT NULL,
    flight_num int NOT NULL,
    PRIMARY KEY (airline_id, flight_num),
    FOREIGN KEY (airline_id, flight_num) REFERENCES Flight(airline_id, flight_num)
		ON DELETE CASCADE 
);

CREATE TABLE InternationalFlight ( # not neccesary to have this table make references to everything, all that matters is flights 
	airline_id CHAR(2) NOT NULL,
    flight_num int NOT NULL, 
    PRIMARY KEY (airline_id, flight_num),
    FOREIGN KEY (airline_id, flight_num) REFERENCES Flight(airline_id, flight_num)
		ON DELETE CASCADE
);

CREATE TABLE Customer (
	customer_id int auto_increment UNIQUE,
    customer_ssn char(11),
    email varchar(30),
    gender varchar(10),
    dob date,
    firstName varchar(45), # broke name into first and last name
    lastName varchar(45), # ^^^
    phone varchar(13),
    account_id int UNIQUE,
    username varchar(20) UNIQUE,
    acc_password varchar(30),
    PRIMARY KEY (customer_ssn)
);

CREATE TABLE Reservations ( # added reservations table to keep track of customer flight history
    reservation_id int AUTO_INCREMENT UNIQUE,
	customer_ssn char(11) NOT NULL,
	reservation_date DATETIME DEFAULT CURRENT_TIMESTAMP,
	status ENUM('Booked', 'Cancelled') NOT NULL DEFAULT 'Booked',
    booking_fee DECIMAL (8,2) NOT NULL DEFAULT (0.00),
    total_price DECIMAL (10,2),
    trip_type ENUM('One_Way', 'Round_Trip') NOT NULL, # moved trip_type (round/oneway) into reservations table 
    PRIMARY KEY (reservation_id),
    FOREIGN KEY (customer_ssn) REFERENCES Customer(customer_ssn)
);

CREATE TABLE Ticket_Class (
	ticket_class VARCHAR(20),
    change_fee DECIMAL(10,2) NOT NULL,
    PRIMARY KEY(ticket_class)
);

CREATE TABLE Flight_Class_Inventory (
    inventory_id INT AUTO_INCREMENT,
    instance_id INT NOT NULL,
    ticket_class VARCHAR(20) NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (inventory_id),
    FOREIGN KEY (instance_id) REFERENCES Flight_Instance(instance_id) ON DELETE CASCADE,
    FOREIGN KEY (ticket_class) REFERENCES Ticket_Class(ticket_class),
    UNIQUE (instance_id, ticket_class)
);

CREATE TABLE Ticket ( # replaced sequence_num with seqment_num; added reservation table to keep track of all flight information, removed seat_num
	ticket_num int AUTO_INCREMENT,
    reservation_id int NOT NULL,
    instance_id int NOT NULL, # references flight instance, so we can remove airline_id, flight_num and flight_date.
    segment_num int NOT NULL,
    fare DECIMAL(10, 2),
    pay_date datetime, # removed pay_time and just storing this as datetime
    special_meal boolean,
    direction ENUM('Outbound', 'Return') NOT NULL, # added direction for roundtrip/oneway table tracking.
    ticket_class varchar(20) NOT NULL, # merged class with tickets, 
    status ENUM('Booked', 'Cancelled') NOT NULL DEFAULT 'Booked', # added status
    PRIMARY KEY (ticket_num),
	FOREIGN KEY (reservation_id) REFERENCES Reservations(reservation_id),
    FOREIGN KEY (instance_id) REFERENCES Flight_Instance(instance_id),
    FOREIGN KEY (ticket_class) REFERENCES Ticket_Class(ticket_class),
	UNIQUE (reservation_id, direction, segment_num)
);

CREATE TABLE Employee (
    employee_ssn CHAR(11),
    firstName VARCHAR(30) NOT NULL,
    lastName VARCHAR(30) NOT NULL,
    acc_username VARCHAR(30) NOT NULL UNIQUE,
    acc_password VARCHAR(40) NOT NULL,
    role ENUM('ADMIN', 'CUSTOMER_REPRESENTATIVE') NOT NULL,
    PRIMARY KEY (employee_ssn)
);

# added customerQuestions as employees (service reps) need to be able to answer questions asked by users.
CREATE TABLE Customer_Question (
    question_id INT AUTO_INCREMENT,
    customer_ssn CHAR(11) NOT NULL,
    employee_ssn CHAR(11),
    question_text TEXT NOT NULL,
    response_text TEXT,
    question_status ENUM('OPEN', 'ANSWERED') DEFAULT 'OPEN',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    answered_at DATETIME,
    PRIMARY KEY (question_id),
    FOREIGN KEY (customer_ssn) REFERENCES Customer(customer_ssn),
    FOREIGN KEY (employee_ssn) REFERENCES Employee(employee_ssn)
);

CREATE TABLE Waiting_Line (
	waitlist_id int auto_increment,
    customer_ssn CHAR(11) NOT NULL,
    instance_id int NOT NULL,
    priority_num int,
    time_entered DATETIME default current_timestamp,
	status ENUM('WAITING', 'NOTIFIED', 'BOOKED', 'CANCELLED') DEFAULT 'WAITING',
	PRIMARY KEY (waitlist_id),
    FOREIGN KEY (customer_ssn) REFERENCES Customer(customer_ssn),
    FOREIGN KEY (instance_id) REFERENCES Flight_Instance(instance_id),
    UNIQUE (customer_ssn, instance_id)
);
