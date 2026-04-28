package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Airport;

public class AirportDAO {

    public List<Airport> getAllAirports() {
        String sql = "SELECT airport_id, airport_name, airport_city FROM Airport";
        List<Airport> airports = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                airports.add(mapAirport(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return airports;
    }

    public Airport getAirportById(String airportId) {
        String sql = "SELECT airport_id, airport_name, airport_city FROM Airport WHERE airport_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airportId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAirport(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addAirport(Airport airport) {
        String sql = "INSERT INTO Airport (airport_id, airport_name, airport_city) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airport.getId());
            ps.setString(2, airport.getName());
            ps.setString(3, airport.getCity());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAirport(Airport airport) {
        String sql = "UPDATE Airport SET airport_name = ?, airport_city = ? WHERE airport_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airport.getName());
            ps.setString(2, airport.getCity());
            ps.setString(3, airport.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // helper method to convert sql row into an object
    private Airport mapAirport(ResultSet rs) throws SQLException {
        return new Airport(
                rs.getString("airport_id"),
                rs.getString("airport_name"),
                rs.getString("airport_city")
        );
    }
}
