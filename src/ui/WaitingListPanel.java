package ui;

import dao.WaitingLineDAO;
import models.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class WaitingListPanel extends JPanel {
    private final WaitingLineDAO waitingLineDAO = new WaitingLineDAO();
    private final DefaultTableModel tableModel;
    private final JTextField instanceIdField;

    public WaitingListPanel(MainFrame frame, Employee employee) {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel titleLabel = new JLabel("Flight Waiting List Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        // Search Controls
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        instanceIdField = new JTextField(10);
        JButton searchBtn = new JButton("View Waiting List");
        JButton backBtn = new JButton("Back to Dashboard");

        searchPanel.add(new JLabel("Instance ID:"));
        searchPanel.add(instanceIdField);
        searchPanel.add(searchBtn);
        searchPanel.add(backBtn);

        // Results Table
        String[] columns = {"Priority", "Passenger Name", "SSN", "Time Entered", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);

        // Action Listeners
        searchBtn.addActionListener(e -> refreshTable());
        backBtn.addActionListener(e -> frame.showEmployeeDashboard(employee));
    }

    private void refreshTable() {
        String input = instanceIdField.getText().trim();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Flight Instance ID.");
            return;
        }

        try {
            int instanceId = Integer.parseInt(input);
            tableModel.setRowCount(0);
            List<Object[]> data = waitingLineDAO.getFullWaitingListForInstance(instanceId);
            
            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No passengers found for Instance ID: " + instanceId);
            } else {
                for (Object[] row : data) {
                    tableModel.addRow(row);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Instance ID must be a valid number.");
        }
    }
}
