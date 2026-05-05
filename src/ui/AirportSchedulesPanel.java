package ui;

import dao.FlightInstanceDAO;
import models.Employee;
import models.FlightInstance;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AirportSchedulesPanel extends JPanel {
    private final FlightInstanceDAO flightInstanceDAO = new FlightInstanceDAO();
    private final JTextField airportIdField = new JTextField(5);
    private final DefaultTableModel departuresModel;
    private final DefaultTableModel arrivalsModel;

    public AirportSchedulesPanel(MainFrame frame, Employee employee) {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header and Search Controls
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewBtn = new JButton("View Schedules");
        JButton backBtn = new JButton("Back to Dashboard");
        
        topPanel.add(new JLabel("Airport Code (e.g., JFK):"));
        topPanel.add(airportIdField);
        topPanel.add(viewBtn);
        topPanel.add(backBtn);

        // Tables Setup
        String[] columns = {"Flight #", "Airline", "Time", "To/From", "Status"};
        departuresModel = new DefaultTableModel(columns, 0);
        arrivalsModel = new DefaultTableModel(columns, 0);

        JPanel tablePanel = new JPanel(new GridLayout(2, 1, 10, 10));
        tablePanel.add(createTableSection("Departures", departuresModel));
        tablePanel.add(createTableSection("Arrivals", arrivalsModel));

        add(topPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);

        viewBtn.addActionListener(e -> refreshSchedules());
        backBtn.addActionListener(e -> frame.showEmployeeDashboard(employee));
    }

    private JPanel createTableSection(String title, DefaultTableModel model) {
        JPanel section = new JPanel(new BorderLayout());
        section.add(new JLabel(title, SwingConstants.LEFT), BorderLayout.NORTH);
        section.add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);
        return section;
    }

    private void refreshSchedules() {
        String airportId = airportIdField.getText().trim().toUpperCase();
        if (airportId.length() != 3) {
            JOptionPane.showMessageDialog(this, "Please enter a valid 3-letter airport code.");
            return;
        }

        departuresModel.setRowCount(0);
        arrivalsModel.setRowCount(0);

        // Fetch Departures
        for (FlightInstance fi : flightInstanceDAO.getSchedulesByAirport(airportId, true)) {
            departuresModel.addRow(new Object[]{fi.getFlightNumber(), fi.getAirlineId(), 
                fi.getDepartureDateTime(), fi.getArrivalAirport(), fi.getStatus()});
        }

        // Fetch Arrivals
        for (FlightInstance fi : flightInstanceDAO.getSchedulesByAirport(airportId, false)) {
            arrivalsModel.addRow(new Object[]{fi.getFlightNumber(), fi.getAirlineId(), 
                fi.getArrivalDateTime(), fi.getDepartureAirport(), fi.getStatus()});
        }
    }
}