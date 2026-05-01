package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.FlightInstance;


public class FlightInstanceDAO {
    public List<FlightInstance> searchFlights(String depAirport, String arrAirport, String date, boolean isFlexible) {
        String sql = "SELECT fi.instance_id, fi.flight_num, fi.airline_id, fi.dep_datetime, fi.arr_datetime, " +
                "f.dep_airport, f.arr_airport, fi.seats_available AS available_seats, fi.status " +
                "FROM Flight_Instance fi " +
                "JOIN Flight f ON f.airline_id = fi.airline_id AND f.flight_num = fi.flight_num " +
                "WHERE f.dep_airport = ? AND f.arr_airport = ? AND ";
        if (isFlexible) {
            sql += "fi.dep_datetime >= DATE_SUB(?, INTERVAL 3 DAY) AND fi.dep_datetime < DATE_ADD(?, INTERVAL 4 DAY)";
        } else {
            sql += "fi.dep_datetime >= ? AND fi.dep_datetime < DATE_ADD(?, INTERVAL 1 DAY)";
        }

        List<FlightInstance> flights = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, depAirport);
            ps.setString(2, arrAirport);
            ps.setString(3, date);
            ps.setString(4, date);
                
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    flights.add(mapFlightInstance(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flights;
    }

    private FlightInstance mapFlightInstance(ResultSet rs) throws SQLException {
        return new FlightInstance(
                rs.getInt("instance_id"),
                rs.getInt("flight_num"),
                rs.getString("airline_id"),
                rs.getString("dep_datetime"),
                rs.getString("arr_datetime"),
                rs.getString("dep_airport"),
                rs.getString("arr_airport"),
                rs.getInt("available_seats"),
                rs.getString("status")
        );
    }
}
