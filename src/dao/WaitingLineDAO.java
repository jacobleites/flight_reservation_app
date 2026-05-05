package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.WaitingLineEntry;

public class WaitingLineDAO {

    public boolean existsWaitingEntry(Connection conn, String customerSsn, int instanceId) throws SQLException {
        String sql = "SELECT 1 FROM Waiting_Line WHERE customer_ssn = ? AND instance_id = ? " +
                "AND status IN ('WAITING', 'NOTIFIED') LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerSsn);
            ps.setInt(2, instanceId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int getNextPriorityNumber(Connection conn, int instanceId) throws SQLException {
        String sql = "SELECT COALESCE(MAX(priority_num), 0) + 1 AS next_priority " +
                "FROM Waiting_Line " +
                "WHERE instance_id = ? AND status IN ('WAITING', 'NOTIFIED')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instanceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("next_priority");
                }
            }
        }
        return 1;
    }

    public boolean addToWaitingLine(Connection conn, String customerSsn, int instanceId, int priorityNum) throws SQLException {
        String sql = "INSERT INTO Waiting_Line (customer_ssn, instance_id, priority_num, status) VALUES (?, ?, ?, 'WAITING')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerSsn);
            ps.setInt(2, instanceId);
            ps.setInt(3, priorityNum);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean existsWaitingEntry(String customerSsn, int instanceId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return existsWaitingEntry(conn, customerSsn, instanceId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isCustomerAlreadyWaiting(String customerSsn, int instanceId) {
        return existsWaitingEntry(customerSsn, instanceId);
    }

    public int getNextPriorityNumber(int instanceId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return getNextPriorityNumber(conn, instanceId);
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
    }

    public boolean addCustomerToWaitingLine(String customerSsn, int instanceId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (existsWaitingEntry(conn, customerSsn, instanceId)) {
                    conn.rollback();
                    return false;
                }
                int priority = getNextPriorityNumber(conn, instanceId);
                boolean inserted = addToWaitingLine(conn, customerSsn, instanceId, priority);
                if (!inserted) {
                    conn.rollback();
                    return false;
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public WaitingLineEntry getFirstWaitingCustomer(Connection conn, int instanceId) throws SQLException {
        String sql = "SELECT waitlist_id, customer_ssn, instance_id, priority_num, time_entered, status " +
                "FROM Waiting_Line " +
                "WHERE instance_id = ? AND status = 'WAITING' " +
                "ORDER BY priority_num ASC, time_entered ASC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instanceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEntry(rs);
                }
            }
        }
        return null;
    }

    public boolean notifyNextWaitingCustomer(Connection conn, int instanceId) throws SQLException {
        WaitingLineEntry first = getFirstWaitingCustomer(conn, instanceId);
        if (first == null) {
            return false;
        }
        String sql = "UPDATE Waiting_Line SET status = 'NOTIFIED' WHERE waitlist_id = ? AND status = 'WAITING'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, first.getWaitlistId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean notifyNextWaitingCustomer(int instanceId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean changed = notifyNextWaitingCustomer(conn, instanceId);
                conn.commit();
                return changed;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<WaitingLineEntry> getNotificationsForCustomer(String customerSsn) {
        String sql = "SELECT waitlist_id, customer_ssn, instance_id, priority_num, time_entered, status " +
                "FROM Waiting_Line WHERE customer_ssn = ? AND status = 'NOTIFIED' " +
                "ORDER BY time_entered ASC";
        List<WaitingLineEntry> entries = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerSsn);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    entries.add(mapEntry(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public List<Object[]> getNotificationDetailsForCustomer(String customerSsn) {
        String sql = "SELECT w.instance_id, fi.airline_id, fi.flight_num, fi.dep_datetime, fi.arr_datetime, " +
                "f.dep_airport, f.arr_airport, dep.airport_name AS dep_airport_name, " +
                "arr.airport_name AS arr_airport_name, ac.model AS aircraft_model " +
                "FROM Waiting_Line w " +
                "LEFT JOIN Flight_Instance fi ON fi.instance_id = w.instance_id " +
                "LEFT JOIN Flight f ON f.airline_id = fi.airline_id AND f.flight_num = fi.flight_num " +
                "LEFT JOIN Airport dep ON dep.airport_id = f.dep_airport " +
                "LEFT JOIN Airport arr ON arr.airport_id = f.arr_airport " +
                "LEFT JOIN Aircraft ac ON ac.aircraft_id = fi.aircraft_id " +
                "WHERE w.customer_ssn = ? AND w.status = 'NOTIFIED' " +
                "ORDER BY w.time_entered ASC";

        List<Object[]> details = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerSsn);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    details.add(new Object[]{
                            rs.getInt("instance_id"),
                            rs.getString("airline_id"),
                            rs.getObject("flight_num"),
                            rs.getString("dep_datetime"),
                            rs.getString("arr_datetime"),
                            rs.getString("dep_airport"),
                            rs.getString("arr_airport"),
                            rs.getString("dep_airport_name"),
                            rs.getString("arr_airport_name"),
                            rs.getString("aircraft_model")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public boolean markAsBooked(Connection conn, String customerSsn, int instanceId) throws SQLException {
        String sql = "UPDATE Waiting_Line SET status = 'BOOKED' " +
                "WHERE customer_ssn = ? AND instance_id = ? AND status = 'NOTIFIED'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerSsn);
            ps.setInt(2, instanceId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean markAsBooked(String customerSsn, int instanceId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return markAsBooked(conn, customerSsn, instanceId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelWaitingEntry(String customerSsn, int instanceId) {
        String sql = "UPDATE Waiting_Line SET status = 'CANCELLED' " +
                "WHERE customer_ssn = ? AND instance_id = ? AND status IN ('WAITING', 'NOTIFIED')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerSsn);
            ps.setInt(2, instanceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private WaitingLineEntry mapEntry(ResultSet rs) throws SQLException {
        return new WaitingLineEntry(
                rs.getInt("waitlist_id"),
                rs.getString("customer_ssn"),
                rs.getInt("instance_id"),
                rs.getInt("priority_num"),
                rs.getString("time_entered"),
                rs.getString("status")
        );
    }

    public List<Object[]> getFullWaitingListForInstance(int instanceId) {
        String sql = "SELECT w.priority_num, c.firstName, c.lastName, c.customer_ssn, w.time_entered, w.status " +
                    "FROM Waiting_Line w JOIN Customer c ON w.customer_ssn = c.customer_ssn " +
                    "WHERE w.instance_id = ? AND w.status IN ('WAITING', 'NOTIFIED') " +
                    "ORDER BY w.priority_num ASC";
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = db.DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, instanceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{
                        rs.getInt("priority_num"),
                        rs.getString("firstName") + " " + rs.getString("lastName"),
                        rs.getString("customer_ssn"),
                        rs.getString("time_entered"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
