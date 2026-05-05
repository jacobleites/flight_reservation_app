package ui;

import dao.AircraftDAO;
import dao.AirportDAO;
import dao.FlightDAO;
import models.Aircraft;
import models.Airport;
import models.Flight;
import models.Employee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * MasterDataPanel handles administrative CRUD operations.
 * Optimized with global refresh and cascading deletion safety checks.
 */
public class MasterDataPanel extends JPanel {
    private final MainFrame frame;
    private final Employee employee;
    private final AircraftDAO aircraftDAO = new AircraftDAO();
    private final AirportDAO airportDAO = new AirportDAO();
    private final FlightDAO flightDAO = new FlightDAO();

    // Models for global refresh
    private DefaultTableModel flightModel;
    private DefaultTableModel aircraftModel;
    private DefaultTableModel airportModel;

    public MasterDataPanel(MainFrame frame, Employee employee) {
        this.frame = frame;
        this.employee = employee;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setupHeader();
        setupTabs();
    }

    private void setupHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Master Data Management Portal");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        
        JPanel topButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // NEW: Global Refresh Button
        JButton refreshBtn = new JButton("Refresh Dashboard");
        refreshBtn.addActionListener(e -> refreshAllData());
        
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> frame.showEmployeeDashboard(employee));
        
        topButtonPanel.add(refreshBtn);
        topButtonPanel.add(backBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(topButtonPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void setupTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Aircrafts", createAircraftTab());
        tabbedPane.addTab("Airports", createAirportTab());
        tabbedPane.addTab("Flights", createFlightTab());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void refreshAllData() {
        refreshFlightTable(flightModel);
        refreshAircraftTable(aircraftModel);
        refreshAirportTable(airportModel);
        JOptionPane.showMessageDialog(this, "Dashboard data successfully synchronized.");
    }

    // --- FLIGHT TAB ---
    private JPanel createFlightTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        flightModel = new DefaultTableModel(new String[]{"Airline", "Flight #", "From", "To", "Dep", "Arr"}, 0);
        JTable table = new JTable(flightModel);
        refreshFlightTable(flightModel);

        JButton createBtn = new JButton("Create Flight");
        createBtn.addActionListener(e -> {
            Flight created = promptForFlight(null);
            if (created != null && flightDAO.addFlight(created)) {
                refreshFlightTable(flightModel);
            } else if (created != null) {
                JOptionPane.showMessageDialog(this, "Could not create flight. Check IDs and constraints.");
            }
        });

        JButton editBtn = new JButton("Edit Flight");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a flight to edit.");
                return;
            }
            Flight existing = new Flight(
                    (int) flightModel.getValueAt(row, 1),
                    (String) flightModel.getValueAt(row, 0),
                    0,
                    (String) flightModel.getValueAt(row, 4),
                    (String) flightModel.getValueAt(row, 5),
                    (String) flightModel.getValueAt(row, 3),
                    (String) flightModel.getValueAt(row, 2)
            );
            Flight full = flightDAO.getFlight(existing.getAirlineId(), existing.getFlightNumber());
            if (full == null) {
                JOptionPane.showMessageDialog(this, "Could not load full flight details.");
                return;
            }
            Flight updated = promptForFlight(full);
            if (updated != null && flightDAO.updateFlight(updated)) {
                refreshFlightTable(flightModel);
            } else if (updated != null) {
                JOptionPane.showMessageDialog(this, "Could not update flight.");
            }
        });

        JButton deleteBtn = new JButton("Delete Flight");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String airlineId = (String) flightModel.getValueAt(row, 0);
                int flightNum = (int) flightModel.getValueAt(row, 1);
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "DANGER: Deleting this flight will also remove all domestic/international records and flight instances. Proceed?", 
                    "Cascading Deletion Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION && flightDAO.deleteFlight(airlineId, flightNum)) {
                    refreshFlightTable(flightModel);
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(createBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    // --- AIRCRAFT TAB ---
    private JPanel createAircraftTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        aircraftModel = new DefaultTableModel(new String[]{"Airline", "ID", "Capacity", "Model"}, 0);
        JTable table = new JTable(aircraftModel);
        refreshAircraftTable(aircraftModel);

        JButton createBtn = new JButton("Create Aircraft");
        createBtn.addActionListener(e -> {
            Aircraft created = promptForAircraft(null);
            if (created != null && aircraftDAO.addAircraft(created)) {
                refreshAircraftTable(aircraftModel);
            } else if (created != null) {
                JOptionPane.showMessageDialog(this, "Could not create aircraft. Check IDs and constraints.");
            }
        });

        JButton editBtn = new JButton("Edit Aircraft");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an aircraft to edit.");
                return;
            }
            int id = (int) aircraftModel.getValueAt(row, 1);
            Aircraft existing = aircraftDAO.getAircraftById(id);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Could not load aircraft details.");
                return;
            }
            Aircraft updated = promptForAircraft(existing);
            if (updated != null && aircraftDAO.updateAircraft(updated)) {
                refreshAircraftTable(aircraftModel);
            } else if (updated != null) {
                JOptionPane.showMessageDialog(this, "Could not update aircraft.");
            }
        });

        JButton deleteBtn = new JButton("Delete Aircraft");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                int id = (int) aircraftModel.getValueAt(row, 1);
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Deleting this aircraft will fail if it is assigned to active flights. Proceed?", 
                    "Integrity Check", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION && aircraftDAO.deleteAircraft(id)) {
                    refreshAircraftTable(aircraftModel);
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(createBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    // --- AIRPORT TAB ---
    private JPanel createAirportTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        airportModel = new DefaultTableModel(new String[]{"ID", "Name", "City"}, 0);
        JTable table = new JTable(airportModel);
        refreshAirportTable(airportModel);

        JButton createBtn = new JButton("Create Airport");
        createBtn.addActionListener(e -> {
            Airport created = promptForAirport(null);
            if (created != null && airportDAO.addAirport(created)) {
                refreshAirportTable(airportModel);
            } else if (created != null) {
                JOptionPane.showMessageDialog(this, "Could not create airport. Check ID uniqueness.");
            }
        });

        JButton editBtn = new JButton("Edit Airport");
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an airport to edit.");
                return;
            }
            String id = (String) airportModel.getValueAt(row, 0);
            Airport existing = airportDAO.getAirportById(id);
            if (existing == null) {
                JOptionPane.showMessageDialog(this, "Could not load airport details.");
                return;
            }
            Airport updated = promptForAirport(existing);
            if (updated != null && airportDAO.updateAirport(updated)) {
                refreshAirportTable(airportModel);
            } else if (updated != null) {
                JOptionPane.showMessageDialog(this, "Could not update airport.");
            }
        });

        JButton deleteBtn = new JButton("Delete Airport");
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                String id = (String) airportModel.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Are you sure you want to delete airport " + id + "? This will affect all routes using this airport.", 
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION && airportDAO.deleteAirport(id)) {
                    refreshAirportTable(airportModel);
                }
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(createBtn);
        actions.add(editBtn);
        actions.add(deleteBtn);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    // --- REFRESH HELPERS ---
    private void refreshFlightTable(DefaultTableModel model) {
        if (model == null) return;
        model.setRowCount(0);
        flightDAO.getAllFlights().forEach(f -> model.addRow(new Object[]{f.getAirlineId(), f.getFlightNumber(), f.getDepartureAirport(), f.getArrivalAirport(), f.getDepartureTime(), f.getArrivalTime()}));
    }

    private void refreshAircraftTable(DefaultTableModel model) {
        if (model == null) return;
        model.setRowCount(0);
        aircraftDAO.getAllAircraft().forEach(a -> model.addRow(new Object[]{a.getAirlineId(), a.getAircraftId(), a.getCapacity(), a.getModel()}));
    }

    private void refreshAirportTable(DefaultTableModel model) {
        if (model == null) return;
        model.setRowCount(0);
        airportDAO.getAllAirports().forEach(a -> model.addRow(new Object[]{a.getId(), a.getName(), a.getCity()}));
    }

    private Airport promptForAirport(Airport existing) {
        JTextField idField = new JTextField(existing == null ? "" : existing.getId(), 8);
        JTextField nameField = new JTextField(existing == null ? "" : existing.getName(), 20);
        JTextField cityField = new JTextField(existing == null ? "" : existing.getCity(), 20);
        if (existing != null) {
            idField.setEditable(false);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Airport ID (3 letters):"));
        form.add(idField);
        form.add(new JLabel("Airport Name:"));
        form.add(nameField);
        form.add(new JLabel("City:"));
        form.add(cityField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                existing == null ? "Create Airport" : "Edit Airport",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String id = idField.getText().trim().toUpperCase();
        String name = nameField.getText().trim();
        String city = cityField.getText().trim();
        if (id.length() != 3 || name.isEmpty() || city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Airport ID must be 3 letters, and name/city are required.");
            return null;
        }
        return new Airport(id, name, city);
    }

    private Aircraft promptForAircraft(Aircraft existing) {
        JTextField airlineField = new JTextField(existing == null ? "" : existing.getAirlineId(), 8);
        JTextField idField = new JTextField(existing == null ? "" : String.valueOf(existing.getAircraftId()), 8);
        JTextField capacityField = new JTextField(existing == null ? "" : String.valueOf(existing.getCapacity()), 8);
        JTextField modelField = new JTextField(existing == null ? "" : existing.getModel(), 20);
        if (existing != null) {
            idField.setEditable(false);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Airline ID (2 letters):"));
        form.add(airlineField);
        form.add(new JLabel("Aircraft ID:"));
        form.add(idField);
        form.add(new JLabel("Capacity:"));
        form.add(capacityField);
        form.add(new JLabel("Model:"));
        form.add(modelField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                existing == null ? "Create Aircraft" : "Edit Aircraft",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String airlineId = airlineField.getText().trim().toUpperCase();
        String model = modelField.getText().trim();
        int aircraftId;
        int capacity;
        try {
            aircraftId = Integer.parseInt(idField.getText().trim());
            capacity = Integer.parseInt(capacityField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Aircraft ID and capacity must be valid numbers.");
            return null;
        }
        if (airlineId.length() != 2 || model.isEmpty() || aircraftId <= 0 || capacity <= 0) {
            JOptionPane.showMessageDialog(this, "Airline ID must be 2 letters, and numeric values must be positive.");
            return null;
        }
        return new Aircraft(airlineId, aircraftId, capacity, model);
    }

    private Flight promptForFlight(Flight existing) {
        JTextField airlineField = new JTextField(existing == null ? "" : existing.getAirlineId(), 8);
        JTextField flightNumField = new JTextField(existing == null ? "" : String.valueOf(existing.getFlightNumber()), 8);
        JTextField aircraftIdField = new JTextField(existing == null ? "" : String.valueOf(existing.getAircraftId()), 8);
        JTextField depAirportField = new JTextField(existing == null ? "" : existing.getDepartureAirport(), 8);
        JTextField arrAirportField = new JTextField(existing == null ? "" : existing.getArrivalAirport(), 8);
        JTextField depTimeField = new JTextField(existing == null ? "" : existing.getDepartureTime(), 10);
        JTextField arrTimeField = new JTextField(existing == null ? "" : existing.getArrivalTime(), 10);
        if (existing != null) {
            airlineField.setEditable(false);
            flightNumField.setEditable(false);
        }

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Airline ID (2 letters):"));
        form.add(airlineField);
        form.add(new JLabel("Flight Number:"));
        form.add(flightNumField);
        form.add(new JLabel("Aircraft ID:"));
        form.add(aircraftIdField);
        form.add(new JLabel("Departure Airport (3 letters):"));
        form.add(depAirportField);
        form.add(new JLabel("Arrival Airport (3 letters):"));
        form.add(arrAirportField);
        form.add(new JLabel("Departure Time (HH:MM[:SS]):"));
        form.add(depTimeField);
        form.add(new JLabel("Arrival Time (HH:MM[:SS]):"));
        form.add(arrTimeField);

        int result = JOptionPane.showConfirmDialog(
                this,
                form,
                existing == null ? "Create Flight" : "Edit Flight",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        String airlineId = airlineField.getText().trim().toUpperCase();
        String depAirport = depAirportField.getText().trim().toUpperCase();
        String arrAirport = arrAirportField.getText().trim().toUpperCase();
        String depTime = normalizeTime(depTimeField.getText().trim());
        String arrTime = normalizeTime(arrTimeField.getText().trim());
        int flightNum;
        int aircraftId;
        try {
            flightNum = Integer.parseInt(flightNumField.getText().trim());
            aircraftId = Integer.parseInt(aircraftIdField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Flight number and aircraft ID must be valid numbers.");
            return null;
        }

        if (airlineId.length() != 2 || depAirport.length() != 3 || arrAirport.length() != 3) {
            JOptionPane.showMessageDialog(this, "Airline ID must be 2 letters and airport codes must be 3 letters.");
            return null;
        }
        if (depTime == null || arrTime == null) {
            JOptionPane.showMessageDialog(this, "Times must be in HH:MM or HH:MM:SS format.");
            return null;
        }
        if (flightNum <= 0 || aircraftId <= 0) {
            JOptionPane.showMessageDialog(this, "Flight number and aircraft ID must be positive.");
            return null;
        }

        return new Flight(flightNum, airlineId, aircraftId, depTime, arrTime, arrAirport, depAirport);
    }

    private String normalizeTime(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        if (value.matches("^\\d{2}:\\d{2}$")) {
            return value + ":00";
        }
        if (value.matches("^\\d{2}:\\d{2}:\\d{2}$")) {
            return value;
        }
        return null;
    }
}
