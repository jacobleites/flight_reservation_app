package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Ticket;

public class TicketDAO {

    public boolean createTicket(Ticket ticket) {
        String sql = "INSERT INTO Ticket (reservation_id, airline_id, flight_num, segment_num, seat_num, fare, pay_date, special_meal, direction, ticket_class) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticket.getReservationId());
            ps.setString(2, ticket.getAirlineId());
            ps.setInt(3, ticket.getFlightNum());
            ps.setInt(4, ticket.getSegmentNum());
            ps.setString(5, ticket.getSeatNum());
            ps.setFloat(6, ticket.getFare());
            ps.setString(7, ticket.getPayDate());
            ps.setBoolean(8, ticket.getSpecialMeal());
            ps.setString(9, ticket.getDirection());
            ps.setString(10, ticket.getTicketClass());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Ticket> getTicketsByFlightNum(String airlineId, int flightNum) {
        String sql = "SELECT ticket_num, reservation_id, airline_id, flight_num, segment_num, seat_num, fare, pay_date, special_meal, direction, ticket_class " +
                "FROM Ticket WHERE airline_id = ? AND flight_num = ?";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airlineId);
            ps.setInt(2, flightNum);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapTicket(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }

    public List<Ticket> getTicketsForReservation(int reservationId) {
        String sql = "SELECT ticket_num, reservation_id, airline_id, flight_num, segment_num, seat_num, fare, pay_date, special_meal, direction, ticket_class " +
                "FROM Ticket WHERE reservation_id = ?";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapTicket(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tickets;
    }
    
    // search for tickets by ticket_num
    public Ticket getTicketByNumber(int ticketNum) {
        String sql = "SELECT ticket_num, reservation_id, airline_id, flight_num, segment_num, seat_num, fare, pay_date, special_meal, direction, ticket_class " +
                "FROM Ticket WHERE ticket_num = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketNum);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTicket(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateTicket(Ticket ticket) {
        String sql = "UPDATE Ticket SET reservation_id = ?, airline_id = ?, flight_num = ?, segment_num = ?, seat_num = ?, " +
                "fare = ?, pay_date = ?, special_meal = ?, direction = ?, ticket_class = ? WHERE ticket_num = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticket.getReservationId());
            ps.setString(2, ticket.getAirlineId());
            ps.setInt(3, ticket.getFlightNum());
            ps.setInt(4, ticket.getSegmentNum());
            ps.setString(5, ticket.getSeatNum());
            ps.setFloat(6, ticket.getFare());
            ps.setString(7, ticket.getPayDate());
            ps.setBoolean(8, ticket.getSpecialMeal());
            ps.setString(9, ticket.getDirection());
            ps.setString(10, ticket.getTicketClass());
            ps.setInt(11, ticket.getTicketNum());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // helper method to convert sql row into an object
    private Ticket mapTicket(ResultSet rs) throws SQLException {
        return new Ticket(
                rs.getInt("ticket_num"),
                rs.getInt("reservation_id"),
                rs.getString("airline_id"),
                rs.getInt("flight_num"),
                rs.getInt("segment_num"),
                rs.getString("seat_num"),
                rs.getFloat("fare"),
                rs.getString("pay_date"),
                rs.getBoolean("special_meal"),
                rs.getString("direction"),
                rs.getString("ticket_class")
        );
    }
}
