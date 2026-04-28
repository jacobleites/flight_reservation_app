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

    public boolean createCustomer(Customer customer) {
        String sql = "INSERT INTO Customer (customer_ssn, email, gender, dob, firstName, lastName, phone, account_id, username, acc_password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getSsn());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getGender());
            ps.setString(4, customer.getDob());
            ps.setString(5, customer.getFirstName());
            ps.setString(6, customer.getLastName());
            ps.setString(7, customer.getPhone());
            ps.setInt(8, customer.getAccount_id());
            ps.setString(9, customer.getUsername());
            ps.setString(10, customer.getPassword());
            return ps.executeUpdate() > 0;
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
    // helper method to convert sql row into an object
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
