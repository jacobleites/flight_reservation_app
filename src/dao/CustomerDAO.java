package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.Customer;

public class CustomerDAO {

    public Customer login(String username, String password) {
        String sql = "SELECT customer_ssn, email, gender, dob, firstName, lastName, phone, account_id, username " +
                "FROM Customer WHERE username = ? AND acc_password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs, null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Customer findBySsn(String customerSsn) {
        String sql = "SELECT customer_ssn, email, gender, dob, firstName, lastName, phone, account_id, username, acc_password " +
                "FROM Customer WHERE customer_ssn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customerSsn);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs, rs.getString("acc_password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Customer findByAccountId(int accountId) {
        String sql = "SELECT customer_ssn, email, gender, dob, firstName, lastName, phone, account_id, username, acc_password " +
                "FROM Customer WHERE account_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCustomer(rs, rs.getString("acc_password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean createCustomer(Customer customer) {
        String nextIdSql = "SELECT COALESCE(MAX(account_id), 0) + 1 AS next_id FROM Customer";
        String insertSql = "INSERT INTO Customer (customer_ssn, email, gender, dob, firstName, lastName, phone, account_id, username, acc_password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int nextAccountId = 1;
            try (PreparedStatement nextIdPs = conn.prepareStatement(nextIdSql);
                 ResultSet rs = nextIdPs.executeQuery()) {
                if (rs.next()) {
                    nextAccountId = rs.getInt("next_id");
                }
            }

            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setString(1, customer.getSsn());
                insertPs.setString(2, customer.getEmail());
                insertPs.setString(3, customer.getGender());
                insertPs.setString(4, customer.getDob());
                insertPs.setString(5, customer.getFirstName());
                insertPs.setString(6, customer.getLastName());
                insertPs.setString(7, customer.getPhone());
                insertPs.setInt(8, nextAccountId);
                insertPs.setString(9, customer.getUsername());
                insertPs.setString(10, customer.getPassword());

                boolean created = insertPs.executeUpdate() > 0;
                conn.commit();
                return created;
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
        String sql = "UPDATE Customer SET email = ?, gender = ?, dob = ?, firstName = ?, lastName = ?, phone = ?, " +
                "account_id = ?, username = ?, acc_password = ? WHERE customer_ssn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getEmail());
            ps.setString(2, customer.getGender());
            ps.setString(3, customer.getDob());
            ps.setString(4, customer.getFirstName());
            ps.setString(5, customer.getLastName());
            ps.setString(6, customer.getPhone());
            ps.setInt(7, customer.getAccount_id());
            ps.setString(8, customer.getUsername());
            ps.setString(9, customer.getPassword());
            ps.setString(10, customer.getSsn());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private Customer mapCustomer(ResultSet rs, String password) throws SQLException {
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
                password
        );
    }
}
