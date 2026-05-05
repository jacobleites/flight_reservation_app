package dao;

import db.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.ActiveFlightSummary;
import models.Customer;
import models.CustomerRevenueSummary;
import models.Employee;
import models.ReservationSummary;
import models.RevenueSummary;
import models.SalesReportRow;

public class AdminDAO {
    private static final String REP_ROLE = "CUSTOMER_REPRESENTATIVE";

    public List<Customer> getAllCustomers() {
        String sql = "SELECT customer_ssn, email, gender, dob, firstName, lastName, phone, account_id, username, acc_password " +
                "FROM Customer ORDER BY lastName ASC, firstName ASC";

        List<Customer> customers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                customers.add(new Customer(
                        rs.getString("customer_ssn"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getString("dob"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("phone"),
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("acc_password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public Customer getCustomerBySsn(String customerSsn) {
        String sql = "SELECT customer_ssn, email, gender, dob, firstName, lastName, phone, account_id, username, acc_password " +
                "FROM Customer WHERE customer_ssn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerSsn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getString("customer_ssn"),
                            rs.getString("email"),
                            rs.getString("gender"),
                            rs.getString("dob"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("phone"),
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("acc_password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addCustomer(Customer customer) {
        String nextAccountIdSql = "SELECT COALESCE(MAX(account_id), 0) + 1 AS next_id FROM Customer";
        String insertSql = "INSERT INTO Customer " +
                "(customer_ssn, email, gender, dob, firstName, lastName, phone, account_id, username, acc_password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int nextId = 1;
                try (PreparedStatement psNext = conn.prepareStatement(nextAccountIdSql);
                     ResultSet rs = psNext.executeQuery()) {
                    if (rs.next()) {
                        nextId = rs.getInt("next_id");
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, customer.getSsn());
                    ps.setString(2, customer.getEmail());
                    ps.setString(3, customer.getGender());
                    ps.setString(4, customer.getDob());
                    ps.setString(5, customer.getFirstName());
                    ps.setString(6, customer.getLastName());
                    ps.setString(7, customer.getPhone());
                    ps.setInt(8, nextId);
                    ps.setString(9, customer.getUsername());
                    ps.setString(10, customer.getPassword());
                    boolean ok = ps.executeUpdate() > 0;
                    conn.commit();
                    return ok;
                }
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

    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE Customer SET email = ?, gender = ?, dob = ?, firstName = ?, lastName = ?, " +
                "phone = ?, username = ?, acc_password = ? WHERE customer_ssn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getEmail());
            ps.setString(2, customer.getGender());
            ps.setString(3, customer.getDob());
            ps.setString(4, customer.getFirstName());
            ps.setString(5, customer.getLastName());
            ps.setString(6, customer.getPhone());
            ps.setString(7, customer.getUsername());
            ps.setString(8, customer.getPassword());
            ps.setString(9, customer.getSsn());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomer(String customerSsn) {
        String sql = "DELETE FROM Customer WHERE customer_ssn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerSsn);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addCustomerRepresentative(Employee rep) {
        String sql = "INSERT INTO Employee (employee_ssn, firstName, lastName, acc_username, acc_password, role) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rep.getEmployeeSsn());
            ps.setString(2, rep.getFirstName());
            ps.setString(3, rep.getLastName());
            ps.setString(4, rep.getUsername());
            ps.setString(5, rep.getPassword());
            ps.setString(6, REP_ROLE);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomerRepresentative(Employee rep) {
        String sql = "UPDATE Employee SET firstName = ?, lastName = ?, acc_username = ?, acc_password = ? " +
                "WHERE employee_ssn = ? AND role = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rep.getFirstName());
            ps.setString(2, rep.getLastName());
            ps.setString(3, rep.getUsername());
            ps.setString(4, rep.getPassword());
            ps.setString(5, rep.getEmployeeSsn());
            ps.setString(6, REP_ROLE);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomerRepresentative(String employeeSsn) {
        String sql = "DELETE FROM Employee WHERE employee_ssn = ? AND role = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeSsn);
            ps.setString(2, REP_ROLE);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Employee> getAllCustomerRepresentatives() {
        String sql = "SELECT employee_ssn, firstName, lastName, acc_username, acc_password, role " +
                "FROM Employee WHERE role = ? ORDER BY lastName ASC, firstName ASC";

        List<Employee> reps = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, REP_ROLE);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reps.add(new Employee(
                            rs.getString("employee_ssn"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("acc_username"),
                            rs.getString("acc_password"),
                            rs.getString("role")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reps;
    }

    public Employee getCustomerRepresentativeBySsn(String employeeSsn) {
        String sql = "SELECT employee_ssn, firstName, lastName, acc_username, acc_password, role " +
                "FROM Employee WHERE employee_ssn = ? AND role = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeSsn);
            ps.setString(2, REP_ROLE);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                            rs.getString("employee_ssn"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("acc_username"),
                            rs.getString("acc_password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<SalesReportRow> getMonthlySalesReport(int year, int month) {
        // Join Reservations to Ticket to total all booked ticket fares per reservation in this month.
        // booking_fee is stored on Reservations, so we add it to each reservation row's ticket total.
        String sql = "SELECT r.reservation_id, r.reservation_date, r.customer_ssn, " +
                "COALESCE(SUM(CASE WHEN t.status = 'Booked' THEN t.fare ELSE 0 END), 0) AS ticket_revenue, " +
                "COALESCE(r.booking_fee, 0) AS booking_fee " +
                "FROM Reservations r " +
                "LEFT JOIN Ticket t ON t.reservation_id = r.reservation_id " +
                "WHERE YEAR(r.reservation_date) = ? AND MONTH(r.reservation_date) = ? AND r.status = 'Booked' " +
                "GROUP BY r.reservation_id, r.reservation_date, r.customer_ssn, r.booking_fee " +
                "ORDER BY r.reservation_date ASC";

        List<SalesReportRow> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new SalesReportRow(
                            rs.getInt("reservation_id"),
                            rs.getString("reservation_date"),
                            rs.getString("customer_ssn"),
                            rs.getBigDecimal("ticket_revenue"),
                            rs.getBigDecimal("booking_fee")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<ReservationSummary> getReservationsByFlightNumber(int flightNum) {
        // Join Reservation -> Ticket -> Flight_Instance to find reservations containing the target flight number.
        String sql = "SELECT DISTINCT r.reservation_id, r.customer_ssn, c.firstName, c.lastName, " +
                "r.reservation_date, r.status, r.total_price, fi.airline_id, fi.flight_num " +
                "FROM Reservations r " +
                "JOIN Customer c ON c.customer_ssn = r.customer_ssn " +
                "JOIN Ticket t ON t.reservation_id = r.reservation_id " +
                "JOIN Flight_Instance fi ON fi.instance_id = t.instance_id " +
                "WHERE fi.flight_num = ? " +
                "ORDER BY r.reservation_date DESC";

        List<ReservationSummary> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flightNum);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapReservationSummary(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<ReservationSummary> getReservationsByCustomerName(String firstNameLike, String lastNameLike) {
        // Join Customer to Reservations and Ticket/Flight_Instance so each row can show reservation + flight identity.
        String sql = "SELECT DISTINCT r.reservation_id, r.customer_ssn, c.firstName, c.lastName, " +
                "r.reservation_date, r.status, r.total_price, fi.airline_id, fi.flight_num " +
                "FROM Customer c " +
                "JOIN Reservations r ON r.customer_ssn = c.customer_ssn " +
                "LEFT JOIN Ticket t ON t.reservation_id = r.reservation_id " +
                "LEFT JOIN Flight_Instance fi ON fi.instance_id = t.instance_id " +
                "WHERE c.firstName LIKE ? AND c.lastName LIKE ? " +
                "ORDER BY r.reservation_date DESC";

        List<ReservationSummary> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + firstNameLike + "%");
            ps.setString(2, "%" + lastNameLike + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapReservationSummary(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<RevenueSummary> getRevenueByFlight() {
        // Revenue per flight (airline_id + flight_num). Booking fee is distributed equally per booked ticket
        // in each reservation so booking fees are not double counted across grouped flights.
        String sql = "SELECT fi.airline_id, fi.flight_num, " +
                "SUM(t.fare + (r.booking_fee / bt.booked_count)) AS total_revenue, " +
                "COUNT(*) AS tickets_sold " +
                "FROM Ticket t " +
                "JOIN Reservations r ON r.reservation_id = t.reservation_id " +
                "JOIN Flight_Instance fi ON fi.instance_id = t.instance_id " +
                "JOIN (SELECT reservation_id, COUNT(*) AS booked_count FROM Ticket WHERE status = 'Booked' GROUP BY reservation_id) bt " +
                "ON bt.reservation_id = t.reservation_id " +
                "WHERE t.status = 'Booked' AND r.status = 'Booked' " +
                "GROUP BY fi.airline_id, fi.flight_num " +
                "ORDER BY total_revenue DESC";

        List<RevenueSummary> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String key = rs.getString("airline_id") + "-" + rs.getInt("flight_num");
                rows.add(new RevenueSummary(
                        "FLIGHT",
                        key,
                        rs.getBigDecimal("total_revenue"),
                        rs.getInt("tickets_sold")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<RevenueSummary> getRevenueByAirline() {
        // Revenue per airline. Same booking fee allocation logic as flight revenue.
        String sql = "SELECT fi.airline_id, " +
                "SUM(t.fare + (r.booking_fee / bt.booked_count)) AS total_revenue, " +
                "COUNT(*) AS tickets_sold " +
                "FROM Ticket t " +
                "JOIN Reservations r ON r.reservation_id = t.reservation_id " +
                "JOIN Flight_Instance fi ON fi.instance_id = t.instance_id " +
                "JOIN (SELECT reservation_id, COUNT(*) AS booked_count FROM Ticket WHERE status = 'Booked' GROUP BY reservation_id) bt " +
                "ON bt.reservation_id = t.reservation_id " +
                "WHERE t.status = 'Booked' AND r.status = 'Booked' " +
                "GROUP BY fi.airline_id " +
                "ORDER BY total_revenue DESC";

        List<RevenueSummary> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new RevenueSummary(
                        "AIRLINE",
                        rs.getString("airline_id"),
                        rs.getBigDecimal("total_revenue"),
                        rs.getInt("tickets_sold")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<CustomerRevenueSummary> getRevenueByCustomer() {
        // Use Reservations.total_price so per-customer totals already include ticket fares + booking fees.
        String sql = "SELECT c.customer_ssn, c.firstName, c.lastName, " +
                "COALESCE(SUM(r.total_price), 0) AS total_revenue, COUNT(*) AS reservation_count " +
                "FROM Customer c " +
                "JOIN Reservations r ON r.customer_ssn = c.customer_ssn " +
                "WHERE r.status = 'Booked' " +
                "GROUP BY c.customer_ssn, c.firstName, c.lastName " +
                "ORDER BY total_revenue DESC";

        List<CustomerRevenueSummary> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new CustomerRevenueSummary(
                        rs.getString("customer_ssn"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getBigDecimal("total_revenue"),
                        rs.getInt("reservation_count")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public CustomerRevenueSummary getTopCustomerByRevenue() {
        String sql = "SELECT c.customer_ssn, c.firstName, c.lastName, " +
                "COALESCE(SUM(r.total_price), 0) AS total_revenue, COUNT(*) AS reservation_count " +
                "FROM Customer c " +
                "JOIN Reservations r ON r.customer_ssn = c.customer_ssn " +
                "WHERE r.status = 'Booked' " +
                "GROUP BY c.customer_ssn, c.firstName, c.lastName " +
                "ORDER BY total_revenue DESC " +
                "LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return new CustomerRevenueSummary(
                        rs.getString("customer_ssn"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getBigDecimal("total_revenue"),
                        rs.getInt("reservation_count")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ActiveFlightSummary> getMostActiveFlights(int limit) {
        // Count booked tickets by airline_id + flight_num to measure activity.
        String sql = "SELECT fi.airline_id, fi.flight_num, COUNT(*) AS tickets_sold " +
                "FROM Ticket t " +
                "JOIN Flight_Instance fi ON fi.instance_id = t.instance_id " +
                "WHERE t.status = 'Booked' " +
                "GROUP BY fi.airline_id, fi.flight_num " +
                "ORDER BY tickets_sold DESC " +
                "LIMIT ?";

        List<ActiveFlightSummary> rows = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new ActiveFlightSummary(
                            rs.getString("airline_id"),
                            rs.getInt("flight_num"),
                            rs.getInt("tickets_sold")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private ReservationSummary mapReservationSummary(ResultSet rs) throws SQLException {
        return new ReservationSummary(
                rs.getInt("reservation_id"),
                rs.getString("customer_ssn"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("reservation_date"),
                rs.getString("status"),
                rs.getBigDecimal("total_price"),
                rs.getString("airline_id"),
                rs.getInt("flight_num")
        );
    }
}
