package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Flight;

public class FlightDAO {

    public List<Flight> searchFlights(String depAirport, String arrAirport) {
        String sql = "SELECT flight_num, airline_id, aircraft_id, dep_time, arr_time, arr_airport, dep_airport " +
                "FROM Flight WHERE dep_airport = ? AND arr_airport = ?";
        List<Flight> flights = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, depAirport);
            ps.setString(2, arrAirport);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    flights.add(mapFlight(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flights;
    }    

    public Flight getFlight(String airlineId, int flightNum) {
        String sql = "SELECT flight_num, airline_id, aircraft_id, dep_time, arr_time, arr_airport, dep_airport " +
                "FROM Flight WHERE airline_id = ? AND flight_num = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airlineId);
            ps.setInt(2, flightNum);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFlight(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // retrieve all flights without filters
    public List<Flight> getAllFlights() {
        String sql = "SELECT flight_num, airline_id, aircraft_id, dep_time, arr_time, arr_airport, dep_airport FROM Flight";
        List<Flight> flights = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                flights.add(mapFlight(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    public boolean addFlight(Flight flight) {
        String sql = "INSERT INTO Flight (flight_num, airline_id, aircraft_id, dep_time, arr_time, arr_airport, dep_airport) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flight.getFlightNumber());
            ps.setString(2, flight.getAirlineId());
            ps.setInt(3, flight.getAircraftId());
            ps.setString(4, flight.getDepartureTime());
            ps.setString(5, flight.getArrivalTime());
            ps.setString(6, flight.getArrivalAirport());
            ps.setString(7, flight.getDepartureAirport());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateFlight(Flight flight) {
        String sql = "UPDATE Flight SET aircraft_id = ?, dep_time = ?, arr_time = ?, arr_airport = ?, dep_airport = ? " +
                "WHERE airline_id = ? AND flight_num = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, flight.getAircraftId());
            ps.setString(2, flight.getDepartureTime());
            ps.setString(3, flight.getArrivalTime());
            ps.setString(4, flight.getArrivalAirport());
            ps.setString(5, flight.getDepartureAirport());
            ps.setString(6, flight.getAirlineId());
            ps.setInt(7, flight.getFlightNumber());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFlight(String airlineId, int flightNum) {
        String sql = "DELETE FROM Flight WHERE airline_id = ? AND flight_num = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airlineId);
            ps.setInt(2, flightNum);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // helper method to convert sql row into an object
    private Flight mapFlight(ResultSet rs) throws SQLException {
        return new Flight(
                rs.getInt("flight_num"),
                rs.getString("airline_id"),
                rs.getInt("aircraft_id"),
                rs.getString("dep_time"),
                rs.getString("arr_time"),
                rs.getString("arr_airport"),
                rs.getString("dep_airport")
        );
    }
}
