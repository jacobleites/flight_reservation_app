package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlightClassInventoryDAO {

    public BigDecimal getBasePrice(Connection conn, int instanceId, String ticketClass) throws SQLException {
        String sql = "SELECT base_price FROM Flight_Class_Inventory WHERE instance_id = ? AND ticket_class = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instanceId);
            ps.setString(2, ticketClass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("base_price");
                }
            }
        }
        return null;
    }

    public int decrementAvailableSeat(Connection conn, int instanceId, String ticketClass) throws SQLException {
        String sql = "UPDATE Flight_Class_Inventory " +
                "SET available_seats = available_seats - 1 " +
                "WHERE instance_id = ? AND ticket_class = ? AND available_seats > 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instanceId);
            ps.setString(2, ticketClass);
            return ps.executeUpdate();
        }
    }

    public int getAvailableSeats(Connection conn, int instanceId, String ticketClass) throws SQLException {
        String sql = "SELECT available_seats FROM Flight_Class_Inventory WHERE instance_id = ? AND ticket_class = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instanceId);
            ps.setString(2, ticketClass);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("available_seats");
                }
            }
        }
        return -1;
    }

    public int incrementAvailableSeat(Connection conn, int instanceId, String ticketClass) throws SQLException {
        String sql = "UPDATE Flight_Class_Inventory " +
                "SET available_seats = available_seats + 1 " +
                "WHERE instance_id = ? AND ticket_class = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instanceId);
            ps.setString(2, ticketClass);
            return ps.executeUpdate();
        }
    }
}
