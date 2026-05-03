package ui;

import java.awt.*;
import javax.swing.*;
import models.Customer;

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
        JButton viewReservationsButton = new JButton("View/Cancel My Reservations");
        JButton askQuestionButton = new JButton("Ask Customer Service Question");
        JButton logoutButton = new JButton("Logout");

        searchFlightsButton.addActionListener(e -> {
            frame.showSearchFlightsScreen(customer);
        });

        viewReservationsButton.addActionListener(e -> {
            frame.showViewReservationsScreen(customer);
        });
        askQuestionButton.addActionListener(e -> {
            frame.showCustomerChatScreen(customer);
        });

        logoutButton.addActionListener(e -> {
            frame.showScreen("MAIN_MENU");
        });

        buttonPanel.add(searchFlightsButton);
        buttonPanel.add(viewReservationsButton);
        buttonPanel.add(askQuestionButton);
        buttonPanel.add(logoutButton);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(buttonPanel);

        add(titleLabel, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }
}
