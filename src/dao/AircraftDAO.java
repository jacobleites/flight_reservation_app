package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Aircraft;

public class AircraftDAO {

    public List<Aircraft> getAllAircraft() {
        String sql = "SELECT airline_id, aircraft_id, capacity, model FROM Aircraft";
        List<Aircraft> aircraftList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                aircraftList.add(mapAircraft(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return aircraftList;
    }

    public Aircraft getAircraftById(int aircraftId) {
        String sql = "SELECT airline_id, aircraft_id, capacity, model FROM Aircraft WHERE aircraft_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, aircraftId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAircraft(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Aircraft> getAircraftByAirlineId(String airlineId) {
        String sql = "SELECT airline_id, aircraft_id, capacity, model FROM Aircraft WHERE airline_id = ?";
        List<Aircraft> aircraftList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airlineId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    aircraftList.add(mapAircraft(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return aircraftList;
    }

    public boolean addAircraft(Aircraft aircraft) {
        String sql = "INSERT INTO Aircraft (airline_id, aircraft_id, capacity, model) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aircraft.getAirlineId());
            ps.setInt(2, aircraft.getAircraftId());
            ps.setInt(3, aircraft.getCapacity());
            ps.setString(4, aircraft.getModel());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAircraft(Aircraft aircraft) {
        String sql = "UPDATE Aircraft SET airline_id = ?, capacity = ?, model = ? WHERE aircraft_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, aircraft.getAirlineId());
            ps.setInt(2, aircraft.getCapacity());
            ps.setString(3, aircraft.getModel());
            ps.setInt(4, aircraft.getAircraftId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Aircraft mapAircraft(ResultSet rs) throws SQLException {
        return new Aircraft(
                rs.getString("airline_id"),
                rs.getInt("aircraft_id"),
                rs.getInt("capacity"),
                rs.getString("model")
        );
    }
}
