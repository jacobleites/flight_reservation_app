package ui;

import models.Employee;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {
    private final MainFrame frame;
    private final Employee employee;

    public AdminDashboardPanel(MainFrame frame, Employee employee) {
        this.frame = frame;
        this.employee = employee;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(
                "Welcome, " + employee.getFirstName(),
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));

        JButton manageUsersButton = new JButton("Manage Users");
        JButton reservationListsButton = new JButton("Reservation Lists");
        JButton salesReportsButton = new JButton("Sales Reports");
        JButton analyticsButton = new JButton("Analytics");

        JButton logoutButton = new JButton("Logout");

        manageUsersButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Manage Users not implemented yet.");
        });

        reservationListsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Reservation Lists not implemented yet.");
        });

        salesReportsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Sales Reports not implemented yet.");
        });

        analyticsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Analytics not implemented yet.");
        });

        logoutButton.addActionListener(e -> {
            frame.showScreen("MAIN_MENU");
        });

        buttonPanel.add(manageUsersButton);
        buttonPanel.add(reservationListsButton);
        buttonPanel.add(salesReportsButton);
        buttonPanel.add(analyticsButton);
        buttonPanel.add(logoutButton);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(buttonPanel);

        add(titleLabel, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }
}