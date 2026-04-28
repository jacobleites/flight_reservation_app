package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Employee;

public class EmployeeDAO {

    public Employee login(String username, String password) {
        String sql = "SELECT employee_ssn, firstName, lastName, acc_username, role FROM Employee " +
                "WHERE acc_username = ? AND acc_password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEmployee(rs, null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Employee findBySsn(String employeeSsn) {
        String sql = "SELECT employee_ssn, firstName, lastName, acc_username, acc_password, role FROM Employee WHERE employee_ssn = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeSsn);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEmployee(rs, rs.getString("acc_password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Employee> getAllEmployees() {
        String sql = "SELECT employee_ssn, firstName, lastName, acc_username, role FROM Employee";
        List<Employee> employees = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                employees.add(mapEmployee(rs, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return employees;
    }
    // helper method to convert sql row into an object
    private Employee mapEmployee(ResultSet rs, String password) throws SQLException {
        return new Employee(
                rs.getString("employee_ssn"),
                rs.getString("firstName"),
                rs.getString("lastName"),
                rs.getString("acc_username"),
                password,
                rs.getString("role")
        );
    }
}
