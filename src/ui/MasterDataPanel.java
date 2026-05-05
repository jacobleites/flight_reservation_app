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
        panel.add(deleteBtn, BorderLayout.SOUTH);
        return panel;
    }

    // --- AIRCRAFT TAB ---
    private JPanel createAircraftTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        aircraftModel = new DefaultTableModel(new String[]{"Airline", "ID", "Capacity", "Model"}, 0);
        JTable table = new JTable(aircraftModel);
        refreshAircraftTable(aircraftModel);

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
        panel.add(deleteBtn, BorderLayout.SOUTH);
        return panel;
    }

    // --- AIRPORT TAB ---
    private JPanel createAirportTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        airportModel = new DefaultTableModel(new String[]{"ID", "Name", "City"}, 0);
        JTable table = new JTable(airportModel);
        refreshAirportTable(airportModel);

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
        panel.add(deleteBtn, BorderLayout.SOUTH);
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
}