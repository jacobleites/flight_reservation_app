package ui;

import models.Customer;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CustomerChatPanel extends JPanel {
    private static final List<CustomerQuestion> QUESTIONS = new ArrayList<>();
    private final JTextArea historyArea = new JTextArea(10, 50);

    public CustomerChatPanel(MainFrame frame, Customer customer) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Customer Service Question (Customer ID: " + customer.getAccount_id() + ")", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JTextArea questionArea = new JTextArea(10, 50);
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);

        JButton submitButton = new JButton("Submit Question");
        JButton backButton = new JButton("Back");

        submitButton.addActionListener(e -> {
            String question = questionArea.getText().trim();
            if (question.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a question before submitting.");
                return;
            }

            submitQuestion(customer, question);
            JOptionPane.showMessageDialog(
                    this,
                    "Your question has been submitted.",
                    "Submitted",
                    JOptionPane.INFORMATION_MESSAGE
            );
            questionArea.setText("");
            refreshHistory(customer);
        });

        backButton.addActionListener(e -> frame.showCustomerDashboard(customer));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(submitButton);
        bottomPanel.add(backButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(new JLabel("Previous messages:"));
        contentPanel.add(new JScrollPane(historyArea));
        contentPanel.add(new JLabel("Enter your question:"));
        contentPanel.add(new JScrollPane(questionArea));

        add(title, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshHistory(customer);
    }

    public static synchronized void submitQuestion(Customer customer, String question) {
        QUESTIONS.add(new CustomerQuestion(customer, question));
    }

    public static synchronized List<CustomerQuestion> getQuestions() {
        return new ArrayList<>(QUESTIONS);
    }

    public static synchronized List<CustomerQuestion> getQuestionsForCustomer(int accountId) {
        List<CustomerQuestion> results = new ArrayList<>();
        for (CustomerQuestion question : QUESTIONS) {
            if (question.getCustomer().getAccount_id() == accountId) {
                results.add(question);
            }
        }
        return results;
    }

    public static synchronized void resolveQuestion(CustomerQuestion question) {
        if (question != null) {
            question.markResolved();
        }
    }

    private void refreshHistory(Customer customer) {
        StringBuilder sb = new StringBuilder();
        List<CustomerQuestion> customerQuestions = getQuestionsForCustomer(customer.getAccount_id());
        if (customerQuestions.isEmpty()) {
            historyArea.setText("No previous messages.");
            return;
        }

        for (CustomerQuestion q : customerQuestions) {
            sb.append("[").append(q.getStatus()).append("] ")
                    .append(q.getSubmittedAt()).append("\n")
                    .append(q.getQuestion()).append("\n\n");
        }
        historyArea.setText(sb.toString());
        historyArea.setCaretPosition(0);
    }

    public static class CustomerQuestion {
        private final Customer customer;
        private final String question;
        private final String submittedAt;
        private boolean resolved;

        public CustomerQuestion(Customer customer, String question) {
            this.customer = customer;
            this.question = question;
            this.submittedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            this.resolved = false;
        }

        public Customer getCustomer() {
            return customer;
        }

        public String getQuestion() {
            return question;
        }

        public String getSubmittedAt() {
            return submittedAt;
        }

        public String getStatus() {
            return resolved ? "Resolved" : "Unresolved";
        }

        public void markResolved() {
            resolved = true;
        }

        @Override
        public String toString() {
            return "Customer ID " + customer.getAccount_id() + " - " + customer.getFirstName() + " " + customer.getLastName() + " (" + customer.getUsername() + ") - " + submittedAt + " [" + getStatus() + "]";
        }
    }
}
