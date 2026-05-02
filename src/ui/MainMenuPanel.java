package ui;

import java.awt.*;
import javax.swing.*;

public class MainMenuPanel extends JPanel {
    public MainMenuPanel(MainFrame frame) {
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Flight Reservation System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton loginButton = new JButton("Login");
        JButton createAccountButton = new JButton("Create Customer Account");
        JButton exitButton = new JButton("Exit");

        loginButton.addActionListener(e -> frame.showScreen("LOGIN"));
        createAccountButton.addActionListener(e -> frame.showScreen("CREATE_CUSTOMER"));
        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);
        buttonPanel.add(exitButton);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.add(buttonPanel);

        add(titleLabel, BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);
    }
}