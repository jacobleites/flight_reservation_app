package ui;

import models.Customer;

import javax.swing.*;
import java.awt.*;

public class CustomerDashboardPanel extends JPanel {
    private final MainFrame frame;
    private final Customer customer;

    public CustomerDashboardPanel(MainFrame frame, Customer customer) {
        this.frame = frame;
        this.customer = customer;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(
                "Welcome, " + customer.getFirstName(),
                SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));

        JButton searchFlightsButton = new JButton("Search Flights");
        JButton viewReservationsButton = new JButton("View My Reservations");
        JButton cancelReservationButton = new JButton("Cancel Reservation");
        JButton askQuestionButton = new JButton("Ask Customer Service Question");
        JButton logoutButton = new JButton("Logout");

        searchFlightsButton.addActionListener(e -> {
            frame.showSearchFlightsScreen(customer);
        });

        viewReservationsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "View reservations not implemented yet.");
        });

        cancelReservationButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Cancel reservation not implemented yet.");
        });

        askQuestionButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Ask question not implemented yet.");
        });

        logoutButton.addActionListener(e -> {
            frame.showScreen("MAIN_MENU");
        });

        buttonPanel.add(searchFlightsButton);
        buttonPanel.add(viewReservationsButton);
        buttonPanel.add(cancelReservationButton);
        buttonPanel.add(askQuestionButton);
        buttonPanel.add(logoutButton);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(buttonPanel);

        add(titleLabel, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }
}