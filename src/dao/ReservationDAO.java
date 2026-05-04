package dao;

import db.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import models.Reservation;

public class ReservationDAO {

    public int createReservation(
            Connection conn,
            String customerSsn,
            BigDecimal bookingFee,
            BigDecimal totalPrice,
            String tripType
    ) throws SQLException {
        String sql = "INSERT INTO Reservations (customer_ssn, status, booking_fee, total_price, trip_type) " +
                "VALUES (?, 'Booked', ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, customerSsn);
            ps.setBigDecimal(2, bookingFee);
            ps.setBigDecimal(3, totalPrice);
            ps.setString(4, tripType);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to create reservation.");
    }

    public boolean createReservation(Reservation reservation) {
        String sql = "INSERT INTO Reservations (customer_ssn, reservation_date, status, total_price, booking_fee, trip_type) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reservation.getSSN());
            ps.setString(2, reservation.getReservationDate());
            ps.setString(3, reservation.getStatus());
            ps.setBigDecimal(4, reservation.getTotalPrice());
            ps.setBigDecimal(5, reservation.getBookingFeeAmount());
            ps.setString(6, reservation.getTripType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> getReservationsForCustomer(String customerSsn) {
        String sql = "SELECT reservation_id, customer_ssn, reservation_date, status, total_price, booking_fee, trip_type " +
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
        String sql = "SELECT reservation_id, customer_ssn, reservation_date, status, total_price, booking_fee, trip_type " +
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

    public boolean cancelReservation(int reservationId, double fee) {
        return cancelReservation(reservationId, BigDecimal.valueOf(fee));
    }

    public boolean cancelReservation(int reservationId, BigDecimal fee) {
        String sql = "UPDATE Reservations SET status = 'Cancelled', total_price = ? WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, fee);
            ps.setInt(2, reservationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelReservation(Connection conn, int reservationId, BigDecimal fee) throws SQLException {
        String sql = "UPDATE Reservations SET status = 'Cancelled', total_price = ? " +
                "WHERE reservation_id = ? AND status = 'Booked'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, fee);
            ps.setInt(2, reservationId);
            return ps.executeUpdate() == 1;
        }
    }

    private Reservation mapReservation(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("reservation_id"),
                rs.getString("customer_ssn"),
                rs.getString("reservation_date"),
                rs.getString("status"),
                rs.getBigDecimal("total_price"),
                rs.getBigDecimal("booking_fee"),
                rs.getString("trip_type")
        );
    }
}
