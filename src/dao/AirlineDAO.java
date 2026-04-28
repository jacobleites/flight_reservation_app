package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Airline;

public class AirlineDAO {

    public List<Airline> getAllAirlines() {
        String sql = "SELECT airline_id, airline_name FROM Airline";
        List<Airline> airlines = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                airlines.add(mapAirline(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return airlines;
    }

    public Airline getAirlineById(String airlineId) {
        String sql = "SELECT airline_id, airline_name FROM Airline WHERE airline_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airlineId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAirline(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addAirline(Airline airline) {
        String sql = "INSERT INTO Airline (airline_id, airline_name) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airline.getId());
            ps.setString(2, airline.getName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAirline(Airline airline) {
        String sql = "UPDATE Airline SET airline_name = ? WHERE airline_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airline.getName());
            ps.setString(2, airline.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // helper method to convert sql row into an object
    private Airline mapAirline(ResultSet rs) throws SQLException {
        return new Airline(
                rs.getString("airline_id"),
                rs.getString("airline_name")
        );
    }
}
