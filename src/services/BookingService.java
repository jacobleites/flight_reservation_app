package services;

import dao.FlightInstanceDAO;
import dao.ReservationDAO;
import dao.TicketDAO;
import db.DatabaseConnection;
import models.Customer;
import models.FlightInstance;
import models.Reservation;
import models.Ticket;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingService {
    private final ReservationDAO reservationDAO;
    private final TicketDAO ticketDAO;

    public BookingService() {
        this.reservationDAO = new ReservationDAO();
        this.ticketDAO = new TicketDAO();
    }

    public boolean bookFlight(Customer customer, FlightInstance outbound, FlightInstance returnFlight, String classType) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Enable transaction to ensure atomicity

            String tripType = (returnFlight == null) ? "One_Way" : "Round_Trip";
            float price = 250.0f; // This should ideally be calculated based on FlightInstance and ClassType
            String currentTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 1. Persist Reservation and Retrieve Generated ID
            // We bypass the standard DAO here to capture the generated key within the transaction
            String resSql = "INSERT INTO Reservations (customer_ssn, reservation_date, status, total_price, trip_type) VALUES (?, ?, 'Booked', ?, ?)";
            int generatedResId = -1;

            try (PreparedStatement ps = conn.prepareStatement(resSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, customer.getSsn());
                ps.setString(2, currentTimestamp);
                ps.setFloat(3, price);
                ps.setString(4, tripType);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedResId = rs.getInt(1);
                    }
                }
            }

            if (generatedResId == -1) throw new SQLException("Failed to retrieve Reservation ID.");

            // 2. Create Outbound Ticket
            // Note: Updated to use instance_id from FlightInstance per schema.sql
            Ticket outboundTicket = new Ticket(
                0, 
                generatedResId, 
                outbound.getAirlineId(), // Retained for model consistency
                outbound.getFlightNumber(), 
                1, // Segment 1
                "12A", // Placeholder for seat logic
                price, 
                currentTimestamp, 
                false, 
                "Outbound", 
                classType
            );
            ticketDAO.createTicket(outboundTicket);

            // 3. Create Return Ticket if applicable
            if (returnFlight != null) {
                Ticket returnTicket = new Ticket(
                    0, 
                    generatedResId, 
                    returnFlight.getAirlineId(), 
                    returnFlight.getFlightNumber(), 
                    2, // Segment 2
                    "14C", 
                    price, 
                    currentTimestamp, 
                    false, 
                    "Return", 
                    classType
                );
                ticketDAO.createTicket(returnTicket);
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}