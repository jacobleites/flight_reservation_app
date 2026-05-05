package dao;

import db.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import models.FlightInstance;
import models.FlightItinerary;

public class FlightInstanceDAO {
    private static final int MAX_SEGMENTS = 3;
    private static final long MIN_LAYOVER_MILLIS = 45L * 60L * 1000L;
    private static final long MAX_LAYOVER_MILLIS = 6L * 60L * 60L * 1000L;

    private static final String SEARCH_SELECT =
            "SELECT fi.instance_id, fi.flight_num, fi.airline_id, fi.dep_datetime, fi.arr_datetime, " +
            "f.dep_airport, f.arr_airport, COALESCE(inv.total_available, 0) AS available_seats, fi.status, fi.fare AS price, " +
            "dep.airport_name AS dep_airport_name, arr.airport_name AS arr_airport_name, ac.model AS aircraft_model " +
            "FROM Flight_Instance fi " +
            "JOIN Flight f ON f.airline_id = fi.airline_id AND f.flight_num = fi.flight_num " +
            "LEFT JOIN Airport dep ON dep.airport_id = f.dep_airport " +
            "LEFT JOIN Airport arr ON arr.airport_id = f.arr_airport " +
            "LEFT JOIN Aircraft ac ON ac.aircraft_id = fi.aircraft_id " +
            "LEFT JOIN ( " +
            "  SELECT instance_id, SUM(available_seats) AS total_available " +
            "  FROM Flight_Class_Inventory " +
            "  GROUP BY instance_id " +
            ") inv ON inv.instance_id = fi.instance_id ";

    public List<FlightInstance> searchFlights(String depAirport, String arrAirport, String date, boolean isFlexible) {
        String sql = SEARCH_SELECT +
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

    public List<FlightInstance> searchBookableFlights(String depAirport, String arrAirport, String date) {
        String sql = SEARCH_SELECT +
                "WHERE f.dep_airport = ? AND f.arr_airport = ? " +
                "AND fi.dep_datetime >= ? AND fi.dep_datetime < DATE_ADD(?, INTERVAL 1 DAY) " +
                "ORDER BY fi.dep_datetime ASC";

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
                rs.getDouble("price"),
                rs.getString("status"),
                rs.getString("dep_airport_name"),
                rs.getString("arr_airport_name"),
                rs.getString("aircraft_model")
        );
    }

    public List<FlightItinerary> searchItineraries(String depAirport, String arrAirport, String date, boolean isFlexible) {
        List<FlightInstance> candidates = searchAllFlightsInWindow(date, isFlexible);
        Map<String, List<FlightInstance>> flightsByDeparture = new HashMap<>();
        for (FlightInstance flight : candidates) {
            flightsByDeparture.computeIfAbsent(flight.getDepartureAirport(), k -> new ArrayList<>()).add(flight);
        }

        List<FlightItinerary> itineraries = new ArrayList<>();
        Set<String> visitedAirports = new HashSet<>();
        visitedAirports.add(depAirport);
        buildItineraries(depAirport, arrAirport, flightsByDeparture, new ArrayList<>(), visitedAirports, itineraries);
        return itineraries;
    }

    private void buildItineraries(
            String currentAirport,
            String destinationAirport,
            Map<String, List<FlightInstance>> flightsByDeparture,
            List<FlightInstance> currentPath,
            Set<String> visitedAirports,
            List<FlightItinerary> itineraries
    ) {
        if (currentPath.size() >= MAX_SEGMENTS) {
            return;
        }

        List<FlightInstance> nextFlights = flightsByDeparture.getOrDefault(currentAirport, new ArrayList<>());
        for (FlightInstance next : nextFlights) {
            if (!isValidConnection(currentPath, next)) {
                continue;
            }

            String nextAirport = next.getArrivalAirport();
            if (visitedAirports.contains(nextAirport)) {
                continue;
            }

            currentPath.add(next);
            if (destinationAirport.equals(nextAirport)) {
                itineraries.add(new FlightItinerary(currentPath));
            } else {
                visitedAirports.add(nextAirport);
                buildItineraries(nextAirport, destinationAirport, flightsByDeparture, currentPath, visitedAirports, itineraries);
                visitedAirports.remove(nextAirport);
            }
            currentPath.remove(currentPath.size() - 1);
        }
    }

    private boolean isValidConnection(List<FlightInstance> currentPath, FlightInstance next) {
        String status = next.getStatus();
        if (status != null && "cancelled".equalsIgnoreCase(status.trim())) {
            return false;
        }

        if (currentPath.isEmpty()) {
            return true;
        }

        FlightInstance last = currentPath.get(currentPath.size() - 1);
        long lastArrival = Timestamp.valueOf(last.getArrivalDateTime()).getTime();
        long nextDeparture = Timestamp.valueOf(next.getDepartureDateTime()).getTime();
        long layover = nextDeparture - lastArrival;
        return layover >= MIN_LAYOVER_MILLIS && layover <= MAX_LAYOVER_MILLIS;
    }

    private List<FlightInstance> searchAllFlightsInWindow(String date, boolean isFlexible) {
        String sql = SEARCH_SELECT + "WHERE ";
        if (isFlexible) {
            sql += "fi.dep_datetime >= DATE_SUB(?, INTERVAL 3 DAY) AND fi.dep_datetime < DATE_ADD(?, INTERVAL 4 DAY)";
        } else {
            sql += "fi.dep_datetime >= ? AND fi.dep_datetime < DATE_ADD(?, INTERVAL 1 DAY)";
        }

        List<FlightInstance> flights = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date);
            ps.setString(2, date);

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

    public List<models.FlightInstance> getSchedulesByAirport(String airportId, boolean isDeparture) {
        String sql = SEARCH_SELECT + "WHERE " + (isDeparture ? "f.dep_airport = ?" : "f.arr_airport = ?") + 
                    " ORDER BY fi.dep_datetime ASC";
    
        List<models.FlightInstance> flights = new ArrayList<>();
        try (Connection conn = db.DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, airportId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    flights.add(mapFlightInstance(rs)); // Reusing existing mapping logic
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }
}
