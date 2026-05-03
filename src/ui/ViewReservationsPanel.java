package ui;

import dao.ReservationDAO;
import models.Customer;
import models.Reservation;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewReservationsPanel extends JPanel {
    private final MainFrame frame;
    private final Customer customer;
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private JTable table;
    private DefaultTableModel tableModel;

    public ViewReservationsPanel(MainFrame frame, Customer customer) {
        this.frame = frame;
        this.customer = customer;
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel titleLabel = new JLabel("My Reservations", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Table setup
        String[] cols = {"ID", "Date", "Status", "Price", "Type"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton cancelButton = new JButton("Cancel Selected Reservation");
        JButton backButton = new JButton("Back to Dashboard");

        cancelButton.addActionListener(e -> cancelSelected());
        backButton.addActionListener(e -> frame.showCustomerDashboard(customer));

        buttonPanel.add(cancelButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        List<Reservation> list = reservationDAO.getReservationsForCustomer(customer.getSsn());
        for (Reservation r : list) {
            tableModel.addRow(new Object[]{
                r.getId(), r.getReservationDate(), r.getStatus(), "$" + r.getPrice(), r.getTripType()
            });
        }
    }

    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.");
            return;
        }

        int resId = (int) tableModel.getValueAt(row, 0);
        String status = (String) tableModel.getValueAt(row, 2);

        if ("Cancelled".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "This reservation is already cancelled.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel reservation #" + resId + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            if (reservationDAO.cancelReservation(resId)) {
                JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.");
                refreshTable();
            }
        }
    }
}
