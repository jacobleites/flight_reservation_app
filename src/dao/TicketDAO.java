package dao;

import db.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import models.Ticket;

public class TicketDAO {

    public int createTicket(
            Connection conn,
            int reservationId,
            int instanceId,
            int segmentNum,
            BigDecimal fare,
            boolean specialMeal,
            String direction,
            String ticketClass
    ) throws SQLException {
        String sql = "INSERT INTO Ticket " +
                "(reservation_id, instance_id, segment_num, fare, pay_date, special_meal, direction, ticket_class, status) " +
                "VALUES (?, ?, ?, ?, NOW(), ?, ?, ?, 'Booked')";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservationId);
            ps.setInt(2, instanceId);
            ps.setInt(3, segmentNum);
            ps.setBigDecimal(4, fare);
            ps.setBoolean(5, specialMeal);
            ps.setString(6, direction);
            ps.setString(7, ticketClass);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create ticket.");
    }

    public boolean createTicket(Ticket ticket) {
        String sql = "INSERT INTO Ticket (reservation_id, instance_id, segment_num, fare, pay_date, special_meal, direction, ticket_class, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticket.getReservationId());
            if (ticket.getInstanceId() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, ticket.getInstanceId());
            }
            ps.setInt(3, ticket.getSegmentNum());
            ps.setBigDecimal(4, ticket.getFareAmount());
            ps.setString(5, ticket.getPayDate());
            ps.setBoolean(6, ticket.getSpecialMeal());
            ps.setString(7, ticket.getDirection());
            ps.setString(8, ticket.getTicketClass());
            ps.setString(9, ticket.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Ticket> getTicketsByFlightNum(String airlineId, int flightNum) {
        String sql =
                "SELECT t.ticket_num, t.reservation_id, t.instance_id, t.segment_num, t.fare, t.pay_date, " +
                "t.special_meal, t.direction, t.ticket_class, t.status, " +
                "f.airline_id, f.flight_num " +
                "FROM Ticket t " +
                "JOIN Flight_Instance fi ON fi.instance_id = t.instance_id " +
                "JOIN Flight f ON f.airline_id = fi.airline_id AND f.flight_num = fi.flight_num " +
                "WHERE f.airline_id = ? AND f.flight_num = ?";
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
        String sql =
                "SELECT t.ticket_num, t.reservation_id, t.instance_id, t.segment_num, t.fare, t.pay_date, " +
                "t.special_meal, t.direction, t.ticket_class, t.status, " +
                "f.airline_id, f.flight_num " +
                "FROM Ticket t " +
                "LEFT JOIN Flight_Instance fi ON fi.instance_id = t.instance_id " +
                "LEFT JOIN Flight f ON f.airline_id = fi.airline_id AND f.flight_num = fi.flight_num " +
                "WHERE t.reservation_id = ?";
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

    public List<Ticket> getTicketsForReservation(Connection conn, int reservationId) throws SQLException {
        String sql =
                "SELECT t.ticket_num, t.reservation_id, t.instance_id, t.segment_num, t.fare, t.pay_date, " +
                "t.special_meal, t.direction, t.ticket_class, t.status, " +
                "f.airline_id, f.flight_num " +
                "FROM Ticket t " +
                "LEFT JOIN Flight_Instance fi ON fi.instance_id = t.instance_id " +
                "LEFT JOIN Flight f ON f.airline_id = fi.airline_id AND f.flight_num = fi.flight_num " +
                "WHERE t.reservation_id = ?";
        List<Ticket> tickets = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tickets.add(mapTicket(rs));
                }
            }
        }
        return tickets;
    }

    public int cancelBookedTicketsByReservation(Connection conn, int reservationId) throws SQLException {
        String sql = "UPDATE Ticket SET status = 'Cancelled' " +
                "WHERE reservation_id = ? AND status = 'Booked'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            return ps.executeUpdate();
        }
    }

    public Ticket getTicketByNumber(int ticketNum) {
        String sql =
                "SELECT t.ticket_num, t.reservation_id, t.instance_id, t.segment_num, t.fare, t.pay_date, " +
                "t.special_meal, t.direction, t.ticket_class, t.status, " +
                "f.airline_id, f.flight_num " +
                "FROM Ticket t " +
                "LEFT JOIN Flight_Instance fi ON fi.instance_id = t.instance_id " +
                "LEFT JOIN Flight f ON f.airline_id = fi.airline_id AND f.flight_num = fi.flight_num " +
                "WHERE t.ticket_num = ?";

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
        String sql = "UPDATE Ticket SET reservation_id = ?, instance_id = ?, segment_num = ?, fare = ?, " +
                "pay_date = ?, special_meal = ?, direction = ?, ticket_class = ?, status = ? WHERE ticket_num = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticket.getReservationId());
            if (ticket.getInstanceId() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, ticket.getInstanceId());
            }
            ps.setInt(3, ticket.getSegmentNum());
            ps.setBigDecimal(4, ticket.getFareAmount());
            ps.setString(5, ticket.getPayDate());
            ps.setBoolean(6, ticket.getSpecialMeal());
            ps.setString(7, ticket.getDirection());
            ps.setString(8, ticket.getTicketClass());
            ps.setString(9, ticket.getStatus());
            ps.setInt(10, ticket.getTicketNum());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Ticket mapTicket(ResultSet rs) throws SQLException {
        return new Ticket(
                rs.getInt("ticket_num"),
                rs.getInt("reservation_id"),
                rs.getInt("instance_id"),
                rs.getString("airline_id"),
                rs.getObject("flight_num") == null ? null : rs.getInt("flight_num"),
                rs.getInt("segment_num"),
                null,
                rs.getBigDecimal("fare"),
                rs.getString("pay_date"),
                rs.getBoolean("special_meal"),
                rs.getString("direction"),
                rs.getString("ticket_class"),
                rs.getString("status")
        );
    }
}
