package ui;

import models.Employee;

import javax.swing.*;
import java.awt.*;

public class CustomerRepDashboardPanel extends JPanel {
    private final MainFrame frame;
    private final Employee employee;

    public CustomerRepDashboardPanel(MainFrame frame, Employee employee) {
        this.frame = frame;
        this.employee = employee;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(
                "Welcome, " + employee.getFirstName(),
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));

        JButton manageReservationsButton = new JButton("Manage Reservations");
        JButton masterDataButton = new JButton("Master Data");
        JButton waitingListButton = new JButton("Waiting List");
        JButton airportSchedulesButton = new JButton("Airport Schedules");
        JButton customerQuestionsButton = new JButton("Customer Questions");

        JButton logoutButton = new JButton("Logout");

        manageReservationsButton.addActionListener(e -> {
            frame.showManageReservationsScreen(employee);
        });

        masterDataButton.addActionListener(e -> {
            frame.showMasterDataPanel(employee);
        });

        waitingListButton.addActionListener(e -> {
            frame.showWaitingListPanel(employee);
        });

        airportSchedulesButton.addActionListener(e -> {
            frame.showAirportSchedulesPanel(employee);
        });

        customerQuestionsButton.addActionListener(e -> {
            frame.showCustomerQuestionsScreen(employee);
        });

        logoutButton.addActionListener(e -> {
            frame.showScreen("MAIN_MENU");
        });

        buttonPanel.add(manageReservationsButton);
        buttonPanel.add(masterDataButton);
        buttonPanel.add(waitingListButton);
        buttonPanel.add(airportSchedulesButton);
        buttonPanel.add(customerQuestionsButton);
        buttonPanel.add(logoutButton);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(buttonPanel);

        add(titleLabel, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }
}
