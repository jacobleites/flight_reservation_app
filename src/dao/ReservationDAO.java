package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Reservation;

public class ReservationDAO {

    public boolean createReservation(Reservation reservation) {
        String sql = "INSERT INTO Reservations (customer_ssn, reservation_date, status, total_price, trip_type) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservation.getSSN());
            ps.setString(2, reservation.getReservationDate());
            ps.setString(3, reservation.getStatus());
            ps.setFloat(4, reservation.getPrice());
            ps.setString(5, reservation.getTripType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> getReservationsForCustomer(String customerSsn) {
        String sql = "SELECT reservation_id, customer_ssn, reservation_date, status, total_price, trip_type " +
                "FROM Reservations WHERE customer_ssn = ?";
        List<Reservation> reservations = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerSsn);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapReservation(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    public Reservation getReservationById(int reservationId) {
        String sql = "SELECT reservation_id, customer_ssn, reservation_date, status, total_price, trip_type " +
                "FROM Reservations WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapReservation(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean cancelReservation(int reservationId) {
        String sql = "UPDATE Reservations SET status = 'Cancelled' WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // helper method to convert sql row into an object
    private Reservation mapReservation(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("reservation_id"),
                rs.getString("customer_ssn"),
                rs.getString("reservation_date"),
                rs.getString("status"),
                rs.getFloat("total_price"),
                rs.getString("trip_type")
        );
    }
}
