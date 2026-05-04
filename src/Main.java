import javax.swing.SwingUtilities;
import ui.MainFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

// todo; ensure that when we cancel a ticket, we update the available seats on that flight.
// make sure that we create a notification and send it to the user who has priority number 1 for
// on the waitlist for the flight that has been updated with new availability.